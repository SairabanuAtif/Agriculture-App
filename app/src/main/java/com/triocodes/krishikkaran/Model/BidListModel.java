package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 31-03-16.
 */
public class BidListModel implements Comparable<BidListModel> {
String mName,mPlace,mMobilenum,mAmount,mBiddedOn;

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }

    public void setmAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public String getmAmount() {
        return mAmount;
    }

    public void setmBiddedOn(String mBiddedOn) {
        this.mBiddedOn = mBiddedOn;
    }

    public String getmBiddedOn() {
        return mBiddedOn;
    }

    public void setmMobilenum(String mMobilenum) {
        this.mMobilenum = mMobilenum;
    }

    public String getmMobilenum() {
        return mMobilenum;
    }

    public void setmPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public String getmPlace() {
        return mPlace;
    }

    @Override
    public int compareTo(BidListModel another) {
        return 0;
    }
}
