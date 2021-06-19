package com.appdevelopers.saraswatistore;

import java.util.Date;

public class MyOrderItemModel {

    private String productId;
    private String productTitle;
    private String productImage;

    private String orderStatus;
    private String address;
    /*private Date orderDate;
    private Date packedDate;
    private Date shippedDate;
    private Date deliveredDate;
    private Date cancelledDate;*/
    private String fullName;
    private String orderID;
    private String paymentMethod;
    private String pinCode;
    private String productPrice;
    private Long productQuantity;
    private String userId;
    private String cuttedPrice;
    //private boolean cancellationRequested;

    private int rating = 0;

    public MyOrderItemModel(String productId, String productTitle, String productImage, String orderStatus, String address, String fullName, String orderID, String paymentMethod, String pinCode, String productPrice, Long productQuantity, String userId, String cuttedPrice) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.orderStatus = orderStatus;
        this.address = address;
        this.fullName = fullName;
        this.orderID = orderID;
        this.paymentMethod = paymentMethod;
        this.pinCode = pinCode;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.userId = userId;
        this.cuttedPrice = cuttedPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }
}

