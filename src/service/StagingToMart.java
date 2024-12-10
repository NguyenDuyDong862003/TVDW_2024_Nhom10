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
                    // Cập nhập log thành công
                    LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_SU", 0, 0, date, time);
                    System.out.println("LDW_SU");

                } else { // Cập nhập log thất bại
                    LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_ERR", 0, 0, date, time);
                    System.out.println("LDW_ERR");
                }

                // 4. Trùng dữ liệu?
                //deleteDuplicateData();
                break;
            } else { // ERROR
                LogService.getInstance().addLog(config.getId(), config.getName(), 0, "LDW_ERR", 0, 0, date, time);
                System.out.println("LDW_ERR");
                break;
            }
        }
    }

    // Xử lí trùng lặp
    private void deleteDuplicateData() {

        String querry = "DELETE a1 FROM aggregate_tvdata a1, aggregate_tvdata a2 \n" +
                "WHERE a1.`SP_Key` > a2.`SP_Key` AND a1.`ProductName` = a2.`ProductName`";

        try {
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querry);
            ps.executeUpdate();

            System.out.println("Xóa dữ liệu trùng lặp thành công!");

            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

        Config configCPS = ConfigService.getInstance().getCPSConfig();
        Config configDMCL = ConfigService.getInstance().getDMCLConfig();
        Config configDMX = ConfigService.getInstance().getDMXConfig();

        List<TiviCPS> listCPS = getDataTableCPS();
        List<TiviDMCL> listDMCL = getDataTableDMCL();
        List<TiviDMX> listDMX = getDataTableDMX();

        String querryTivi = "INSERT INTO `tivi`(`ID_config`, `ProductName`, `OriginalPrice`, `DiscountedPrice`, `ProductLink`, `ImageLink`, `id_dateOnCSV`, `date_expired`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Ngày hết hạn
        LocalDate date = LocalDate.of(9999, 12, 31);

        try {
            // Tạo kết nối đến TVDW
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querryTivi);

            // CPS
            for (TiviCPS data : listCPS) {
                // SPKey ASIC
                ps.setInt(1, configCPS.getId());
                ps.setString(2, data.getProductName());
                ps.setDouble(3, data.getOriginalPrice());
                ps.setDouble(4, data.getDiscountedPrice());
                ps.setString(5, data.getProductLink());
                ps.setString(6, data.getImageLink());
                ps.setInt(7, data.getIdDateOnCSV());
                ps.setDate(8, Date.valueOf(date));

                ps.executeUpdate();
            }

            // DMCL
            for (TiviDMCL data : listDMCL) {
                // SPKey ASIC
                ps.setInt(1, configDMCL.getId());
                ps.setString(2, data.getTitle());
                ps.setDouble(3, data.getOriginalPrice());
                ps.setDouble(4, data.getDiscountedPrice());
                ps.setString(5, data.getProductDetailLink());
                ps.setString(6, data.getImgSrc());
                ps.setInt(7, data.getIdDateOnCSV());
                ps.setDate(8, Date.valueOf(date));

                ps.executeUpdate();
            }

            // DMX
            for (TiviDMX data : listDMX) {
                // SPKey ASIC
                ps.setInt(1, configDMX.getId());
                ps.setString(2, data.getName());
                ps.setDouble(3, data.getOriginalPrice());
                ps.setDouble(4, data.getDiscountedPrice());
                ps.setString(5, data.getProductDetailLink());
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

        DateDim dim = DateDimService.getInstance().getLastDateDim();
        List<Tivi> listTivi = getDataTableTivi();
        Config config = ConfigService.getInstance().getNameSource();

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

    // Lấy dữ liệu từ bảng Weekly_DMCL
    private List<TiviDMCL> getDataTableDMCL() {

        List<TiviDMCL> listDMCL = new ArrayList<>();
        String querryDMCL = "SELECT * FROM Weekly_DMCL";

        try {
            // Tạo kết nối đến Staging
            conn = new DBConnec().connecStaging();
            ps = conn.prepareStatement(querryDMCL);
            rs = ps.executeQuery();

            while (rs.next()) {
                listDMCL.add(new TiviDMCL(rs.getString("Title"),
                        rs.getString("ImgSrc"),
                        rs.getString("ProductDetailLink"),
                        rs.getDouble("OriginalPrice"),
                        rs.getDouble("DiscountPrice"),
                        rs.getString("ScreenSize"),
                        rs.getString("Resolution"),
                        rs.getInt("id_dateOnCSV"),
                        rs.getDate("dateOnCSV").toLocalDate()));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listDMCL;
    }

    // Lấy dữ liệu từ bảng Weekly_DMX
    private List<TiviDMX> getDataTableDMX() {

        List<TiviDMX> listDMX = new ArrayList<>();
        String querryDMX = "SELECT * FROM Weekly_DMX";

        try {
            // Tạo kết nối đến Staging
            conn = new DBConnec().connecStaging();
            ps = conn.prepareStatement(querryDMX);
            rs = ps.executeQuery();

            while (rs.next()) {
                listDMX.add(new TiviDMX(rs.getString("Name"),
                        rs.getString("ImageLink"),
                        rs.getString("ScreenSize"),
                        rs.getString("Resolution"),
                        rs.getDouble("OriginalPrice"),
                        rs.getDouble("DiscountPrice"),
                        rs.getString("ProductDetailLink"),
                        rs.getInt("id_dateOnCSV"),
                        rs.getDate("dateOnCSV").toLocalDate()));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listDMX;
    }

    public static void main(String[] args) {
        //new StagingToMart().insertDataIntoTableAgg();
        //new StagingToMart().deleteDuplicateData();
    }
}
