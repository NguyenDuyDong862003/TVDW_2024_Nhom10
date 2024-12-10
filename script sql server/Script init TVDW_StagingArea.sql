-- Thay đúng đường dẫn của file csv date dim trước khi run nha
DECLARE @Path_File_Date_Dim_CSV NVARCHAR(255) = 'C:\Users\ASUS\Desktop\Data warehouse\month_dim_date_dim_dw_20200626\Date_Dim\date_dim_without_quarter.csv';
-- run script này sẽ tạo ra: 
-- +Database TVDW_StagingArea
-- +Table date_dim (đã insert sẵn toàn bộ data ở file csv date dim của thầy)
-- +Table String_Temp_Weekly_CPS
-- +Table String_Temp_Weekly_DMCL
-- +Table String_Temp_Weekly_DMX
-- +Table Weekly_CPS
-- +Table Weekly_DMCL
-- +Table Weekly_DMX
-- +Procedure Delete_Data_String_Temp_Weekly_Table
-- +Procedure Delete_Data_Weekly_Table
-- +Procedure Get_Rows_In_String_Temp_Table
-- +Procedure Load_From_File_CSV_To_String_Temp_Weekly_Table
-- +Procedure Load_From_String_Temp_Weekly_CPS_Table_To_Weekly_CPS_Table
-- +Procedure Load_From_String_Temp_Weekly_DMCL_Table_To_Weekly_DMCL_Table
-- +Procedure Load_From_String_Temp_Weekly_DMX_Table_To_Weekly_DMX_Table
-- +Procedure Set_Id_Date_On_CSV_And_Date_On_CSV_For_Weekly_Table

---- Nếu run sai có thể drop database đi làm lại
--use master;
--drop database TVDW_StagingArea;
--create database TVDW_StagingArea;
--go
--use TVDW_StagingArea;

---------------------------------------------------------------------------------------

-- tao DB TVDW_StagingArea
IF DB_ID('TVDW_StagingArea') IS NULL
BEGIN
    CREATE DATABASE TVDW_StagingArea;
END
ELSE
BEGIN
    PRINT 'Database "TVDW_StagingArea" already exists.';
END

use TVDW_StagingArea;

-- tao bang datedim va load vo luon
IF OBJECT_ID('date_dim', 'U') IS NULL
BEGIN
	--drop table date_dim;
	CREATE TABLE date_dim (
		date_sk INT PRIMARY KEY,                -- Mã ngày
		full_date DATE,                         -- Ngày đầy đủ (yyyy-MM-dd)
		day_since_2005 INT,                     -- Số ngày kể từ năm 2005
		month_since_2005 INT,                   -- Số tháng kể từ năm 2005
		day_of_week VARCHAR(20),                -- Tên ngày trong tuần (ví dụ: "Monday")
		calendar_month VARCHAR(20),             -- Tên tháng (ví dụ: "January")
		calendar_year INT,                      -- Năm (ví dụ: 2023)
		calendar_year_month VARCHAR(20),        -- Kết hợp năm và tháng (ví dụ: "2023-Jan")
		day_of_month INT,                       -- Ngày trong tháng
		day_of_year INT,                        -- Ngày trong năm
		week_of_year_sunday INT,                -- Tuần trong năm (tính từ chủ nhật)
		year_week_sunday VARCHAR(20),            -- Năm và tuần (tính từ chủ nhật)
		week_sunday_start DATE,                 -- Ngày bắt đầu tuần (tính từ chủ nhật)
		week_of_year_monday INT,                -- Tuần trong năm (tính từ thứ hai)
		year_week_monday VARCHAR(20),            -- Năm và tuần (tính từ thứ hai)
		week_monday_start DATE,                 -- Ngày bắt đầu tuần (tính từ thứ hai)
		holiday VARCHAR(20),                    -- Thông tin về ngày nghỉ (ví dụ: "Non-Holiday")
		day_type VARCHAR(20)                    -- Loại ngày ("Weekend" hoặc "Weekday")
	);
	-- chèn data từ file csv vào bảng date_dim
	DECLARE @sql NVARCHAR(MAX);
	SET @sql = N'
		BULK INSERT date_dim
		FROM ''' + @Path_File_Date_Dim_CSV + '''
		WITH (
			FIELDTERMINATOR = '','',
			ROWTERMINATOR = ''0x0a'',
			FIRSTROW = 1,
			CODEPAGE = ''65001'' -- UTF-8
		);';
	EXEC sp_executesql @sql;
