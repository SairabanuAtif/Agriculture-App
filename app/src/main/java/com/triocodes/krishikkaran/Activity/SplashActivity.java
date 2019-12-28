package com.triocodes.krishikkaran.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.triocodes.krishikkaran.Data.DataBaseHelper;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.R;


import io.fabric.sdk.android.Fabric;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    DataBaseHelper dbhelper;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        getdata();

        if (DataBaseQueryHelper.getInstance().isNewUserLogin() == 0 && DataBaseQueryHelper.getInstance().isNewUserRegister() == 0) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(SplashActivity.this, BaseActivity.class));
                }
            }, 2000);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }, 2000);
        }
    }

    private void getdata() {
        // TODO Auto-generated method stub
        dbhelper = new DataBaseHelper(this);
        try {
            dbhelper.createDataBase();
        } catch (IOException ioe) {

            throw new Error("Unable to create database");
        }
        try {
            dbhelper.openDatabase();
            dbhelper.getQueryhelper();
        } catch (SQLException sqle) {
            throw sqle;
        }
    }
}
