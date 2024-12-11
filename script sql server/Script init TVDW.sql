-- Thay đúng đường dẫn của file csv date dim, đường dẫn của thư mục chứa file csv hàng tuần trước khi run nha
DECLARE @Path_File_Date_Dim_CSV NVARCHAR(255) = 'D:\TVDW_2024_Nhom10\Date_Dim\date_dim_without_quarter.csv';
GO
DECLARE @Folder_Contain_Weekly_CSV NVARCHAR(255) = 'D:\TVDW_2024_Nhom10\Weekly csv';
GO
-- run script này sẽ tạo ra: 
-- +Database TVDW
-- +Table config_file (đã insert sẵn 3 dòng tương ứng với 3 web)
-- +Table date_dim (đã insert sẵn toàn bộ data ở file csv date dim của thầy)
-- +Table Tivi
-- +Procedure InsertTivi

---- Nếu run sai có thể drop database đi làm lại
--use master;
--drop database TVDW;
--create database TVDW;
--go
--use TVDW;

---------------------------------------------------------------------------------------

-- tao DB TVDW
IF DB_ID('TVDW') IS NULL
BEGIN
    CREATE DATABASE TVDW;
END
ELSE
BEGIN
    PRINT 'Database "TVDW" already exists.';
END
GO
use TVDW;

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
---- từ 1 ngày có thể truy ra được date_sk
--SELECT date_sk FROM date_dim WHERE full_date = '2024-11-08';

-- tao bang config_file
IF OBJECT_ID('config_file', 'U') IS NULL
BEGIN
	--drop table config_file;
    CREATE TABLE config_file (
		ID INT PRIMARY KEY,
		Name NVARCHAR(50), -- tên web
		Source NVARCHAR(255), -- link web
		Source_File_Location NVARCHAR(255), -- thư mục lưu các file csv
		Destination_Table_String_Staging NVARCHAR(50), -- tên bảng tạm chuỗi
		Destination_Table_Staging NVARCHAR(50), -- tên bảng tạm
		Destination_Table_DW NVARCHAR(50) -- tên bảng chung cuối cùng trong DB DW
	);
	INSERT INTO config_file (ID, Name, Source, Source_File_Location, Destination_Table_String_Staging, Destination_Table_Staging, Destination_Table_DW)
	VALUES (1, 'CPS', 'https://cellphones.com.vn/tivi.html', @Folder_Contain_Weekly_CSV, 'String_Temp_Weekly_CPS', 'Weekly_CPS', 'Tivi');
	INSERT INTO config_file (ID, Name, Source, Source_File_Location, Destination_Table_String_Staging, Destination_Table_Staging, Destination_Table_DW)
	VALUES (2, 'DMCL', 'https://dienmaycholon.com/tivi', @Folder_Contain_Weekly_CSV, 'String_Temp_Weekly_DMCL', 'Weekly_DMCL', 'Tivi');
	INSERT INTO config_file (ID, Name, Source, Source_File_Location, Destination_Table_String_Staging, Destination_Table_Staging, Destination_Table_DW)
	VALUES (3, 'DMX', 'https://www.dienmayxanh.com/tivi', @Folder_Contain_Weekly_CSV, 'String_Temp_Weekly_DMX', 'Weekly_DMX', 'Tivi');
END;
--select * from config_file;