END
--SELECT TOP 3 * FROM date_dim;
-- từ 1 ngày có thể truy ra được date_sk
--SELECT date_sk FROM date_dim WHERE full_date = '2024-11-08';

-- tao bang string temp weekly cps
IF OBJECT_ID('String_Temp_Weekly_CPS', 'U') IS NULL
BEGIN
	--drop table String_Temp_Weekly_CPS;
    CREATE TABLE String_Temp_Weekly_CPS (
        Col1 NVARCHAR(255),
        Col2 NVARCHAR(255),
        Col3 NVARCHAR(255),
        Col4 NVARCHAR(255),
        Col5 NVARCHAR(255),
    );
END;
-- tao bang string temp weekly dmcl
IF OBJECT_ID('String_Temp_Weekly_DMCL', 'U') IS NULL
BEGIN
	--drop table String_Temp_Weekly_DMCL;
    CREATE TABLE String_Temp_Weekly_DMCL (
        Col1 NVARCHAR(255),
        Col2 NVARCHAR(255),
        Col3 NVARCHAR(255),
        Col4 NVARCHAR(255),
        Col5 NVARCHAR(255),
		Col6 NVARCHAR(255),
		Col7 NVARCHAR(255),
    );
END;
-- tao bang string temp weekly dmx
IF OBJECT_ID('String_Temp_Weekly_DMX', 'U') IS NULL
BEGIN
	--drop table String_Temp_Weekly_DMX;
    CREATE TABLE String_Temp_Weekly_DMX (
        Col1 NVARCHAR(255),
        Col2 NVARCHAR(255),
        Col3 NVARCHAR(255),
        Col4 NVARCHAR(255),
        Col5 NVARCHAR(255),
		Col6 NVARCHAR(255),
		Col7 NVARCHAR(255),
    );
END;

