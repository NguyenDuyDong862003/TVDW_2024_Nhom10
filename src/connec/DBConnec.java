package connec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnec {
    // Staging
    public Connection connecStaging() {
        String url = "jdbc:mysql://" + DBProperties.hostStaging + ":" + DBProperties.portStaging + "/" + DBProperties.dbNameStaging;
        String user = DBProperties.userNameStaging;
        String password = DBProperties.passStaging;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            //System.out.println("Kết nối thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
        return conn;
    }

    // TVDW
    public Connection connecTVDW() {
        String url = "jdbc:mysql://" + DBProperties.hostTVDW + ":" + DBProperties.portTVDW + "/" + DBProperties.dbNameTVDW;
        String user = DBProperties.userNameTVDW;
        String password = DBProperties.passTVDW;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            //System.out.println("Kết nối thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
        return conn;
    }

    // Control
    public Connection connecControl() {
        String url = "jdbc:mysql://" + DBProperties.hostControl + ":" + DBProperties.portControl + "/" + DBProperties.dbNameControl;
        String user = DBProperties.userNameControl;
        String password = DBProperties.passControl;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            //System.out.println("Kết nối thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
        return conn;
    }
}
