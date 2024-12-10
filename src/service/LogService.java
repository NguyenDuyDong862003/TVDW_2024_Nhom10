package service;

import connec.DBConnec;
import model.Log;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LogService {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static LogService instance;

    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    public List<Log> getListLog() {
        List<Log> list = new ArrayList<>();
        String querry = "SELECT * FROM file_log";

        try {
            conn = new DBConnec().connecControl();
            ps = conn.prepareStatement(querry);
            rs = ps.executeQuery();

            while (rs.next()) {
                Log log = new Log();
                log.setId(rs.getInt("ID"));
                log.setIdConfig(rs.getInt("ID_config"));
                log.setFileName(rs.getString("filename"));
                log.setFileSizeKB(rs.getInt("file_size_kb"));
                log.setStatus(rs.getString("status"));
                log.setCountLines(rs.getInt("count_lines"));
                log.setCountRows(rs.getInt("count_rows"));
                log.setCreateAt(rs.getDate("create_at").toLocalDate());
                log.setDtUpdate(rs.getTime("dt_update").toLocalTime());

                list.add(log);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {}
        return list;
    }

    public void addLog(int idConfig, String fileName, int fileSizeKB, String status, int countLines, int countRows, LocalDate createAt, LocalTime dtUpdate) {
        String querry = "INSERT INTO `file_log`(`ID_config`, `filename`, `file_size_kb`, `status`, `count_lines`, `count_rows`, `create_at`, `dt_update`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = new DBConnec().connecControl();
            ps = conn.prepareStatement(querry);

            ps.setInt(1, idConfig);
            ps.setString(2, fileName);
            ps.setInt(3, fileSizeKB);
            ps.setString(4, status);
            ps.setInt(5, countLines);
            ps.setInt(6, countRows);
            ps.setDate(7, Date.valueOf(createAt));
            ps.setTime(8, Time.valueOf(dtUpdate));

            ps.executeUpdate();

            ps.close();
            conn.close();
        } catch (SQLException e) {}
    }

    public static void main(String[] args) {
        List<Log> list = LogService.getInstance().getListLog();
        String status = "LW_SU";
        for (Log st : list) {
            if (st.getStatus().equals(status)) {
                System.out.println("GOOD");
                break;
            } else System.out.println("BAD");
        }

        // Hàm lấy ngày, tháng, giờ
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

//        for (Log status : list) { // Lặp qua từng phần tử trong list
//            if (status.getStatus() == "LW_SU") {
//                System.out.println("Good");
//                LogService.getInstance().addLog(1, "1", 0, "LDW_ST", 0, 0, date, time);
//            } else {
//                System.out.println("Bad");
//                LogService.getInstance().addLog(1, "1", 0, "LDW_ERR", 0, 0, date, time);
//            }
//        }
    }
}
