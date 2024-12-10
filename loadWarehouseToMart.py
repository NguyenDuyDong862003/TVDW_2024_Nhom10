import configparser
import pyodbc  # Thư viện để kết nối và thao tác với MS SQLSERVER.
from pyodbc import Error  # Class xử lý lỗi của pyodbc.
from datetime import datetime  # Sửa đổi import để dùng trực tiếp datetime.now().
import time
import os

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
            "host": config['sqlserver']['host'],  
            "port": int(config['sqlserver']['port']),
            "user": config['sqlserver']['user'],
            "password": config['sqlserver']['password'],
            "database": config['sqlserver']['database'],
            "trusted_connection": config.getboolean('sqlserver', 'trusted_connection', fallback=True)  # Nếu có
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
def ExecuteQuery(cursor, query, params=None, fetchOne=False):
    try:
        cursor.execute(query, params)  # Thực thi truy vấn SQL
        # Trả về kết quả tùy theo yêu cầu
        return cursor.fetchone() if fetchOne else cursor.fetchall()
    except pyodbc.Error as e:
        print(f"Lỗi khi thực thi truy vấn: {e}")
        return None

# Hàm gọi proceduce ở DataMart.
def CallProc(cursor, proceduceName, params=None, fetchOne=False):
    try:
        cursor.callproc(proceduceName, params)
        return cursor.fetchone() if fetchOne else cursor.fetchall()
    except Error as e:
        print(f"Lỗi gọi proceduce {proceduceName}: {e}")
        return None
    finally:
        cursor.close()   

# Hàm cập nhật trạng thái và thời gian của một dòng.
def UpdateStatus(cursor, table, ID, status):
    try:
        query = f"""
        UPDATE {table}
        SET status = ?, updated_at = ?
        WHERE ID = ?;
        """
        updatedAt = datetime.now()  # Lấy thời gian hiện tại
        cursor.execute(query, (status, updatedAt, ID))  # Thực thi truy vấn
        print(f"Cập nhật trạng thái '{status}' cho ID = {ID}.")
    except pyodbc.Error as e:
        print(f"Lỗi khi cập nhật trạng thái: {e}")

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

def connect(filename):
    # 1. Đọc file .ini để lấy cấu hình
    db_config = configparser.ConfigParser()
    db_config.read(filename)

    # Lấy các giá trị cấu hình từ file .ini
    host = db_config.get('sqlserver', 'host')
    user = db_config.get('sqlserver', 'user')
    password = db_config.get('sqlserver', 'password')
    database = db_config.get('sqlserver', 'database')
    trusted_connection = db_config.getboolean('sqlserver', 'trusted_connection', fallback=True)
    # 2. Kết nối tới cơ sở dữ liệu
    try:
        # Kiểm tra nếu sử dụng Windows Authentication (trusted_connection = True)
        if trusted_connection:
            connection_string = (
                f"DRIVER={{ODBC Driver 17 for SQL Server}};"
                f"SERVER={host};"
                f"DATABASE={database};"
                f"Trusted_Connection=yes;"
            )
        else:
            connection_string = (
                f"DRIVER={{ODBC Driver 17 for SQL Server}};"
                f"SERVER={host};"
                f"DATABASE={database};"
                f"UID={user};"
                f"PWD={password};"
            )
        
        # Kết nối đến SQL Server
        cnx = pyodbc.connect(connection_string)
        print("Connected to SQL Server database")
        return cnx
    
    except pyodbc.Error as err:
        print(f"Error: {err}")
        current_date = datetime.now().strftime("%d%m%Y")
        WriteErrorLog(str(err), fr"D:\\TVDW_2024_Nhom10\\ERR\\error_CONNECT_DB\\{current_date}_tv.txt")
        return None

def countdown(t):
    while t:
        minis, secs = divmod(t, 60)
        timer = '{:02d}:{:02d}'.format(minis, secs)
        print("\r", timer, end="")
        time.sleep(1)
        t -= 1
    print("\r", '00:00', end="")
    print()

