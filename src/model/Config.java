package model;

public class Config {
    private int id;
    private String name;
    private String sourceWeb;
    private String sourceFileLocation;
    private String destinationTableTempStaging;
    private String destinationTableStaing;
    private String destinationTableDW;

    public Config() {};

    public Config(int id, String name, String sourceWeb, String sourceFileLocation, String destinationTableTempStaging, String destinationTableStaing, String destinationTableDW) {
        this.id = id;
        this.name = name;
        this.sourceWeb = sourceWeb;
        this.sourceFileLocation = sourceFileLocation;
        this.destinationTableTempStaging = destinationTableTempStaging;
        this.destinationTableStaing = destinationTableStaing;
        this.destinationTableDW = destinationTableDW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceWeb() {
        return sourceWeb;
    }

    public void setSourceWeb(String sourceWeb) {
        this.sourceWeb = sourceWeb;
    }

    public String getSourceFileLocation() {
        return sourceFileLocation;
    }

    public void setSourceFileLocation(String sourceFileLocation) {
        this.sourceFileLocation = sourceFileLocation;
    }

    public String getDestinationTableTempStaging() {
        return destinationTableTempStaging;
    }

    public void setDestinationTableTempStaging(String destinationTableTempStaging) {
        this.destinationTableTempStaging = destinationTableTempStaging;
    }

    public String getDestinationTableStaing() {
        return destinationTableStaing;
    }

    public void setDestinationTableStaing(String destinationTableStaing) {
        this.destinationTableStaing = destinationTableStaing;
    }

    public String getDestinationTableDW() {
        return destinationTableDW;
    }

    public void setDestinationTableDW(String destinationTableDW) {
        this.destinationTableDW = destinationTableDW;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sourceWeb='" + sourceWeb + '\'' +
                ", sourceFileLocation='" + sourceFileLocation + '\'' +
                ", destinationTableTempStaging='" + destinationTableTempStaging + '\'' +
                ", destinationTableStaing='" + destinationTableStaing + '\'' +
                ", destinationTableDW='" + destinationTableDW + '\'' +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(new Config().getName());
    }
}
