package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 28-03-16.
 */
public class VegetablesModel implements Comparable<VegetablesModel> {
String mProductName,mSellerName,mSellerEmail,mQuantity,mAmount,mSellerMobile,mSellerAddress, mThumbnailUrl,mQuantityUnit;
    int mProductCode,totalProducts;

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmSellerAddress() {
        return mSellerAddress;
    }

    public void setmSellerAddress(String mSellerAddress) {
        this.mSellerAddress = mSellerAddress;
    }

    public String getmSellerEmail() {
        return mSellerEmail;
    }

    public void setmSellerEmail(String mSellerEmail) {
        this.mSellerEmail = mSellerEmail;
    }

    public String getmSellerMobile() {
        return mSellerMobile;
    }

    public void setmSellerMobile(String mSellerMobile) {
        this.mSellerMobile = mSellerMobile;
    }

    public String getmSellerName() {
        return mSellerName;
    }

    public void setmSellerName(String mSellerName) {
        this.mSellerName = mSellerName;
    }

    public void setmProductCode(int mProductCode) {
        this.mProductCode = mProductCode;
    }

    public int getmProductCode() {
        return mProductCode;
    }

    public String getmThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setmThumbnailUrl(String mThumbnailUrl) {
        this.mThumbnailUrl = mThumbnailUrl;
    }

    public String getmQuantityUnit() {
        return mQuantityUnit;
    }

    public void setmQuantityUnit(String mQuantityUnit) {
        this.mQuantityUnit = mQuantityUnit;
    }

    @Override
    public int compareTo(VegetablesModel another) {
        return 0;
    }
}
