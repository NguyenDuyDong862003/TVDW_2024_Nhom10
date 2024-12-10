package model;

import java.time.LocalDate;

public class Tivi {
    private int SPKey;
    private int IDConfig;
    private String ProductName;
    private double OriginalPrice;
    private double DiscountPrice;
    private String ProductLink;
    private String ImageLink;
    private int idDateOnCSV;
    private LocalDate dateExpired;

    public Tivi(int SPKey, int IDConfig, String productName, double originalPrice, double discountPrice, String productLink, String imageLink, int idDateOnCSV, LocalDate dateExpired) {
        this.SPKey = SPKey;
        this.IDConfig = IDConfig;
        ProductName = productName;
        OriginalPrice = originalPrice;
        DiscountPrice = discountPrice;
        ProductLink = productLink;
        ImageLink = imageLink;
        this.idDateOnCSV = idDateOnCSV;
        this.dateExpired = dateExpired;
    }

    public int getSPKey() {
        return SPKey;
    }

    public void setSPKey(int SPKey) {
        this.SPKey = SPKey;
    }

    public int getIDConfig() {
        return IDConfig;
    }

    public void setIDConfig(int IDConfig) {
        this.IDConfig = IDConfig;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public double getOriginalPrice() {
        return OriginalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        OriginalPrice = originalPrice;
    }

    public double getDiscountPrice() {
        return DiscountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        DiscountPrice = discountPrice;
    }

    public String getProductLink() {
        return ProductLink;
    }

    public void setProductLink(String productLink) {
        ProductLink = productLink;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public int getIdDateOnCSV() {
        return idDateOnCSV;
    }

    public void setIdDateOnCSV(int idDateOnCSV) {
        this.idDateOnCSV = idDateOnCSV;
    }

    public LocalDate getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(LocalDate dateExpired) {
        this.dateExpired = dateExpired;
    }
}
