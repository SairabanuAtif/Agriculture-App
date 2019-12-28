package com.triocodes.krishikkaran.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.triocodes.krishikkaran.Adapter.BidListAdapter;
import com.triocodes.krishikkaran.Adapter.VegetablesAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.BidListModel;
import com.triocodes.krishikkaran.Model.VegetablesModel;
import com.triocodes.krishikkaran.ParentActivity;
import com.triocodes.krishikkaran.R;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BidListActivity extends ParentActivity implements
        VolleyCallback, Response.Listener, Response.ErrorListener, View.OnClickListener {

    Toolbar mToolbar;
    HashMap<String, Object> extras;
    Handler mHandler;
    ListView mListViewBidList;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    private ArrayList<BidListModel> List = new ArrayList<BidListModel>();
    private BidListAdapter adapter;
    LinearLayout mLayoutNoBidRecord;
    int ProductId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_list);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbarBidList);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
            }
        });
        extras = new HashMap<String, Object>();
        mHandler = new Handler();
        Intent intent = getIntent();
        ProductId = intent.getIntExtra("ProductId", 0);
        mListViewBidList = (ListView) this.findViewById(R.id.list_bid_list);
        mLayoutNoBidRecord = (LinearLayout) this.findViewById(R.id.layout_no_bid_record);
        mLayoutNoBidRecord.setVisibility(View.GONE);
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
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("ProductID", ProductId);
            establishConnection(Request.Method.POST, Constants.mBidListUrl, mVegetableDetails);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void volleyOnSuccess() {
        dismissAlert();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    List.clear();
                    try {
                        String responseType = (String) extras.get("response");
                        JSONObject mJsonObject = new JSONObject(responseType);
                        String mString = mJsonObject.getString("d");
                        JSONArray mJsonArray = new JSONArray(mString);
                        for (int i = 0; i < mJsonArray.length(); i++) {
                            JSONObject mJson = mJsonArray.getJSONObject(i);
                            String mName = mJson.getString("Name");
                            String mPlace = mJson.getString("Place");
                            String mMobile = mJson.getString("MobileNumber");
                            String mAmount = mJson.getString("Amount");
                            String mBiddedOn = mJson.getString("CreatedOn");


                            BidListModel mBidListModel = new BidListModel();
                            mBidListModel.setmName(mName);
                            mBidListModel.setmPlace(mPlace);
                            mBidListModel.setmMobilenum(mMobile);
                            mBidListModel.setmAmount(mAmount);
                            mBidListModel.setmBiddedOn(mBiddedOn);


                            List.add(mBidListModel);
                        }
                    } catch (Exception e) {

                    }

                    try {
                        if (List != null && List.size() > 0) {
                            Collections.sort(List);
                            adapter = new BidListAdapter(BidListActivity.this, List);
                            mListViewBidList.setAdapter(adapter);

                        } else {
                            mLayoutNoBidRecord.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });


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
