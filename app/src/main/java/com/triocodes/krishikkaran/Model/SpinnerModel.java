package com.triocodes.krishikkaran.Model;

/**
 * Created by admin on 09-03-16.
 */
public class SpinnerModel implements Comparable<SpinnerModel> {
    String text;
    int id;
    String pinCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    @Override
    public int compareTo(SpinnerModel another) {
        return this.id < another.id? -1 : 1;
    }
}
