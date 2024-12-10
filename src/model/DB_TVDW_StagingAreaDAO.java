package model;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB_TVDW_StagingAreaDAO {
	private Connection connection;

	public DB_TVDW_StagingAreaDAO(String serverName, int port, String username, String password) throws SQLException {
		String url = String.format(
				"jdbc:sqlserver://%s:%d;databaseName=TVDW_StagingArea;encrypt=true;trustServerCertificate=true;",
				serverName, port);

		// Khởi tạo kết nối cho DB_Staging
//		String url = "jdbc:sqlserver://LAPTOP-F6E5464R\\SQLEXPRESS:1433;databaseName=TVDW_StagingArea;encrypt=true;trustServerCertificate=true;";
//		String username = "sa";
//		String password = "123456789";

		connection = DriverManager.getConnection(url, username, password);
		System.out.println("Kết nối thành công DB TVDW_StagingArea");
	}

	public Connection getConnection() {
		return connection;
	}

	public void deleteDataOfStringTempWeeklyTable(String destination_Table_Temp_Staging) {
		try {
			// Gọi thủ tục SQL
			String sql = "{CALL Delete_Data_String_Temp_Weekly_Table(?)}";
			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thiết lập tham số cho thủ tục
				callableStatement.setString(1, destination_Table_Temp_Staging); // @Destination_Table_Temp_Staging

				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Xóa data từ bảng " + destination_Table_Temp_Staging + " thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadFromFileCSVToStringTempWeeklyTable(String pathFileCsv, String destination_Table_Temp_Staging) {
		try {
			// Gọi thủ tục SQL
			String sql = "{CALL Load_From_File_CSV_To_String_Temp_Weekly_Table(?, ?)}";
			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thiết lập tham số cho thủ tục
				callableStatement.setString(1, pathFileCsv); // @Path_File_CSV
				callableStatement.setString(2, destination_Table_Temp_Staging); // @Destination_Table_Temp_Staging

				// Thực thi thủ tục
				callableStatement.execute();
				File file = new File(pathFileCsv);
				System.out.println("Gọi thủ tục load từ " + file.getName() + " tới bảng "
						+ destination_Table_Temp_Staging + " thành công!");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteDataOfWeeklyTable(String destination_Table_Staging) {
		try {
			// Gọi thủ tục SQL
			String sql = "{CALL Delete_Data_Weekly_Table(?)}";
			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thiết lập tham số cho thủ tục
				callableStatement.setString(1, destination_Table_Staging); // @Destination_Table_Staging

				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Xóa data từ bảng " + destination_Table_Staging + " thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadFromStringTempWeeklyCPSTableToWeeklyCPSTable() {
		try {
			// Câu lệnh gọi thủ tục không tham số
			String sql = "{CALL Load_From_String_Temp_Weekly_CPS_Table_To_Weekly_CPS_Table}";

			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Gọi thủ tục load từ bảng String temp CPS tới bảng WeeklyCPS thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void LoadFromStringTempWeeklyDMCLTableToWeeklyDMCLTable() {
		try {
			// Câu lệnh gọi thủ tục không tham số
			String sql = "{CALL Load_From_String_Temp_Weekly_DMCL_Table_To_Weekly_DMCL_Table}";

			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Gọi thủ tục load từ bảng String temp DMCL tới bảng WeeklyDMCL thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadFromStringTempWeeklyDMXTableToWeeklyDMXTable() {
		try {
			// Câu lệnh gọi thủ tục không tham số
			String sql = "{CALL Load_From_String_Temp_Weekly_DMX_Table_To_Weekly_DMX_Table}";

			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Gọi thủ tục load từ bảng String temp DMX tới bảng WeeklyDMX thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setIdDateOnCSVAndDateOnCSVForWeeklyTable(String destinationTableStaging, Date dateOnCSV) {
		try {
			// Câu lệnh gọi thủ tục có tham số
			String sql = "{CALL Set_Id_Date_On_CSV_And_Date_On_CSV_For_Weekly_Table(?, ?)}";

			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thiết lập tham số cho thủ tục
				callableStatement.setString(1, destinationTableStaging); // @Destination_Table_Staging
				callableStatement.setDate(2, dateOnCSV); // @dateOnCSV

				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Gọi thủ tục Set_Id_Date_On_CSV_And_Date_On_CSV_For_Weekly_Table thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getRowsOfStringTempTable(String nameStringTempTable) {
		try {
			String sql = "{CALL Get_Rows_In_String_Temp_Table(?)}";
			try (CallableStatement stmt = connection.prepareCall(sql)) {
				stmt.setString(1, nameStringTempTable);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("row_count");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}