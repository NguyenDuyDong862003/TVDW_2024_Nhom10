package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DB_TVDW_ControlDAO {
	private Connection connection;

	public DB_TVDW_ControlDAO(String serverName, int port, String username, String password) throws SQLException {
		// Tạo URL kết nối
		String url = String.format(
				"jdbc:sqlserver://%s:%d;databaseName=TVDW_Control;encrypt=true;trustServerCertificate=true;",
				serverName, port);

		// Khởi tạo kết nối cho DB_Control
//		String url = "jdbc:sqlserver://LAPTOP-F6E5464R\\SQLEXPRESS:1433;databaseName=TVDW_Control;encrypt=true;trustServerCertificate=true;";
//		String username = "sa";
//		String password = "123456789";

		connection = DriverManager.getConnection(url, username, password);
		System.out.println("Kết nối thành công DB TVDW_Control");
	}

	public Connection getConnection() {
		return connection;
	}

	public void insertFileLogIfNotExist(int idConfig, String filename, int count, int fileSizeKB, Date time,
			Date dtUpdate) {
		CallableStatement stmt = null;

		try {
			// Gọi thủ tục InsertFileLog
			String sql = "{CALL InsertFileLogIfNotExist(?, ?, ?, ?, ?, ?)}";
			stmt = connection.prepareCall(sql);

			// Thiết lập các tham số cho thủ tục
			stmt.setInt(1, idConfig);
			stmt.setString(2, filename);
			stmt.setInt(3, fileSizeKB);
			stmt.setInt(4, count);
			// Nếu time null, sử dụng ngày hiện tại
			stmt.setDate(5, time != null ? time : new java.sql.Date(System.currentTimeMillis()));
			// Nếu dtUpdate null, để null
			stmt.setTimestamp(6, dtUpdate != null ? new Timestamp(dtUpdate.getTime()) : null);

			// Thực thi thủ tục
			stmt.executeUpdate();

//			System.out.println("File " + filename
//					+ " đã được chèn vào bảng file_log thành công qua thủ tục InsertFileLogIfNotExist.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Lỗi khi gọi thủ tục InsertFileLogIfNotExist: " + e.getMessage());
		} finally {
			// Đảm bảo đóng statement và connection sau khi sử dụng
			try {
				if (stmt != null)
					stmt.close();
//				if (connection != null)
//					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getPathFolderFromIdConfig(int idConfig) {
		// Chuỗi SQL truy vấn
		String sql = "SELECT Source_File_Location FROM config_file WHERE ID = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Gán tham số cho câu truy vấn
			statement.setInt(1, idConfig);
			// Thực thi truy vấn
			ResultSet resultSet = statement.executeQuery();
			// Kiểm tra và trả về giá trị nếu tìm thấy
			if (resultSet.next()) {
				return resultSet.getString("Source_File_Location");
			} else {
				System.out.println("Không tìm thấy bản ghi với ID = " + idConfig);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getNameTableStringStagingFromIdConfig(int idConfig) {
		// Chuỗi SQL truy vấn
		String sql = "SELECT Destination_Table_Temp_Staging FROM config_file WHERE ID = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Gán tham số cho câu truy vấn
			statement.setInt(1, idConfig);
			// Thực thi truy vấn
			ResultSet resultSet = statement.executeQuery();
			// Kiểm tra và trả về giá trị nếu tìm thấy
			if (resultSet.next()) {
				return resultSet.getString("Destination_Table_Temp_Staging");
			} else {
				System.out.println("Không tìm thấy bản ghi với ID = " + idConfig);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getNameTableWeeklyFromIdConfig(int idConfig) {
		// Câu truy vấn SQL
		String sql = "SELECT Destination_Table_Staging FROM config_file WHERE ID = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Gán tham số cho câu truy vấn
			statement.setInt(1, idConfig);
			// Thực thi truy vấn
			ResultSet resultSet = statement.executeQuery();
			// Kiểm tra và trả về giá trị nếu tìm thấy
			if (resultSet.next()) {
				return resultSet.getString("Destination_Table_Staging");
			} else {
				System.out.println("Không tìm thấy bản ghi với ID = " + idConfig);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getIdConfigFileFromNameWeb(String nameWeb) { // ví dụ: nameWeb="cps"
		// in hoa
		nameWeb = nameWeb.toUpperCase();
		// Câu truy vấn SQL
		String sql = "SELECT ID FROM config_file WHERE Name = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Gán tham số cho câu truy vấn
			statement.setString(1, nameWeb);
			// Thực thi truy vấn
			ResultSet resultSet = statement.executeQuery();
			// Kiểm tra và trả về giá trị nếu tìm thấy
			if (resultSet.next()) {
				return resultSet.getInt("ID");
			} else {
				System.out.println("Không tìm thấy bản ghi với Name = " + nameWeb);
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void updateCountRows(String filename, int rows) {
		try {
			String sql = "{CALL Update_count_rows(?, ?)}";
			try (CallableStatement stmt = connection.prepareCall(sql)) {
				// Truyền giá trị cho các tham số của thủ tục
				stmt.setString(1, filename);
				stmt.setInt(2, rows);
				// Thực thi thủ tục
				stmt.execute();
				System.out.println("✅ Cập nhật thành công count_rows cho file " + filename);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateStatus(String filename, String status) {
		try {
			String sql = "{CALL Update_status(?, ?)}";
			try (CallableStatement stmt = connection.prepareCall(sql)) {
				// Truyền giá trị cho các tham số của thủ tục
				stmt.setString(1, filename);
				stmt.setString(2, status);
				// Thực thi thủ tục
				stmt.execute();
				System.out.println("✅ Cập nhật thành công status " + status + " cho file " + filename);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateDtUpdate(String filename, java.util.Date dtUpdate) {
		try {
			String sql = "{CALL Update_dt_update(?, ?)}";
			try (CallableStatement stmt = connection.prepareCall(sql)) {
				// Truyền giá trị cho các tham số của thủ tục
				stmt.setString(1, filename);
				if (dtUpdate != null) {
					// Chuyển đổi từ java.util.Date sang java.sql.Timestamp
					stmt.setTimestamp(2, new java.sql.Timestamp(dtUpdate.getTime()));
				} else {
					stmt.setNull(2, java.sql.Types.TIMESTAMP); // Truyền NULL nếu không có dt_update
				}
				// Thực thi thủ tục
				stmt.execute();
				System.out.println("✅ Cập nhật thành công dt_update cho file " + filename);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi khi gọi thủ tục Update_dt_update: " + e.getMessage());
			e.printStackTrace();
		}
	}
}