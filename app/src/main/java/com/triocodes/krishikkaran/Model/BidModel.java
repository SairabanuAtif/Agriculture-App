package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 30-03-16.
 */
public class BidModel implements Comparable<BidModel> {
    String  mProductName,mSellerName,mSellerEmail,mQuantity,mAmount,mSellerMobile,mSellerAddress, mThumbnailUrl,mQuantityUnit,mMinAmount;
    int mProductCode,mBidId,mTotalProducts;

    public int getmTotalProducts() {
        return mTotalProducts;
    }

    public void setmTotalProducts(int mTotalProducts) {
        this.mTotalProducts = mTotalProducts;
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

    public int getmBidId() {
        return mBidId;
    }

    public void setmBidId(int mBidId) {
        this.mBidId = mBidId;
    }

    public String getmQuantityUnit() {
        return mQuantityUnit;
    }

    public void setmQuantityUnit(String mQuantityUnit) {
        this.mQuantityUnit = mQuantityUnit;
    }

    public void setmMinAmount(String mMinAmount) {
        this.mMinAmount = mMinAmount;
    }

    public String getmMinAmount() {
        return mMinAmount;
    }

    @Override
    public int compareTo(BidModel another) {
        return 0;
    }
}
