package com.triocodes.krishikkaran.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.triocodes.krishikkaran.Adapter.BarterProductAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.BarterProductModel;
import com.triocodes.krishikkaran.ParentActivity;
import com.triocodes.krishikkaran.R;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BarterProductClickActivity extends ParentActivity implements
        VolleyCallback, Response.ErrorListener, Response.Listener, BarterProductAdapter.customButtonListener {
    Toolbar mToolbar;
    HashMap<String, Object> extras;
    Handler mHandler;
    ListView mListBarterProduct;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    ServiceCallEnum mServiceCallEnum;
    private ArrayList<BarterProductModel> List = new ArrayList<BarterProductModel>();
    private BarterProductAdapter adapter;
    String RegistrationId, RegistrationIdToPass;
    LinearLayout mLayoutNoBarterRecord;
    int ProductId;
    String ProductName, Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barter_product_click);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbarBarterProduct);
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
        Email = intent.getStringExtra("Email");
        extras = new HashMap<String, Object>();
        mHandler = new Handler();
        mListBarterProduct = (ListView) this.findViewById(R.id.list_barter_product);
        mLayoutNoBarterRecord = (LinearLayout) this.findViewById(R.id.layout_no_barter_record);
        mLayoutNoBarterRecord.setVisibility(View.GONE);

        RegistrationId = DataBaseQueryHelper.getInstance().getRegisterIdRegister();
        if (RegistrationId == null) {
            RegistrationIdToPass = DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        } else {
            RegistrationIdToPass = RegistrationId;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        passData();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (List.size() == 0) {
                    passData();
                }
            }
        }, 2500);
    }

    private void passData() {
        JSONObject mBidProductDetails = new JSONObject();
        try {
            mBidProductDetails.put("RegistrationId", RegistrationIdToPass);
            mServiceCallEnum = ServiceCallEnum.BARTERPRODUCTLIST;
            establishConnection(Request.Method.POST, Constants.mBarterProductListUrl, mBidProductDetails);
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
                        if (mServiceCallEnum == ServiceCallEnum.BARTERPRODUCTLIST) {
                            handleBarterList(responseType);
                        } else if (mServiceCallEnum == ServiceCallEnum.BARTERSEND) {
                            JSONObject mJsonObject = new JSONObject(responseType);
                            String mString = mJsonObject.getString("d");
                            JSONArray mJsonArray = new JSONArray(mString);
                            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
                            String mStatus = mJson.getString("status");
                            if (mStatus.equalsIgnoreCase("true")) {
                                showSuccess("Success", "Successfully sent barter interest to your Mail");
                            } else {
                                showMessage("Sorry", "Receiver does not have Mail ID");
                            }
                        }
                    } catch (Exception e) {
                        showError("Error", e.toString());

                    }
                }
            }
        });
    }

    private void handleBarterList(String responseType) {
        List.clear();
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject mJson = mJsonArray.getJSONObject(i);
                String mId = mJson.getString("ProductID");
                String mProduct = mJson.getString("ProductForBarter");

                BarterProductModel mBarterProductModel = new BarterProductModel();
                mBarterProductModel.setmId(mId);
                mBarterProductModel.setmProduct(mProduct);

                List.add(mBarterProductModel);
            }

        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                Collections.sort(List);
                adapter = new BarterProductAdapter(BarterProductClickActivity.this, List);
                adapter.setCustomButtonListener(BarterProductClickActivity.this);
                mListBarterProduct.setAdapter(adapter);

            } else {
                mLayoutNoBarterRecord.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void volleyOnError() {
        dismissAlert();
        if (mHandler.post(new Runnable() {
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
        })) ;


    }

    @Override
    public HashMap<String, Object> getExtras() {
        return extras;
    }

    @Override
    public void onBarterSendButtonClickListener(int position) {
        try {
            JSONObject mBarterDetails = new JSONObject();
            String mId = List.get(position).getmId();
            String mProductName = List.get(position).getmProduct();
            mBarterDetails.put("MyProdutID", mId);
            mBarterDetails.put("ProductID", ProductId);
            mBarterDetails.put("UrProductName", ProductName);
            mBarterDetails.put("Email", Email);
            mBarterDetails.put("ProductName", mProductName);
            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.BARTERSEND;
            establishConnection(Request.Method.POST, Constants.mBarterProductSendUrl, mBarterDetails);
        } catch (Exception e) {

        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }
}
