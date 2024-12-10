package model;

import java.time.LocalDate;

public class TiviDMX {
    private String Name;
    private String ImageLink;
    private String ScreenSize;
    private String Resolution;
    private double OriginalPrice;
    private double DiscountedPrice;
    private String ProductDetailLink;
    private int idDateOnCSV;
    private LocalDate dateOnCSV;

    public TiviDMX(String name, String imageLink, String screenSize, String resolution, double originalPrice, double discountedPrice, String productDetailLink, int idDateOnCSV, LocalDate dateOnCSV) {
        Name = name;
        ImageLink = imageLink;
        ScreenSize = screenSize;
        Resolution = resolution;
        OriginalPrice = originalPrice;
        DiscountedPrice = discountedPrice;
        ProductDetailLink = productDetailLink;
        this.idDateOnCSV = idDateOnCSV;
        this.dateOnCSV = dateOnCSV;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getScreenSize() {
        return ScreenSize;
    }

    public void setScreenSize(String screenSize) {
        ScreenSize = screenSize;
    }

    public String getResolution() {
        return Resolution;
    }

    public void setResolution(String resolution) {
        Resolution = resolution;
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

    public String getProductDetailLink() {
        return ProductDetailLink;
    }

    public void setProductDetailLink(String productDetailLink) {
        ProductDetailLink = productDetailLink;
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
        return "TiviDMX{" +
                "Name='" + Name + '\'' +
                ", ImageLink='" + ImageLink + '\'' +
                ", ScreenSize='" + ScreenSize + '\'' +
                ", Resolution='" + Resolution + '\'' +
                ", OriginalPrice=" + OriginalPrice +
                ", DiscountedPrice=" + DiscountedPrice +
                ", ProductDetailLink='" + ProductDetailLink + '\'' +
                ", idDateOnCSV=" + idDateOnCSV +
                ", dateOnCSV=" + dateOnCSV +
                '}';
    }
}
