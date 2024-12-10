package main;

import java.io.IOException;
import java.sql.SQLException;

import model.CheckAndInsertFileLogIfNotExist;

public class MainCheckAndInsertFileLogIfNotExist {
	public static void main(String[] args) {
		// 1.1. Lấy tên file cấu hình SQL
		String pathConfigSQL = null;
		if (args.length > 0)
			pathConfigSQL = args[0];
		else {
			pathConfigSQL = "SQL account.txt";
			System.out.println("Lấy tên file cấu hình mặc định: " + pathConfigSQL);
		}

		CheckAndInsertFileLogIfNotExist checkAndInsertFileLogIfNotExist = null;
		try {
			// 1.2. Kết nối Database TVDW_Control
			checkAndInsertFileLogIfNotExist = new CheckAndInsertFileLogIfNotExist(pathConfigSQL);
			// 1.3. Lấy path thư mục chứa file csv từ bảng config_file
			String path = checkAndInsertFileLogIfNotExist.getControlDAO().getPathFolderFromIdConfig(1);
			System.out.println(path);
			try {
				checkAndInsertFileLogIfNotExist.duyetFileCSVTrongThuMucWeekly(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		// 1.4. Đóng kết nối Database TVDW_Control
		try {
			if (checkAndInsertFileLogIfNotExist != null)
				checkAndInsertFileLogIfNotExist.getControlDAO().getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}