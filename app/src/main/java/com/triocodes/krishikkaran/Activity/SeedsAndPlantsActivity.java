package com.triocodes.krishikkaran.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
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

public class SeedsAndPlantsActivity extends ParentActivity implements VolleyCallback, Response.ErrorListener,Response.Listener {
    Toolbar mToolbar;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;
    HashMap<String,Object>extras;
    Handler mHandler;
    private ArrayList<VegetablesModel> List=new ArrayList<VegetablesModel>();
    private VegetablesAdapter adapter;
    private ServiceCallEnum mServiceCallEnum;
    ListView mListView;
    LinearLayout mLayoutNoSeeds;
    SwipeRefreshLayout swipeRefreshLayout;
    View footer, header;
    int x = 1;
    int y = 12;
    int totalItem=0;
    int ListCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeds_and_plants);
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
        extras=new HashMap<String, Object>();
        mHandler=new Handler();
        mListView=(ListView)this.findViewById(R.id.list_seeds_plants);
        mLayoutNoSeeds=(LinearLayout)this.findViewById(R.id.layout_no_seeds_plants);
        mLayoutNoSeeds.setVisibility(View.GONE);
        footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_view, null, false);
        mListView.addFooterView(footer);
        List.clear();
        dismissAlert();
        passData();


    }
    private void passData() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("CategoryID", "4");
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            super.showProgress("Please Wait");
            establishConnection(Request.Method.POST, Constants.mSeedsandplantsUrl, mVegetableDetails);
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
                        handleSeedsAndPlantsList(responseType);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void handleSeedsAndPlantsList(String responseType) {
        try{
            JSONObject mJsonObject=new JSONObject(responseType);
            String mString=mJsonObject.getString("d");
            JSONArray mJsonArray=new JSONArray(mString);
            for(int i=0;i<mJsonArray.length();i++){
                JSONObject mJson=mJsonArray.getJSONObject(i);
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

                VegetablesModel mSeedsAndPlantsModel=new VegetablesModel();
                mSeedsAndPlantsModel.setmSellerName(mName);
                mSeedsAndPlantsModel.setmSellerAddress(mAddress);
                mSeedsAndPlantsModel.setmAmount(mAmount);
                mSeedsAndPlantsModel.setmQuantity(mQuantity);
                mSeedsAndPlantsModel.setmProductName(mProductName);
                mSeedsAndPlantsModel.setmSellerEmail(mEmail);
                mSeedsAndPlantsModel.setmProductCode(mProductCode);
                mSeedsAndPlantsModel.setmSellerMobile(mMobile);
                mSeedsAndPlantsModel.setmQuantityUnit(mQuantityUnit);
                mSeedsAndPlantsModel.setmThumbnailUrl(mImageUrl);
                mSeedsAndPlantsModel.setTotalProducts(mTotalProducts);

                List.add(mSeedsAndPlantsModel);

            }
        }catch (Exception e){

        }
        try{
            if(List!=null&&List.size()>0){
                Collections.sort(List);
                adapter=new VegetablesAdapter(this,List);
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
                mLayoutNoSeeds.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){

        }
    }
    private void passDataForScroll() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("CategoryID", "4");
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            super.showProgress("Please Wait");
            establishConnection(Request.Method.POST, Constants.mSeedsandplantsUrl, mVegetableDetails);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_button_entry, R.anim.back_button_exit);
    }



}
