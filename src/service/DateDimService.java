package service;

import connec.DBConnec;
import model.DateDim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateDimService {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private static DateDimService instance;

    public static DateDimService getInstance() {
        if (instance == null) {
            instance = new DateDimService();
        }
        return instance;
    }

    public DateDim getLastDateDim() {
        String querry = "SELECT c.date_sk, c.full_date, c.day_since_2005, c.month_since_2005, c.day_of_week, " +
                "c.calendar_month, c.calendar_year, c.calendar_year_month, c.day_of_month, c.day_of_year, c.week_of_year_sunday, c.year_week_sunday, c.week_sunday_start, " +
                "c.week_of_year_monday, c.year_week_monday, c.week_monday_start, c.holiday, c.day_type\n" +
                "FROM Tivi t\n" +
                "INNER JOIN datedim c ON t.id_dateOnCSV = c.date_sk LIMIT 1";

        try {
            conn = new DBConnec().connecTVDW();
            ps = conn.prepareStatement(querry);
            rs = ps.executeQuery();

            while (rs.next()) {
                return new DateDim(rs.getInt("date_sk"),
                        rs.getDate("full_date").toLocalDate(),
                        rs.getInt("day_since_2005"),
                        rs.getInt("month_since_2005"),
                        rs.getString("day_of_week"),
                        rs.getString("calendar_month"),
                        rs.getInt("calendar_year"),
                        rs.getString("calendar_year_month"),
                        rs.getInt("day_of_month"),
                        rs.getInt("day_of_year"),
                        rs.getInt("week_of_year_sunday"),
                        rs.getString("year_week_sunday"),
                        rs.getDate("week_sunday_start").toLocalDate(),
                        rs.getInt("week_of_year_monday"),
                        rs.getString("year_week_monday"),
                        rs.getDate("week_monday_start").toLocalDate(),
                        rs.getString("holiday"),
                        rs.getString("day_type"));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        DateDim dim = new DateDimService().getLastDateDim();
        System.out.println(dim.getFullDate());
    }
}
