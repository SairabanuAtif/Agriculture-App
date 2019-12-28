package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 31-03-16.
 */
public class BarterProductModel implements Comparable<BarterProductModel> {
    String mId,mProduct;

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmId() {
        return mId;
    }

    public void setmProduct(String mProduct) {
        this.mProduct = mProduct;
    }

    public String getmProduct() {
        return mProduct;
    }

    @Override
    public int compareTo(BarterProductModel another) {
        return 0;
    }
}
