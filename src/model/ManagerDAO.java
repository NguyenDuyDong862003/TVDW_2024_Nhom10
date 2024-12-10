package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;
import java.util.StringTokenizer;

public class ManagerDAO {
	DB_TVDW_ControlDAO controlDAO;
	DB_TVDW_StagingAreaDAO stagingAreaDAO;
	DB_TVDWDAO tvdwDAO;

	public ManagerDAO(String configFilePath) throws SQLException, FileNotFoundException, IOException {
		// Đường dẫn tới file cấu hình
		if (configFilePath == null)
			configFilePath = "SQL account.txt";

		try (FileInputStream fis = new FileInputStream(configFilePath)) {
			// Tạo đối tượng Properties để đọc file
			Properties props = new Properties();
			props.load(fis);
			// Lấy thông tin từ file
			String serverName = props.getProperty("serverName");
			int port = Integer.parseInt(props.getProperty("port"));
			String username = props.getProperty("username");
			String password = props.getProperty("password");

			// 2.2. Kết nối 3 Database: TVDW_Control, TVDW_StagingArea và TVDW
			controlDAO = new DB_TVDW_ControlDAO(serverName, port, username, password);
			stagingAreaDAO = new DB_TVDW_StagingAreaDAO(serverName, port, username, password);
			tvdwDAO = new DB_TVDWDAO(serverName, port, username, password);
		}
	}

	public DB_TVDW_ControlDAO getControlDAO() {
		return controlDAO;
	}

	public DB_TVDW_StagingAreaDAO getStagingAreaDAO() {
		return stagingAreaDAO;
	}

	public DB_TVDWDAO getTvdwDAO() {
		return tvdwDAO;
	}

