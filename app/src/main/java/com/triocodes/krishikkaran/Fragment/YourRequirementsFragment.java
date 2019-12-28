package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.triocodes.krishikkaran.Adapter.SpinnerAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.SpinnerModel;
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
public class YourRequirementsFragment extends ParentFragment implements VolleyCallback, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Activity mActivity;
    HashMap<String, Object> extras;
    Handler mhandler;
    ServiceCallEnum mServiceCallEnum;
    TextView mTextPost;
    EditText mEditProductName, mEditQuantity, mEditExpectedPrice, mEditUserName, mEditUserEmail, mEditUserMobile, mEditExactLocation, mEditJob;
    RadioGroup mRequirement;
    RadioButton mRbSinglePurchase, mRbDaily, mRbWeekly, mRbmonthly, mRbYearly;
    CheckBox mCheckDeliveryRequired;
    Spinner mSpinnerquantity, mSpinnerCategory, mSpinnerState, mSpinnerDistrict, mSpinnerLocation;
    ArrayList<SpinnerModel> mSpinnerquantityList,mSpinnerCategoryList,mSpinnerStateList,mSpinnerDistrictList,mSpinnerLocationList;
    int spinnerSelectedId;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.your_requirements_fragment_layout, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, YourRequirementsFragment.this);
        extras=new HashMap<String, Object>();
        mhandler=new Handler();

        mTextPost = (TextView) mActivity.findViewById(R.id.text_button_your_req_post);
        mTextPost.setOnClickListener(this);

        mEditProductName = (EditText) mActivity.findViewById(R.id.edittext_your_req_product_name);
        mEditQuantity = (EditText) mActivity.findViewById(R.id.edittext_your_req_total_quantity);
        mEditExpectedPrice = (EditText) mActivity.findViewById(R.id.edittext_your_req_expected_price);
        mEditUserName = (EditText) mActivity.findViewById(R.id.edittext_your_req_name);
        mEditUserEmail = (EditText) mActivity.findViewById(R.id.edittext_your_req_email);
        mEditUserMobile = (EditText) mActivity.findViewById(R.id.edittext_your_req_mobile);
        mEditExactLocation = (EditText) mActivity.findViewById(R.id.edittext_your_req_exact_location);

        mEditExactLocation.setOnClickListener(this);

        mEditJob = (EditText) mActivity.findViewById(R.id.edittext_your_req_job);

        mSpinnerCategory = (Spinner) mActivity.findViewById(R.id.spinner_your_req_catagory);
        mSpinnerquantity = (Spinner) mActivity.findViewById(R.id.spinner_your_req_quantity_measure);
        mSpinnerState = (Spinner) mActivity.findViewById(R.id.spinner_your_req_state);
        mSpinnerDistrict = (Spinner) mActivity.findViewById(R.id.spinner_your_req_district);
        mSpinnerLocation = (Spinner) mActivity.findViewById(R.id.spinner_your_req_location);

        mCheckDeliveryRequired = (CheckBox) mActivity.findViewById(R.id.checkbox_your_req_delivery_required);

        mRbSinglePurchase = (RadioButton) mActivity.findViewById(R.id.rb_your_req_single_purchase);
        mRbDaily = (RadioButton) mActivity.findViewById(R.id.rb_your_req_daily);
        mRbWeekly = (RadioButton) mActivity.findViewById(R.id.rb_your_req_weekly);
        mRbmonthly = (RadioButton) mActivity.findViewById(R.id.rb_your_req_monthly);
        mRbYearly = (RadioButton) mActivity.findViewById(R.id.rb_your_req_yearly);

        mSpinnerState.setOnItemSelectedListener(this);
        mSpinnerDistrict.setOnItemSelectedListener(this);
        mSpinnerLocation.setOnItemSelectedListener(this);
        mSpinnerCategory.setOnItemSelectedListener(this);
        mSpinnerquantity.setOnItemSelectedListener(this);

        showProgress("Please Wait");
        mServiceCallEnum=ServiceCallEnum.CATAGORY;
        super.establishConnection(Request.Method.POST, Constants.mCatagoryUrl, new JSONObject());
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
            case R.id.text_button_your_req_post: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {
                        if (mobileValidation(mEditUserMobile.getText().toString())) {
                            if (emailValidation(mEditUserEmail.getText().toString())) {
                                connectToServer();
                            } else {
                                showMessage("Content not correct", "Email should be valid");
                            }
                        } else {
                            showMessage("Content not correct", "Mobile number should be valid");
                        }
                    }
                } else {
                    showError("Connectivity Issue", "No internet connection available", FragmentTransactionEnum.NONE);
                }
                break;
            }

        }

    }


    private boolean nullChecker() {
        if (mEditProductName.getText().toString().trim().length() == 0 || mEditProductName.getText().toString() == "") {
            super.shakeEditText(R.id.edittext_your_req_product_name);
            return false;
        }  else if (mEditQuantity.getText().toString().trim().length() == 0 || mEditQuantity.getText().toString() == "") {
            super.shakeEditText(R.id.edittext_your_req_total_quantity);
            return false;
        } else if (mEditExpectedPrice.getText().toString().trim().length() == 0 || mEditExpectedPrice.getText().toString() == "") {
            super.shakeEditText(R.id.edittext_your_req_expected_price);
            return false;
        } else if (mEditExactLocation.getText().toString().trim().length() == 0 || mEditExactLocation.getText().toString() == "") {
            shakeEditText(R.id.edittext_your_req_exact_location);
            return false;
        } else if (mEditJob.getText().toString().trim().length() == 0 || mEditJob.getText().toString() == "") {
            shakeEditText(R.id.edittext_your_req_job);
            return false;
        } else if (mEditUserName.getText().toString().trim().length() == 0 || mEditUserName.getText().toString() == "") {
            shakeEditText(R.id.edittext_your_req_name);
            return false;
        } else if (mEditUserMobile.getText().toString().trim().length() == 0 || mEditUserMobile.getText().toString() == "") {
            shakeEditText(R.id.edittext_your_req_mobile);
            return false;
        } else if (mEditUserEmail.getText().toString().trim().length() == 0 || mEditUserEmail.getText().toString() == "") {
            shakeEditText(R.id.edittext_your_req_email);
            return false;
        }
        return true;
    }

    private void connectToServer() {
        JSONObject mYourRequirementsDetails = new JSONObject();
        try {
            String mRequirement = null;
            if (mRbSinglePurchase.isChecked()) {
                mRequirement = "Single Purchase";
            } else if (mRbDaily.isChecked()) {
                mRequirement = "Daily";
            } else if (mRbWeekly.isChecked()) {
                mRequirement = "Weekly";
            } else if (mRbmonthly.isChecked()) {
                mRequirement = "Monthly";
            } else if (mRbYearly.isChecked()) {
                mRequirement = "Yearly";
            }

            boolean mDeliveryRequired = false;
            if (mCheckDeliveryRequired.isChecked()) {
                mDeliveryRequired = true;
            }
            mYourRequirementsDetails.put("Category", mSpinnerCategoryList.get(mSpinnerCategory.getSelectedItemPosition()).getText());
            mYourRequirementsDetails.put("ProductName", mEditProductName.getText().toString());
            mYourRequirementsDetails.put("ProductQuantity", mEditQuantity.getText().toString());
            mYourRequirementsDetails.put("QuantityMeasure", mSpinnerquantityList.get(mSpinnerquantity.getSelectedItemPosition()).getId());
            mYourRequirementsDetails.put("ExpectedPrice", mEditExpectedPrice.getText().toString());
            mYourRequirementsDetails.put("Requirment", mRequirement);
            mYourRequirementsDetails.put("DeliveryRequired", mDeliveryRequired);
            mYourRequirementsDetails.put("Location", mSpinnerLocationList.get(mSpinnerLocation.getSelectedItemPosition()).getPinCode());
            mYourRequirementsDetails.put("ExactLocation", mEditExactLocation.getText().toString());
            mYourRequirementsDetails.put("Name", mEditUserName.getText().toString());
            mYourRequirementsDetails.put("MobileNumber", mEditUserMobile.getText().toString());
            mYourRequirementsDetails.put("Email", mEditUserEmail.getText().toString());
            mYourRequirementsDetails.put("Job", mEditJob.getText().toString());
            mYourRequirementsDetails.put("CreatedBy", "Admin");

            JSONArray mYourRequirementsArray = new JSONArray();
            mYourRequirementsArray.put(mYourRequirementsDetails);
            showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.YOURREQUIREMENTS;
            super.establishConnection(Request.Method.POST, Constants.mYourRequirementsUrl, mYourRequirementsDetails);


        } catch (Exception e) {

        }

    }


    @Override
    public void volleyOnSuccess() {
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                if (!extras.isEmpty()) {
                    try {
                        String responseType = (String) extras.get("response");

                        if(mServiceCallEnum==ServiceCallEnum.CATAGORY){
                            setmSpinnerCatagory(responseType);
                            mServiceCallEnum=ServiceCallEnum.QUANTITY;
                            establishConnection(Request.Method.POST,Constants.mQuantityUnitUrl,new JSONObject());
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.QUANTITY){
                            dismissAlert();
                            setmSpinnerQuantityUnit(responseType);
                            passDataForState();
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.STATE){
                            setmSpinnerState(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.DISTRICT){
                            dismissAlert();
                            setmSpinnerDistrict(responseType);
                        }
                        else if(mServiceCallEnum==ServiceCallEnum.POSTOFFICE){
                            dismissAlert();
                            setmSpinnerLocation(responseType);
                        }
                        else if (mServiceCallEnum == ServiceCallEnum.YOURREQUIREMENTS) {
                            dismissAlert();
                            JSONObject mJsonObject = new JSONObject(responseType);
                            String mString = mJsonObject.getString("d");
                            JSONArray mJsonArray = new JSONArray(mString);
                            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
                            String mStatus = mJson.getString("status");
                            if (mStatus.equalsIgnoreCase("true")) {
                                showSuccess("Success", "Your requirement has been updated succesfully", FragmentTransactionEnum.YOURREQUIREMENTS);
                                clearData();
                            } else {
                                showMessage("Sorry", "Failed to post your requirement");
                            }
                        }


                    } catch (Exception e) {

                    }
                }
            }
        });

    }


    private void clearData() {
        mEditProductName.getText().clear();
        mEditQuantity.getText().clear();
        mEditExpectedPrice.getText().clear();
        mEditUserName.getText().clear();
        mEditUserEmail.getText().clear();
        mEditUserMobile.getText().clear();
        mEditExactLocation.getText().clear();
        mEditJob.getText().clear();


        mEditProductName.setHintTextColor(Color.parseColor("#398808"));
        mEditQuantity.setHintTextColor(Color.parseColor("#398808"));
        mEditExpectedPrice.setHintTextColor(Color.parseColor("#398808"));
        mEditUserName.setHintTextColor(Color.parseColor("#398808"));
        mEditUserEmail.setHintTextColor(Color.parseColor("#398808"));
        mEditUserMobile.setHintTextColor(Color.parseColor("#398808"));
        mEditExactLocation.setHintTextColor(Color.parseColor("#398808"));
        mEditJob.setHintTextColor(Color.parseColor("#398808"));
    }

    private void passDataForState() {
        JSONObject mStateDetails=new JSONObject();
        try{
            mStateDetails.put("CountryId", 1);
            mServiceCallEnum=ServiceCallEnum.STATE;
            super.establishConnection(Request.Method.POST,Constants.mStateUrl,mStateDetails);
        }catch (Exception e){

        }
    }

    private void setmSpinnerLocation(String responseType) {
        mSpinnerLocationList=new ArrayList<>();
        try{
            JSONObject mPostOfficeObject=new JSONObject(responseType);
            String mPostOfficeString=mPostOfficeObject.getString("d");
            JSONArray mPostOfficeArray=new JSONArray(mPostOfficeString);
            for(int i=0;i<mPostOfficeArray.length();i++){
                JSONObject json_obj=mPostOfficeArray.getJSONObject(i);
                int mId=json_obj.getInt("PostOfficeId");
                String mName=json_obj.getString("PostOffice");
                String mPinCode=json_obj.getString("PinCode");

                SpinnerModel mSpinnerModel=new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);
                mSpinnerModel.setPinCode(mPinCode);
                mSpinnerLocationList.add(mSpinnerModel);
            }
        }catch (Exception e){

        }
        Collections.sort(mSpinnerLocationList);
        SpinnerAdapter adapterLevel=new SpinnerAdapter(mActivity,mSpinnerLocationList);
        mSpinnerLocation.setAdapter(adapterLevel);
    }

    private void setmSpinnerDistrict(String responseType) {
        mSpinnerDistrictList=new ArrayList<>();
        try{
            JSONObject mJsonObj=new JSONObject(responseType);
            String mString=mJsonObj.getString("d");
            JSONArray mJsonArray=new JSONArray(mString);
            for(int i=0;i<mJsonArray.length();i++){
                JSONObject mJson=mJsonArray.getJSONObject(i);
                int mId=mJson.getInt("DistrictId");
                String mName=mJson.getString("District");

                SpinnerModel mSpinnerModel=new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);
                mSpinnerDistrictList.add(mSpinnerModel);
            }
        }catch (Exception e){

        }
        Collections.sort(mSpinnerDistrictList);
        SpinnerAdapter mSpinnerAdapter=new SpinnerAdapter(mActivity,mSpinnerDistrictList);
        mSpinnerDistrict.setAdapter(mSpinnerAdapter);
    }

    private void setmSpinnerState(String responseType) {
        mSpinnerStateList=new ArrayList<>();
        try{
            JSONObject mJsonObj=new JSONObject(responseType);
            String mString=mJsonObj.getString("d");
            JSONArray mJsonArray=new JSONArray(mString);
            for(int i=0;i<mJsonArray.length();i++){
                JSONObject mJson=mJsonArray.getJSONObject(i);
                int mId=mJson.getInt("StateId");
                String mName=mJson.getString("State");
                SpinnerModel mSpinnerModel=new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);
                mSpinnerStateList.add(mSpinnerModel);
            }
        }catch (Exception e){

        }
        Collections.sort(mSpinnerStateList);
        SpinnerAdapter mSpinnerAdapter=new SpinnerAdapter(mActivity,mSpinnerStateList);
        mSpinnerState.setAdapter(mSpinnerAdapter);
    }


    private void setmSpinnerQuantityUnit(String responseType) {
        mSpinnerquantityList=new ArrayList<>();
        try{
            JSONObject mJsonObj=new JSONObject(responseType);
            String mString=mJsonObj.getString("d");
            JSONArray mJsonArray=new JSONArray(mString);
            for(int i=0;i<mJsonArray.length();i++){
                JSONObject mJson=mJsonArray.getJSONObject(i);
                int mId=mJson.getInt("QuantityId");
                String mName=mJson.getString("Quantity");
                SpinnerModel mSpinnerModel=new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);
                mSpinnerquantityList.add(mSpinnerModel);

            }
        }catch (Exception e){

        }
        Collections.sort(mSpinnerquantityList);
        SpinnerAdapter mSpinnerAdapter=new SpinnerAdapter(mActivity,mSpinnerquantityList);
        mSpinnerquantity.setAdapter(mSpinnerAdapter);
    }

    private void setmSpinnerCatagory(String responseType) {
        mSpinnerCategoryList=new ArrayList<>();
        try {
            JSONObject mJsonObj = new JSONObject(responseType);
            String mString=mJsonObj.getString("d");
            JSONArray mJsonArray=new JSONArray(mString);
            for(int i=0;i<mJsonArray.length();i++){
                JSONObject mJson=mJsonArray.getJSONObject(i);
                int mId=mJson.getInt("CategoryID");
                String mName=mJson.getString("CategoryName");

                SpinnerModel mSpinnerModel=new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);

                mSpinnerCategoryList.add(mSpinnerModel);
            }
        }catch (Exception e){

        }
        Collections.sort(mSpinnerCategoryList);
        SpinnerAdapter mSpinnertAdapter=new SpinnerAdapter(mActivity,mSpinnerCategoryList);
        mSpinnerCategory.setAdapter(mSpinnertAdapter);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_your_req_state: {
                spinnerSelectedId=mSpinnerStateList.get(mSpinnerState.getSelectedItemPosition()).getId();
                passDataForDistrict(spinnerSelectedId);
                break;
            }
            case R.id.spinner_your_req_district:{
                spinnerSelectedId=mSpinnerDistrictList.get(mSpinnerDistrict.getSelectedItemPosition()).getId();
                passDataForLocation(spinnerSelectedId);
            }

        }

    }

    private void passDataForLocation(int spinnerSelectedId) {
        JSONObject mLocationDetails=new JSONObject();
        try{
            mLocationDetails.put("DistrictId",spinnerSelectedId);
            showProgress("Please Wait");
            mServiceCallEnum=ServiceCallEnum.POSTOFFICE;
            establishConnection(Request.Method.POST,Constants.mPostOfficeUrl,mLocationDetails);
        }catch (Exception e){

        }
    }

    private void passDataForDistrict(int spinnerSelectedId) {
        JSONObject mDistrictDetails=new JSONObject();
        try{
            mDistrictDetails.put("StateId",spinnerSelectedId);
            showProgress("Please Wait");
            mServiceCallEnum=ServiceCallEnum.DISTRICT;
            super.establishConnection(Request.Method.POST,Constants.mDistrictUrl,mDistrictDetails);
        }catch (Exception e){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
