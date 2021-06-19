package com.appdevelopers.saraswatistore;

public class GridProductModel {

    private String productID;
    private String gridProductImage;
    private String gridProductTitle;
    private String gridProductDescription;
    private String gridProductPrice;
    private String gridProductCuttedPrice;

    public GridProductModel(String productID,String gridProductImage, String gridProductTitle, String gridProductDescription, String gridProductPrice,String gridProductCuttedPrice) {
        this.productID = productID;
        this.gridProductImage = gridProductImage;
        this.gridProductTitle = gridProductTitle;
        this.gridProductDescription = gridProductDescription;
        this.gridProductPrice = gridProductPrice;
        this.gridProductCuttedPrice = gridProductCuttedPrice;
    }


    public String getGridProductCuttedPrice() {
        return gridProductCuttedPrice;
    }

    public void setGridProductCuttedPrice(String gridProductCuttedPrice) {
        this.gridProductCuttedPrice = gridProductCuttedPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getGridProductImage() {
        return gridProductImage;
    }

    public void setGridProductImage(String gridProductImage) {
        this.gridProductImage = gridProductImage;
    }

    public String getGridProductTitle() {
        return gridProductTitle;
    }

    public void setGridProductTitle(String gridProductTitle) {
        this.gridProductTitle = gridProductTitle;
    }

    public String getGridProductDescription() {
        return gridProductDescription;
    }

    public void setGridProductDescription(String gridProductDescription) {
        this.gridProductDescription = gridProductDescription;
    }

    public String getGridProductPrice() {
        return gridProductPrice;
    }

    public void setGridProductPrice(String gridProductPrice) {
        this.gridProductPrice = gridProductPrice;
    }
}