-- Câu hỏi: liệu có cần tạo 1 bảng config_file ở trong DB TVDW ko?
-- Hay chỉ cần tham chiếu đến bảng config_file ở trong DB TVDW_Control? --> SQL Server ko hỗ trợ tham chiếu xuyên DB
IF OBJECT_ID('Tivi', 'U') IS NULL
BEGIN
	--drop table Tivi;
    CREATE TABLE Tivi (
		SP_Key INT IDENTITY(1,1) PRIMARY KEY,
		ID_config INT,
		FOREIGN KEY (ID_config) REFERENCES config_file(ID),
        ProductName NVARCHAR(255),  -- Tên sản phẩm (Chuỗi ký tự, vì có thể có dấu và tiếng Việt)
		OriginalPrice DECIMAL(18, 2),  -- Giá gốc (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		DiscountedPrice DECIMAL(18, 2),  -- Giá giảm (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		ProductLink NVARCHAR(500),  -- Link chi tiết (Độ dài 500 ký tự để chứa URL dài)
		ImageLink NVARCHAR(500),  -- Link ảnh (Độ dài 500 ký tự để chứa URL dài)
		id_dateOnCSV INT,
		FOREIGN KEY (id_dateOnCSV) REFERENCES date_dim(date_sk),
		dateOnCSV DATE,
		date_expired DATE
    );
END;
--select * from Tivi;

-- viết hàm proc nhận vào ngày, dựa vào ngày sẽ lọc trong thư mục chứa file csv, và kiểm tra xem tên file đã có trong bảng file_log chưa
-- nếu có rồi thì ko làm gì
-- nếu chưa có thì insert dòng mới vô bảng file_log với thông tin của file csv này

-- nhưng trong SQL Server, không có cách trực tiếp để duyệt qua các thư mục trong hệ thống file từ bên trong SQL Server bằng câu lệnh SQL thuần túy.
-- vậy thì proc này sẽ nhận thêm vào danh sách file csv trong thư mục này để check

-- nếu trong DW chưa có dòng trong STAGING, thì insert vô
-- nếu có rồi thì, nếu khác 1 trong các trường ko phải khóa chính thì thêm vô và cập nhật expried => current date
-- 9999-12-31 là giá hiện tại

IF OBJECT_ID('InsertTivi', 'P') IS NOT NULL
    DROP PROCEDURE InsertTivi;
GO
    CREATE PROCEDURE InsertTivi
		@ID_config INT,
        @ProductName NVARCHAR(255),  -- Tên sản phẩm (Chuỗi ký tự, vì có thể có dấu và tiếng Việt)
		@OriginalPrice DECIMAL(18, 2),  -- Giá gốc (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		@DiscountedPrice DECIMAL(18, 2),  -- Giá giảm (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		@ProductLink NVARCHAR(500),  -- Link chi tiết (Độ dài 500 ký tự để chứa URL dài)
		@ImageLink NVARCHAR(500),  -- Link ảnh (Độ dài 500 ký tự để chứa URL dài)
		@id_dateOnCSV INT,
		@dateOnCSV DATE
    AS
    BEGIN
		DECLARE @IsActiveInsert BIT = 0; -- False

		-- check xem sản phẩm có nằm trong 1 web chưa tồn tại?
		IF NOT EXISTS (
			SELECT 1
			FROM Tivi
			WHERE ID_config = @ID_config
		)
		BEGIN
			-- web chưa tồn tại
			SET @IsActiveInsert = 1;
		END
		ELSE
		BEGIN
			-- web đã tồn tại, check xem đây có phải là sản phẩm mới trong web ko?
			IF NOT EXISTS (
				SELECT 1
				FROM Tivi
				WHERE ID_config = @ID_config
				AND ProductName = @ProductName
			)
			BEGIN
				-- là sản phẩm mới trong web 
				SET @IsActiveInsert = 1;
			END
			ELSE
			BEGIN
				-- đây là loại sản phẩm cũ trong web, check xem giá sản phẩm này gần đây nhất có khác với sản phẩm chuẩn bị chèn vào ko?
				IF EXISTS (
					SELECT 1
					FROM Tivi
					WHERE ID_config = @ID_config
					AND ProductName = @ProductName
					AND date_expired = '9999-12-31'
					AND (OriginalPrice != @OriginalPrice OR DiscountedPrice != @DiscountedPrice)
				)
				BEGIN
					-- có khác giá, so sánh ngày của sản phẩm gần nhất với sản phẩm chuẩn bị thêm vào
					IF EXISTS (
						SELECT 1
						FROM Tivi
						WHERE ID_config = @ID_config
						AND ProductName = @ProductName
						AND date_expired = '9999-12-31'
						AND (OriginalPrice != @OriginalPrice OR DiscountedPrice != @DiscountedPrice)
						AND id_dateOnCSV < @id_dateOnCSV
					)
					BEGIN
						-- sản phẩm chuẩn bị thêm vào mới hơn sản phẩm gần nhất

						-- Cập nhật giá trị của date_expired dựa trên full_date từ bảng date_dim
						DECLARE @new_date_expired DATE;
						-- Lấy giá trị full_date từ bảng date_dim
						SELECT @new_date_expired = full_date 
						FROM date_dim 
						WHERE date_sk = @id_dateOnCSV;

						UPDATE Tivi
						SET date_expired = @new_date_expired
						WHERE ID_config = @ID_config
						AND ProductName = @ProductName
						AND date_expired = '9999-12-31'
						-- AND (OriginalPrice != @OriginalPrice OR DiscountedPrice != @DiscountedPrice) -- ko cần cái này vì ở IF đã check rồi
						-- AND id_dateOnCSV < @id_dateOnCSV -- ko cần cái này vì ở IF đã check rồi

						SET @IsActiveInsert = 1;
					END
					ELSE
					BEGIN
						-- sản phẩm chuẩn bị thêm vào cũ hơn sản phẩm gần nhất
						print('sản phẩm này cũ rồi');
						-- Ví dụ sản phẩm mới nhất vào ngày 16/11/2024, mà tự dưng insert vào DW 1 sản phẩm của ngày 09/11/2024
						-- thì nó kỳ lắm: vì nếu update thì đây lại là update giá cũ rồi, nên ko cho update
					END
				END
				ELSE
				BEGIN
					-- ko khác giá
					-- ==> chèn cùng 1 dòng giống nhau thì cũng sẽ rơi vào đây nên ko sao
					print('ko khác giá');
				END
			END
		END
		
		IF (@IsActiveInsert = 1)
		BEGIN
			INSERT INTO Tivi (ID_config,ProductName,OriginalPrice,DiscountedPrice,ProductLink,ImageLink,id_dateOnCSV,dateOnCSV,date_expired)
			VALUES (
				@ID_config,
				@ProductName,
				@OriginalPrice,
				@DiscountedPrice,
				@ProductLink,
				@ImageLink,
				@id_dateOnCSV,
				@dateOnCSV,
				'9999-12-31'
			);
		END
	END
GO

-- DBCC CHECKIDENT ('Tivi', RESEED, 0); -- đặt giá trị tự động tăng về 0
-- delete from Tivi;
-- select * from Tivi;

select ID_config,SP_Key,ProductName,OriginalPrice,DiscountedPrice,dateOnCSV,date_expired
from Tivi
order by ProductName, SP_Key;