def main(filePath, ID_config):
    count = 0
    currentdate = datetime.now().strftime("%Y:%m:%d")
    current_date = datetime.now().strftime("%d%m%Y")
    fileconfigs = '\dbconfig\dbControl.ini'
    configWarehouse = '\dbconfig\dbWarehouse.ini'
    configMart = '\dbconfig\dbMart.ini'
    save_directoryEr = r"D:\TVDW_2024_Nhom10\ERR\error_LOAD_MART"
    file_err = fr"{save_directoryEr}\{current_date}_tv.txt"
    while True:
        # 1 , 2 
        current_dir_CT = os.path.dirname(__file__) + fileconfigs
        connctrl = connect(current_dir_CT)
        current_dir_WH = os.path.dirname(__file__) + configWarehouse
        connWh = connect(current_dir_WH)
        current_dir_M = os.path.dirname(__file__) + configMart
        connM = connect(current_dir_M)
        # 3 kiểm tra kết nối đến cơ sở dữ liệu
        if connctrl is not None and connWh is not None and connM is not None:
            try:
                controlcursor = connctrl.cursor()
                # 4. Lấy dòng file_log từ TVDW_Control với status= T_SU và ID_config
                controlcursor.callproc('getFileLogToday', ('T_SU', ID_config))
                status = list(controlcursor.stored_results())
                st = status[0].fetchone()
                print(st)
                while (True):
                    # 5. kiểm tra dữ liệu vừa lấy có tồn tại không
                    if st is not None:
                        martcursor = connM.cursor()
                        # 6. cập nhật status = LDM_ST
                        UpdateStatus(controlcursor, 'log_file', st[0], 'LDM_ST')
                        # 7. truncat bảng aggregate_tvdata ở datamart
                        use_query = f"USE DataMart"
                        ExecuteQuery(martcursor, use_query)
                        truncate_query = f"TRUNCATE TABLE aggregate_tvdata "
                        ExecuteQuery(martcursor, truncate_query)

                        print('truncat success')

                        # 8. copy dữ liệu trước ngày hôm nay từ bảng tvdata sang bảng aggregate_tvdata ở datamart
                        CallProc(martcursor, 'copyBeforeCurrentDate')
                        print('copy data success')
                        # 9. lấy dữ liệu cần insert ngày hôm nay aggregate_tvdata ở warehouse
                        whcursor = connWh.cursor()
                        source_cursor = CallProc(whcursor, 'getDataAggerateCurrentDate')
                        sourcewh = list(source_cursor.stored_results())

                        rows = sourcewh[0].fetchall()
                        # 10 ghi dữ liệu vào bảng aggregate_tvdata ở datamart
                        for row in rows:
                            insertQ = '''INSERT INTO [DataMart].[aggregate_tvdata] (
                                            [NameSource], [ProductName], [OriginalPrice], [DiscountedPrice],  
                                            [ProductLink], [ImageLink], [dateOnCSV], [date_expired]
                                        ) VALUES (
                                            @NameSource, @ProductName, @OriginalPrice, @DiscountedPrice, 
                                            @ProductLink, @ImageLink, @dateOnCSV, @date_expired;
                                        '''
                            ExecuteQuery(martcursor, insertQ, {
                                            'NameSource': row[1], 'ProductName': row[2], 'OriginalPrice': row[3], 'DiscountedPrice': row[4], 
                                            'ProductLink': row[5], 'ImageLink': row[6], 'dateOnCSV': row[7], 'date_expired': row[8]
                                        })
                        print('insert success')
                        # 11. đổi tên bảng aggregate_tvdata và tvdata ở datamart
                        CallProc(martcursor, 'renameTable')
                        connM.commit()
                        print('rename success')

                        # 12. cập nhật file_log status = LDM_SU 
                        UpdateStatus(controlcursor, 'log_file', st[0],'LDM_SU')
                        connM.commit()
                        connWh.commit()
                        connctrl.commit()
                        # 13. đóng connection tất cả database
                        martcursor.close()
                        whcursor.close()
                        connWh.close()
                        connM.close()
                        print("Dữ liệu đã được thêm thành công!")
                        break
                    else:
                        # 5.1. Kiểm tra số lần lặp có hơn 5 lần không ? 
                        count += 1
                        if count >= 5:
                            # Ghi lỗi vào file D:\\error_LOAD_MART.txt
                            err = "Không lấy được dòng log"
                            print(f"{err}")
                            with open(file_err, 'a') as file:
                                file.write(f'Error: {str(err)}\n')
                            break
                        else:
                            continue
            finally:
                # 5.2 đóng kết nối db control
                controlcursor.close()
                connctrl.close()
            break
        else:
            # 3.1 kiểm tra lần lặp có lớn hơn 5 không?
            count += 1
            if count >= 5:
                err = "connect unsuccess"
                print(f"Lỗi connect db : {err}")
                with open(file_err, 'a') as file:
                    file.write(f'Error: {str(err)}\n')
                break
            print('reconnect last:')
            countdown(600)
            print('start reconnect')

if __name__ == '__main__':
    # Khởi tạo trình phân tích tham số
    parser = argparse.ArgumentParser(description="Script xử lý dữ liệu với tham số từ file config và ID_config.")
    parser.add_argument("filePath", type=str, help="Đường dẫn tới file cấu hình (config.ini).")
    parser.add_argument("ID_config", type=int, help="ID cấu hình trong database.")
    
    # Phân tích tham số
    args = parser.parse_args()
    
    # Gọi hàm main với các tham số
    main(args.filePath, args.ID_config)
    
