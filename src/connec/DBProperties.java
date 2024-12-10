package connec;

import java.io.IOException;
import java.util.Properties;

public class DBProperties {
    private static Properties proStaging = new Properties();
    private static Properties proTVDW = new Properties();
    private static Properties proControl = new Properties();

    static {
        try {
            proStaging.load(DBProperties.class.getClassLoader().getResourceAsStream("resources/DBStaging.properties"));
            proTVDW.load(DBProperties.class.getClassLoader().getResourceAsStream("resources/TVDW.properties"));
            proControl.load(DBProperties.class.getClassLoader().getResourceAsStream("resources/DBControl.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // proStaging
    public static String hostStaging = proStaging.getProperty("db.host");
    public static String portStaging = proStaging.getProperty("db.port");
    public static String userNameStaging = proStaging.getProperty("db.username");
    public static String passStaging = proStaging.getProperty("db.password");
    public static String dbNameStaging = proStaging.getProperty("db.name");

    // proTVDW
    public static String hostTVDW = proTVDW.getProperty("db.host");
    public static String portTVDW = proTVDW.getProperty("db.port");
    public static String userNameTVDW = proTVDW.getProperty("db.username");
    public static String passTVDW = proTVDW.getProperty("db.password");
    public static String dbNameTVDW = proTVDW.getProperty("db.name");

    // proControl
    public static String hostControl = proControl.getProperty("db.host");
    public static String portControl = proControl.getProperty("db.port");
    public static String userNameControl = proControl.getProperty("db.username");
    public static String passControl = proControl.getProperty("db.password");
    public static String dbNameControl = proControl.getProperty("db.name");

}
