package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Activity.BidProductClickActivity;
import com.triocodes.krishikkaran.Adapter.BidAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.BidModel;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by admin on 24-02-16.
 */
public class BidFragment extends ParentFragment implements View.OnClickListener,VolleyCallback,BidAdapter.customButtonListener {
    private Activity mActivity;
    HashMap<String, Object> extras;
    Handler mhandler;
    ListView mListViewBid;
    private ArrayList<BidModel>List=new ArrayList<BidModel>();
    private BidAdapter adapter;
    private ServiceCallEnum mServiceCallEnum;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout mLayoutNoBid;
    View footer, header;
    int x = 1;
    int y = 12;
    int totalItem=0;
    int ListCount=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bid_fragment_layout,null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, BidFragment.this);
        extras = new HashMap<String, Object>();
        mhandler = new Handler();
        mListViewBid=(ListView)mActivity.findViewById(R.id.list_bid);
        mLayoutNoBid=(LinearLayout)mActivity.findViewById(R.id.layout_no_bid);
        mLayoutNoBid.setVisibility(View.GONE);
        footer = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_view, null, false);
        mListViewBid.addFooterView(footer);
        passData();
    }

    private void passData() {

        try {
            JSONObject mJsonObj = new JSONObject();
            mJsonObj.put("CountFrom", x);
            mJsonObj.put("CountTo", y);
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mViewAllBidProductsUrl, mJsonObj);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {

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
    public void volleyOnSuccess() {
        dismissAlert();
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    try {
                        String responseType = (String) extras.get("response");
                        handleViewAlllBidProducts(responseType);
                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    private void handleViewAlllBidProducts(String responseType) {
        try{
            JSONObject mJsonObject=new JSONObject(responseType);
            String mString=mJsonObject.getString("d");
            JSONArray mJsonArray=new JSONArray(mString);
            for(int i=0;i<mJsonArray.length();i++){
                JSONObject mJson=mJsonArray.getJSONObject(i);
                String mProductName=mJson.getString("ProductName");
                String mName=mJson.getString("Name");
                String mAddress=mJson.getString("Location");
                String mMobile=mJson.getString("MobileNumber");
                String mQuantity=mJson.getString("TotalQuantity");
                String mQuantityUnit=mJson.getString("Quantity");
                String mAmount=mJson.getString("ActualSalesPrice");
                String mImageUrl=mJson.getString("ProductImagePath");
                String mMinAmount=mJson.getString("MinimumBidAmount");
                int mProductCode=mJson.getInt("ProductID");
                int mBidId=mJson.getInt("BiddID");
                int mTotalProduct=mJson.getInt("totalProducts");

                BidModel mBidModel=new BidModel();
                mBidModel.setmProductName(mProductName);
                mBidModel.setmSellerName(mName);
                mBidModel.setmSellerAddress(mAddress);
                mBidModel.setmSellerMobile(mMobile);
                mBidModel.setmQuantity(mQuantity);
                mBidModel.setmAmount(mAmount);
                mBidModel.setmQuantityUnit(mQuantityUnit);
                mBidModel.setmThumbnailUrl(mImageUrl);
                mBidModel.setmProductCode(mProductCode);
                mBidModel.setmBidId(mBidId);
                mBidModel.setmTotalProducts(mTotalProduct);
                mBidModel.setmMinAmount(mMinAmount);

                List.add(mBidModel);
            }
        }catch (Exception e){

        }
        try{
            if(List!=null&&List.size()>0){
                Collections.sort(List);
                adapter=new BidAdapter(mActivity,List);
                adapter.setCustomButtonListener(BidFragment.this);
                mListViewBid.setAdapter(adapter);
                mListViewBid.setSelection(totalItem);

                if(List.size()<5){
                    mListViewBid.removeFooterView(footer);
                }
                footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListCount = List.get(0).getmTotalProducts();
                        if (List.size() < ListCount) {
                            x = y + 1;
                            y = x + 11;//11
                            totalItem=x-1;
                            //  Toast.makeText(mActivity,x+","+ y,Toast.LENGTH_LONG).show();

                            passDataForScroll();
                            // mListView.setSelection(total);
                        } else {
                            mListViewBid.removeFooterView(footer);

                        }
                    }
                });
            }
            else{
                mLayoutNoBid.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){

        }
        adapter.notifyDataSetChanged();

    }

    private void passDataForScroll() {
            try {
                JSONObject mJsonObj = new JSONObject();
                mJsonObj.put("CountFrom", x);
                mJsonObj.put("CountTo", y);
                super.showProgress("Please Wait");
                super.establishConnection(Request.Method.POST, Constants.mViewAllBidProductsUrl, mJsonObj);
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
    public void OnBidThisProductClickListener(int position) {
        int ProductId=List.get(position).getmProductCode();
        String ProductName=List.get(position).getmProductName();
        String mMinAmount=List.get(position).getmMinAmount();
        Intent intent = new Intent(mActivity, BidProductClickActivity.class);
        intent.putExtra("ProductId", ProductId);
        intent.putExtra("ProductName",ProductName);
        intent.putExtra("MinAmount",mMinAmount);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);

    }


}
