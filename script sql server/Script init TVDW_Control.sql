-- Thay đúng đường dẫn của thư mục chứa file csv hàng tuần trong máy mấy bạn trước khi run nha
DECLARE @Folder_Contain_Weekly_CSV NVARCHAR(255) = 'D:\TVDW_2024_Nhom10\Date_Dim\date_dim_without_quarter.csv';
GO
-- run script này sẽ tạo ra: 
-- +Database TVDW_Control
-- +Table config_file (đã insert sẵn 3 dòng tương ứng với 3 web)
-- +Table file_log
-- +Procedure GetFileLogWithConfigDetails
-- +Procedure InsertFileLogIfNotExist
-- +Procedure Update_count_rows
-- +Procedure Update_dt_update
-- +Procedure Update_status

---- Nếu run sai có thể drop database đi làm lại
--use master;
--drop database TVDW_Control;
--create database TVDW_Control;
--go
--use TVDW_Control;

---------------------------------------------------------------------------------------

-- tao DB TVDW_Control
IF DB_ID('TVDW_Control') IS NULL
BEGIN
    CREATE DATABASE TVDW_Control;
END
ELSE
BEGIN
    PRINT 'Database "TVDW_Control" already exists.';
END
GO
use TVDW_Control;

-- tao bang config_file
IF OBJECT_ID('config_file', 'U') IS NULL
BEGIN
    CREATE TABLE config_file (
		ID INT PRIMARY KEY,
		Name NVARCHAR(50), -- tên web
		Source_Web NVARCHAR(255), -- link web
		Source_File_Location NVARCHAR(255), -- thư mục lưu các file csv
		Destination_Table_Temp_Staging NVARCHAR(50), -- tên bảng tạm chuỗi
		Destination_Table_Staging NVARCHAR(50), -- tên bảng tạm
		Destination_Table_DW NVARCHAR(50) -- tên bảng chung cuối cùng trong DB DW
	);
	INSERT INTO config_file (ID, Name, Source_Web, Source_File_Location, Destination_Table_Temp_Staging, Destination_Table_Staging, Destination_Table_DW)
	VALUES (1, 'CPS', 'https://cellphones.com.vn/tivi.html', @Folder_Contain_Weekly_CSV, 'String_Temp_Weekly_CPS', 'Weekly_CPS', 'Tivi');
	INSERT INTO config_file (ID, Name, Source_Web, Source_File_Location, Destination_Table_Temp_Staging, Destination_Table_Staging, Destination_Table_DW)
	VALUES (2, 'DMCL', 'https://dienmaycholon.com/tivi', @Folder_Contain_Weekly_CSV, 'String_Temp_Weekly_DMCL', 'Weekly_DMCL', 'Tivi');
	INSERT INTO config_file (ID, Name, Source_Web, Source_File_Location, Destination_Table_Temp_Staging, Destination_Table_Staging, Destination_Table_DW)
	VALUES (3, 'DMX', 'https://www.dienmayxanh.com/tivi', @Folder_Contain_Weekly_CSV, 'String_Temp_Weekly_DMX', 'Weekly_DMX', 'Tivi');
END;
select * from config_file;

-- tao bang file_log
IF OBJECT_ID('file_log', 'U') IS NULL
BEGIN
	-- drop table file_log;
	CREATE TABLE file_log (       
		ID INT IDENTITY(1,1) PRIMARY KEY, -- Khóa chính tự tăng cho bảng, mã định danh duy nhất cho mỗi bản ghi
		ID_config INT,                   -- Khóa ngoại liên kết với bảng control.config_file (nếu có)
		filename NVARCHAR(255),          -- Tên file
		file_size_kb INT,                -- Kích thước file tính bằng KB
		status NVARCHAR(10),                  -- Trạng thái của file (VD: ER, OK)
		count_lines INT,                       -- Số lượng bản ghi trong file
		count_rows INT,						-- số dòng thật sự đã load dc vô bảng String temp
		create_at DATE,                       -- Thời gian (Ngày tải file)
		dt_update DATETIME,               -- Thời gian cập nhật
	);
	-- Nếu bảng `control.config_file` đã có, bạn có thể thêm ràng buộc khóa ngoại
	ALTER TABLE file_log
	ADD CONSTRAINT fk_config_file
	FOREIGN KEY (ID_config) REFERENCES config_file(ID);
