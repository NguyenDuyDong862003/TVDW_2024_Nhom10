package connec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnec {

    // SQL Sever
    public Connection connecDBMart() throws SQLException {

		String url = "jdbc:sqlserver://" + DBProperties.hostMart + "\\SQLEXPRESS:" + DBProperties.portMart +";databaseName=" + DBProperties.dbNameMart +";encrypt=true;trustServerCertificate=true;";
		String username = DBProperties.userNameMart;
		String password = DBProperties.passMart;

        Connection conn = null;

        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Kết nối thành công DB TVDW");

        return conn;
    }

    public Connection connecControl() throws SQLException {

        String url = "jdbc:sqlserver://" + DBProperties.hostControl + "\\SQLEXPRESS:" + DBProperties.portControl +";databaseName=" + DBProperties.dbNameControl +";encrypt=true;trustServerCertificate=true;";
        String username = DBProperties.userNameControl;
        String password = DBProperties.passControl;

        Connection conn = null;

        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Kết nối thành công DB TVDW");

        return conn;
    }

    public Connection connecStaging() throws SQLException {

        String url = "jdbc:sqlserver://" + DBProperties.hostStaging + "\\SQLEXPRESS:" + DBProperties.hostStaging +";databaseName=" + DBProperties.dbNameStaging +";encrypt=true;trustServerCertificate=true;";
        String username = DBProperties.userNameStaging;
        String password = DBProperties.passStaging;

        Connection conn = null;

        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Kết nối thành công DB TVDW");

        return conn;
    }

    public Connection connecTVDW() throws SQLException {

        String url = "jdbc:sqlserver://" + DBProperties.hostTVDW + "\\SQLEXPRESS:" + DBProperties.hostTVDW +";databaseName=" + DBProperties.dbNameTVDW +";encrypt=true;trustServerCertificate=true;";
        String username = DBProperties.userNameTVDW;
        String password = DBProperties.passTVDW;

        Connection conn = null;

        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Kết nối thành công DB TVDW");

        return conn;
    }


    // MySQL
    // Staging
    public Connection connecStaging1() {
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
    public Connection connecTVDW1() {
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
    public Connection connecControl1() {
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
