package service;

import connec.DBConnec;
import model.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigService {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static ConfigService instance;

    private ConfigService() {

    }

    public static ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }


    // Lấy Config cho CPS
    public Config getCPSConfig() {
        String querry = "SELECT * FROM config_file where Name = 'CPS'";

        try {
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querry);
            rs = ps.executeQuery();

            while (rs.next()) {
                return new Config(rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Source_Web"),
                        rs.getString("Source_File_Location"),
                        rs.getString("Destination_Table_Temp_Staging"),
                        rs.getString("Destination_Table_Staging"),
                        rs.getString("Destination_Table_DW"));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {}
        return null;
    }

    // Lấy Config cho DMCL
    public Config getDMCLConfig() {
        String querry = "SELECT * FROM config_file where Name = 'DMCL'";

        try {
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querry);
            rs = ps.executeQuery();

            while (rs.next()) {
                return new Config(rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Source_Web"),
                        rs.getString("Source_File_Location"),
                        rs.getString("Destination_Table_Temp_Staging"),
                        rs.getString("Destination_Table_Staging"),
                        rs.getString("Destination_Table_DW"));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {}
        return null;
    }

    // Lấy Config cho DMX
    public Config getDMXConfig() {
        String querry = "SELECT * FROM config_file where Name = 'DMX'";

        try {
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querry);
            rs = ps.executeQuery();

            while (rs.next()) {
                return new Config(rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Source_Web"),
                        rs.getString("Source_File_Location"),
                        rs.getString("Destination_Table_Temp_Staging"),
                        rs.getString("Destination_Table_Staging"),
                        rs.getString("Destination_Table_DW"));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {}
        return null;
    }

    // So sánh để lấy NameSource
    public Config getNameSource() {
        String sql = "SELECT DISTINCT c.id, c.Name, c.Source_Web, c.Source_File_Location, c.Destination_Table_Temp_Staging, c.Destination_Table_Staging, c.Destination_Table_DW " +
                "FROM config_file c " +
                "INNER JOIN tivi t ON t.ID_config = c.ID;";

        try {
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                //list.add(
                        return new Config(rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Source_Web"),
                        rs.getString("Source_File_Location"),
                        rs.getString("Destination_Table_Temp_Staging"),
                        rs.getString("Destination_Table_Staging"),
                        rs.getString("Destination_Table_DW"))
                //)
                ;
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {}

//        List<Config> list = new ArrayList<>();
//        String CPS = "CPS";
//        String DMCL = "DMCL";
//        String DMX = "DMX";
//
//        for (Config item : list) {
//            if (item.getName().equals(CPS)) {
//
//            }
//        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(new ConfigService().getNameSource());
    }
}
