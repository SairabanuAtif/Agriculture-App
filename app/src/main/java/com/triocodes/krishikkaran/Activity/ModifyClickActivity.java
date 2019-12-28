package com.triocodes.krishikkaran.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.triocodes.krishikkaran.Adapter.SpinnerAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.SpinnerModel;
import com.triocodes.krishikkaran.ParentActivity;
import com.triocodes.krishikkaran.R;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ModifyClickActivity extends ParentActivity implements Response.Listener, Response.ErrorListener, VolleyCallback, View.OnClickListener, AdapterView.OnItemSelectedListener {
    Toolbar mToolbar;
    HashMap<String, Object> extras;
    Handler mHandler;
    TextView mTextSave, mTextCancel, mTextBarter, mTextUploadImage, mTextQuantityUnit, mTextCatagory;
    EditText mEditProductName, mEditTotalQuantity, mEditAmountPerUnit;
    Spinner mSpinnerQuantityUnit, mSpinnerCatagory;
    ServiceCallEnum mServiceCallEnum;
    ImageView imageView;
    String RegistrationId, RegistrationIdToPass;
    private static int RESULT_LOAD_IMAGE = 1;
    private String selectedImagePath;
    ArrayList<SpinnerModel> mSpinnerQuantityUnitList, mSpinnerCatagoryList;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    VolleyCallback mVolleyCallback;
    int ProductId, CategoryId, QuantityId;
    String ProductName, TotalQuantity, ImagePath, Amount;
    int mCatagoryId;
    int mQuantityId;
    private Bitmap bitmap;

    private Uri mImageCaptureUri;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 1;
    BitmapDrawable drawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_click);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbarModify);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
            }
        });

        Intent intent = getIntent();
        ProductId = intent.getIntExtra("ProductId", 0);
        ProductName = intent.getStringExtra("ProductName");
        CategoryId = intent.getIntExtra("CategoryId", 0);
        TotalQuantity = intent.getStringExtra("TotalQuantity");
        QuantityId = intent.getIntExtra("QuantityId", 0);
        Amount = intent.getStringExtra("Amount");
        ImagePath = intent.getStringExtra("ImagePath");

        extras = new HashMap<String, Object>();
        mHandler = new Handler();

        mTextSave = (TextView) this.findViewById(R.id.text_button_modify_product_save);
        mTextUploadImage = (TextView) this.findViewById(R.id.text_button_modify_product_upload_image);
        mEditProductName = (EditText) this.findViewById(R.id.edittext_modify_product_product_name);
        mEditAmountPerUnit = (EditText) this.findViewById(R.id.edittext_modify_product_amount_per_unit);
        mEditTotalQuantity = (EditText) this.findViewById(R.id.edittext_modify_product_total_quantity);
        mTextQuantityUnit = (TextView) this.findViewById(R.id.text_quantity_unit);
        mTextCatagory = (TextView) this.findViewById(R.id.text_category);


        mTextBarter = (TextView) this.findViewById(R.id.text_button_modify_product_barter);
        mTextBarter.setOnClickListener(this);
        mTextCancel = (TextView) this.findViewById(R.id.text_button_modify_product_cancel);
        mTextCancel.setOnClickListener(this);

        RegistrationId = DataBaseQueryHelper.getInstance().getRegisterIdRegister();

        if (RegistrationId == null) {
            RegistrationIdToPass = DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        } else {
            RegistrationIdToPass = RegistrationId;
        }
        imageView = (ImageView) this.findViewById(R.id.image_modify_product);


        mTextSave.setOnClickListener(this);
        mTextUploadImage.setOnClickListener(this);
        bindData();

    }

    private void bindData() {
        switch (CategoryId) {
            case 1:
                mTextCatagory.setText("Vegetables");
                break;
            case 2:
                mTextCatagory.setText("Fruits");
                break;
            case 3:
                mTextCatagory.setText("Spices");
                break;
            case 4:
                mTextCatagory.setText("Seeds and Plants");
                break;
            case 5:
                mTextCatagory.setText("Flowers");
                break;
            case 6:
                mTextCatagory.setText("Herbs and Cereals");
                break;
            case 7:
                mTextCatagory.setText("Dairy Products");
                break;
            case 8:
                mTextCatagory.setText("Animals");
                break;
            case 9:
                mTextCatagory.setText("Equipments");
                break;
            case 10:
                mTextCatagory.setText("Handloom");
                break;
        }

        switch (QuantityId) {
            case 1:
                mTextQuantityUnit.setText("KiloGrams");
                break;
            case 2:
                mTextQuantityUnit.setText("Grams");
                break;
            case 3:
                mTextQuantityUnit.setText("Litre");
                break;
            case 4:
                mTextQuantityUnit.setText("Numbers");
                break;
            case 5:
                mTextQuantityUnit.setText("Acre");
                break;


        }

        mEditProductName.setText(ProductName);
        mEditTotalQuantity.setText(TotalQuantity);
        mEditAmountPerUnit.setText(Amount);

        Picasso.with(this).load(ImagePath)
                .placeholder(R.drawable.noimage).skipMemoryCache().into(imageView);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_button_modify_product_save: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {

                            ConnectToServer();


                    }
                } else {
                    showError("Connectivity Issue", "No internet connection available");
                }
                break;
            }
            case R.id.text_button_modify_product_barter: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {
                        ConnectToBarter();
                    }
                } else {
                    showError("Connectivity Issue", "No internet connection available");
                }
                break;
            }
            case R.id.text_button_modify_product_cancel: {
                onBackPressed();
                break;
            }
            case R.id.text_button_modify_product_upload_image: {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                this.startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            }
        }
    }

    private void ConnectToBarter() {
        JSONObject mBarterDetails = new JSONObject();
        try {

            mBarterDetails.put("productID", ProductId);
            mBarterDetails.put("RegistrationId", RegistrationIdToPass);
            mBarterDetails.put("UserProductName", mEditProductName.getText().toString());

            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.MODIFYYOURPRODUCTBARTER;
            establishConnection(Request.Method.POST, Constants.mBarterInterestUrl, mBarterDetails);

        } catch (Exception e) {

        }
    }

    private boolean nullChecker() {
        if (mEditProductName.getText().toString().trim().length() == 0 || mEditProductName.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_modify_product_product_name);
            return false;
        } else if (mEditTotalQuantity.getText().toString().trim().length() == 0 || mEditTotalQuantity.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_modify_product_total_quantity);
            return false;
        } else if (mEditAmountPerUnit.getText().toString().trim().length() == 0 || mEditAmountPerUnit.getText().toString() == "") {
            super.shakeEdittext(R.id.edittext_modify_product_amount_per_unit);
            return false;
        }
        return true;
    }


    private void ConnectToServer() {
        JSONObject mModifyProductDetails = new JSONObject();
        try {
            mModifyProductDetails.put("ProductId", ProductId);
            mModifyProductDetails.put("ProductName", mEditProductName.getText().toString());
            mModifyProductDetails.put("CategoryId", CategoryId);//mCategoryId
            mModifyProductDetails.put("TotalQuantity", mEditTotalQuantity.getText().toString());
            mModifyProductDetails.put("QuantityId", QuantityId);//mQuantityId
            mModifyProductDetails.put("amount", mEditAmountPerUnit.getText().toString());
            mModifyProductDetails.put("RegistrationId", RegistrationIdToPass);
            if(bitmap==null) {
                drawable = (BitmapDrawable) imageView.getDrawable();
                bitmap = drawable.getBitmap();
                String m=getStringImage(bitmap);
                String defaultImage="iVBORw0KGgoAAAANSUhEUgAAAHMAAABzCAYAAACrQz3mAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYxIDY0LjE0MDk0OSwgMjAxMC8xMi8wNy0xMDo1NzowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNS4xIFdpbmRvd3MiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6RDhBNzM0NTcwMTY2MTFFNjgwRTdGQjEzNTExOENEMzMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6RDhBNzM0NTgwMTY2MTFFNjgwRTdGQjEzNTExOENEMzMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpEOEE3MzQ1NTAxNjYxMUU2ODBFN0ZCMTM1MTE4Q0QzMyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpEOEE3MzQ1NjAxNjYxMUU2ODBFN0ZCMTM1MTE4Q0QzMyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pi2llY8AAEHFSURBVHja7J15sO1X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";
                String modifieddefaultImage="/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\n" +
                        "AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEB\n" +
                        "AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCABzAHMDASIA\n" +
                        "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\n" +
                        "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\n" +
                        "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\n" +
                        "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\n" +
                        "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\n" +
                        "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\n" +
                        "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\n" +
                        "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD/AD/6\n" +
                        "KKKACuk8G+DfF/xF8XeGPAHgDwxrvjTxt4z13SfC/g/wf4X0m+1zxN4o8Ta5fw6Zofh7w9ommwXO\n" +
                        "oazrWs6hPBYaXpdhbz319fTQWlpBLcSxxt9sfsD/APBOD9o//gob8Q7zwr8INHsNF8IeGLaz1n4h\n" +
                        "fFTxnc3OieBPCfh6TXNL0e7kj1P7BeSeIfEKHUVuLLwj4fttR8R3ltBf3senDTrLULuP+/T/AIJ4\n" +
                        "/wDBHL9nL9iDwff33wa8JWnxb+Ilzquh63N+0H8WfBPgXVvHdpq/h6TUjYzfClJNA1C++EOnMLqK\n" +
                        "6vrLw/r13qGo6jZ6fe6nrFxLY6WIfm884oy7I06MlUxuYSpupSy3CJVMTKCcl7Sor2o0tPjqNOTu\n" +
                        "oKUmfN55xRl2SJ0ZKpjMwlTdSll2Ej7TEyinJe0qK/LRpe78dRrmfuwUpOx/G1+yl/wb2/8ABRH9\n" +
                        "o+48TTfEPwRD+yLoPh2wt7qPWP2mNI8XeENT8RXs94bVdE8N+CNP8O6t40utRSJJ7+W5v9CsNBt7\n" +
                        "O3kF5rcF9daHZXv7b/DX/g1y/Y5sfD2nP8Xf2lv2kvG3iU6bZf25H8LNJ+G/gDw1Ya/NaxG8s9Mv\n" +
                        "vGPgj4jalqul2t6J4LLULyw0m91K0WC7n0rTJ5JrGP8ArK8G+F9F0XxF/bHiZl1m0vY0sLifU5Nj\n" +
                        "2+tNvR0BkAyEYqM+w+UHNb8+s+EvAE/iLT7bTJvEl3rWo213b2cVo9xb2iRuW2RyRqQQOenGTgk5\n" +
                        "zX5ljONuI8bTdaGKwWR4ZzqQlCEHisTTShOVGTqT+N15RUYqjC6babdrv81xnGnEWNpurDFYLJMP\n" +
                        "zzpyhCH1nFU7QlKjJ1J35/rEkoxVGF4vmTbtd/ymyf8ABrl+wtLcLBafEf8AbIjMh/dpd/ET4HmU\n" +
                        "qc7Swj/ZsQYIHb25OcmO7/4Nb/2HtLjSTVPiR+2FbF5hFHC3xD+CMc0wJKholb9m5wVY7SrZIwRk\n" +
                        "ZJNf1iN4g1vWNeOu6d4QvPs3k26xwtp9wgVkDcBfL5Axg9+vJwcxeKtf8SeI7zTPEPiLwddWUvh6\n" +
                        "aE2+nW+n3Dw31tbFirSlYyMydwegJJIB3V5Ms74g9lXnHijN5TVRKg/q0VQnR54qdaUvYWjyQ96E\n" +
                        "PinJuFm4u/kyzriD2VecOJ82lONRKi/qyVCpS57Tqym6NocsVzwp3vO6jFyaZ/JV4v8A+DW39hy6\n" +
                        "0l18L/tHftYfDLVEjCNq/jnSfhN8UNHWZvPAl/sbQPBXwiu3iH7lvJGth2CTL9oUyoyfiX+1V/wb\n" +
                        "s/t7fs+aF4X8T/Cqz0D9rzTPEt9rdtc6T+ztpXjrXvGng6201dPksLrxb4V17whoF9JHr6X/AJel\n" +
                        "t4W/t8LcafrK6qNPhj02a7/0avHXiTwp8TvEfhm+1caj4T0OAIuvaati8MHno+I0JZMlXHDMenXr\n" +
                        "81bXib4XaJ448X2UngaW20bQLG2tGnu7G4XzmVFYiVRGWLSSdWX1IB4Az6uXcZcTYepXVLFYPiDD\n" +
                        "Uq9ChRp1lHD47ExqRnKVeEqd3SpUklzyrQ1m+W1oSZ6mXcY8S4erXVLFYPP8NSr0KFGFZRoY3FKr\n" +
                        "GcpV4ypP9zSpJWk60L86nFr3Wf43PjfwP40+GfjHxN8PPiL4V17wT448G6zqHhzxd4P8U6Ve6J4m\n" +
                        "8M+IdLuJLTVND1/RdRgt7/SdW026iktb/Tr2CK8tLlJbe5hSZJFrlq/1Jf8AgoZ/wTL/AGV/2s/D\n" +
                        "et/C/wCOPgDRNL8Wa7rVr4s0j9oT4W+Cfhzo/wAedO1aw03XbHTxr/jaXwfd654v8IzTa2L3xH4K\n" +
                        "1fUY7PWnstOmXULHV7LS9Wg/gK/4KF/8Esf2kv8Agnl4ot5PHmm2/jL4T+Jr/VI/Afxc8ILqGpeG\n" +
                        "dRtbO4lWLTvEkz6XZHwv4oS3VZrnQ9TSG6aHZfwxCyuLV2/Sch4tyzPalXBpyweZ4duNfLsQ4qsn\n" +
                        "H4pUZKThXppWlem3JRac4xVz9HyHizLM9nVwibweZ4d8tfLsRKKrKSvzSoyUnGvCy5r025Ri05xV\n" +
                        "z8z6KKK+pPqQooooAK/TX/gl1/wTa8e/8FHfjnN4RtdTPgv4R+Arey8RfGT4kzRK50Lw3Ncvb2+l\n" +
                        "+HLSYomv+LdeuYxp+jaJbyrJ5kv9o6lPYaFa6rrVv8Yfs8/s/fFv9qn40+APgB8DfCGoeN/iX8Rt\n" +
                        "YGj+G/D+neQjOIbW51HV9X1G8u5rex0jQPDejWN/4h8Ta/qt1a6L4f8ADlhqmva5qFpo9jfXyf6k\n" +
                        "H/BO/wDYd+Gv7KnwR+Hn7Lvw7l1C48K+BDceIvFOsS31xev4++LuuR2Z8ceLx5trYPHpmpX+nR6f\n" +
                        "4asHtlm03wlp+g6ZdTXN7bXWoTfJ8VcQTyXCUsPglCrm2YTdDAUptctN3kqmLrK91SorVJ6VKnLC\n" +
                        "7SZ8rxTn88mwtLD4KMK2bZhP2GApTfuQevtMVWV/4dGLTSdlUqOML2uejfsWfs0/Ar4ZeEvDX7PX\n" +
                        "wj0LTPhr8L/BGn3h0bwxCkK6jruozpAur+MPFeoJBD/wkHizXZLWO41bV54kARLbStMtrLQbLStJ\n" +
                        "h7r4u/tJeDv2PNFm06TWr/xlqHiO+udG+HfgbwXoN74i8a6nrVukk19b6LoWnKZtQNpaL9qvrud4\n" +
                        "NM0+Ah9Qv4POtt/rHjD4cWkHxB0XTvhnLL/wkV6qwavbQE+ZYCUmOVGj3M0SgYYg4AGTya7bXP2c\n" +
                        "NG/ZW+IcP7cviDxx4fg0r4Sfs5/FXS/HnhjxDZB7rWHiSXxlZHwvrM921voWoanqmk6bDqV9Dbtq\n" +
                        "U8OladolvFdw6ncta/leQZHnGcY7FUptU8bh8XWlmPEca1SpOrCvGm40aUHL2dStGN17OSdOhJRd\n" +
                        "lODZ+V5Bkeb5xjcVSnJU8Zh8VWlmPEaqzqTqwrxi1Rowc+SdZJW9nJclB8suVypo8S/ZO8afDr9r\n" +
                        "/wAVW/hadfiTb6lHol1438SeHtf8D3PhyHw3DBf2tjF/bWqSXUkCXWsXVyg0iCwe+S4WG9LTxGCe\n" +
                        "v1/8K/Bn4d+Dgn9kaDCzoFAlvQl5ISpJyWmiJyT26DLDnJNfi1+xn+1jpHwq/Y++PX7dnxNuF+JP\n" +
                        "jT4x/HePR7JdO1OGzuvF8mn6Xptvofh+w1Oa2vYdK0vw/wDaPGt1Z2MUNzZ6XoGkyWen27SJHZj6\n" +
                        "Jsv+Cs/geHxx8IfCvib4PeLtH0f4k+DrbxRd+LbS81HU4ba91BNctLDR/APh9PCUGsfE+zfxPol3\n" +
                        "4FXxDaR6FDqniO2v5/DmmalpCWV3cfr+VcLZLlEU6GEhWxFo8+MxK9tiZyjf3vaVOZwV9VGFoxvJ\n" +
                        "JJM/Xcr4XyXKYxdHCQrYhKPPjMSvbYmco83vOdTmcdW5KMbRi3Ky2v8ArCmm6bGNsen2SDsEtLdR\n" +
                        "6dFjA/z1PWlbTtOcYews2B6hrWBgevYxn/JPOck/Bv7OX7fvg347SftGXXibwLq/wc0D9nl7GfxH\n" +
                        "rXi/WbW/Y6XPL4qt72TXLTTtPSDw7rOm3Hha9hk0G11DXXvHYJpt9c3EckB838Sf8FMrXwp8K4f2\n" +
                        "gNW/Z/8AF0fwP8UeKNT8E/CvxT/wmHh6Lxp428QWVlq9zbahqXgG6srceFvBuoS+H9dtIvEUXiXX\n" +
                        "dThmsUMvhjZd2rN73s6drckLduVW6dPkvw1dtff5IWtyRt25Vb7rf13b1PvjxZ8HPh141hlg13w9\n" +
                        "ayJKMP8AZY4rVicONwMUPX5iecjJHAxz8leMf2UPEHhSQa78Jtfe1hsJXuP7Al82ae8QtIBArtEQ\n" +
                        "fl4A3ds4xyey8f8A7Xlz4MuvgV8ONK+Fl14p/aK+N/h/RPEFj8FbXxbDYWfgyxudLbUPEWoeM/HU\n" +
                        "3h65On6H4ba01i2XU4PC093qr6RqksGkQRQTMvOfCf8Abs8P+PNT/aQi8beBZfhn4f8A2W4rux+K\n" +
                        "3i278VQ+ItIHiqw13xRo17o3hSOz0DT7vxDYOfCuoTaZqLRWOr6lPNpWnxeF0ur6DPhZnwvkma3l\n" +
                        "XwcKWI0cMZhV7DFQkm3GSq00pSSkoycJNwbSTTu2eDmfDGS5qpSr4SFLEbwxmG/cYqnJPmi41aaU\n" +
                        "mk0nySvB6qSd2385+E7/AMK23irxJZfETw9c6d49j028ltbjUmaSCYqHIs4YpgER3xhCOnOOgz87\n" +
                        "/GL9m7Qvil8IviB4d+MvhPT/AB58Kfitp8ej+MPhDq9xqttYeJNAh1a01ZIJLnQ7/StS0+5tL7TL\n" +
                        "HUtO1LTNQtNSsNQtrS9sLyG6himH0jdfHHSf2n/ip4C+Euu/ADWPhxqHxf8AhrqPxc+D/j8+M9D1\n" +
                        "zxDeeEorfV7zRNV+IXhbR9OA8CWniO00iSfTDL4i1hJJbzSbCWRNQuLm3h5vxL4t8Y+BfDus/CnW\n" +
                        "9Pe58U28gtYLu8j3R3EbvLGsmlMwZnjCD52U8HcMkkmvyDPcjr8O1IPG1ZqjR+s4jKM3wVN0a1TH\n" +
                        "OcZU6eL9koSeNioNUp83JV5pOak42f5FnuSV+HqkXjqk1Rw6xOIyjN8DTdKtPHuSlThi40nFyxkV\n" +
                        "FqlJv2dTmm5RbhHm/wAyH/grl/wS38S/8E7fivp+s+ELnXPGH7OPxNle4+G3jvVdPtLK+0bXnW9v\n" +
                        "NX+GPiWGyubqBNb8MpC62F+siDXtCSz1w2djPcTabH+QFf6yX7RP7Jfhz9oL4A/EX4K/HTQ9I8Rf\n" +
                        "D/4n6Hf2V3I2nPqWq+AvEkunajb+G/iRoaw3mn3llr/g7ULtdRsmtNRsf7StBqHhvVJ5fD+q6zYz\n" +
                        "f5gf7YP7Lvj/APYx/aS+K37N/wAR0efXfht4jl0m316PTtS0/SvFuhXFrb6n4c8Y+Ho9TtrS7m8P\n" +
                        "+KtDvdO13RLqa3iluNLvrS4kt4XkaFf0rgniapn+AlQzCnKhm2Cp0vrVOcOR16U+ZUsZCK0Sqctq\n" +
                        "sY6U6t42UZRS/SOCuJaufYCVHMKcqGa4OnS+s05xUHiKU3ONPGU4rTlqOKVSMbKFXmXKoyjb5poo\n" +
                        "or7U+1P7Kv8Ag2A/ZK0Bfh78ef2yfGnwxt7vxrP460D4N/s4fEm61vVLbUvDtu3gbxS3x9h03w3Z\n" +
                        "65bWl1aa9ovjr4eWCa9r+hXsJC6jY+E9ThuofFUZ/td1Dw3pfgH4d2V4viK40XV1iS50rUbKHzpr\n" +
                        "3VgHMWmOARtjLfxZyPm7DFfHH7FP7Hll+yP8I/gr+xdonxBk+J2gfs7eGtS0Kz8YzeGI/B8viu98\n" +
                        "QeM/FPjfVtXHhiLxB4mi0RptR8T3Ya2XW9QZtjXD3bSOwr7RHhjWdc+Kfh74dweJLLXfDthf22r3\n" +
                        "ukJFvubP5/3iSyOGGI+h7HOM5Xn8EzjGVc3zrNcfChKvGpiaeQ5RLnfLSlTqunKrCKq0qijUqxqV\n" +
                        "pSpSdWD5W4ODlf8AB84xdXNs4zbHwoyrxqYinkWUyVT3aUoVZU3WhGNWlU5alWM67lSk6kGovk5X\n" +
                        "K/0r+zh8M5/CfhDVPil4mNtN418S2U+owalqcc7x2jPHcCATxI0Uphd40M6QSJK0W8ROsp82vxx/\n" +
                        "a71H9vT9qX9lm4/Zb8TfCDULrxl4n8a61pHxL+Or698PtM+FFh8KrzxhLqcWoaXa+HdYm17UZP7J\n" +
                        "t28NRaCnhqPXIPD72+p6gZ9fkktD/RR4otNP0nwdc2UVo72dnbhILWE4bZGkxCjAHy9MgdiOPl5+\n" +
                        "RIdT8N6Bpms6xp8RbxJ4kJtI9EmdZQDHK8alLZmIUnCsxCEk4zkEk/s+S5XQybLcNl9BL91BOrU3\n" +
                        "lXxEnOVavUla851JvmcpatOKbunf9lybK6GTZbhsvoLSlBe1qPWdevJzlWr1JPWU6k/ecpXdmlq0\n" +
                        "7/l9b/8ABP8A8W2f7IP7J/7M/wAFNEvfFfhD4e/FC51Dx7cTatofhsz6ZffbrnX/ABnfw63q9uI5\n" +
                        "b251rXZdOsbWTUr+0t7hNMtRdFFZ/pbxF8BPiv4i/wCChnij4sx+DNL8MeBvh/8ADO18H/s9Xt5f\n" +
                        "eFnsrnxBo/hrSdE0w6f4Ys7691TTNI0261nxbf2F/Jo0FpaR29gLMNdsmP0J8I/DDxnbra6xL4jt\n" +
                        "4U1G0t3n0+GF4Vt45VLPEVVAhkQNgkZII5JbIrL8P+ALDRPie/irXvGceoXmlebb6bpQlkLQxT+Y\n" +
                        "HDqx2kgYIPUHcCCd1eoemfjF8K/2Gv25z+yD8cfhFqHgrQ/BOu+MfFun+NPEU+ueK/D2r+N/jPNo\n" +
                        "Fx4fOi+A7WfT9Xn0Twlouj6pZeJfFk/ifWdZlufEus3miaRZ2dl4ffVteuOt+LX7Mf7dvxJ+A37J\n" +
                        "2lXvwY8M29j8Drnwj4Vs/ghp3ifw/cXniBNO0uwt7n4l/EPVJ9csdE0+PXxplrpV1oNne3d9of8A\n" +
                        "aXiPV9TvLVL66gh/c/Wfiz4E0C9t7DUtbggmnYKFwx2csOSOOx9+vXPNJPi/4YvE1G60d31XTdMt\n" +
                        "pZ7rUrYkW8bxBt0B3KTv+X9TwQpyAfij8brb9qj9m79qfTf2s/HGpfBi+8SeNvhZ4ksLjSjdavrV\n" +
                        "p4CgsLKWO60rwt4I026/4Tvxfb+CtGi0u71TxVptjaeGry8m12TxVq/hvS9RTU28g/Z7/Zx/ar+P\n" +
                        "f7DXxsPhK38N2Nn8ZfH1z8R73Wdf1mVfF3xnu/Cmsaatt4S021a3stD8K6XF4n07xLrM/ifWdSc6\n" +
                        "z4jTRtEisNK0KPVvEL/fH7SPh9Pijrmr+Jda+Kt38NPhn8XPA/hTwL46jg8G6frXi298F+FPEnib\n" +
                        "Xrjw94F8Z3muQJ4H07xnNroj8X27+HtaTUZdM0idla3F5Yybuk/HtYrKz+DnwVsoPCvgXw54e0rw\n" +
                        "34Ou0hma2gsNNhS1t4hO4Ms07wQB7m6mdri6uHmurmWS4lkloAo/sVfDn9pXw1pHgjR9Y/Z38IfA\n" +
                        "G70aHw1p/wAWvjX4l8Y2/wASvif8YtB8JmG1t/Ceg6Rc3mr6l4RsdYi0+2tby41PxNL4X8PafJdz\n" +
                        "/Djw1aA6RbWn1r+0v8LY/E2hp440exW48T+FozLZoqENPbj7Q84fap3FVBI3Z4wM5xu9o+GerXWp\n" +
                        "+EtMj1GYT6lZW0VteXHOLmZNytOMnOH25H1PdST3VxAlxbz28iqyTxSROrAFSrq6nIIIIII6+4wc\n" +
                        "c+dmmW4fNsvxOAxMU4Voe5OycqVWLnKlWg3e06U+WcX3dndI8/NMtoZrgMRgcQk41oe5PlTlSqxc\n" +
                        "pUq0OZNKdKahOL7t/wB5v8XPOn8Z6dqN/b6hNpllrkZ0CZFjBc3tqZEuI/LOSAHB+ZeevcZP8m//\n" +
                        "AAc5fsZ3HjT4E/DP9r2DxLqlz40/Z9utC+B+teDY9BN7Frnwv8deJvE+taH4rOuR6iLjTJ/CPi27\n" +
                        "/sj+yW0i5g1SHxcLt9VsJtLitL3+xDxX4OvfCvxk1PwHYQA6ajjWdGMahIY76/kkkZ2UKqkd9uM4\n" +
                        "LZ6Enxv4q+CtP07V/GXgvxprNr4n0P4p/Dzxp4B8XWNrGFiHh3xj4c1LQtb0m4jZGjcz6ffTwssi\n" +
                        "ujB2Dowzn8Jy3E4rh3NqWZ1oVF9Sx8sqzWfOo0nTq1JQrpU5z1ftn7aj7KMuVOLlJKTPwrLcTiuH\n" +
                        "s2o5nXjUvgsdLK81m5KNKVOtVlDEWpym1rWXtqPs4y5UuabV9f8AHMooor+hz+hT/X3h+J+t+GvE\n" +
                        "+r63pX2drq9mZPNuGCuBGzoNhYE8heRj06gEn7M/Yz1C78f/ABB8QePdasbO3vl0v7AptUURyENK\n" +
                        "rSsVAHmEJySMnJOScmvj34cav8PtGm8UL8QtNi1Jp43TRvOBPkXALqSuM4O7PJ75JJODX2V+wI8D\n" +
                        "f8JeYAFhkvbl7RMfctTNOY1GecALx65GcFef5n4GdavxFksa2PhXo1cVicV9RUm54evRVTlqVYuP\n" +
                        "LGVRz54NSbkrttNu/wDM/BEq9biPIoVsfCtRq4jG4p4BSblh8RRcuSrWjKGkqjm6lNxk7p+9ezPu\n" +
                        "z4ma1J4e8J3uqwwC6kikggSEpuVjM0q5ZcHjIXPtuzgkk/kf4u1yWH9pDQdRvJ722KX0E0unwnbp\n" +
                        "cSyTKVJTOFJGQ3A6nPABP7E+MLWC78P3kFwiyRkxthgMbgZCrc55BXI+nfGT8DeK/h1oPiPxTqUs\n" +
                        "D2ba1biN23TxLOojd2jCJnex+ULheeTzxmv6YP6YPs/xf4vbQfh0+vQI/mTacPIMYOUZ43COOCR/\n" +
                        "eBPqOeSap+ANC0rWPBOm6zexLLf61ardXN7Km65SRpplIR87hwmMcfePTGa+J/iR8TPiXrng/SPA\n" +
                        "n9gR6CItSg0X7fBdLJdXNrF+6S5e23l0QqoJYIB82SSdxr8Nf2mv2zv2sNP+Kf7TeofBrx14k0H4\n" +
                        "c/sj+AvEWi+JtSi1nUX0fw3eeHLS48P6rdaD4Re8l8LeIPiX4412PxNoPhTUPEWmz2egx3KaxLDd\n" +
                        "anoOnaXcgH9Z8XgLwg8UYutD07UZEYstzeWkM82STzvdWP8ACehx368mSPwr4X0TSNVtrHQ7G2sr\n" +
                        "iOea7toLdVjmykm/cgzksP1IzkA5/j6+Cv8AwWB+L37Pn/BNnwz41+IvjzXta+LP7THxZ8U2Xwi8\n" +
                        "SeOtZu/Eup+HPB+ladpXh/xd4ntLjxFeXbhbfxXpd5baZa3rPoFjeavH4gW3cte21z9SfDf9qvx1\n" +
                        "8If24v2ofBnxc+KXxM8O/A74Zfsqq0XjPxl8RfE813438deJvD/hG40bxjpq+MvEF3pTeMfEXiC7\n" +
                        "8Tt4RfSrX7Hpk8dp4O0awQxtbsAe8eHfjL4O+JP7b3xC+COjaD8QfFlr8LbfUL/XDf6Xpv8AwrLS\n" +
                        "prPW7HSpPDlgw1241W61Q3V3dq6y6Ja2KR6VqhjvZXj2t+mmsan8GV8Nmewi8J6Br1hDGdJ0q3eG\n" +
                        "2uJ71S6NE1vwdwK4K4zkHgnJP4tf8E1fC/hTUvDutftLX/j3xVZ/Fbxp478YeCdLg1DxK66T4ykT\n" +
                        "T5dQvr/ULK9Rr7xv4zvNTXxpPHdz313NaWltquorCbltTul/Vz4S+Afhd8VNfn1DxN4dFn4o8OXE\n" +
                        "l1BBdPc25v7hWJZ2STaD5rfPjbjg9AAaAPcPhl4l+KereMNN0+XQrbT/AAz/AGQl1cXYhEYll+b5\n" +
                        "Uk2jcXABAz6gkkEn67rB8O3cFxYmCCFbdLBls/IXGyLyt6BFIGCFwozj0zySTvUAfnd+19qsvw+8\n" +
                        "V+FfHlrGTPJdQQSEDmSOAuoVuuR8vGR0zzyDXw/49+Ll78Vb9Gu9I0zTvKkZlurGJEu5cI+DMVRW\n" +
                        "JO35s5IDHgAnP2z/AMFAip8OeEEVgJBqeSe+PNkA75wR/Trya+J/GfiP4fa7pfha08GaDb6Rqdhp\n" +
                        "8cGt3UIy97eJG4klkzgZbaSeuMjk5xX828d1a9PP8/wVPMKdDCc2AxUsFJPmxlarCKqKl7klGcG+\n" +
                        "eo3KF1ypNtXP5u45rV6XEHEGBp4+FDCXwGKlgpx9/F4irBKp7F8kkpU1y1Kjk4qzbUnJs/x96KKK\n" +
                        "/pI/pE/12fG2i/2J4n1XTZYxuhmLYZVyPMLN6d+M+/c/LX1x+wXr8Vh461/w7NII0m0wzxBiQplL\n" +
                        "yOFUZI5GcYyct6kkflh+yf8AtX3/AO3v+yL8B/2ydai+H+neL/ir4fvrL4n+Fvhrd3UvhnwV8TPD\n" +
                        "OtappGu+G003U/EXibXPDkstjb6V4ks9B8R6vd61BoOt6LfyXV1Y3tjqE31X8JvF0/gL4j+H/EEM\n" +
                        "pSGS/tLe/IOF+yBnEhbg8fMCc8cYJ65/lDCSlwpxxB1k4wwGayp1Faylh6lScFPV35ZQcakXuklo\n" +
                        "9WfyrhpS4V42pyrRcYZfmjp1dLc2Gq1JU1NXfwuEozT/AJHfa1/1P+NWpfFXTtfhk8N+MNIXw60c\n" +
                        "kd3oJtWfUDKwYRnzQeACQcHttJOQ1fn1qFh8R9G+I+veNfEGpXVsNMSK605/MmS0u3ZiTH5QYbwB\n" +
                        "xt7cE5OK/UzTviH4D8UarbWtvYW15NqipNDcyRhlCL8pO7b1yRjke2SGrgf2hrP4T+EvCyeLvH3i\n" +
                        "zwt4O0BLuG1m1XxVrWm6LozTymVreziutSu7aGe7uAjm3s4Xe6mKsIYnZWB/q6LjKMZRalGUYyjJ\n" +
                        "O6lGSTjJO+qkrNO7umnfv/VMZRnGM4tSjKMZRknpKMknGSfVSVmn2ad31/PbxX8UruXXdI+IP9h6\n" +
                        "lZ3E5g0eVp3lawuXZgpuILcDCSOeVk6gkZYkHPgfhn4A/D/xH4B/bU8J38PjTV9H/al8SPq3xNhv\n" +
                        "b3QzqGmtL4k8UeJrWXwPJF4Zt102DRNR8T3l3bnWf7clne3sVvZp4opI2/SXRfiR+yV8QU0HwP4I\n" +
                        "+JHw38f+KLy1vL+w8MeGdd07XdRWLSIFub7UZbLTp7qWwtoImjZbu9ENvO5EVvLLODGe2s/gjoFx\n" +
                        "purW1tNc6ZH4iV2u54FMc1uWWSMxxENwjLgY+vfeaYz8Nn/YV/4Jz6d+znB+zd4ztvGfivStCu7T\n" +
                        "UvAnxH1DxPFefEvw1rdodcaPSdMv5tIk0LS/Ctjd69q1wnhGy0FNEmvL6XUtQt7rWEh1Memw/B39\n" +
                        "krWtJ8PeGvi34Qtvi/4lsYli074ifFWOy8Z/FvUFOgweG7O7l8YapppubG203R7KztdHsNJjstP0\n" +
                        "yS2h1O1tv7bNxqb/AKC2H7Bvw+TU7tRq91qUjSyXKRXGH8qSSR5C4XzDgs2CT3LEnBXmew/ZR8G6\n" +
                        "74iTQruKWLUtMKvDqCxYK2cRfCLJv4OFPy57DqAcgHzH+yN+zB+zn+yB4zPizSL3W9e1nVW1S88M\n" +
                        "3fxH1e116bwvbakjRagng6xs9K0nTNGm1Gzjgs9Y1gWMmv6jaQ/ZLnVPsU1zaP8AWPiX44eCNN8T\n" +
                        "nxp/aVhrE8TKl3pfh+JrOUW8cjqgcRjl8AbmOSSxzyGI625/Zs+GumapFN4n8S6hK9niPS0nk3Kk\n" +
                        "JJDooLttBCdPQtkA1qXX7O/ws1KLUn0G1LLLbRrPNFB8/BYllYMMsSoJzz8w5JyaAPoH4S/GHwp8\n" +
                        "Q7eP/hHdPuLVZ4xO4lZmKOzSExyFhkuOpOT9M17rkZxkZ7DPPcdM+3v36kEn4s/Zq+IPwm1TS/Gf\n" +
                        "hv4c674Q8RXngHU4tI8SzeHNZ0nWLnRdRka9WCx1p9Mu7pbC9drK7V7S5ZLmN7edJY1dXWu2k+Jl\n" +
                        "3Jr1zEEvY4LKKd57p0K2KmJJnA83bgA7Rgdyw7DlNpJttJJNtvRJK923fRJK7fRdbpsTaSbbSSTb\n" +
                        "b0SSvdt30SSu30XW6bPk79vfX0n1nw/4dRw0lrLBcyR5OUWVmIYjpgk59QSM5JGfg6wj/wBJjWJB\n" +
                        "uO/AUYJwkvoM9AfXjHJzXo/xq8eTfEr4l634hkbdbxZ0+BQQUBtJmTcowOCOhx0I5IBB8g8TfFj4\n" +
                        "Z/AL4dfE/wCOHxc8VeFvCfhD4a+AvFvicXXi7xFpPhyy1/xDpvh/Vb3w/wCCNFu9WvrJNT8V+M7+\n" +
                        "0j0Lwv4e09rjWtc1i6ttL0exutQmigb+Tc6rT4m4xxf1K9T65j44bCtRd5U6cnSpys29HGHP2Sbv\n" +
                        "fVn8pZxVnxLxfjHg71PrmPjhcLZX56VOUqUJrV7xXM+iSWtrt/5L9FFFf1mf1cf1ef8ABsR+2D4Q\n" +
                        "8L+M/jJ+wV4ztbyHVfjxfwfFj4LeIm1e0g0W2+IvgLwlqVt4n8E3el3jxtJqPxA8KwWt1o2o6dL9\n" +
                        "rOp+ErPw9Np2oPrFhdWP9edxHJF5sLLIsyMyFFRzIkgZ1Hyr82QVP4+u3Nf5Ofg/xh4r+Hnivw74\n" +
                        "68C+ItY8J+L/AAnrGn+IPDPifw/qFzpmt6FremXKXenappeoWksVxaXlpcRpLDNFIrBgMkrkV/qK\n" +
                        "/wDBLj9uv4Uf8FBPgb4N/aXt7HR9N8W2ktp4O/aG+F1hrFhqF94A+JsUl3DFrh0tVt7/AE7wb8To\n" +
                        "LK48U+Cbm6shYqo1/wAI2mratqvhbX7+vxbxM4TeKxOHz3DONOFSVHCZnNxco0ldQoYyaV3yRSVK\n" +
                        "pbSPuybd5N/jHiXwrLE4jD55hnGnCpKlhczqSjJxox5nTo4yaWrhGK9nU25f3Tu3Jt/r5+xt4w8M\n" +
                        "eIHu/COvTm18SWsbf2PJcgxvJbrIu/Y0rAlsYAUcklRyRz8a/Gn4g/CDXP2kP2vLj9sT4M/Ebxz4\n" +
                        "F/Zr+HNhrvwngvYfFWn/AA5PhCw8P3OteKLnRrzS9U0jT9R8dePdcC/2bcXNxcxXGi6ZDpheym0O\n" +
                        "/Z/oL442Fn4Q8PXfxR+HGlXa6zoqfbdN07SpLSzu9Wa3gnuY9Ptpbi4tLVZb6WFIImu7q3tFklU3\n" +
                        "NxFD5kw/MTWf+CjPw/8A+CjH7I2mfDXxj4f8Z+E/2uIvFureEY/2X/Dya7Jd6jqy+MLS203WfHtg\n" +
                        "PDtvq+r6X4c8EWst/DPqA0jw03jHUrk32nO2n2Ztva4P4njg50uF87xdN4uhSgstx17UcbhbzjSp\n" +
                        "ylJpxrQUeWLlpOHKlLmg0/Y4Q4njg5UuFs8xlOWMoUaay7Gu8KONwl6ipU5Sm/drU1G0eZ2nDl95\n" +
                        "SWvm/wCwd+1n8Fr258Z/GfRfD3w0+EHjew0nx/4n8T6NoEHjeOz8DfAjw94w8OaHZ6b4h8S+MNe1\n" +
                        "yxt59Q1i70m5ZbfULHU/ECXWjSjRXisUlH7G/sv/APBTf4HftW/EC0+F3h/TfFnhrV38JP4z0S71\n" +
                        "3wJ4s8O2fizwqObfxDo7a1YWlythqFsRqWlyaraae+p6VJBqOmpcWk8Ez/LP7Wv7HWofs+f8E2vh\n" +
                        "J8PvBnw40a61+Dxbo+uftDa14H8IadPf6us+meOfEbHxjrGk6c2oeIPC3hDWr6w0iy1XVpZdMgj0\n" +
                        "vw1Pi1SLTIo+08C+A/EV98bfjn+2D+yp8A/EvjTwH49+C+leA/hfd3qaB8OrXwd4itvA/gXS9a1P\n" +
                        "TNF8U6hp/ifxhoXg6y8MLpGhz+GdGvLXWfEC33hXRXbT7KHxQv6f/X5+b7fnq7O/6f8A1+fm+356\n" +
                        "uzv73p3/AAUy/Zgkufjj4l8J6rd634L/AGf4w3xW+IP/AAi/iyHQNM8uTW0uINKln0qO58Uz240O\n" +
                        "8kb/AIRq01GC4RtPOnz3Q1DTPN9ck/by/Zch+AHhj9rCbxLqGh/DPx1qVxoHhLWrzwl4mh1TxTqk\n" +
                        "V1rdpJDpHh1dMfXTFJNoOr7Li+srWJIbG5up2itjHO/4i+Eo7zTf+CYXxO+GPw5+Cfi29bVviVJ4\n" +
                        "r/aE+Iuo+FRZeG9J8O2uueGrHRdE0jVppWvfEl1cXGh6Q+vWlnbraeG9Oh8TSeIYYIdQtry59e+I\n" +
                        "GgXHx5j/AOCaX7OWnfCLx/8ADX4N6bBoXh+a51vTRo3iPxZeRT+ANM+JPje20Cxkv20bRdPkn1J/\n" +
                        "DPiq9W3XUNR1nX9QitINKS2LgH6T+Jv2if2f/jN8ZfDHwR8MfENo/immn6hd3Phiy0vV5oI7vSNO\n" +
                        "bWdW8ParrcFrJosPiTTNPi1CTUdGg1C4m06fTdU07Unt9TgSzc+JX7dv7PnwFsfiv4c0fUfE3jLx\n" +
                        "f8L/AAdc3vjKHwf4G8UeKfDXgjXJ7O4tvDOm+OvFGl6Zc6J4eu9e1xLfSY7e7umksrySZdaSxht7\n" +
                        "uZfg34UWuo+F/wBvT9qg/Dr4I6/ffEDwFofiT4Tfs7+C/D/h6O28G+D9L04ReDNL+IninXNQSGz0\n" +
                        "bR7fwloSa6Li9llvvG+qeI7uxgnvfEGqpfP4f8Pdc8WWH/BPP9o/4Y+Cvgx8Qb/x/rnxAu/Ev7Qf\n" +
                        "xE1PwreRWGkeD7LWvBtq+mPrNzH9u1TxJNeC4GqeGpIHudI0CT4h+K9Re2gQzsAfo/8A8EhfAln4\n" +
                        "f/Y4bx7rbPFqHxo+I/ibxW93JGWu7zS9Juo/CFlBcuV8xlOqaJr1+skpLudSll3mJoyfpf8AaY+I\n" +
                        "EVr4bvPhz8NreO913U0Y6mbYr51isaXAZcxtvJdecYz2Br5O/Z5/a+8O+DP2V/h/4G8N/DjxDoNn\n" +
                        "4U+Gfw+0DQ9X8SwRaff+KPHEml3138SdU0fQADcw+DrDWXsm8O+JLpoz4jkvdWFtZxwaYl7c2fhH\n" +
                        "D4iudd1P4j6/qcNzrGtO81no82XnvImdsyW8bn5UiTG4Z6Buo6/mXF3FdCpOfDOV13UxOKUqWZ4u\n" +
                        "hODWBwjUlUhCTfLPEVPg5E3KKnZJ1ZKJ+ZcW8V4epUnwzldeVTE4qMqeaYvDyg1l+DampxjJvlli\n" +
                        "K3NyRgm5JNpe/JX+WY0kieaCaOVLlJWFwsqSKxn3sJD86hjluRwc88tjj+UL/g5f/be02Wy+Gv8A\n" +
                        "wT8+G3iTStUh0TUY/ix+0cdOltrqaz8bLA0Pww+H93K+mRXVheeFND1DVvE+uRWGq3mk3934r0bT\n" +
                        "NVtLfxL4Tnii/qD/AOCqX7aPwX/YL/Z81/8AaC8f3sN/4uvjr/hT4U+FtA03S7+48WfFu58MeI73\n" +
                        "wjaalo91rekT/wDCF6FrNppsnxE1+1a6bRdLurWOOzutW1DRtNuP8t742/GT4g/tEfGD4kfHT4q6\n" +
                        "vDrnxD+Kni3WfG3jPVrbT7PS7S81/W7yW8vpLLStPihsNKsVkk8qx0zT4YbDT7NILKxt4rSKKJfB\n" +
                        "8N+D5YTMcVnWKcatHDc1DLZtL97UlzxrYjkvJJ0o/u788rVW3Ftxbfh+HPCEsLmOJzjFctWhhuah\n" +
                        "lk7Je1qSTVau4KU0nSi/ZpqTXtfaWbcFJ+XUUUV+1n7SFfZv7CX7dPx0/wCCe/x60T44/BPVlP8A\n" +
                        "qNJ+IXgLUn/4pL4qeA2uop9V8CeL4PKmc6bfmNLmx1G0CavoGtwab4l0K6ttc0+wvE+MqKipSp1q\n" +
                        "c6NWEalKpGUKlOcVKE4STUoyi9Gmm7p992yKlKnWpzo1YRqUqkZQqU5xUoThJNSjKL0aabun33bP\n" +
                        "9Yj9iP8Ab1/Z6/4KP/BXQH+BHxL8NySeIgbfXfhh4k12ysfi78OfFGmaVBqviPwtqWg6idN1PxFp\n" +
                        "2kwTm6s/GXh/T7nw7rGjbNRjntLiPUtOt/oTx9pd14Vbw94L8PKra5ptyzvqmlEzXspZnK7nUknB\n" +
                        "Cn2wxI5Jr/I4+Enxc+JfwG+JPhD4vfB/xjrHgT4h+BtYtdd8K+K9CnEGo6VqdpJvimj3rJDPE/Md\n" +
                        "xaXUctrcwFoLmGSJmWv7zP8Agm1/wcLfsvftFWuj6R+1Z4q0T9mv9o7QPCWjw634/wDHl54a8Ofs\n" +
                        "+/FbxHbyWmnXus6JrKX8a/DnxHqzSyatquga5plj4Gto4b670fxLYxTad4Sg/HeKuAqtKj9ZyaOI\n" +
                        "xMXOFOsuf2uLwWAjVlVVPB02k68YSg+Rc/t4p8t5xTa/HuKuA6kKMsRlEcRiIzlCjWXP7TF4PARq\n" +
                        "yqung4WUqyhJL2fv/WIqSipSjFs/pt+FX7Ufxa+H0kXhrxZo154g0+FUaW8kgea/hgjMh52jcCAV\n" +
                        "BH0yCVyfufwP+1P8K/HE8On2eoT2mqOFWSyu4hHJHLkqysCwYfNjgrkDuck18bfDTx54Uv8Aw5Hr\n" +
                        "M+u6Rrw8WabHrWh+LtEuNN1nw3rWlX8bT2lxoeu6dNd6Zqum3UTxy2d9Y3M1tPEyywyujBq8p+Je\n" +
                        "ieHPBmiv4i0BEt/FmvanYy2WoQXO5o43uP3n7qNlAyCDg5xg56EnzMv4g4g4ewUZRzOGY4WgpOvg\n" +
                        "8yp2xeEpU5qMMPGqqspqvNqyhUTi5TV5pRTPLwHEHEHDuCjNZpDMsJh4zlicHmNNrGYWjTdo0KdS\n" +
                        "E3P6xJtR9nWXLd29reN3+xDeN/CUT+XceINLtn2qwS4vIYWZW3bSBIy5ztPcnIPBFU7n4j+A7WN5\n" +
                        "JfFuhYjGWC6lbM2MkHhHbpjJz0yOeCa/I3xX4E8R/ETU/DOi2upzRX1tb2lxrOpJeSoUgmiR1LKs\n" +
                        "rZPzNnjPIBJxmuovP2dvDV9NaaTa+KJ7S6sI1uNUuWvZwLuKJm81DvfG6RUPAwRleQa+jXHmfV3W\n" +
                        "+p8P4WpTpyhThUrY+VF1akkrwhF0mpSpv47S5FaS5nK6X0a47z+tLELCcPYadOlKnTpVa2PlR9vU\n" +
                        "lFSnCEHSd5UlpU97kupWk3dL7r8XftW/CLwlbzSXGsNeyoreVHZKswlfD7QrK7cEqP4T29Wr4f8A\n" +
                        "iv8Ata/E3xro95/whOkXOj+G1Zt2rxwvHe+V5jBWZmBwGUggjjr3JrnfEPws8JQ6VqUKackMFviX\n" +
                        "T9aa+Mj7YBIzIImY581k5GCCWPcE1k+FPEVn/wAIV4g1TXddtNL8L6JpupXmvWWqfY9O0XTND0S0\n" +
                        "nu9R1rWNavXgs9K02ys7WS+1DUby5htLSzinurmeOCOWWvls14k4ozWq8tr42nl1PEUZ+zp5ZCXt\n" +
                        "akoykpRqVqso1JU+bkf7hc84ySpczUz5fNeJOKc0rSy2tjaeW08RRn7KGWQl7apKMpRlGpWrShNw\n" +
                        "v7N/uYuU4zXs1KSmnU8MfCDRde8Ov4i8Y6nq954i1pVltJ41aTCT71SSZh90D7zHpgnJGGz8u/tg\n" +
                        "/tk/BL9gf4Dax8U/2gfEEuh2/ghrnRPhzpmmSKvin4o+IDbyTp4X8NQlJZHkYPAdY1toZNN0G1mi\n" +
                        "mu/Ov7nStLufye/bU/4OSv2Wf2VtBv8AwH+zrqsH7VPxF1fSPE8FhrPgPXbXTvhl8MNYEGoaf4dj\n" +
                        "1zW9U0e8k8cPpl+INX1DQ/D1tDpOpacINOt/GcN9NqZsv4X/ANrP9sX9of8Abg+LurfG39pDx03j\n" +
                        "LxtqtppOnsbHSdK8N+G9N07RdNh0nS7PRPCnh+1sNC0eO3sLeKKeaysY7zU7jztU1m6vdZutQ1Cb\n" +
                        "2uHOA/rMMLicbhqmXUVSgsTTqJrG4/3udqs5TlOlerFVVVlyV4vlhGCvKoe1w5wIq8MLicZhqmX0\n" +
                        "Y04rE06ilHG5haUZ/vm6jnTftYe19tLkrK0Ixg+Z1F7b/wAFF/8AgpJ8f/8AgpL8Y2+IfxXvz4e8\n" +
                        "EaAzw/C/4LeH9X8Rz/Df4b202l6NpOsal4d0fXNb1k23ibxwug6brHj3W45lk1zXUVo4rXRrPRdI\n" +
                        "tvz1oor9go0aWHpU6FCnGlSpRjCnThFRhCEU1GMUnold+d3dtvU/X6NKlh6VOhQpxpUqUIwp04JK\n" +
                        "EIRTUYxS2SXz2bbaCiiitDQKKKKACiiigD+yb/g0E+K/xV8VftSftOfCDxR8TPH3iP4V+GP2StQ8\n" +
                        "QeGfhhr3jPxLq/w68PeILb42fCHTLbXdC8Fahqlz4d0jV7fTtX1TT4NTsNNgvorDUdSs47gW93dx\n" +
                        "yf17fEmKL+14E8uPZHdRbF2LtTE4I2LghcEAjHfHORklFfi3ijGKlSaik5U6fM0km37Spq3u9lu3\n" +
                        "01dtfxnxPjFSpNRinKnHmairyaqU7N9/m36s7PwTNMPF8xEsgJs7ZSRI4JURyYB+boMDAyQOOuOe\n" +
                        "x+IkkkOmwSRSPFI2pqjSRuyOyFpMozKwYqcDKkkdM5xyUV8bhW/7Hxur0qVLav8A5+U/P9fm3qfH\n" +
                        "4dv+yMdq9J1ravR81PVa6f8ABerd2+PgJn1rTYJyZoTsBhlJkiILSZHluWXB7jH1Jr+XT/g8W8Q+\n" +
                        "IPB3ww/4Jo6V4R13WPC+m+Jr79rk+JdO8O6nfaLYeIG0yy/ZiXTDrtnptxbQaudNXU9RFgb+Oc2g\n" +
                        "vr8WxQXNyXKK+18PEnmmKk0m0p2bSbX7vo3dr7z7Tw9jGWZ4pySk4wnyuSTa/d09m22v+C/O/wDB\n" +
                        "1RRRX7MfsQUUUUAFFFFAH//Z\n";
                if(m.equalsIgnoreCase(modifieddefaultImage)){
                    mModifyProductDetails.put("img",defaultImage);
                }else{
                    mModifyProductDetails.put("img", getStringImage(bitmap));
                }

            }else {
                mModifyProductDetails.put("img", getStringImage(bitmap));
            }


            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.MODIFYYOURPRODUCTSAVE;
            establishConnection(Request.Method.POST, Constants.mModifyProductUrl, mModifyProductDetails);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void volleyOnSuccess() {
        dismissAlert();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    try {
                        String responseType = (String) extras.get("response");
                        if (mServiceCallEnum == ServiceCallEnum.MODIFYYOURPRODUCTSAVE) {
                            HandleModifyProductSave(responseType);
                        } else if (mServiceCallEnum == ServiceCallEnum.MODIFYYOURPRODUCTBARTER) {
                            HandleModifyBarter(responseType);
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void HandleModifyBarter(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
            String mStatus = mJson.getString("status");
            if (mStatus.equalsIgnoreCase("true")) {
                showSuccess("Success", "Product Added to barter");
            } else {
                showMessage("Sorry", "Your have already added this product to barter");
            }
        } catch (Exception e) {

        }

    }

    private void HandleModifyProductSave(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJSonArray = new JSONArray(mString);
            JSONObject mJson = new JSONObject(String.valueOf(mJSonArray.get(0)));
            String Status = mJson.getString("status");
            if (Status.equalsIgnoreCase("true")) {
                showSuccess("Success", "Product modified succesfully");
            } else {
                showMessage("Sorry", "Could not modify your product");
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void volleyOnError() {
        dismissAlert();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    try {
                        String responseType = (String) extras.get("response");
                        showWarning("Something went wrong","Please check for your network");
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    @Override
    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public void establishConnection(int method, String url, JSONObject jsonRequest) {
        mQueue = CustomVolleyRequestQueue.getInstance(this)
                .getRequestQueue();
        CustomRequest = new CustomJSONObjectRequest(method, url,
                jsonRequest, this, this);
        CustomRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CustomRequest.setTag(REQUEST_TAG);
        mQueue.add(CustomRequest);
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        String mErrorMessage;
        NetworkResponse response = volleyError.networkResponse;
        if (response != null && response.data != null) {
            mErrorMessage = String.valueOf(response.statusCode);
            switch (response.statusCode) {

                case HttpURLConnection.HTTP_NOT_FOUND:
                    mErrorMessage = mErrorMessage + " error page not found";
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    mErrorMessage = mErrorMessage + " error internal server error";
                    break;
            }
        } else {
            mErrorMessage = "Unexpected Error";
        }
        getExtras().put(this.getResources().getString(R.string.string_server_response), mErrorMessage);
        volleyOnError();
    }

    @Override
    public void onResponse(Object response) {
        getExtras().put(getResources().getString(R.string.string_server_response), response.toString());
        volleyOnSuccess();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                /**
                 * After cropping the image, get the bitmap of the cropped image and
                 * display it on imageview.
                 */
                if (extras != null) {
                    bitmap = extras.getParcelable("data");

                    imageView.setImageBitmap(bitmap);
                }
                File f = new File(mImageCaptureUri.getPath());
                /**
                 * Delete the temporary image
                 */
                if (f.exists())
                    f.delete();

                break;
        }
    }

    private Bitmap get_Reduce_bitmap_Picture(String selectedImagePath) {
        int ample_size = 9;
        Bitmap bitmap = null;
        BitmapFactory.Options bitoption = new BitmapFactory.Options();
        bitoption.inSampleSize = ample_size;
        Bitmap bitmapPhoto = BitmapFactory.decodeFile(selectedImagePath, bitoption);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(selectedImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        Matrix matrix = new Matrix();

        if ((orientation == 3)) {
            matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else if (orientation == 6) {
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else if (orientation == 8) {
            matrix.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else {
            matrix.postRotate(0);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        }

        return bitmap;

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }


    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);
            mOptions = options;

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);

                return convertView;
            }

            return null;
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        /**
         * Open image crop app by starting an intent
         * �com.android.camera.action.CROP�.
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        /**
         * Check if there is image cropper app installed.
         */
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                intent, 0);

        int size = list.size();

        /**
         * If there is no image cropper app, display warning message
         */
        if (size == 0) {

            Toast.makeText(this, "Can not find image crop app",
                    Toast.LENGTH_SHORT).show();

            return;
        } else {
            /**
             * Specify the image path, crop dimension and scale
             */
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            /**
             * There is posibility when more than one image cropper app exist,
             * so we have to check for it first. If there is only one app, open
             * then app.
             */

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                /**
                 * If there are several app exist, create a custom chooser to
                 * let user selects the app.
                 */
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(
                        getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        CROP_FROM_CAMERA);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null,
                                    null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

}
