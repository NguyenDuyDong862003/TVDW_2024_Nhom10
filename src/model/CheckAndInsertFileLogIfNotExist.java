package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

public class CheckAndInsertFileLogIfNotExist {
	DB_TVDW_ControlDAO controlDAO;

	public CheckAndInsertFileLogIfNotExist(String configSQLFilePath)
			throws FileNotFoundException, IOException, SQLException {
		try (FileInputStream fis = new FileInputStream(configSQLFilePath)) {
			// Tạo đối tượng Properties để đọc file
			Properties props = new Properties();
			props.load(fis);
			// Lấy thông tin từ file
			String serverName = props.getProperty("serverName");
			int port = Integer.parseInt(props.getProperty("port"));
			String username = props.getProperty("username");
			String password = props.getProperty("password");

			controlDAO = new DB_TVDW_ControlDAO(serverName, port, username, password);
		}
	}

	public DB_TVDW_ControlDAO getControlDAO() {
		return controlDAO;
	}

	public int countRowCSV(String pathCSV) {
		int lineCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(pathCSV))) {
			while (br.readLine() != null) {
				lineCount++;
			}
			return lineCount - 1; // trừ 1 vì có dòng tiêu đề rồi ko tính nó
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String extractWebNameOfCSV(String fileName) {
		// fileName = "data_cps_20241109.csv"; // ví dụ
		// Tạo StringTokenizer với dấu phân cách "_"
		StringTokenizer tokenizer = new StringTokenizer(fileName, "_");
		// Bỏ qua phần "data"
		tokenizer.nextToken();
		// Lấy phần nằm giữa (phần "cps")
		String extractedPart = tokenizer.nextToken();
		return extractedPart;
	}

	public void getInforFileCSVAndInsertFileLogIfNotExist(String path) {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			// Lấy kích thước file (tính bằng KB và làm tròn lên)
			double fileSizeKB = (double) file.length() / 1024;
			int fileSizeKBRounded = (int) Math.ceil(fileSizeKB); // Làm tròn lên

			String fileName = file.getName();
			String webName = extractWebNameOfCSV(fileName);
			int idConfig = controlDAO.getIdConfigFileFromNameWeb(webName);

			int countRow = countRowCSV(path);
			controlDAO.insertFileLogIfNotExist(idConfig, fileName, countRow, fileSizeKBRounded, null, null);
		} else {
			System.out.println("Đây không phải là một file hợp lệ.");
		}
	}

	public void duyetFileCSVTrongThuMucWeekly(String folderWeeklyCSV) throws Exception {
		File directory = new File(folderWeeklyCSV);

		// Kiểm tra xem đường dẫn có phải là thư mục hay không
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				// 1.3.1. Loop: với mỗi file trong thư mục chứa CSV
				for (File file : files) {
					String pathFileCSV = file.getAbsolutePath();
					// Gọi hàm để insert file này vô file log
					this.getInforFileCSVAndInsertFileLogIfNotExist(pathFileCSV);
				}
			} else {
				System.out.println("Không thể liệt kê các file trong thư mục.");
			}
		} else {
			System.out.println("Đường dẫn không hợp lệ hoặc không phải thư mục.");
		}
	}
}