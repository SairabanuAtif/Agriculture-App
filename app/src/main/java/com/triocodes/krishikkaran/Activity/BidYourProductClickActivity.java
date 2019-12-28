package com.triocodes.krishikkaran.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.ParentActivity;
import com.triocodes.krishikkaran.R;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BidYourProductClickActivity extends ParentActivity implements Response.Listener, Response.ErrorListener, VolleyCallback, View.OnClickListener  {
    //Bid in your products
    Toolbar mToolbar;
    HashMap<String, Object> extras;
    Handler mHandler;
    TextView mTextProductCode, mTextProductName,  mTextAdd, mTextCancel;
    EditText mEditMinBidAmount, mEditBidClosingDay;
    int ProductId;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    ServiceCallEnum mServiceCallEnum;
    String RegistrationId, RegistrationIdToPass,ProductName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_your_product_click);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbarBidDetails);
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
        ProductName=intent.getStringExtra("ProductName");
        extras = new HashMap<String, Object>();
        mHandler = new Handler();

        RegistrationId = DataBaseQueryHelper.getInstance().getRegisterIdRegister();
        if (RegistrationId == null) {
            RegistrationIdToPass = DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        } else {
            RegistrationIdToPass = RegistrationId;
        }

        mTextProductCode = (TextView) this.findViewById(R.id.text_bid_your_product_product_code);
        mTextProductName = (TextView) this.findViewById(R.id.text_bid_your_product_product_name);
        mTextAdd = (TextView) this.findViewById(R.id.text_button_bid_your_products_add);
        mTextCancel = (TextView) this.findViewById(R.id.text_button_bid_your_products_cancel);

        mEditMinBidAmount = (EditText) this.findViewById(R.id.edittext_bid_your_product_min_bid_amount);
        mEditBidClosingDay = (EditText) this.findViewById(R.id.edittext_bid_your_product_closing_day);
        mEditBidClosingDay.setOnClickListener(this);

        mTextAdd.setOnClickListener(this);
        mTextCancel.setOnClickListener(this);

        bindData();


    }

    private void bindData() {
        mTextProductCode.setText(String.valueOf(ProductId));
        mTextProductName.setText(ProductName);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_button_bid_your_products_add: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {
                        ConnectToDelete();
                    }
                } else {
                    showError("Connectivity Issue", "No internet connection available");
                }
                break;
            }
            case R.id.text_button_bid_your_products_cancel: {
                onBackPressed();
                break;
            }
            case R.id.edittext_bid_your_product_closing_day: {
                super.setDate(R.id.edittext_bid_your_product_closing_day);
                break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }


    private void ConnectToDelete() {
        JSONObject mBarterDetails = new JSONObject();
        try {

            mBarterDetails.put("ProductID", ProductId);
            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.BIDYOURPRODUCTDELETE;
            establishConnection(Request.Method.POST, Constants.mBidYourProductsDeleteUrl, mBarterDetails);

        } catch (Exception e) {

        }
    }

    private void ConnectToAdd() {
        String mConvertedDate=convertDate(mEditBidClosingDay.getText().toString());
        JSONObject mBarterDetails = new JSONObject();
        try {

            mBarterDetails.put("ProductID", String.valueOf(ProductId));
            mBarterDetails.put("RegistrationID", RegistrationIdToPass);
            mBarterDetails.put("BidClosingDay",mConvertedDate);
            mBarterDetails.put("MinimumBidAmount",mEditMinBidAmount.getText().toString());
            mBarterDetails.put("CreatedBy", "Admin");

            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.BIDYOURPRODUCTADD;
            establishConnection(Request.Method.POST, Constants.mBidYourProductsAddUrl, mBarterDetails);

        } catch (Exception e) {

        }
    }

    private boolean nullChecker() {
        if (mEditMinBidAmount.getText().toString().trim().length() == 0 || mEditMinBidAmount.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_bid_your_product_min_bid_amount);
            return false;
        } else if (mEditBidClosingDay.getText().toString().trim().length() == 0 || mEditBidClosingDay.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_bid_your_product_closing_day);
            return false;
        }
        return true;
    }

    @Override
    public void volleyOnSuccess() {
        dismissAlert();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    try {

                        if (mServiceCallEnum == ServiceCallEnum.BIDYOURPRODUCTDELETE) {
                            String responseType = (String) extras.get("response");
                            handleBidYourProductsDelete(responseType);
                        }
                        else if (mServiceCallEnum == ServiceCallEnum.BIDYOURPRODUCTADD) {
                            String responseType = (String) extras.get("response");
                            handleBidYourProductsAdd(responseType);
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void handleBidYourProductsAdd(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJSonArray = new JSONArray(mString);
            JSONObject mJson = new JSONObject(String.valueOf(mJSonArray.get(0)));
            String Status = mJson.getString("status");
            if (Status.equalsIgnoreCase("true")) {
                showSuccess("Success", "Successfully bidded your product");
                clearData();
            } else {
                showMessage("Sorry", "Could not bid your product");
            }
        } catch (Exception e) {

        }
    }

    private void clearData() {
        mEditMinBidAmount.getText().clear();
        mEditBidClosingDay.getText().clear();

        mEditMinBidAmount.setHintTextColor(Color.parseColor("#398808"));
        mEditBidClosingDay.setHintTextColor(Color.parseColor("#398808"));


    }


    private void handleBidYourProductsDelete(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
            String mStatus = mJson.getString("status");
            if (mStatus.equalsIgnoreCase("true")) {
                ConnectToAdd();
            } else {
                showMessage("Sorry","Issue in process");
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

    public static String convertDate(String s) {
        String targetDateFormat = null;
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date;
        try {
            date = originalFormat.parse(s);

            targetDateFormat = targetFormat.format(date);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return targetDateFormat;
    }
}
