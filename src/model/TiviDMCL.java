package model;

import java.time.LocalDate;

public class TiviDMCL {
    private String Title;
    private String ImgSrc;
    private String ProductDetailLink;
    private double OriginalPrice;
    private double DiscountedPrice;
    private String ScreenSize;
    private String Resolution;
    private int idDateOnCSV;
    private LocalDate dateOnCSV;

    public TiviDMCL(String title, String imgSrc, String productDetailLink, double originalPrice, double discountedPrice, String screenSize, String resolution, int idDateOnCSV, LocalDate dateOnCSV) {
        Title = title;
        ImgSrc = imgSrc;
        ProductDetailLink = productDetailLink;
        OriginalPrice = originalPrice;
        DiscountedPrice = discountedPrice;
        ScreenSize = screenSize;
        Resolution = resolution;
        this.idDateOnCSV = idDateOnCSV;
        this.dateOnCSV = dateOnCSV;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImgSrc() {
        return ImgSrc;
    }

    public void setImgSrc(String imgSrc) {
        ImgSrc = imgSrc;
    }

    public String getProductDetailLink() {
        return ProductDetailLink;
    }

    public void setProductDetailLink(String productDetailLink) {
        ProductDetailLink = productDetailLink;
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
        return "TiviDMCL{" +
                "Title='" + Title + '\'' +
                ", ImgSrc='" + ImgSrc + '\'' +
                ", ProductDetailLink='" + ProductDetailLink + '\'' +
                ", OriginalPrice=" + OriginalPrice +
                ", DiscountedPrice=" + DiscountedPrice +
                ", ScreenSize='" + ScreenSize + '\'' +
                ", Resolution='" + Resolution + '\'' +
                ", idDateOnCSV=" + idDateOnCSV +
                ", dateOnCSV=" + dateOnCSV +
                '}';
    }
}
