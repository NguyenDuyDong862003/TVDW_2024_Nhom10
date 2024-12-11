-- run script này sẽ tạo ra: 
-- +Database DataMart
-- +Table aggregate_tvdata
-- +Table tvdata
-- +Procedure copyBeforeCurrentDate
-- +Procedure getTVData
-- +Procedure renameTable

---- Nếu run sai có thể drop database đi làm lại
--use master;
--drop database DataMart;
--create database DataMart;
--go
--use TVDW_DataMart;

---------------------------------------------------------------------------------------

-- tao DB DataMart
IF DB_ID('DataMart') IS NULL
BEGIN
    CREATE DATABASE DataMart;
END
ELSE
BEGIN
    PRINT 'Database "DataMart" already exists.';
END
GO

use DataMart;
GO

-- Table structure for aggregate_tvdata
IF OBJECT_ID('aggregate_tvdata', 'U') IS NULL
BEGIN
	--drop table aggregate_tvdata;
    CREATE TABLE aggregate_tvdata (
		SP_Key INT IDENTITY(1,1) PRIMARY KEY,
		NameSource NVARCHAR(255),
        ProductName NVARCHAR(255),  -- Tên sản phẩm (Chuỗi ký tự, vì có thể có dấu và tiếng Việt)
		OriginalPrice DECIMAL(18, 2),  -- Giá gốc (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		DiscountedPrice DECIMAL(18, 2),  -- Giá giảm (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		ProductLink NVARCHAR(500),  -- Link chi tiết (Độ dài 500 ký tự để chứa URL dài)
		ImageLink NVARCHAR(500),  -- Link ảnh (Độ dài 500 ký tự để chứa URL dài)
		dateOnCSV DATE,
		date_expired DATE
    );
END;
GO

-- Table structure for tvdata
IF OBJECT_ID('tvdata', 'U') IS NULL
BEGIN
	--drop table tvdata;
    CREATE TABLE tvdata (
		SP_Key INT IDENTITY(1,1) PRIMARY KEY,
		NameSource NVARCHAR(255),
        ProductName NVARCHAR(255),  -- Tên sản phẩm (Chuỗi ký tự, vì có thể có dấu và tiếng Việt)
		OriginalPrice DECIMAL(18, 2),  -- Giá gốc (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		DiscountedPrice DECIMAL(18, 2),  -- Giá giảm (Sử dụng kiểu DECIMAL để lưu giá tiền, với 2 chữ số thập phân)
		ProductLink NVARCHAR(500),  -- Link chi tiết (Độ dài 500 ký tự để chứa URL dài)
		ImageLink NVARCHAR(500),  -- Link ảnh (Độ dài 500 ký tự để chứa URL dài)
		dateOnCSV DATE,
		date_expired DATE
    );
END;
GO

-- Procedure for copyBeforeCurrentDate
IF OBJECT_ID('copyBeforeCurrentDate', 'P') IS NOT NULL
    DROP PROCEDURE copyBeforeCurrentDate;
GO

CREATE PROCEDURE copyBeforeCurrentDate
AS
BEGIN
    INSERT INTO aggregate_tvdata (
        NameSource, ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink, 
        dateOnCSV, date_expired
    )
    SELECT 
        NameSource, ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink, 
        dateOnCSV, date_expired
    FROM tvdata
    WHERE dateOnCSV <= DATEADD(DAY, -1, CAST(GETDATE() AS DATE));
END;
GO

-- Procedure for getTVData
IF OBJECT_ID('getTVData', 'P') IS NOT NULL
    DROP PROCEDURE getTVData;
GO

CREATE PROCEDURE getTVData
    @date DATE
AS
BEGIN
    SELECT 
        dateOnCSV, ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink
    FROM tvdata
    WHERE CAST(dateOnCSV AS DATE) = @date;
END;
GO

-- Procedure for renameTable
IF OBJECT_ID('renameTable', 'P') IS NOT NULL
    DROP PROCEDURE renameTable;
GO

CREATE PROCEDURE renameTable
AS
BEGIN
    EXEC sp_rename 'aggregate_tvdata', 'temp_table';
    EXEC sp_rename 'tvdata', 'aggregate_tvdata';
    EXEC sp_rename 'temp_table', 'tvdata';
END;
GO