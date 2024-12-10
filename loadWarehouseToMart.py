import configparser
import pyodbc  # Thư viện để kết nối và thao tác với MS SQLSERVER.
from pyodbc import Error  # Class xử lý lỗi của pyodbc.
from datetime import datetime  # Sửa đổi import để dùng trực tiếp datetime.now().

# Hàm đọc cấu hình từ file .ini.
def ReadDatabaseConfig(filePath):
    try:
        # Tạo đối tượng ConfigParser
        config = configparser.ConfigParser()
        config.read(filePath)  # Đọc file .ini
        
        # Kiểm tra nếu section 'database' không tồn tại
        if 'database' not in config:
            raise ValueError("Không tìm thấy section [database] trong file .ini.")
        
        # Trích xuất thông tin từ section [database]
        return {
            "host": config['database']['ip'],
            "port": int(config['database']['port']),
            "user": config['database']['username'],
            "password": config['database']['password'],
            "database": config['database']['dbname'],
        }
    except Exception as e:
        print(f"Lỗi khi đọc file .ini: {e}")
        return None

# Hàm kết nối đến cơ sở dữ liệu.
def ConnectToDatabase(configDb):
    try:
        connection = pyodbc.connect(**configDb)
        if connection.is_connected():
            print("Kết nối thành công tới database!")
            return connection
    except Error as e:
        print(f"Lỗi khi kết nối tới database: {e}")
        return None

# Hàm thực thi câu truy vấn SQL.
def ExecuteQuery(connection, query, params=None, fetchOne=False):
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute(query, params)
        return cursor.fetchone() if fetchOne else cursor.fetchall()
    except Error as e:
        print(f"Lỗi khi thực thi truy vấn: {e}")
        return None
    finally:
        cursor.close()

# Hàm cập nhật trạng thái và thời gian của một dòng.
def UpdateStatus(connection, table, ID, status):
    try:
        query = f"""
        UPDATE {table}
        SET status = @status, updated_at = @updateAt
        WHERE ID = @ID;
        """
        updatedAt = datetime.now()
        ExecuteQuery(connection, query, (status, updatedAt, ID))
        connection.commit()
        print(f"Cập nhật trạng thái '{status}' cho ID = {ID}.")
    except Error as e:
        print(f"Lỗi khi cập nhật trạng thái: {e}")

# Hàm gọi proceduce.
def CallProc(connection, table, ID, status):
    try:
        query = f"""
        UPDATE {table}
        SET status = @status, updated_at = @updateAt
        WHERE ID = @ID;
        """
        updatedAt = datetime.now()
        ExecuteQuery(connection, query, (status, updatedAt, ID))
        connection.commit()
        print(f"Cập nhật trạng thái '{status}' cho ID = {ID}.")
    except Error as e:
        print(f"Lỗi khi cập nhật trạng thái: {e}")        

# Hàm truncate aggregate_tvdata ở DataMart.
def TruncateAggregateTVData(connection):
    try:
        query = f"""
        TRUNCATE TABLE aggregate_tvdata
        """
        ExecuteQuery(connection, query)
        connection.commit()
        print(f"Truncate bảng aggregate_tvdata trong DataMart thành công")
    except Error as e:
        print(f"Lỗi khi truncate aggregate_tvdata ở DataMart: {e}")

# Hàm load dữ liệu vào aggregate_tvdata ở DataMart từ bảng aggregate_tvdata ở TVDW .
def LoadWarehouseToMart(connection, row):
    try:
        query = """
        SELECT dest_table_staging
        FROM TVDW_Control.config_file
        WHERE index_id = %s;
        """
        configRow = ExecuteQuery(connection, query, (row['config_file_id'],), fetchOne=True)
        if not configRow:
            print(f"Không tìm thấy cấu hình cho config_file_id = {row['config_file_id']}")
            return
        
        sourceTable = configRow['dest_table_staging']
        connection.database = 'TVDW'
        connection.database = 'DataMart'

        #load dữ liệu từ staging qua data warehouse
        loadDataAggregateTVDataQuery = f"""
        INSERT INTO DataMartaggregate_tvdata (NameSource, ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink, dateOnCSV, date_expired)
        SELECT
            d.song_id,
            t.Top,
            t.TimeGet,
            t.date_dim_id
        FROM db_staging.{sourceTable} t
        INNER JOIN dim_song d
            ON d.song_name = t.SongName AND d.Artist = t.Artist
        WHERE NOT EXISTS (
            SELECT 1 FROM top_song_fact dw
            WHERE dw.time_get = t.TimeGet AND dw.song_key = d.song_id
        );
        """
        ExecuteQuery(connection, loadDataAggregateTVDataQuery)
        connection.commit()
        print(f"Dữ liệu đã được load vào bảng aggregate_tvdata từ bảng aggregate_tvdata ở TVDW.")
    except Error as e:
        print(f"Lỗi khi load dữ liệu: {e}")

