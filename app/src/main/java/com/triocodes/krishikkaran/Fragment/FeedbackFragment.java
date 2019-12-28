package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by admin on 24-02-16.
 */
public class FeedbackFragment extends ParentFragment implements VolleyCallback, View.OnClickListener {
    Activity mActivity;
    Handler mhandler;
    HashMap<String, Object> extras;
    EditText mEditName, mEditEmail, mEditPhone, mEditMessage;
    TextView mTextSend;
    ServiceCallEnum mServiceCallEnum;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, FeedbackFragment.this);
        mhandler = new Handler();
        extras = new HashMap<String, Object>();
        mEditEmail = (EditText) mActivity.findViewById(R.id.edittext_feedback_email);
        mEditName = (EditText) mActivity.findViewById(R.id.edittext_feedback_name);
        mEditMessage = (EditText) mActivity.findViewById(R.id.edittext_feedback_message);
        mEditPhone = (EditText) mActivity.findViewById(R.id.edittext_feedback_phone);
        mTextSend = (TextView) mActivity.findViewById(R.id.text_button_feedback_send);
        mTextSend.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.text_button_feedback_send: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {
                        if (mobileValidation(mEditPhone.getText().toString())) {
                            if (emailValidation(mEditEmail.getText().toString())) {
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
            }
        }

    }

    private void clearData() {
        mEditName.getText().clear();
        mEditEmail.getText().clear();
        mEditPhone.getText().clear();
        mEditMessage.getText().clear();

        mEditName.setHintTextColor(Color.parseColor("#398808"));
        mEditEmail.setHintTextColor(Color.parseColor("#398808"));
        mEditPhone.setHintTextColor(Color.parseColor("#398808"));
        mEditMessage.setHintTextColor(Color.parseColor("#398808"));

    }

    private boolean nullChecker() {
        if (mEditName.getText().toString().trim().length() == 0 || mEditName.getText().toString().trim() == "") {
            shakeEditText(R.id.edittext_feedback_name);
            return false;
        }
        if (mEditEmail.getText().toString().trim().length() == 0 || mEditEmail.getText().toString().trim() == "") {
            shakeEditText(R.id.edittext_feedback_email);
            return false;
        }
        if (mEditPhone.getText().toString().trim().length() == 0 || mEditPhone.getText().toString().trim() == "") {
            shakeEditText(R.id.edittext_feedback_phone);
            return false;
        }
        if (mEditMessage.getText().toString().trim().length() == 0 || mEditMessage.getText().toString().trim() == "") {
            shakeEditText(R.id.edittext_feedback_message);
            return false;
        }
        return true;
    }

    private void connectToServer() {
        JSONObject mFeedbackDetails = new JSONObject();
        try {
            mFeedbackDetails.put("Name", mEditName.getText().toString());
            mFeedbackDetails.put("Email", mEditEmail.getText().toString());
            mFeedbackDetails.put("Phone", mEditPhone.getText().toString());
            mFeedbackDetails.put("Message", mEditMessage.getText().toString());
            showProgress("Please Wait");
            super.establishConnection(Request.Method.POST, Constants.mFeedbackUrl, mFeedbackDetails);
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
                        JSONObject mJsonObject = new JSONObject(responseType);
                        String mJsonString = mJsonObject.getString("d");
                        JSONArray mJsonArray = new JSONArray(mJsonString);
                        JSONObject json = new JSONObject(String.valueOf(mJsonArray.get(0)));
                        String mStatus = json.getString("status");
                        if (mStatus.equalsIgnoreCase("true")) {
                            showSuccess("Success", "Feedback updated successfully", FragmentTransactionEnum.NONE);
                            clearData();
                        } else {
                            showMessage("Sorry", "Could not update feedback");
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
        if (mhandler.post(new Runnable() {
            @Override
            public void run() {
                String responseType = (String) extras.get("response");
                showWarning("Something went wrong","Please check for your network");
            }
        })) ;

    }

    @Override
    public HashMap<String, Object> getExtras() {
        return extras;
    }
}
