package com.triocodes.krishikkaran.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 08-03-16.
 */
public class DataBaseQueryHelper {
    static DataBaseQueryHelper dataBaseQueryHelper;
    static SQLiteDatabase db;
    private DataBaseHelper dbhelper;
    public DataBaseQueryHelper(Context myContext, SQLiteDatabase db) {
        dataBaseQueryHelper=this;
        dbhelper=new DataBaseHelper(myContext);
        this.db=db;
    }
    public static DataBaseQueryHelper getInstance(){
        return dataBaseQueryHelper;
    }
    public String getMobileLogin(){
        String mMobile=null;
        Cursor mCur=db.rawQuery("select*from LoginTable",null);
        if(mCur!=null&&mCur.moveToFirst()){
            mMobile=mCur.getString(mCur.getColumnIndex("MobileNumber"));
        }
        return mMobile;
    }

    public String getMobileRegister(){
        String mMobile=null;
        Cursor mCur=db.rawQuery("select*from RegistrationDetails",null);
        if(mCur!=null&&mCur.moveToFirst()){
            mMobile=mCur.getString(mCur.getColumnIndex("MobileNumber"));
        }
        return mMobile;
    }

    public String getRegisterIdLogin(){
        String mRegId=null;
        Cursor mCur=db.rawQuery("select*from LoginTable",null);
        if(mCur!=null&&mCur.moveToFirst()){
            mRegId=mCur.getString(mCur.getColumnIndex("RegistrationId"));
        }
        return mRegId;
    }

    public String getRegisterIdRegister(){
        String mRegId=null;
        Cursor mCur=db.rawQuery("select*from RegistrationDetails",null);
        if(mCur!=null&&mCur.moveToFirst()){
            mRegId=mCur.getString(mCur.getColumnIndex("RegistrationId"));
        }
        return mRegId;
    }

    public void insertEntryLogin(String mobile,String password,String registrationId){
        ContentValues values=new ContentValues();
        values.put("MobileNumber",mobile);
        values.put("Password",password);
        values.put("RegistrationId",registrationId);
        db.insert("LoginTable",null,values);
    }

    public void insertEntryRegister(String mobile,String password,String registrationId){
        ContentValues values=new ContentValues();
        values.put("MobileNumber",mobile);
        values.put("Password",password);
        values.put("RegistrationId",registrationId);
        db.insert("RegistrationDetails",null,values);
    }
    public int isNewUserLogin(){
        int isNew=0;
        Cursor mCur=db.rawQuery("Select * from LoginTable",null);
        if (mCur != null && mCur.moveToFirst()) {
            isNew = 1;
        }
        return isNew;
    }

    public int isNewUserRegister(){
        int isNew=0;
        Cursor mCur=db.rawQuery("Select * from RegistrationDetails",null);
        if (mCur != null && mCur.moveToFirst()) {
            isNew = 1;
        }
        return isNew;
    }

   /* public void insertEntryToBackButtonDetails(int c){
        ContentValues values=new ContentValues();
        values.put("C",c);
        db.insert("BackButtonDetails",null,values);

    }*/

   /* public void updateEntryToBackButtonDetails(int c){
        ContentValues values=new ContentValues();
        values.put("C",c);
        db.update("BackButtonDetails", values, "C=" + c, null);
    }
*/
   /* public String getBackButtonValue(){
        String mBackButtonValue=null;
        Cursor mCur=db.rawQuery("select * from BackButtonDetails",null);
        if (mCur!=null&&mCur.moveToFirst()){
            mBackButtonValue=mCur.getString(mCur.getColumnIndex("C"));
        }
        return mBackButtonValue;
    }*/

   /* public String getBackButtonValue(){
        String mRegId=null;
        Cursor mCur=db.rawQuery("select*from BackButtonDetails",null);
        if(mCur!=null&&mCur.moveToFirst()){
            mRegId=mCur.getString(mCur.getColumnIndex("C"));
        }
        return mRegId;
    }*/

}
