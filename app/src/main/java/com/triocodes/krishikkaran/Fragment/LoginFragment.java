package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
    import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.triocodes.krishikkaran.Activity.MainActivity;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by admin on 08-03-16.
 */
public class LoginFragment extends ParentFragment implements VolleyCallback, View.OnClickListener {
    Activity mActivity;
    HashMap<String, Object> extras;
    Handler mhandler;
    TextView mTextLogin;
    EditText mEditMobile, mEditPassword;
    ServiceCallEnum mServiceCallEnum;
    SweetAlertDialog mAlertDialog;
    private RequestQueue mQueue;
    private CustomJSONObjectRequest CustomRequest;

    public LoginFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, LoginFragment.this);
        extras = new HashMap<String, Object>();
        mhandler = new Handler();
        mTextLogin = (TextView) mActivity.findViewById(R.id.text_login);
        mTextLogin.setOnClickListener(this);
        mEditMobile = (EditText) mActivity.findViewById(R.id.edittext_login_mobile);
        mEditPassword = (EditText) mActivity.findViewById(R.id.edittext_login_password);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, null);
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
        if (isConnectingToInternet()) {
            if (nullChecker()) {
                if (mobileValidation(mEditMobile.getText().toString())) {
                    connectToServer();
                } else {
                    showMessage("Content not correct", "Mobile number should be valid");
                }
            }
        } else {
            showError("Connectivity Issue", "No internet connection available", FragmentTransactionEnum.NONE);
        }

    }

    private boolean nullChecker() {
        if (mEditMobile.getText().toString().trim().length() == 0 || mEditMobile.getText().toString() == "") {
            shakeEditText(R.id.edittext_login_mobile);
            return false;
        } else if (mEditPassword.getText().toString().trim().length() == 0 || mEditPassword.getText().toString() == "") {
            shakeEditText(R.id.edittext_login_password);
            return false;
        }
        return true;
    }

    private void connectToServer() {
        JSONObject mLoginObject = new JSONObject();
        try {
            mLoginObject.put("MobileNumber", mEditMobile.getText().toString());
            mLoginObject.put("Password", mEditPassword.getText().toString());

            showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.LOGIN;
            establishConnection(Request.Method.POST, Constants.mLoginUrl, mLoginObject);
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
                        if (mServiceCallEnum == ServiceCallEnum.LOGIN) {
                            JSONObject mJsonObject = new JSONObject(responseType);
                            String mString = mJsonObject.getString("d");
                            JSONArray mJsonArray = new JSONArray(mString);
                            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
                            String mStatus = mJson.getString("status");
                            String mRegistrationId = mJson.getString("RegistrationID");
                            if (mStatus.equalsIgnoreCase("true") && mRegistrationId.equalsIgnoreCase("2")) {
                                showMessage("Login Failed", "Please Register");
                            } else if (mStatus.equalsIgnoreCase("true") && mRegistrationId != "2") {
                                DataBaseQueryHelper.getInstance().insertEntryLogin(mEditMobile.getText().toString(), mEditPassword.getText().toString(), mRegistrationId);

                                startActivity(new Intent(mActivity, MainActivity.class));
                            } else if (mStatus.equalsIgnoreCase("false") && mRegistrationId.equalsIgnoreCase("0")) {
                                showMessage("Sorry", "Mobile number or Password incorrect");
                            }

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

    public void dismissAlerts() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }


}
