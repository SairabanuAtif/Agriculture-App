package com.triocodes.krishikkaran.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;

public class BidProductClickActivity extends ParentActivity implements
        Response.Listener, Response.ErrorListener, VolleyCallback, View.OnClickListener {

    Toolbar mToolbar;
    HashMap<String, Object> extras;
    Handler mHandler;
    TextView mTextProductCode, mTextProductName, mTextMinBidAmount, mTextAdd, mTextCancel, mTextBidList;
    EditText mEditName, mEditPlace, mEditMobile, mEditAmount;
    ServiceCallEnum mServiceCallEnum;
    int ProductId;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    String RegistrationId, RegistrationIdToPass, ProductName, MinAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_product_click);
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

        RegistrationId = DataBaseQueryHelper.getInstance().getRegisterIdRegister();
        if (RegistrationId == null) {
            RegistrationIdToPass = DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        } else {
            RegistrationIdToPass = RegistrationId;
        }
        Intent intent = getIntent();
        ProductId = intent.getIntExtra("ProductId", 0);
        ProductName = intent.getStringExtra("ProductName");
        MinAmount = intent.getStringExtra("MinAmount");
        extras = new HashMap<String, Object>();
        mHandler = new Handler();
        mTextProductCode = (TextView) this.findViewById(R.id.text_bid_details_product_code);
        mTextProductName = (TextView) this.findViewById(R.id.text_bid_details_product_name);
        mTextMinBidAmount = (TextView) this.findViewById(R.id.text_bid_details_min_bid_amount);
        mTextAdd = (TextView) this.findViewById(R.id.text_button_bid_details_add);
        mTextCancel = (TextView) this.findViewById(R.id.text_button_bid_details_cancel);
        mTextBidList = (TextView) this.findViewById(R.id.text_button_bid_details_bid_list);
        mEditName = (EditText) this.findViewById(R.id.edittext_bid_details_name);
        mEditAmount = (EditText) this.findViewById(R.id.edittext_bid_details_amount);
        mEditMobile = (EditText) this.findViewById(R.id.edittext_bid_details_mobile);
        mEditPlace = (EditText) this.findViewById(R.id.edittext_bid_details_place);

        mTextAdd.setOnClickListener(this);
        mTextCancel.setOnClickListener(this);
        mTextBidList.setOnClickListener(this);

        bindData();

    }

    private void bindData() {
        mTextProductCode.setText(String.valueOf(ProductId));
        mTextProductName.setText(ProductName);
        mTextMinBidAmount.setText(MinAmount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_button_bid_details_add: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {
                        if (mobileValidation(mEditMobile.getText().toString())) {
                            ConnectToServer();
                        } else {
                            showMessage("Content not correct", "Mobile number should be valid");
                        }
                    } else {

                    }
                } else {
                    showError("Connectivity Issue", "No internet connection available");
                }
                break;
            }
            case R.id.text_button_bid_details_bid_list: {
                Intent intent = new Intent(BidProductClickActivity.this, BidListActivity.class);
                intent.putExtra("ProductId", ProductId);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
            }
            case R.id.text_button_bid_details_cancel: {
                onBackPressed();
                break;
            }
        }
    }

    private void clearData() {
        mEditName.getText().clear();
        mEditMobile.getText().clear();
        mEditPlace.getText().clear();
        mEditAmount.getText().clear();

        mEditName.setHintTextColor(Color.parseColor("#398808"));
        mEditMobile.setHintTextColor(Color.parseColor("#398808"));
        mEditPlace.setHintTextColor(Color.parseColor("#398808"));
        mEditAmount.setHintTextColor(Color.parseColor("#398808"));

    }

    private boolean nullChecker() {
        if (mEditName.getText().toString().trim().length() == 0 || mEditName.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_bid_details_name);
            return false;
        } else if (mEditMobile.getText().toString().trim().length() == 0 || mEditMobile.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_bid_details_mobile);
            return false;
        } else if (mEditAmount.getText().toString().trim().length() == 0 || mEditAmount.getText().toString().trim() == "") {
            super.shakeEdittext(R.id.edittext_bid_details_amount);
            return false;
        }

        return true;
    }

    private void ConnectToServer() {
        JSONObject mBidProductDetails = new JSONObject();
        try {
            mBidProductDetails.put("ProductID", ProductId);
            mBidProductDetails.put("name", mEditName.getText().toString());
            mBidProductDetails.put("place", mEditPlace.getText().toString());
            mBidProductDetails.put("mobilenumber", mEditMobile.getText().toString());
            mBidProductDetails.put("Amount", mEditAmount.getText().toString());

            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.BIDDETAILSADD;
            establishConnection(Request.Method.POST, Constants.mBidDetailsAddUrl, mBidProductDetails);
        } catch (Exception e) {

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
                        if (mServiceCallEnum == ServiceCallEnum.BIDDETAILSADD) {
                            handleBidDetailsAdd(responseType);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    private void handleBidDetailsAdd(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
            String mStatus = mJson.getString("status");
            if (mStatus.equalsIgnoreCase("true")) {
                showSuccess("Succsess", "Successfully added your product to bid");
                clearData();
            } else {
                showMessage("Sorry", "Failed to add your product to bid");
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
                        showWarning("Something went wrong", "Please check for your network");
                    } catch (Exception e) {

                    }
                }
            }
        });

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


}


