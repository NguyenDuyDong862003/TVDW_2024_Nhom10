package model;

import java.time.LocalDate;

public class AggregateTvData {
    private int SPKey;
    private String NameSource;
    private String ProductName;
    private double 	OriginalPrice;
    private double 	DiscountPrice;
    private String ProductLink;
    private String ImageLink;
    private LocalDate DateOnCSV;
    private LocalDate dateExpired;

    public AggregateTvData(int SPKey, String nameSource, String productName, double originalPrice, double discountPrice, String productLink, String imageLink, LocalDate dateOnCSV, LocalDate dateExpired) {
        this.SPKey = SPKey;
        NameSource = nameSource;
        ProductName = productName;
        OriginalPrice = originalPrice;
        DiscountPrice = discountPrice;
        ProductLink = productLink;
        ImageLink = imageLink;
        DateOnCSV = dateOnCSV;
        this.dateExpired = dateExpired;
    }

    public int getSPKey() {
        return SPKey;
    }

    public void setSPKey(int SPKey) {
        this.SPKey = SPKey;
    }

    public String getNameSource() {
        return NameSource;
    }

    public void setNameSource(String nameSource) {
        NameSource = nameSource;
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

    public LocalDate getDateOnCSV() {
        return DateOnCSV;
    }

    public void setDateOnCSV(LocalDate dateOnCSV) {
        DateOnCSV = dateOnCSV;
    }

    public LocalDate getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(LocalDate dateExpired) {
        this.dateExpired = dateExpired;
    }

    @Override
    public String toString() {
        return "AggregateTvData{" +
                "SPKey=" + SPKey +
                ", NameSource='" + NameSource + '\'' +
                ", ProductName='" + ProductName + '\'' +
                ", OriginalPrice=" + OriginalPrice +
                ", DiscountPrice=" + DiscountPrice +
                ", ProductLink='" + ProductLink + '\'' +
                ", ImageLink='" + ImageLink + '\'' +
                ", DateOnCSV=" + DateOnCSV +
                ", dateExpired=" + dateExpired +
                '}';
    }
}
