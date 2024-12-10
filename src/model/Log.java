package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Log {
    private int id;
    private int idConfig;
    private String fileName;
    private int fileSizeKB;
    private String status;
    private int countLines;
    private int countRows;
    private LocalDate createAt;
    private LocalTime dtUpdate;

    public Log() {};

    public Log(int idConfig, String fileName, int fileSizeKB, String status, int countLines, int countRows, LocalDate createAt, LocalTime dtUpdate) {
        this.idConfig = idConfig;
        this.fileName = fileName;
        this.fileSizeKB = fileSizeKB;
        this.status = status;
        this.countLines = countLines;
        this.countRows = countRows;
        this.createAt = createAt;
        this.dtUpdate = dtUpdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSizeKB() {
        return fileSizeKB;
    }

    public void setFileSizeKB(int fileSizeKB) {
        this.fileSizeKB = fileSizeKB;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCountLines() {
        return countLines;
    }

    public void setCountLines(int countLines) {
        this.countLines = countLines;
    }

    public int getCountRows() {
        return countRows;
    }

    public void setCountRows(int countRows) {
        this.countRows = countRows;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public LocalTime getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(LocalTime dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", idConfig=" + idConfig +
                ", fileName='" + fileName + '\'' +
                ", fileSizeKB=" + fileSizeKB +
                ", status='" + status + '\'' +
                ", countLines=" + countLines +
                ", countRows=" + countRows +
                ", createAt=" + createAt +
                ", dtUpdate=" + dtUpdate +
                '}';
    }
}
