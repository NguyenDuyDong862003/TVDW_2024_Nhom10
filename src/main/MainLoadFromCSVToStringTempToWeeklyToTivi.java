package main;

import java.io.IOException;
import java.sql.SQLException;

import model.ManagerDAO;

public class MainLoadFromCSVToStringTempToWeeklyToTivi {
	public static void main(String[] args) {
		// 2.1. Lấy tên file cấu hình SQL
		String pathConfigSQL = null;
		if (args.length > 0)
			pathConfigSQL = args[0];
		else
			System.out.println("Lấy tên file cấu hình mặc định: SQL account.txt");

		ManagerDAO managerDAO = null;
		try {
			// Gồm 2.2.
			managerDAO = new ManagerDAO(pathConfigSQL);
			// Gồm 2.3.
			managerDAO.duyetTableFileLogAndLoadFromCSVToStringTemp();
			// Gồm 2.4.
			managerDAO.duyetTableFileLogAndLoadFromStringTempToWeekly();
			// Gồm 2.5.
			managerDAO.duyetTableFileLogAndLoadFromWeeklyToTivi();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		if (managerDAO != null) {
			// 2.6. Đóng kết nối 3 Database TVDW_Control, TVDW_StagingArea và TVDW
			try {
				managerDAO.getControlDAO().getConnection().close();
				managerDAO.getStagingAreaDAO().getConnection().close();
				managerDAO.getTvdwDAO().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}