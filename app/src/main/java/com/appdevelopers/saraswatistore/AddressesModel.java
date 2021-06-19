package com.appdevelopers.saraswatistore;

public class AddressesModel {

    private String fullName;
    private String mobileNo;
    private String address;
    private String pinCode;
    private Boolean selected;

    public AddressesModel(String fullName, String address, String pinCode,Boolean selected,String mobileNo) {
        this.fullName = fullName;
        this.address = address;
        this.pinCode = pinCode;
        this.selected = selected;
        this.mobileNo = mobileNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
