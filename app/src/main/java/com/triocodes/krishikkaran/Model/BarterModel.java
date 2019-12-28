package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 30-03-16.
 */
public class BarterModel implements Comparable<BarterModel> {
    int mProductId,mBarterId,mTotalProducts;
    String mProductName,mName,mLocation,mMobile,mEmail,mQuantity,mQuantityUnit,mAmount,mImageUrl,mRegId;

    public void setmRegId(String mRegId) {
        this.mRegId = mRegId;
    }

    public String getmRegId() {
        return mRegId;
    }

    public void setmTotalProducts(int mTotalProducts) {
        this.mTotalProducts = mTotalProducts;
    }

    public int getmTotalProducts() {
        return mTotalProducts;
    }

    public String getmQuantityUnit() {
        return mQuantityUnit;
    }

    public void setmQuantityUnit(String mQuantityUnit) {
        this.mQuantityUnit = mQuantityUnit;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public int getmBarterId() {
        return mBarterId;
    }

    public void setmBarterId(int mBarterId) {
        this.mBarterId = mBarterId;
    }

    public void setmProductCode(int mProductCode) {
        this.mProductId = mProductCode;
    }

    public int getmProductCode() {
        return mProductId;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    @Override
    public int compareTo(BarterModel another) {
        return 0;
    }
}
