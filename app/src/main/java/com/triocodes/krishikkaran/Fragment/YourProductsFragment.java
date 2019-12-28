package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Activity.BidYourProductClickActivity;
import com.triocodes.krishikkaran.Activity.ModifyClickActivity;
import com.triocodes.krishikkaran.Adapter.SpinnerAdapter;
import com.triocodes.krishikkaran.Adapter.YourProductsAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.SpinnerModel;
import com.triocodes.krishikkaran.Model.YourProductsModel;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by admin on 25-02-16.
 */
public class YourProductsFragment extends ParentFragment implements VolleyCallback, View.OnClickListener, YourProductsAdapter.customButtonListener {
    private Activity mActivity;
    ListView mListYourProducts;
    HashMap<String, Object> extras;
    Handler mhandler;
    private ArrayList<YourProductsModel> List = new ArrayList<YourProductsModel>();
    private YourProductsAdapter adapter;
    private ServiceCallEnum mServiceCallEnum;
    String RegistrationId, RegistrationIdToPass;
    int mProductId;
    SweetAlertDialog mAlertDialog;
    FragmentManager mFragmentManager;
    TextView txtRefresh;


    public YourProductsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.your_products_fragment_layout, container,false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, YourProductsFragment.this);
        extras = new HashMap<String, Object>();
        mhandler = new Handler();
        mListYourProducts = (ListView) mActivity.findViewById(R.id.list_your_products);
        txtRefresh = (TextView) mActivity.findViewById(R.id.text_your_products_refresh);
        txtRefresh.setOnClickListener(this);
        mFragmentManager = getFragmentManager();

        RegistrationId = DataBaseQueryHelper.getInstance().getRegisterIdRegister();

        if (RegistrationId == null) {
            RegistrationIdToPass = DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        } else {
            RegistrationIdToPass = RegistrationId;
        }
        if (isConnectingToInternet()) {
            passData();
        } else {
            showError("Connectivity Issue", "No internet connection available", FragmentTransactionEnum.NONE);
        }

    }

    private void passData() {

        JSONObject mYourProductDetails = new JSONObject();
        try {

            mYourProductDetails.put("RegistrationId", RegistrationIdToPass);
            showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.YOURPRODUCTS;
            establishConnection(Request.Method.POST, Constants.mYourProductUrl, mYourProductDetails);
        } catch (Exception e) {

        }
    }

    private void passDataFromRefresh() {

        JSONObject mYourProductDetails = new JSONObject();
        try {

            mYourProductDetails.put("RegistrationId", RegistrationIdToPass);
            mServiceCallEnum = ServiceCallEnum.YOURPRODUCTS;
            establishConnection(Request.Method.POST, Constants.mYourProductUrl, mYourProductDetails);
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
        switch (v.getId()) {
            case R.id.text_your_products_refresh: {
                Refresh();
            }
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
                        if (mServiceCallEnum == ServiceCallEnum.YOURPRODUCTS) {
                            handleYourProducts(responseType);

                        } else {
                            JSONObject mJsonObject = new JSONObject(responseType);
                            String mJsonString = mJsonObject.getString("d");
                            JSONArray mJsonArray = new JSONArray(mJsonString);
                            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
                            String mStatus = mJson.getString("status");
                            if (mStatus.equalsIgnoreCase("true")) {
                                int size=List.size();
                                if(size==1){
                                   mListYourProducts.setAdapter(null);
                                    Refresh();
                                }
                                Refresh();

                            } else {
                                showMessage("Sorry", "Cannot delete this product");
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }


    private void handleYourProducts(String responseType) {
        List.clear();
        try {

            JSONObject mJsonObject = new JSONObject(responseType);
            String mString = mJsonObject.getString("d");
            JSONArray mJsonArray = new JSONArray(mString);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject mJson = mJsonArray.getJSONObject(i);
                String mProductName = mJson.getString("ProductName");
                int mProductCode = mJson.getInt("ProductId");
                String mTotalQuantity = mJson.getString("TotalQuantity");
                int mQuantityId = mJson.getInt("QuantityId");
                int mCatagoryId = mJson.getInt("CategoryId");
                String mAmount = mJson.getString("Amount");
                String mImg=mJson.getString("ProductImagePath");
                YourProductsModel mYourProductsModel = new YourProductsModel();
                mYourProductsModel.setmProductCode(mProductCode);
                mYourProductsModel.setmProductName(mProductName);
                mYourProductsModel.setmTotalQuantity(mTotalQuantity);
                mYourProductsModel.setmQuantityId(mQuantityId);
                mYourProductsModel.setmCatagoryId(mCatagoryId);
                mYourProductsModel.setmAmount(mAmount);
                mYourProductsModel.setmImgPath(mImg);

                List.add(mYourProductsModel);

            }

        } catch (Exception e) {
            Toast.makeText(mActivity, e.toString(), Toast.LENGTH_LONG).show();
        }
        try {
            if (List != null && List.size() > 0) {
                Collections.sort(List);
                adapter = new YourProductsAdapter(mActivity, List);
                adapter.setCustomButtonListner(YourProductsFragment.this);
                mListYourProducts.setAdapter(adapter);

            }
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
    public void onModifyClickListner(int position) {

        int ProductId=List.get(position).getmProductCode();
        String ProductName=List.get(position).getmProductName();
        int CategoryId=List.get(position).getmCatagoryId();
        String TotalQuantity=List.get(position).getmTotalQuantity();
        int QuantityId=List.get(position).getmQuantityId();
        String Amount=List.get(position).getmAmount();
        String ImagePath=List.get(position).getmImgPath();

        Intent intent = new Intent(mActivity, ModifyClickActivity.class);
        intent.putExtra("ProductId",ProductId);
        intent.putExtra("ProductName",ProductName);
        intent.putExtra("CategoryId",CategoryId);
        intent.putExtra("TotalQuantity",TotalQuantity);
        intent.putExtra("QuantityId",QuantityId);
        intent.putExtra("Amount",Amount);
        intent.putExtra("ImagePath",ImagePath);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }


    @Override
    public void onDeleteClickListener(int position) {
        mProductId = List.get(position).getmProductCode();
        alert(mProductId);
    }

    private void alert(int ProductId) {
        final int mproductId=ProductId;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
        builder.setMessage("Are you sure you want to delete this product?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                connectToDelete(mproductId);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toFragment(FragmentTransactionEnum.NONE);
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void connectToDelete(int mproductId) {

        JSONObject mJsonObj = new JSONObject();
        try {
            mJsonObj.put("ProductID", mproductId);
            super.showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.DELETEYOURPRODUCT;
            super.establishConnection(Request.Method.POST, Constants.mDeleteProductUrl, mJsonObj);
        } catch (Exception e) {

        }
    }

    @Override
    public void onBidClickListener(int position) {
        int ProductId = List.get(position).getmProductCode();
        String ProductName= List.get(position).getmProductName();
        Intent intent = new Intent(mActivity, BidYourProductClickActivity.class);
        intent.putExtra("ProductId", ProductId);
        intent.putExtra("ProductName",ProductName);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    public void dismissAlertHere() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }


    public void Refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List.clear();
                passDataFromRefresh();
            }
        }, 500);
    }
}
