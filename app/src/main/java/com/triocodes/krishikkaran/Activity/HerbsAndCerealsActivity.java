package com.triocodes.krishikkaran.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.triocodes.krishikkaran.Adapter.VegetablesAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
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

public class HerbsAndCerealsActivity extends ParentActivity implements VolleyCallback, Response.ErrorListener,Response.Listener {
    Toolbar mToolbar;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    HashMap<String, Object> extras;
    Handler mhandler;
    private ArrayList<VegetablesModel> List = new ArrayList<VegetablesModel>();
    private VegetablesAdapter adapter;
    ListView mListView;
    LinearLayout mLayoutNoHerbs;
    View footer, header;
    int x = 1;
    int y = 12;
    int totalItem=0;
    int ListCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herbs_and_cereals);
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
        extras = new HashMap<String, Object>();
        mhandler = new Handler();
        mLayoutNoHerbs=(LinearLayout)this.findViewById(R.id.layout_no_herbs);
        mLayoutNoHerbs.setVisibility(View.GONE);
        mListView = (ListView) this.findViewById(R.id.list_herbs_cereals);
        footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_view, null, false);
        mListView.addFooterView(footer);
        List.clear();
        passData();
    }
    private void passData() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("CategoryID", "6");
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            super.showProgress("Please Wait");
            establishConnection(Request.Method.POST, Constants.mHerbsCerealsUrl, mVegetableDetails);
        } catch (Exception e) {

        }
    }

    @Override
    public void volleyOnSuccess() {
        dismissAlert();
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    try {
                        String responseType = (String) extras.get("response");
                        handleHerbsAndCereals(responseType);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void handleHerbsAndCereals(String responseType) {

        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject mJson = mJsonArray.getJSONObject(i);
                String mProductName = mJson.getString("ProductName");
                String mName = mJson.getString("Name");
                String mAddress = mJson.getString("Location");
                String mQuantity = mJson.getString("TotalQuantity");
                String mMobile = mJson.getString("MobileNumber");
                String mEmail = mJson.getString("Email");
                String mAmount = mJson.getString("ActualSalesPrice");
                int mProductCode = mJson.getInt("ProductId");
                String mQuantityUnit = mJson.getString("Quantity");
                String mImageUrl=mJson.getString("ProductImagePath");
                int mTotalProducts=mJson.getInt("totalProducts");

                VegetablesModel mHerbsCerealsModel = new VegetablesModel();
                mHerbsCerealsModel.setmProductName(mProductName);
                mHerbsCerealsModel.setmSellerAddress(mAddress);
                mHerbsCerealsModel.setmAmount(mAmount);
                mHerbsCerealsModel.setmQuantity(mQuantity);
                mHerbsCerealsModel.setmSellerName(mName);
                mHerbsCerealsModel.setmSellerMobile(mMobile);
                mHerbsCerealsModel.setmSellerEmail(mEmail);
                mHerbsCerealsModel.setmProductCode(mProductCode);
                mHerbsCerealsModel.setmQuantityUnit(mQuantityUnit);
                mHerbsCerealsModel.setmThumbnailUrl(mImageUrl);
                mHerbsCerealsModel.setTotalProducts(mTotalProducts);

                List.add(mHerbsCerealsModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                Collections.sort(List);
                adapter = new VegetablesAdapter(this, List);
                mListView.setAdapter(adapter);
                mListView.setSelection(totalItem);

                if(List.size()<5){
                    mListView.removeFooterView(footer);
                }
                footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListCount = List.get(0).getTotalProducts();
                        if (List.size() < ListCount) {
                            x = y + 1;
                            y = x + 11;//11
                            totalItem=x-1;
                            passDataForScroll();
                        } else {
                            mListView.removeFooterView(footer);

                        }
                    }
                });

            }
            else{
                mLayoutNoHerbs.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }

    }

    private void passDataForScroll() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("CategoryID", "6");
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            super.showProgress("Please Wait");
            establishConnection(Request.Method.POST, Constants.mHerbsCerealsUrl, mVegetableDetails);
        } catch (Exception e) {

        }
    }


    @Override
    public void volleyOnError() {
        dismissAlert();
        mhandler.post(new Runnable() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }
}
