package service;

import connec.DBConnec;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StagingToMart {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Xem các bảng Weekly_ đã có dữ liệu chưa?
    // Bằng cách lấy status của Log
    // Sau khi có được status của Log
    // Nếu xem status: LW_ST?
    // Chuyển dữ liệu từ các bảng Weekly_ qua Tivi rồi chuyển qua Agg
    // CPS first
    public void getData() {

        Config config = ConfigService.getInstance().getCPSConfig();
        List<Log> list = LogService.getInstance().getListLog();
        String status = "LW_SU";

        // Hàm lấy ngày, tháng, giờ
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        // Kiểm tra xem có dòng lw_su không?
        for (Log st : list) {
            if (st.getStatus().equals(status)) {
                // 1. Cập nhập Log là Start to Table Tivi
                LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_ST", 0, 0, date, time);
                // Tiến hành lấy dữ liệu bảng Weekly_ và chèn dữ liệu vào bảng Tivi
                insertDataIntoTableTivi();

                // 2. Kiểm tra xem Table Tivi có dữ liệu không?
                // Có nên kiểm tra lại log xem status thành transform rồi làm tiếp hay không kiểm tra log và tiếp tục làm?
                if (getDataTableTivi() != null) { // Chèn dữ liệu qua Tabel Tivi thành công

                    // 3. Đưa dữ liệu từ Table Tivi qua Table aggregate_tvdata
                    insertDataIntoTableAgg();

                    // 4. Kiểm tra xem Table agg có dữ liệu không?
                    // Cập nhập lại log
                    if (getDataTableAgg() != null) { // Chèn dữ liệu qua Table agg thành công
                        // Cập nhập log thành công
                        LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_SU", 0, 0, date, time);
                        System.out.println("LDW_SU");
                    }
                } else { // Cập nhập log thất bại
                    LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_ERR", 0, 0, date, time);
                    System.out.println("LDW_ERR");
                }

                // Trùng dữ liệu?
                break;
            } else { // ERROR
                LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_ERR", 0, 0, date, time);
                System.out.println("LDW_ERR");
                break;
            }
        }
    }

    // Lấy dữ liệu từ bảng Weekly_CPS
    private List<TiviCPS> getDataTableCPS() {

        List<TiviCPS> listCPS = new ArrayList<>();
        String querryCPS = "SELECT * FROM Weekly_CPS";

        try {
            // Tạo kết nối đến Staging
            conn = new DBConnec().connecStaging();
            ps = conn.prepareStatement(querryCPS);
            rs = ps.executeQuery();

            while (rs.next()) {
                listCPS.add(new TiviCPS(rs.getString("ProductName"),
                        rs.getDouble("OriginalPrice"),
                        rs.getDouble("DiscountedPrice"),
                        rs.getString("ProductLink"),
                        rs.getString("ImageLink"),
                        rs.getInt("id_dateOnCSV"),
                        rs.getDate("dateOnCSV").toLocalDate()));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listCPS;
    }

    // insert dữ liệu từ các Table Weekly_ vào Table Tivi
    private void insertDataIntoTableTivi() {

        Config config = ConfigService.getInstance().getCPSConfig();
        List<TiviCPS> listCPS = getDataTableCPS();

        String querryTivi = "INSERT INTO `tivi`(`ID_config`, `ProductName`, `OriginalPrice`, `DiscountedPrice`, `ProductLink`, `ImageLink`, `id_dateOnCSV`, `date_expired`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Ngày hết hạn
        LocalDate date = LocalDate.of(9999, 12, 31);

        try {
            // Tạo kết nối đến TVDW
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querryTivi);

            for (TiviCPS data : listCPS) {
                // SPKey ASIC
                ps.setInt(1, config.getId());
                ps.setString(2, data.getProductName());
                ps.setDouble(3, data.getOriginalPrice());
                ps.setDouble(4, data.getDiscountedPrice());
                ps.setString(5, data.getProductLink());
                ps.setString(6, data.getImageLink());
                ps.setInt(7, data.getIdDateOnCSV());
                ps.setDate(8, Date.valueOf(date));

                ps.executeUpdate();
            }


            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy dữ liệu from Table Tivi lưu vào 1 list
    private List<Tivi> getDataTableTivi() {

        List<Tivi> listTivi = new ArrayList<>();
        String querryTivi = "SELECT * FROM Tivi";

        try {
            // Tạo kết nối đến TVDW
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querryTivi);
            rs = ps.executeQuery();

            while (rs.next()) {
                listTivi.add(new Tivi(rs.getInt("SP_Key"),
                        rs.getInt("ID_config"),
                        rs.getString("ProductName"),
                        rs.getDouble("OriginalPrice"),
                        rs.getDouble("DiscountedPrice"),
                        rs.getString("ProductLink"),
                        rs.getString("ImageLink"),
                        rs.getInt("id_dateOnCSV"),
                        rs.getDate("date_expired").toLocalDate()));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listTivi;
    }

    // insert dữ liệu từ Table Tivi vào Table agg
    private void insertDataIntoTableAgg() {

        Config config = ConfigService.getInstance().getNameSource();
        DateDim dim = DateDimService.getInstance().getLastDateDim();
        List<Tivi> listTivi = getDataTableTivi();

        String querryAgg = "INSERT INTO aggregate_tvdata (SP_Key, NameSource, ProductName, OriginalPrice, DiscountedPrice, ProductLink, ImageLink, dateOnCSV, date_expired) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Tạo kết nối đến TVDW
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querryAgg);

            for (Tivi data : listTivi) {
                ps.setInt(1, data.getSPKey());
                ps.setString(2, config.getName());
                ps.setString(3, data.getProductName());
                ps.setDouble(4, data.getOriginalPrice());
                ps.setDouble(5, data.getDiscountPrice());
                ps.setString(6, data.getProductLink());
                ps.setString(7, data.getImageLink());
                ps.setDate(8, Date.valueOf(dim.getFullDate()));
                ps.setDate(9, Date.valueOf(data.getDateExpired()));
                ps.executeUpdate();
            }


            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy dữ liệu from Table agg lưu vào 1 list
    private List<AggregateTvData> getDataTableAgg() {

        List<AggregateTvData> listAgg = new ArrayList<>();
        String querryAgg = "SELECT * FROM aggregate_tvdata";

        try {
            // Tạo kết nối đến TVDW
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querryAgg);
            rs = ps.executeQuery();

            while (rs.next()) {
                listAgg.add(new AggregateTvData(rs.getInt("SP_Key"),
                        rs.getString("NameSource"),
                        rs.getString("ProductName"),
                        rs.getDouble("OriginalPrice"),
                        rs.getDouble("DiscountedPrice"),
                        rs.getString("ProductLink"),
                        rs.getString("ImageLink"),
                        rs.getDate("dateOnCSV").toLocalDate(),
                        rs.getDate("date_expired").toLocalDate()));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listAgg;
    }

    public static void main(String[] args) {
        new StagingToMart().getData();
    }
}
