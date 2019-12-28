package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Activity.BarterProductClickActivity;
import com.triocodes.krishikkaran.Activity.BidListActivity;
import com.triocodes.krishikkaran.Adapter.BarterAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.BarterModel;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by admin on 24-02-16.
 */
public class BarterFragment extends ParentFragment implements VolleyCallback, View.OnClickListener, BarterAdapter.customButtonListener {
    private Activity mActivity;
    HashMap<String, Object> extras;
    Handler mHandler;
    ListView mListViewBarter;
    private ArrayList<BarterModel> List = new ArrayList<BarterModel>();
    private BarterAdapter adapter;
    LinearLayout mLayoutNoBarter;
    View footer, header;
    int x = 1;
    int y = 12;
    int totalItem=0;
    int ListCount=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.barter_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, BarterFragment.this);
        extras = new HashMap<String, Object>();
        mHandler = new Handler();
        mListViewBarter = (ListView) mActivity.findViewById(R.id.list_barter);
        mLayoutNoBarter=(LinearLayout)mActivity.findViewById(R.id.layout_no_barter);
        mLayoutNoBarter.setVisibility(View.GONE);
        footer = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_view, null, false);
        mListViewBarter.addFooterView(footer);

        passData();


    }

    private void passData() {
        try {
            JSONObject mJsonObj = new JSONObject();
            mJsonObj.put("CountFrom", x);
            mJsonObj.put("CountTo", y);
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mViewAllBarterProductsUrl, mJsonObj);
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                    try {
                        String responseType = (String) extras.get("response");
                        handleViewAllBarterProducts(responseType);
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private void handleViewAllBarterProducts(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject mJson = mJsonArray.getJSONObject(i);
                int mProductCode = mJson.getInt("ProductID");
                int mBarterId = mJson.getInt("BarterMasterID");
                String mProductName = mJson.getString("ProductName");
                String mName = mJson.getString("Name");
                String mLocation = mJson.getString("Location");
                String mMobile = mJson.getString("MobileNumber");
                String mEmail = mJson.getString("Email");
                String mQuantity = mJson.getString("TotalQuantity");
                String mQuantityUnit = mJson.getString("Quantity");
                String mAmount = mJson.getString("Amount");
                String mImageUrl = mJson.getString("ProductImagePath");
                int mTotalProduct=mJson.getInt("totalProducts");
                String mRegId=mJson.getString("RegistrationId");

                BarterModel mBarterModel = new BarterModel();
                mBarterModel.setmProductCode(mProductCode);
                mBarterModel.setmBarterId(mBarterId);
                mBarterModel.setmProductName(mProductName);
                mBarterModel.setmName(mName);
                mBarterModel.setmQuantity(mQuantity);
                mBarterModel.setmQuantityUnit(mQuantityUnit);
                mBarterModel.setmAmount(mAmount);
                mBarterModel.setmEmail(mEmail);
                mBarterModel.setmLocation(mLocation);
                mBarterModel.setmMobile(mMobile);
                mBarterModel.setmImageUrl(mImageUrl);
                mBarterModel.setmTotalProducts(mTotalProduct);
                mBarterModel.setmRegId(mRegId);

                List.add(mBarterModel);

            }
        } catch (Exception e) {

        }
        try {
            if (List != null & List.size() > 0) {
                Collections.sort(List);
                adapter = new BarterAdapter(mActivity, List);
                adapter.setCustomButtonListner(BarterFragment.this);
                mListViewBarter.setAdapter(adapter);
                mListViewBarter.setSelection(totalItem);

                if(List.size()<5){
                    mListViewBarter.removeFooterView(footer);
                }

                footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListCount = List.get(0).getmTotalProducts();
                        if (List.size() < ListCount) {
                            x = y + 1;
                            y = x + 11;//11
                            totalItem=x-1;
                            passDataForScroll();

                        } else {
                            mListViewBarter.removeFooterView(footer);

                        }
                    }
                });
            }
            else{
                mLayoutNoBarter.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
        adapter.notifyDataSetChanged();
    }

    private void passDataForScroll() {
        try {
            JSONObject mJsonObj = new JSONObject();
            mJsonObj.put("CountFrom", x);
            mJsonObj.put("CountTo", y);
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mViewAllBarterProductsUrl, mJsonObj);
        } catch (Exception e) {

        }
    }
    @Override
    public void volleyOnError() {
        dismissAlert();
        if(mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(!extras.isEmpty()){
                    try{
                        JSONObject mJsonObject=new JSONObject();
                        String responseType=(String)extras.get("response");
                        showWarning("Something went wrong","Please check for your network");
                    }catch (Exception e){

                    }
                }
            }
        }));
    }

    @Override
    public HashMap<String, Object> getExtras() {
        return extras;
    }

    @Override
    public void OnBarterProductClickListener(int position) {
        int productId=List.get(position).getmProductCode();
        String productName=List.get(position).getmProductName();
        String email=List.get(position).getmEmail();
        Intent intent = new Intent(mActivity, BarterProductClickActivity.class);
        intent.putExtra("ProductId",productId);
        intent.putExtra("ProductName",productName);
        intent.putExtra("Email",email);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }
}
