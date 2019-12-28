package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Adapter.VegetablesAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
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
public class SearchFragment extends ParentFragment implements VolleyCallback, View.OnClickListener, AdapterView.OnItemSelectedListener {
    Activity mActivity;
    EditText mEditProductName, mEditDistrict, mEditLocation;
    TextView mTextSearch,mTextAnotherSearch;
    ListView mListView;
    LinearLayout mLayoutAnotherSearch,mLayoutSearch;
    Spinner mSpinnerSearch;
    ServiceCallEnum mServiceCallEnum;
    HashMap<String, Object> extras;
    Handler mHandler;
    LinearLayout mLayoutNoSearchResults;
    private ArrayList<VegetablesModel> List = new ArrayList<VegetablesModel>();
    private VegetablesAdapter adapter;
    int selectedId = 0;
    View footer, header;
    int x = 1;
    int y = 12;
    int totalItem=0;
    int ListCount=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_layout, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, SearchFragment.this);
        extras = new HashMap<String, Object>();
        mHandler = new Handler();
        mEditProductName = (EditText) mActivity.findViewById(R.id.edittext_search_product_name);
        mEditDistrict = (EditText) mActivity.findViewById(R.id.edittext_search_district);
        mEditLocation = (EditText) mActivity.findViewById(R.id.edittext_search_location);
        mTextSearch=(TextView)mActivity.findViewById(R.id.text_button_search_fragment_search);
        mLayoutAnotherSearch=(LinearLayout)mActivity.findViewById(R.id.layout_another_search);
        mLayoutSearch=(LinearLayout)mActivity.findViewById(R.id.layout_search);
        mLayoutSearch.setOnClickListener(this);
        mTextAnotherSearch=(TextView)mActivity.findViewById(R.id.textview_another_search);
        mTextAnotherSearch.setOnClickListener(this);
        mTextSearch.setOnClickListener(this);
        mListView = (ListView) mActivity.findViewById(R.id.list_search);
        footer = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.load_more_view, null, false);
        mListView.addFooterView(footer);
        mSpinnerSearch = (Spinner) mActivity.findViewById(R.id.spinner_search_select_search);
        mLayoutNoSearchResults=(LinearLayout)mActivity.findViewById(R.id.layout_no_search_results);
        mLayoutNoSearchResults.setVisibility(View.GONE);
        mSpinnerSearch.setOnItemSelectedListener(this);

        String[] list = getResources().getStringArray(R.array.search);
        ArrayAdapter<String> ar = new ArrayAdapter<String>(mActivity, R.layout.frame_spinner, R.id.txt_item, list);
        mSpinnerSearch.setAdapter(ar);

        mEditDistrict.setVisibility(View.GONE);
        mEditLocation.setVisibility(View.GONE);
        mEditProductName.setVisibility(View.GONE);
        mLayoutAnotherSearch.setVisibility(View.GONE);


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

            case R.id.text_button_search_fragment_search: {
                if (isConnectingToInternet()) {
                    if (selectedId == 1) {
                        if (nullCheckerName()) {
                            x=1;y=12;
                            totalItem=0;
                            mLayoutSearch.setVisibility(View.GONE);
                            mLayoutAnotherSearch.setVisibility(View.VISIBLE);

                            passDataForNameSearch();
                        }
                    }
                    else if(selectedId==2){
                        if (nullCheckerDistrict()) {
                            x=1;y=12;
                            totalItem=0;
                            mLayoutSearch.setVisibility(View.GONE);
                            mLayoutAnotherSearch.setVisibility(View.VISIBLE);
                            passDataForDistrictSearch();
                        }
                    }
                    else if(selectedId==3){
                        if (nullCheckerLocation()) {
                            x=1;y=12;
                            totalItem=0;
                            mLayoutSearch.setVisibility(View.GONE);
                            mLayoutAnotherSearch.setVisibility(View.VISIBLE);
                            passDataForLocationSearch();
                        }
                    }


                } else {
                    showError("Connectivity Issue", "No internet connection available", FragmentTransactionEnum.NONE);
                }
                break;
            }
            case R.id.textview_another_search:{
                mLayoutAnotherSearch.setVisibility(View.GONE);
                mLayoutSearch.setVisibility(View.VISIBLE);
            }

        }
    }

    private boolean nullCheckerLocation() {
        if (mEditLocation.getText().toString().trim().length() == 0 || mEditLocation.getText().toString() == "") {
            shakeEditText(R.id.edittext_search_location);
            return false;
        }
        return true;
    }

    private boolean nullCheckerDistrict() {
        if (mEditDistrict.getText().toString().trim().length() == 0 || mEditDistrict.getText().toString() == "") {
            shakeEditText(R.id.edittext_search_district);
            return false;
        }
        return true;
    }

    private boolean nullCheckerName() {
        if (mEditProductName.getText().toString().trim().length() == 0 || mEditProductName.getText().toString() == "") {
            shakeEditText(R.id.edittext_search_product_name);
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
                        String responseType = (String) extras.get("response");
                        if(mServiceCallEnum==ServiceCallEnum.PRODUCTNAMESEARCH) {
                            mEditProductName.setText("");
                            mEditProductName.setHintTextColor(Color.parseColor("#398808"));
                            handleNameSearchList(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.LOCATIONSEARCH){
                            mEditLocation.setText("");
                            mEditLocation.setHintTextColor(Color.parseColor("#398808"));
                            handlemLocationSearch(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.DISTRICTSEARCH){
                            mEditDistrict.setText("");
                            mEditDistrict.setHintTextColor(Color.parseColor("#398808"));
                            handlemDistrictSearch(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.PRODUCTNAMESEARCHSCROLL){
                            handleNameSearchListScroll(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.DISTRICTSEARCHSCROLL){
                            handlemDistrictSearchScroll(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.LOCATIONSEARCHSCROLL){
                            handlemLocationSearchScroll(responseType);
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private void handlemLocationSearchScroll(String responseType) {
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
                String mImageUrl = mJson.getString("ProductImagePath");
                int mTotalProducts=mJson.getInt("totalProducts");

                VegetablesModel mVegetablesModel = new VegetablesModel();
                mVegetablesModel.setmProductName(mProductName);
                mVegetablesModel.setmSellerAddress(mAddress);
                mVegetablesModel.setmAmount(mAmount);
                mVegetablesModel.setmQuantity(mQuantity);
                mVegetablesModel.setmSellerName(mName);
                mVegetablesModel.setmSellerMobile(mMobile);
                mVegetablesModel.setmSellerEmail(mEmail);
                mVegetablesModel.setmProductCode(mProductCode);
                mVegetablesModel.setmQuantityUnit(mQuantityUnit);
                mVegetablesModel.setmThumbnailUrl(mImageUrl);
                mVegetablesModel.setTotalProducts(mTotalProducts);

                List.add(mVegetablesModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                mLayoutNoSearchResults.setVisibility(View.GONE);
                footer.setVisibility(View.VISIBLE);
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
                            totalItem=x-1;
                            //  Toast.makeText(mActivity,x+","+ y,Toast.LENGTH_LONG).show();

                            passDataForScroll();
                            // mListView.setSelection(total);
                        } else {
                            //mListView.removeFooterView(footer);
                            footer.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                footer.setVisibility(View.GONE);
                  mLayoutNoSearchResults.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
        adapter.notifyDataSetChanged();
    }

    private void handlemDistrictSearchScroll(String responseType) {
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
                String mImageUrl = mJson.getString("ProductImagePath");
                int mTotalProducts=mJson.getInt("totalProducts");

                VegetablesModel mVegetablesModel = new VegetablesModel();
                mVegetablesModel.setmProductName(mProductName);
                mVegetablesModel.setmSellerAddress(mAddress);
                mVegetablesModel.setmAmount(mAmount);
                mVegetablesModel.setmQuantity(mQuantity);
                mVegetablesModel.setmSellerName(mName);
                mVegetablesModel.setmSellerMobile(mMobile);
                mVegetablesModel.setmSellerEmail(mEmail);
                mVegetablesModel.setmProductCode(mProductCode);
                mVegetablesModel.setmQuantityUnit(mQuantityUnit);
                mVegetablesModel.setmThumbnailUrl(mImageUrl);
                mVegetablesModel.setTotalProducts(mTotalProducts);

                List.add(mVegetablesModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                mLayoutNoSearchResults.setVisibility(View.GONE);
                footer.setVisibility(View.VISIBLE);
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
                            totalItem=x-1;
                            //  Toast.makeText(mActivity,x+","+ y,Toast.LENGTH_LONG).show();

                            passDataForScroll();
                            // mListView.setSelection(total);
                        } else {
                            footer.setVisibility(View.GONE);
                           // mListView.removeFooterView(footer);

                        }
                    }
                });

            } else {
                footer.setVisibility(View.GONE);

                 mLayoutNoSearchResults.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
        adapter.notifyDataSetChanged();
    }

    private void handleNameSearchListScroll(String responseType) {
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

                VegetablesModel mVegetablesModel = new VegetablesModel();
                mVegetablesModel.setmProductName(mProductName);
                mVegetablesModel.setmSellerAddress(mAddress);
                mVegetablesModel.setmAmount(mAmount);
                mVegetablesModel.setmQuantity(mQuantity);
                mVegetablesModel.setmSellerName(mName);
                mVegetablesModel.setmSellerMobile(mMobile);
                mVegetablesModel.setmSellerEmail(mEmail);
                mVegetablesModel.setmProductCode(mProductCode);
                mVegetablesModel.setmQuantityUnit(mQuantityUnit);
                mVegetablesModel.setmThumbnailUrl(mImageUrl);
                mVegetablesModel.setTotalProducts(mTotalProducts);

                List.add(mVegetablesModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                ListCount = List.get(0).getTotalProducts();
                footer.setVisibility(View.VISIBLE);
                mLayoutNoSearchResults.setVisibility(View.GONE);
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
                            y = x + 11;
                            totalItem=x-1;
                            passDataForScroll();
                        } else {
                            footer.setVisibility(View.GONE);

                        }
                    }
                });

            }
            else{
                mListView.removeFooterView(footer);
                mLayoutNoSearchResults.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
        adapter.notifyDataSetChanged();

    }


    private void handlemDistrictSearch(String responseType) {
        List.clear();
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
                String mImageUrl = mJson.getString("ProductImagePath");
                int mTotalProducts=mJson.getInt("totalProducts");

                VegetablesModel mVegetablesModel = new VegetablesModel();
                mVegetablesModel.setmProductName(mProductName);
                mVegetablesModel.setmSellerAddress(mAddress);
                mVegetablesModel.setmAmount(mAmount);
                mVegetablesModel.setmQuantity(mQuantity);
                mVegetablesModel.setmSellerName(mName);
                mVegetablesModel.setmSellerMobile(mMobile);
                mVegetablesModel.setmSellerEmail(mEmail);
                mVegetablesModel.setmProductCode(mProductCode);
                mVegetablesModel.setmQuantityUnit(mQuantityUnit);
                mVegetablesModel.setmThumbnailUrl(mImageUrl);
                mVegetablesModel.setTotalProducts(mTotalProducts);

                List.add(mVegetablesModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                mLayoutNoSearchResults.setVisibility(View.GONE);
                footer.setVisibility(View.VISIBLE);
                Collections.sort(List);
                adapter = new VegetablesAdapter(mActivity, List);
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
                            y = x + 11;
                            totalItem=x-1;
                            passDataForScroll();
                        } else {
                            footer.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                footer.setVisibility(View.GONE);
                mLayoutNoSearchResults.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
         adapter.notifyDataSetChanged();

    }

    private void handlemLocationSearch(String responseType) {
        List.clear();
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
                String mImageUrl = mJson.getString("ProductImagePath");
                int mTotalProducts=mJson.getInt("totalProducts");

                VegetablesModel mVegetablesModel = new VegetablesModel();
                mVegetablesModel.setmProductName(mProductName);
                mVegetablesModel.setmSellerAddress(mAddress);
                mVegetablesModel.setmAmount(mAmount);
                mVegetablesModel.setmQuantity(mQuantity);
                mVegetablesModel.setmSellerName(mName);
                mVegetablesModel.setmSellerMobile(mMobile);
                mVegetablesModel.setmSellerEmail(mEmail);
                mVegetablesModel.setmProductCode(mProductCode);
                mVegetablesModel.setmQuantityUnit(mQuantityUnit);
                mVegetablesModel.setmThumbnailUrl(mImageUrl);
                mVegetablesModel.setTotalProducts(mTotalProducts);

                List.add(mVegetablesModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                mLayoutNoSearchResults.setVisibility(View.GONE);

                footer.setVisibility(View.VISIBLE);
                Collections.sort(List);
                adapter = new VegetablesAdapter(mActivity, List);
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
                            footer.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                footer.setVisibility(View.GONE);
                mLayoutNoSearchResults.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
         adapter.notifyDataSetChanged();
    }

    private void handleNameSearchList(String responseType) {
        List.clear();

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

                VegetablesModel mVegetablesModel = new VegetablesModel();
                mVegetablesModel.setmProductName(mProductName);
                mVegetablesModel.setmSellerAddress(mAddress);
                mVegetablesModel.setmAmount(mAmount);
                mVegetablesModel.setmQuantity(mQuantity);
                mVegetablesModel.setmSellerName(mName);
                mVegetablesModel.setmSellerMobile(mMobile);
                mVegetablesModel.setmSellerEmail(mEmail);
                mVegetablesModel.setmProductCode(mProductCode);
                mVegetablesModel.setmQuantityUnit(mQuantityUnit);
                mVegetablesModel.setmThumbnailUrl(mImageUrl);
                mVegetablesModel.setTotalProducts(mTotalProducts);

                List.add(mVegetablesModel);
            }
        } catch (Exception e) {

        }
        try {
            if (List != null && List.size() > 0) {
                footer.setVisibility(View.VISIBLE);

                ListCount = List.get(0).getTotalProducts();
                mLayoutNoSearchResults.setVisibility(View.GONE);
                Collections.sort(List);
                adapter = new VegetablesAdapter(mActivity, List);
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
                            footer.setVisibility(View.GONE);
                        }
                    }
                });

            }
            else{
                footer.setVisibility(View.GONE);
               mLayoutNoSearchResults.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
         adapter.notifyDataSetChanged();

    }

    private void passDataForScroll() {
        if(selectedId==1){
            try {
                JSONObject mVegetableDetails = new JSONObject();
                mVegetableDetails.put("ProductName",mEditProductName.getText().toString());
                mVegetableDetails.put("CountFrom",x);
                mVegetableDetails.put("CountTo",y);
                super.showProgress("Please Wait");
                mServiceCallEnum = ServiceCallEnum.PRODUCTNAMESEARCHSCROLL;
                super.establishConnection(Request.Method.POST, Constants.mNameSearchUrl, mVegetableDetails);
            } catch (Exception e) {

            }
        }
        else if(selectedId==2){
            try {
                JSONObject mVegetableDetails = new JSONObject();
                mVegetableDetails.put("district",mEditDistrict.getText().toString());
                mVegetableDetails.put("CountFrom",x);
                mVegetableDetails.put("CountTo",y);
                super.showProgress("Please Wait");
                mServiceCallEnum = ServiceCallEnum.DISTRICTSEARCHSCROLL;
                super.establishConnection(Request.Method.POST, Constants.mDistrictSearchUrl, mVegetableDetails);
            } catch (Exception e) {

            }

        }
        else if(selectedId==3){
            try {
                JSONObject mVegetableDetails = new JSONObject();
                mVegetableDetails.put("Location",mEditLocation.getText().toString());
                mVegetableDetails.put("CountFrom",x);
                mVegetableDetails.put("CountTo",y);
                super.showProgress("Please Wait");
                mServiceCallEnum = ServiceCallEnum.LOCATIONSEARCHSCROLL;
                super.establishConnection(Request.Method.POST, Constants.mLocationSearchUrl, mVegetableDetails);
            } catch (Exception e) {

            }
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_search_select_search: {
                if (mSpinnerSearch.getSelectedItemPosition() == 0) {
                    mEditDistrict.setVisibility(View.GONE);
                    mEditLocation.setVisibility(View.GONE);
                    mEditProductName.setVisibility(View.GONE);
                }
                if (mSpinnerSearch.getSelectedItemPosition() == 1) {
                    mEditProductName.setVisibility(View.VISIBLE);
                    mEditDistrict.setVisibility(View.GONE);
                    mEditLocation.setVisibility(View.GONE);
                    selectedId = 1;
                } else if (mSpinnerSearch.getSelectedItemPosition() == 2) {
                    mEditDistrict.setVisibility(View.VISIBLE);
                    mEditLocation.setVisibility(View.GONE);
                    mEditProductName.setVisibility(View.GONE);
                    selectedId = 2;
                } else if (mSpinnerSearch.getSelectedItemPosition() == 3) {
                    mEditLocation.setVisibility(View.VISIBLE);
                    mEditProductName.setVisibility(View.GONE);
                    mEditDistrict.setVisibility(View.GONE);
                    selectedId = 3;
                }
            }
        }

    }

    private void passDataForDistrictSearch() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("district",mEditDistrict.getText().toString());
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            mServiceCallEnum = ServiceCallEnum.DISTRICTSEARCH;
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mDistrictSearchUrl, mVegetableDetails);
        } catch (Exception e) {

        }

    }

    private void passDataForLocationSearch() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("Location",mEditLocation.getText().toString());
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            mServiceCallEnum = ServiceCallEnum.LOCATIONSEARCH;
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mLocationSearchUrl, mVegetableDetails);
        } catch (Exception e) {

        }

    }

    private void passDataForNameSearch() {
        try {
            JSONObject mVegetableDetails = new JSONObject();
            mVegetableDetails.put("ProductName",mEditProductName.getText().toString());
            mVegetableDetails.put("CountFrom",x);
            mVegetableDetails.put("CountTo",y);
            mServiceCallEnum = ServiceCallEnum.PRODUCTNAMESEARCH;
            super.showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mNameSearchUrl, mVegetableDetails);
        } catch (Exception e) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
