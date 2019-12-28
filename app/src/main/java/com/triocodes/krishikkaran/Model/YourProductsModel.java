package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 10-03-16.
 */
public class YourProductsModel implements Comparable<YourProductsModel> {
    String mProductName, mTotalQuantity,mAmount,mImgPath;
    int mProductCode,mCatagoryId,mQuantityId;

    public int getmProductCode() {
        return mProductCode;
    }

    public void setmProductCode(int mProductCode) {
        this.mProductCode = mProductCode;
    }

    public String getmImgPath() {
        return mImgPath;
    }

    public void setmImgPath(String mImgPath) {
        this.mImgPath = mImgPath;
    }

    public String getmProductName() {
        return mProductName;
    }

    public void setmProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public String getmTotalQuantity() {
        return mTotalQuantity;
    }

    public void setmTotalQuantity(String mTotalQuantity) {
        this.mTotalQuantity = mTotalQuantity;
    }

    public int getmCatagoryId() {
        return mCatagoryId;
    }

    public void setmCatagoryId(int mCatagoryId) {
        this.mCatagoryId = mCatagoryId;
    }

    public int getmQuantityId() {
        return mQuantityId;
    }

    public void setmQuantityId(int mQuantityId) {
        this.mQuantityId = mQuantityId;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    @Override
    public int compareTo(YourProductsModel another) {
        return this.mProductCode > another.mProductCode ? -1 : 1;
    }

}
