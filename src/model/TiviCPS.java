package model;

import java.time.LocalDate;

public class TiviCPS {
    private String ProductName;
    private double 	OriginalPrice;
    private double 	DiscountedPrice;
    private String ProductLink;
    private String ImageLink;
    private int idDateOnCSV;
    private LocalDate dateOnCSV;

    public TiviCPS(String productName, double originalPrice, double discountedPrice, String productLink, String imageLink, int idDateOnCSV, LocalDate dateOnCSV) {
        ProductName = productName;
        OriginalPrice = originalPrice;
        DiscountedPrice = discountedPrice;
        ProductLink = productLink;
        ImageLink = imageLink;
        this.idDateOnCSV = idDateOnCSV;
        this.dateOnCSV = dateOnCSV;
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

    public double getDiscountedPrice() {
        return DiscountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        DiscountedPrice = discountedPrice;
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

    public LocalDate getDateOnCSV() {
        return dateOnCSV;
    }

    public void setDateOnCSV(LocalDate dateOnCSV) {
        this.dateOnCSV = dateOnCSV;
    }

    @Override
    public String toString() {
        return "TiviCPS{" +
                "ProductName='" + ProductName + '\'' +
                ", OriginalPrice=" + OriginalPrice +
                ", DiscountedPrice=" + DiscountedPrice +
                ", ProductLink='" + ProductLink + '\'' +
                ", ImageLink='" + ImageLink + '\'' +
                ", idDateOnCSV=" + idDateOnCSV +
                ", dateOnCSV=" + dateOnCSV +
                '}';
    }
}
