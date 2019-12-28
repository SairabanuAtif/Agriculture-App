package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Adapter.VegetablesAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.VegetablesModel;
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
public class ProductsForSaleFragment extends ParentFragment implements VolleyCallback, View.OnClickListener {
    private Activity mActivity;
    HashMap<String, Object> extras;
    Handler mHandler;
    private ArrayList<VegetablesModel> List = new ArrayList<VegetablesModel>();
    private VegetablesAdapter adapter;
    private ServiceCallEnum mServiceCallEnum;
    ListView mListView;
    View footer, header;
    int x = 1;
    int y = 12;
    int totalItem = 0;
    int ListCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.products_for_sale_fragment_layout, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, ProductsForSaleFragment.this);
        extras = new HashMap<String, Object>();
        mHandler = new Handler();
        mListView = (ListView) mActivity.findViewById(R.id.list_products_for_sale);
        footer = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_view, null, false);
        mListView.addFooterView(footer);
        passData();


    }

    private void passData() {
        try {
            JSONObject mJsonObj = new JSONObject();
            mJsonObj.put("CountFrom", x);
            mJsonObj.put("CountTo", y);
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mProductsForSaleUrl, mJsonObj);
        } catch (Exception e) {

        }
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
                        handleProductsForSaleList(responseType);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void handleProductsForSaleList(String responseType) {
        try {
            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject mJson = mJsonArray.getJSONObject(i);
                String mProductName = mJson.getString("ProductName");
                String mName = mJson.getString("Name");
                String mMobile = mJson.getString("MobileNumber");
                String mEmail = mJson.getString("Email");
                String mQuantity = mJson.getString("TotalQuantity");
                String mAddress = mJson.getString("Location");
                String mAmount = mJson.getString("ActualSalesPrice");
                String mImageUrl = mJson.getString("ProductImagePath");
                int mProductCode = mJson.getInt("ProductId");
                String mQuantityUnit = mJson.getString("Quantity");
                int mTotalProducts = mJson.getInt("totalProducts");

                VegetablesModel mProductsForSaleModel = new VegetablesModel();
                mProductsForSaleModel.setmProductCode(mProductCode);
                mProductsForSaleModel.setmProductName(mProductName);
                mProductsForSaleModel.setmSellerAddress(mAddress);
                mProductsForSaleModel.setmSellerMobile(mMobile);
                mProductsForSaleModel.setmSellerEmail(mEmail);
                mProductsForSaleModel.setmQuantity(mQuantity);
                mProductsForSaleModel.setmAmount(mAmount);
                mProductsForSaleModel.setmSellerName(mName);
                mProductsForSaleModel.setmThumbnailUrl(mImageUrl);
                mProductsForSaleModel.setmQuantityUnit(mQuantityUnit);
                mProductsForSaleModel.setTotalProducts(mTotalProducts);
                List.add(mProductsForSaleModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                Collections.sort(List);
                adapter = new VegetablesAdapter(mActivity, List);
                mListView.setAdapter(adapter);

                mListView.setSelection(totalItem);

                footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListCount = List.get(0).getTotalProducts();
                        if (List.size() < ListCount) {
                            x = y + 1;
                            y = x + 11;//11
                            totalItem = x - 1;
                            //  Toast.makeText(mActivity,x+","+ y,Toast.LENGTH_LONG).show();

                            passDataForScroll();
                            // mListView.setSelection(total);
                        } else {
                            mListView.removeFooterView(footer);

                        }
                    }
                });
            } else {

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
            super.establishConnection(Request.Method.POST, Constants.mProductsForSaleUrl, mJsonObj);
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
    public HashMap<String, Object> getExtras() {
        return extras;
    }

}