# Ghi lỗi vào file
def WriteErrorLog(errorMessage, filePath):
    try:
        with open(filePath, "a", encoding="utf-8") as errorFile:
            currentTime = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            errorFile.write(f"[{currentTime}] {errorMessage}\n")
        print(f"Lỗi đã được ghi vào file: {filePath}")
    except Exception as e:
        print(f"Lỗi khi ghi lỗi vào file: {e}")

import argparse

def main(filePath, ID_config):
    retryCount = 0  # Đếm số lần thử
    while retryCount < 5:
        try:
            # 1. Đọc file config.ini ( db control info ) để lấy cấu hình
            config = ReadDatabaseConfig(filePath)
            if not config:
                raise ValueError("Cấu hình database không hợp lệ hoặc bị lỗi.")
            # 2. Kết nối TVDW_Control
            # 3. Kiểm tra kết nối cơ sở dữ liệu?
            try:
                connection = pyodbc.connect(
                    host=config['host'],
                    port=config['port'],
                    user=config['user'],
                    password=config['password'],
                    database=config['database']
                )
                if connection.is_connected():
                     # 4. Query TVDW_Control.file_log dòng với status = Extract_Complete , config_id
                    row = ExecuteQuery(connection, """
                    SELECT *
                    FROM file_log
                    WHERE (status = 'T_SU' OR status = 'T_ERR') 
                    AND ID_config = @IDConfig
                    ORDER BY id
                    OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY;
                    """, (ID_config,), fetchOne=True)
                    # 5. Kiểm tra kết quả truy vấn thành công ?
                    if row:
                        # 6. cập nhật status = Extract_Staging_Start
                        UpdateStatus(connection, "TVDW_Control.file_log", row['ID'], "LDM_ST")
                        # 7. truncate bảng aggregate_tvdata ở datamart
                        TruncateAggregateTVData(connection)
                        # 8. copy dữ liệu trước ngày hôm nay từ bảng tvdata sang bảng aggregate_tvdata ở datamart

                        # 9. lấy dữ liệu cần insert ngày hôm nay Tivi ở warehouse
                        # 10. Ghi dữ liệu từ TVDW vào DataMart
                        # 11. Kiểm tra load dữ liệu thành công ?
                        try:
                            LoadWarehouseToMart(connection, row)
                            # 13. cập nhật status = LDM_SU
                            UpdateStatus(connection, "TVDW_Control.file_log", row['ID'], "LDM_SU")
                            break
                        except Exception as e:
                            # Ghi lỗi vào file D:\\error_LOAD_MART.txt
                            WriteErrorLog(str(e),"D:\\TVDW_2024_Nhom10\\ERR\\error_LOAD_MART.txt")
                            # 11.1. cập nhật status = LDM_ERR
                            UpdateStatus(connection, "TVDW_Control.file_log", row['ID'], "LDM_ERR")
                            connection.close()
                            break
                    else:
                        connection.close()
                        break
            except Exception as e:
                # Ghi lỗi vào file D:\\error_CONNECT_DB
                WriteErrorLog(str(e), "D:\\TVDW_2024_Nhom10\\ERR\\error_CONNECT_DB.txt")
                # 3.1. Kiểm tra số lần lặp có hơn 5 lần không ? 
                retryCount += 1
                if retryCount > 5:
                    break
                print(f"Thử lại lần thứ {retryCount}...")
        finally:
            if 'connection' in locals() and connection.is_connected():
                connection.close()
                print("Đã đóng kết nối database.")

if __name__ == "__main__":
    # Khởi tạo trình phân tích tham số
    parser = argparse.ArgumentParser(description="Script xử lý dữ liệu với tham số từ file config và ID_config.")
    parser.add_argument("filePath", type=str, help="Đường dẫn tới file cấu hình (config.ini).")
    parser.add_argument("ID_config", type=int, help="ID cấu hình trong database.")
    
    # Phân tích tham số
    args = parser.parse_args()
    
    # Gọi hàm main với các tham số
    main(args.filePath, args.ID_config)
