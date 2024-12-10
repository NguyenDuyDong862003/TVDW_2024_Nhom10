package model;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_TVDWDAO {
	private Connection connection;

	public DB_TVDWDAO(String serverName, int port, String username, String password) throws SQLException {
		String url = String.format("jdbc:sqlserver://%s:%d;databaseName=TVDW;encrypt=true;trustServerCertificate=true;",
				serverName, port);

		// Khởi tạo kết nối cho DB_DW
//		String url = "jdbc:sqlserver://LAPTOP-F6E5464R\\SQLEXPRESS:1433;databaseName=TVDW;encrypt=true;trustServerCertificate=true;";
//		String username = "sa";
//		String password = "123456789";

		connection = DriverManager.getConnection(url, username, password);
		System.out.println("Kết nối thành công DB TVDW");
	}

	public Connection getConnection() {
		return connection;
	}

	public void insertTivi(int idConfig, String productName, BigDecimal originalPrice, BigDecimal discountedPrice,
			String productLink, String imageLink, int idDateOnCSV, Date dateOnCSV) {
		try {
			// Câu lệnh gọi thủ tục
			String sql = "{CALL InsertTivi(?, ?, ?, ?, ?, ?, ?, ?)}";

			try (CallableStatement callableStatement = connection.prepareCall(sql)) {
				// Thiết lập tham số cho thủ tục
				callableStatement.setInt(1, idConfig); // @ID_config
				callableStatement.setString(2, productName); // @ProductName
				callableStatement.setBigDecimal(3, originalPrice); // @OriginalPrice
				callableStatement.setBigDecimal(4, discountedPrice); // @DiscountedPrice
				callableStatement.setString(5, productLink); // @ProductLink
				callableStatement.setString(6, imageLink); // @ImageLink
				callableStatement.setInt(7, idDateOnCSV); // @id_dateOnCSV
				callableStatement.setDate(8, dateOnCSV); // @dateOnCSV (date)

				// Thực thi thủ tục
				callableStatement.execute();
				System.out.println("Gọi thủ tục thêm sản phẩm Tivi thành công!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}