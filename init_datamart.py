import pyodbc

def execute_sql_script(file_path, connection_string):
    try:
        # Kết nối đến cơ sở dữ liệu
        with pyodbc.connect(connection_string) as conn:
            cursor = conn.cursor()

            # Đọc file SQL
            with open(file_path, 'r', encoding='utf-8') as file:
                sql_script = file.read()

            # Bỏ qua các đoạn script liên quan đến tạo procedure
            lines = sql_script.splitlines()
            filtered_script = []
            skip_block = False

            for line in lines:
                stripped_line = line.strip().lower()

                if stripped_line.startswith("create procedure"):
                    skip_block = True

                if not skip_block:
                    filtered_script.append(line)

                if skip_block and stripped_line == "go":
                    skip_block = False

            # Thực thi script đã loại bỏ phần tạo procedure
            final_script = "\n".join(filtered_script)
            commands = final_script.split("GO")

            for command in commands:
                if command.strip():
                    cursor.execute(command)

            print("Database initialized successfully, without procedures.")

    except Exception as e:
        print(f"An error occurred: {e}")

# Thông tin kết nối SQL Server
server = "localhost,1433"  # Tên server kèm cổng

# Đường dẫn tới file script SQL
script_path = "D:\TVDW_2024_Nhom10\script sql server\Script init DataMart.sql"  # Thay bằng đường dẫn thực tế

database = "master"  # Kết nối tới database mặc định

# Gọi hàm để chạy script
execute_sql_script(server, database, script_path)