-- tao bang weekly cps
IF OBJECT_ID('Weekly_CPS', 'U') IS NULL
BEGIN
	--drop table Weekly_CPS;
    CREATE TABLE Weekly_CPS (
        ProductName NVARCHAR(255),  -- Tên sản phẩm (Chuỗi ký tự, vì có thể có dấu và tiếng Việt)
		OriginalPrice DECIMAL(18, 2),  -- Giá gốc (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		DiscountedPrice DECIMAL(18, 2),  -- Giá giảm (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		ProductLink NVARCHAR(500),  -- Link chi tiết (Độ dài 500 ký tự để chứa URL dài)
		ImageLink NVARCHAR(500),  -- Link ảnh (Độ dài 500 ký tự để chứa URL dài)
		id_dateOnCSV INT,
		dateOnCSV DATE,
		FOREIGN KEY (id_dateOnCSV) REFERENCES date_dim(date_sk)
    );
END;
-- tao bang weekly dmcl
IF OBJECT_ID('Weekly_DMCL', 'U') IS NULL
BEGIN
	--drop table Weekly_DMCL;
    CREATE TABLE Weekly_DMCL (
        Title NVARCHAR(255),  -- Tên sản phẩm
		ImgSrc NVARCHAR(500),  -- Link ảnh sản phẩm
		ProductDetailLink NVARCHAR(500),  -- Link chi tiết sản phẩm
		OriginalPrice DECIMAL(18, 2),  -- Giá gốc
		DiscountPrice DECIMAL(18, 2),  -- Giá giảm
		ScreenSize NVARCHAR(50),  -- Kích thước màn hình
		Resolution NVARCHAR(50),  -- Độ phân giải
		id_dateOnCSV INT,
		dateOnCSV DATE,
		FOREIGN KEY (id_dateOnCSV) REFERENCES date_dim(date_sk)
    );
END;
-- tao bang weekly dmx
IF OBJECT_ID('Weekly_DMX', 'U') IS NULL
BEGIN
	--drop table Weekly_DMX;
    CREATE TABLE Weekly_DMX (
        Name NVARCHAR(255),  -- Tên sản phẩm
		ImageLink NVARCHAR(500),  -- Link ảnh sản phẩm
		ScreenSize NVARCHAR(50),  -- Kích thước màn hình
		Resolution NVARCHAR(50),  -- Độ phân giải
		OriginalPrice DECIMAL(18, 2),  -- Giá gốc
		DiscountPrice DECIMAL(18, 2),  -- Giá giảm
		ProductDetailLink NVARCHAR(500),  -- Link chi tiết sản phẩm
		id_dateOnCSV INT,
		dateOnCSV DATE,
		FOREIGN KEY (id_dateOnCSV) REFERENCES date_dim(date_sk)
    );
END;

IF OBJECT_ID('Delete_Data_String_Temp_Weekly_Table', 'P') IS NOT NULL
    DROP PROCEDURE Delete_Data_String_Temp_Weekly_Table;
GO
    CREATE PROCEDURE Delete_Data_String_Temp_Weekly_Table
		@Destination_Table_String_Staging NVARCHAR(50) -- tên bảng tạm chuỗi
    AS
    BEGIN
		DECLARE @SQL NVARCHAR(MAX);
		SET @SQL = 'DELETE FROM ' + @Destination_Table_String_Staging;
		EXEC sp_executesql @SQL;
	END
GO

IF OBJECT_ID('Load_From_File_CSV_To_String_Temp_Weekly_Table', 'P') IS NOT NULL
    DROP PROCEDURE Load_From_File_CSV_To_String_Temp_Weekly_Table;
GO
    CREATE PROCEDURE Load_From_File_CSV_To_String_Temp_Weekly_Table
        @Path_File_CSV NVARCHAR(255), -- path file.csv thay cho @Source_File_Location
		@Destination_Table_String_Staging NVARCHAR(50) -- tên bảng tạm chuỗi
    AS
    BEGIN
		DECLARE @SQL NVARCHAR(MAX);
		-- Tạo câu lệnh BULK INSERT động
		SET @SQL = N'
		BULK INSERT '+@Destination_Table_String_Staging+'
		FROM ''' + @Path_File_CSV + '''
		WITH (
		    FIELDTERMINATOR = '','',  -- Phân tách bằng dấu phẩy
			ROWTERMINATOR = ''0x0a'',   -- Kết thúc mỗi dòng bằng xuống dòng
			FIRSTROW = 2,              -- Bắt đầu từ dòng 2 do các file csv data đều có dòng tiêu để
			CODEPAGE = ''65001'' -- UTF-8
		);';
		-- Thực thi câu lệnh BULK INSERT
		EXEC sp_executesql @SQL;
	END
GO

IF OBJECT_ID('Delete_Data_Weekly_Table', 'P') IS NOT NULL
    DROP PROCEDURE Delete_Data_Weekly_Table;
GO
    CREATE PROCEDURE Delete_Data_Weekly_Table
		@Destination_Table_Staging NVARCHAR(50) -- tên bảng weekly
    AS
    BEGIN
		DECLARE @SQL NVARCHAR(MAX);
		SET @SQL = 'DELETE FROM ' + @Destination_Table_Staging;
		EXEC sp_executesql @SQL;
	END
GO

IF OBJECT_ID('Load_From_String_Temp_Weekly_CPS_Table_To_Weekly_CPS_Table', 'P') IS NOT NULL
    DROP PROCEDURE Load_From_String_Temp_Weekly_CPS_Table_To_Weekly_CPS_Table;
GO
    CREATE PROCEDURE Load_From_String_Temp_Weekly_CPS_Table_To_Weekly_CPS_Table
		
    AS
    BEGIN
		-- convert từ bảng string temp sang bảng weekly đã dc định dạng đúng cột
		INSERT INTO Weekly_CPS (ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink, id_dateOnCSV)
		SELECT 
			Col1,  -- Tên sản phẩm
			TRY_CAST(Col2 AS DECIMAL(18, 2)),  -- Giá gốc | TRY_CAST trả về null nếu có lỗi
			TRY_CAST(Col3 AS DECIMAL(18, 2)),  -- Giá giảm
			Col4,  -- Link chi tiết
			Col5,   -- Link ảnh
			NULL
		FROM String_Temp_Weekly_CPS;
	END
GO

IF OBJECT_ID('Load_From_String_Temp_Weekly_DMCL_Table_To_Weekly_DMCL_Table', 'P') IS NOT NULL
    DROP PROCEDURE Load_From_String_Temp_Weekly_DMCL_Table_To_Weekly_DMCL_Table;
GO
    CREATE PROCEDURE Load_From_String_Temp_Weekly_DMCL_Table_To_Weekly_DMCL_Table
		
    AS
    BEGIN
		-- convert từ bảng string temp sang bảng weekly đã dc định dạng đúng cột
		INSERT INTO Weekly_DMCL (Title, ImgSrc, ProductDetailLink, OriginalPrice, DiscountPrice, ScreenSize, Resolution, id_dateOnCSV)
		SELECT 
			Col1,  -- Tên sản phẩm
			Col2,   -- Link ảnh
			Col3,  -- Link chi tiết
			TRY_CAST(Col4 AS DECIMAL(18, 2)),  -- Giá gốc | TRY_CAST trả về null nếu có lỗi
			TRY_CAST(Col5 AS DECIMAL(18, 2)),  -- Giá giảm
			Col6,  -- ScreenSize
			Col7,  -- Resolution
			NULL
		FROM String_Temp_Weekly_DMCL;
	END
GO

IF OBJECT_ID('Load_From_String_Temp_Weekly_DMX_Table_To_Weekly_DMX_Table', 'P') IS NOT NULL
    DROP PROCEDURE Load_From_String_Temp_Weekly_DMX_Table_To_Weekly_DMX_Table;
GO
    CREATE PROCEDURE Load_From_String_Temp_Weekly_DMX_Table_To_Weekly_DMX_Table
		
    AS
    BEGIN
		-- convert từ bảng string temp sang bảng weekly đã dc định dạng đúng cột
		INSERT INTO Weekly_DMX (Name, ImageLink, ScreenSize, Resolution, OriginalPrice, DiscountPrice, ProductDetailLink, id_dateOnCSV)
		SELECT 
			Col1,  -- Tên sản phẩm
			Col2,   -- Link ảnh
			Col3,  -- ScreenSize
			Col4,  -- Resolution
			TRY_CAST(Col5 AS DECIMAL(18, 2)),  -- Giá gốc | TRY_CAST trả về null nếu có lỗi
			TRY_CAST(Col6 AS DECIMAL(18, 2)),  -- Giá giảm
			Col7,  -- Link chi tiết
			NULL
		FROM String_Temp_Weekly_DMX;
	END
GO

IF OBJECT_ID('Set_Id_Date_On_CSV_And_Date_On_CSV_For_Weekly_Table', 'P') IS NOT NULL
    DROP PROCEDURE Set_Id_Date_On_CSV_And_Date_On_CSV_For_Weekly_Table;
GO
    CREATE PROCEDURE Set_Id_Date_On_CSV_And_Date_On_CSV_For_Weekly_Table
		@Destination_Table_Staging NVARCHAR(50), -- tên bảng weekly
		@dateOnCSV DATE
	AS
	BEGIN
		-- Lấy giá trị date_sk từ bảng date_dim
		DECLARE @id_dateOnCSV INT;
		SELECT @id_dateOnCSV = date_sk FROM date_dim WHERE full_date = @dateOnCSV;
		-- update cột id_dateOnCSV
		DECLARE @SQL NVARCHAR(MAX);
		SET @SQL = 
			'UPDATE ' + @Destination_Table_Staging + 
			' SET id_dateOnCSV = ' + CAST(@id_dateOnCSV AS NVARCHAR(10)) + 
			', dateOnCSV = ''' + CONVERT(NVARCHAR(10), @dateOnCSV, 120) + '''';
		-- Thực thi câu lệnh BULK UPDATE
		EXEC sp_executesql @SQL;
	END
GO

IF OBJECT_ID('Get_Rows_In_String_Temp_Table', 'P') IS NOT NULL
    DROP PROCEDURE Get_Rows_In_String_Temp_Table;
GO
    CREATE PROCEDURE Get_Rows_In_String_Temp_Table
		@Destination_Table_String_Staging NVARCHAR(50) -- tên bảng tạm
	AS
	BEGIN
		-- Khai báo biến SQL động
		DECLARE @SQL NVARCHAR(MAX);
		-- Tạo lệnh SQL để đếm số dòng
		SET @SQL = N'SELECT COUNT(*) AS row_count FROM ' + QUOTENAME(@Destination_Table_String_Staging);
		-- Thực thi câu lệnh và trả về số lượng dòng
		EXEC sp_executesql @SQL;
	END
GO

--delete from String_Temp_Weekly_CPS;
--select * from String_Temp_Weekly_CPS;
--select * from Weekly_CPS;

--select * from String_Temp_Weekly_DMCL;
--select * from Weekly_DMCL;

--select * from String_Temp_Weekly_DMX;
--select * from Weekly_DMX;

--EXEC Get_Rows_In_String_Temp_Table 'String_Temp_Weekly_CPS';