END
--select * from file_log;

IF OBJECT_ID('InsertFileLogIfNotExist', 'P') IS NOT NULL
    DROP PROCEDURE InsertFileLogIfNotExist;
GO
    CREATE PROCEDURE InsertFileLogIfNotExist
        @ID_config INT,
        @filename NVARCHAR(255),
        @file_size_kb INT,
        @count_lines INT,
        @create_at DATE = null,
        @dt_update DATETIME = null
    AS
    BEGIN
		-- 1.3.1.1. Kiểm tra xem filename đã tồn tại trong bảng file_log chưa?
		IF EXISTS (SELECT 1 FROM file_log WHERE filename = @filename)
		BEGIN
			PRINT 'File ' + @filename + ' đã tồn tại trong bảng file_log.'
			-- Có thể trả về một giá trị lỗi hoặc làm gì đó để thông báo rằng file đã tồn tại
		END
		ELSE
		BEGIN
			-- 1.3.1.2. Insert thông tin file csv vô bảng file_log, trạng thái là ER
			INSERT INTO file_log (ID_config, filename, file_size_kb, status, count_lines, create_at, dt_update) 
			VALUES (
				@ID_config, 
				@filename, 
				@file_size_kb, 
				'ER', 
				@count_lines, 
				COALESCE(@create_at, CAST(GETDATE() AS DATE)), -- Nếu @time là NULL, dùng ngày hiện tại
				COALESCE(@dt_update, GETDATE()) -- Nếu @dt_update là NULL, dùng datetime hiện tại
			)
		END
    END
GO

IF OBJECT_ID('GetFileLogWithConfigDetails', 'P') IS NOT NULL
    DROP PROCEDURE GetFileLogWithConfigDetails;
GO
	CREATE PROCEDURE GetFileLogWithConfigDetails
AS
BEGIN
    SELECT 
        config_file.Name,
        config_file.Source_File_Location,
        file_log.filename,
		file_log.status,
		file_log.count_lines,
		file_log.count_rows,
		file_log.dt_update,
        config_file.Destination_Table_Temp_Staging,
        config_file.Destination_Table_Staging,
        config_file.Destination_Table_DW
    FROM file_log JOIN config_file
    ON file_log.ID_config = config_file.ID;
END;
GO

IF OBJECT_ID('Update_count_rows', 'P') IS NOT NULL
    DROP PROCEDURE Update_count_rows;
GO
	CREATE PROCEDURE Update_count_rows
		@filename NVARCHAR(255),
		@rows INT
	AS
	BEGIN
		UPDATE file_log
		SET count_rows=@rows
		WHERE filename=@filename
	END;
GO

IF OBJECT_ID('Update_status', 'P') IS NOT NULL
    DROP PROCEDURE Update_status;
GO
	CREATE PROCEDURE Update_status
		@filename NVARCHAR(255),
		@status NVARCHAR(10)
	AS
	BEGIN
		UPDATE file_log
		SET status=@status
		WHERE filename=@filename
	END;
GO

IF OBJECT_ID('Update_dt_update', 'P') IS NOT NULL
    DROP PROCEDURE Update_dt_update;
GO
	CREATE PROCEDURE Update_dt_update
		@filename NVARCHAR(255),
		@dt_update DATETIME = null
	AS
	BEGIN
		UPDATE file_log
		SET dt_update=COALESCE(@dt_update, GETDATE()) -- Nếu @dt_update là NULL, dùng datetime hiện tại
		WHERE filename=@filename
	END;
GO

-- delete from file_log;
-- select * from file_log;

EXEC GetFileLogWithConfigDetails;

-- ----------------------------
-- Procedure structure for getLogFileToday
-- ----------------------------
IF OBJECT_ID('getLogFileToday', 'P') IS NOT NULL
    DROP PROCEDURE getLogFileToday;
GO

CREATE PROCEDURE getLogFileToday
    @status_In NVARCHAR(255) -- Tham số đầu vào
	@ID_config INT
AS
BEGIN
    -- Routine body goes here...
    SELECT *
    FROM file_log
    WHERE status = @status_In AND ID_config = @ID_config;
END;
GO