	public void insertTiviFromWeeklyCPSTable() {
		String selectQuery = "SELECT ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink, id_dateOnCSV, dateOnCSV FROM Weekly_CPS";

		Connection connection = stagingAreaDAO.getConnection();

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectQuery)) {
			while (resultSet.next()) {
				String productName = resultSet.getString("ProductName");
				BigDecimal originalPrice = resultSet.getBigDecimal("OriginalPrice");
				BigDecimal discountedPrice = resultSet.getBigDecimal("DiscountedPrice");
				String productLink = resultSet.getString("ProductLink");
				String imageLink = resultSet.getString("ImageLink");
				int idDateOnCSV = resultSet.getInt("id_dateOnCSV");
				Date dateOnCSV = resultSet.getDate("dateOnCSV");

				// Gọi lại hàm insertTivi
				tvdwDAO.insertTivi(1, productName, originalPrice, discountedPrice, productLink, imageLink, idDateOnCSV,
						dateOnCSV);
			}
			System.out.println("Tất cả sản phẩm từ Weekly_CPS đã được chèn thành công!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTiviFromWeeklyDMCLTable() {
		String selectQuery = "SELECT Title, OriginalPrice, DiscountPrice, ProductDetailLink, ImgSrc, id_dateOnCSV, dateOnCSV FROM Weekly_DMCL";

		Connection connection = stagingAreaDAO.getConnection();

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectQuery)) {
			while (resultSet.next()) {
				String productName = resultSet.getString("Title");
				BigDecimal originalPrice = resultSet.getBigDecimal("OriginalPrice");
				BigDecimal discountedPrice = resultSet.getBigDecimal("DiscountPrice");
				String productLink = resultSet.getString("ProductDetailLink");
				String imageLink = resultSet.getString("ImgSrc");
				int idDateOnCSV = resultSet.getInt("id_dateOnCSV");
				Date dateOnCSV = resultSet.getDate("dateOnCSV");

				// Gọi lại hàm insertTivi
				tvdwDAO.insertTivi(2, productName, originalPrice, discountedPrice, productLink, imageLink, idDateOnCSV,
						dateOnCSV);
			}
			System.out.println("Tất cả sản phẩm từ Weekly_DMCL đã được chèn thành công!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTiviFromWeeklyDMXTable() {
		String selectQuery = "SELECT Name, OriginalPrice, DiscountPrice, ProductDetailLink, ImageLink, id_dateOnCSV, dateOnCSV FROM Weekly_DMX";
		Connection connection = stagingAreaDAO.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectQuery)) {
			while (resultSet.next()) {
				String productName = resultSet.getString("Name");
				BigDecimal originalPrice = resultSet.getBigDecimal("OriginalPrice");
				BigDecimal discountedPrice = resultSet.getBigDecimal("DiscountPrice");
				String productLink = resultSet.getString("ProductDetailLink");
				String imageLink = resultSet.getString("ImageLink");
				int idDateOnCSV = resultSet.getInt("id_dateOnCSV");
				Date dateOnCSV = resultSet.getDate("dateOnCSV");

				// Gọi lại hàm insertTivi
				tvdwDAO.insertTivi(3, productName, originalPrice, discountedPrice, productLink, imageLink, idDateOnCSV,
						dateOnCSV);
			}
			System.out.println("Tất cả sản phẩm từ Weekly_DMX đã được chèn thành công!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getStringDateFromNameFileCSV(String fileName) {
		// fileName = "data_cps_20241109.csv"; // ví dụ

		// Tạo StringTokenizer với dấu phân cách "_" và "."
		StringTokenizer tokenizer = new StringTokenizer(fileName, "_.");
		// Bỏ qua phần "data"
		tokenizer.nextToken();
		// bỏ qua nằm giữa (phần "cps")
		tokenizer.nextToken();
		String date = tokenizer.nextToken(); // ví dụ: "20241109"

		// Lấy các phần từ chuỗi date
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);
		// Kết hợp lại thành định dạng yyyy-MM-dd
		String formattedDate = year + "-" + month + "-" + day;

		return formattedDate;
	}

	public void duyetTableFileLogAndLoadFromCSVToStringTemp() {
		String filename = null;

		Connection connection = controlDAO.getConnection();
		// Tạo CallableStatement để gọi thủ tục
		String procedureCall = "{CALL GetFileLogWithConfigDetails}";
		try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
			// Thực thi thủ tục
			try (ResultSet rs = callableStatement.executeQuery()) {
				// 2.3. Loop: với mỗi dòng trong bảng file_log
				while (rs.next()) {
					String status = rs.getString("status");
					// 2.3.1. Kiểm tra status='ER'
					if (status.equals("ER") == false)
						// 2.3.1.1. Tiếp tục vòng lặp
						continue;
					// 2.3.2. Set status='LST_ST'
					this.controlDAO.updateStatus(filename, "LST_ST"); // Load String Temp - Start

					String sourceFileLocation = rs.getString("Source_File_Location");
					filename = rs.getString("filename");
					String destinationTableStringStaging = rs.getString("Destination_Table_Temp_Staging");
					String pathFileCSV = sourceFileLocation + "\\" + filename;

					// 2.3.3. Xóa tất cả dòng trong bảng String_Temp_Weekly_ tương ứng
					this.stagingAreaDAO.deleteDataOfStringTempWeeklyTable(destinationTableStringStaging);
					// 2.3.4. Load từng dòng từ filename csv vô bảng String_Temp_Weekly_ tương ứng
					this.stagingAreaDAO.loadFromFileCSVToStringTempWeeklyTable(pathFileCSV,
							destinationTableStringStaging);
					// 2.3.5. Cập nhật cột count_rows là số lượng dòng trong bảng
					// String_Temp_Weekly_ tương ứng
					int row = stagingAreaDAO.getRowsOfStringTempTable(destinationTableStringStaging);
					this.controlDAO.updateCountRows(filename, row);
					// 2.3.6. Set status='LST_SU'
					this.controlDAO.updateStatus(filename, "LST_SU"); // Load String Temp - SUCCESS
					// 2.3.7. Cập nhật cột dt_update
					this.controlDAO.updateDtUpdate(filename, null);
					// 2.3.8. Break loop
					break; // chỉ lấy 1 dòng hợp lệ mỗi lần run jar
				}
			}
		} catch (SQLException e) {
			if (filename != null)
				// 2.3.9. Set status='LST_ERR'
				this.controlDAO.updateStatus(filename, "LST_ERR"); // Load String Temp - Error
			e.printStackTrace();
		}
	}

	public void duyetTableFileLogAndLoadFromStringTempToWeekly() {
		String filename = null;

		Connection connection = controlDAO.getConnection();

		// Tạo CallableStatement để gọi thủ tục
		String procedureCall = "{CALL GetFileLogWithConfigDetails}";
		try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
			// Thực thi thủ tục
			try (ResultSet rs = callableStatement.executeQuery()) {
				// 2.4. Loop: với mỗi dòng trong bảng file_log
				while (rs.next()) {
					String status = rs.getString("status");
					// 2.4.1. Kiểm tra status='LST_SU'
					if (status.equals("LST_SU") == false)
						// 2.4.1.1. Tiếp tục vòng lặp
						continue;
					// 2.4.2. Set status='LW_ST'
					this.controlDAO.updateStatus(filename, "LW_ST"); // Load Weekly - Start

					String name = rs.getString("Name");
					filename = rs.getString("filename");
					String destinationTableStaging = rs.getString("Destination_Table_Staging");

					// 2.4.3. Xóa tất cả dòng trong bảng Weekly_ tương ứng
					this.stagingAreaDAO.deleteDataOfWeeklyTable(destinationTableStaging);
					// 2.4.4. Convert từng dòng từ bảng String_Temp_Weekly_ vô bảng Weekly_ tương
					// ứng
					if (name.equals("CPS")) {
						this.stagingAreaDAO.loadFromStringTempWeeklyCPSTableToWeeklyCPSTable();
					} else if (name.equals("DMCL")) {
						this.stagingAreaDAO.LoadFromStringTempWeeklyDMCLTableToWeeklyDMCLTable();
					} else if (name.equals("DMX")) {
						this.stagingAreaDAO.loadFromStringTempWeeklyDMXTableToWeeklyDMXTable();
					} else {
						throw new Exception("Tên file csv ko hợp lệ");
					}
					Date date = Date.valueOf(getStringDateFromNameFileCSV(filename)); // Định dạng yyyy-MM-dd
					// 2.4.5. Cập nhật cột id_dateOnCSV cho bảng Weekly_ theo ngày trên tên file csv
					this.stagingAreaDAO.setIdDateOnCSVAndDateOnCSVForWeeklyTable(destinationTableStaging, date);
					// 2.4.6. Set status='LW_SU'
					this.controlDAO.updateStatus(filename, "LW_SU"); // Load Weekly - SUCCESS
					// 2.4.7. Cập nhật cột dt_update
					this.controlDAO.updateDtUpdate(filename, null);
					// 2.4.8. Break loop
					break; // chỉ lấy 1 dòng hợp lệ mỗi lần run jar
				}
			} catch (Exception e) {
				if (filename != null)
					// 2.4.9. Set status='LW_ERR'
					this.controlDAO.updateStatus(filename, "LW_ERR"); // Load Weekly - Error
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// part 3 là với những dòng trong bảng file_log có status=LW_SU:
	// load từ table weekly tương ứng vào bảng tivi,
	// update status từ LW_SU thành LDW_SU (Load Data Warehouse SUCCESS)
	// update cột time update,
	public void duyetTableFileLogAndLoadFromWeeklyToTivi() {
		String filename = null;

		Connection connection = controlDAO.getConnection();

		// Tạo CallableStatement để gọi thủ tục
		String procedureCall = "{CALL GetFileLogWithConfigDetails}";
		try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
			// Thực thi thủ tục
			try (ResultSet rs = callableStatement.executeQuery()) {
				// 2.5. Loop: với mỗi dòng trong bảng file_log
				while (rs.next()) {
					String status = rs.getString("status");
					// 2.5.1. Kiểm tra status='LW_SU'
					if (status.equals("LW_SU") == false)
						// 2.5.1.1. Tiếp tục vòng lặp
						continue;

					String name = rs.getString("Name");
					filename = rs.getString("filename");
					// 2.5.2. Set status='LDW_ST'
					this.controlDAO.updateStatus(filename, "LDW_ST"); // Load Data Warehouse - Start
					// 2.5.3. Đưa data từ bảng Weekly_ tương ứng vô bảng Tivi
					if (name.equals("CPS")) {
						this.insertTiviFromWeeklyCPSTable();
					} else if (name.equals("DMCL")) {
						this.insertTiviFromWeeklyDMCLTable();
					} else if (name.equals("DMX")) {
						this.insertTiviFromWeeklyDMXTable();
					} else {
						throw new Exception("Tên file csv ko hợp lệ");
					}
					// 2.5.4. Set status='LDW_SU'
					this.controlDAO.updateStatus(filename, "LDW_SU"); // Load Data Warehouse - SUCCESS
					// 2.5.5. Cập nhật cột dt_update
					this.controlDAO.updateDtUpdate(filename, null);
					// 2.5.6. Break loop
					break; // chỉ lấy 1 dòng hợp lệ mỗi lần run jar
				}
			} catch (Exception e) {
				if (filename != null)
					// 2.5.7. Set status='LDW_ERR'
					this.controlDAO.updateStatus(filename, "LDW_ERR"); // Load Data Warehouse - Error
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}