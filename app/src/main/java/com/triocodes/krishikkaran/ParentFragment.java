package com.triocodes.krishikkaran;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Interface.FragmentListenerCallback;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by admin on 25-02-16.
 */
public class ParentFragment extends Fragment implements Response.Listener,Response.ErrorListener{
    private Activity mActivity;
    private VolleyCallback mVolleyCallback;
    private FragmentListenerCallback mAlertCalback;
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private CustomJSONObjectRequest CustomRequest;
    public static final String REQUEST_TAG = "MainVolleyActivity";
    SweetAlertDialog mAlertDialog;
    private final Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    private static final String SHARED_DETAILS = "SHARED_DETAILS";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public void initialize(Activity mActivity,VolleyCallback mVolleyCallback){
        this.mActivity=mActivity;
        this.mVolleyCallback=mVolleyCallback;
//        this.mAlertCalback=(FragmentListenerCallback)mActivity;
        this.appSharedPrefs=mActivity.getSharedPreferences(SHARED_DETAILS,Activity.MODE_PRIVATE);
        this.prefsEditor=appSharedPrefs.edit();
    }

    public boolean emailValidation(String str){
        if(str.equals("")||EMAIL_ADDRESS_PATTERN.matcher(str).matches()==false)
            return false;
        else
            return true;
    }

    public boolean mobileValidation(String strmob) {

        if (strmob.matches("[0-9]{10}"))
            return true;
        else
            return false;
    }

    public void establishConnection(int method,String url,JSONObject jsonRequest){

        mQueue= CustomVolleyRequestQueue.getInstance(this.mActivity).getRequestQueue();
        CustomRequest=new CustomJSONObjectRequest(method,url,jsonRequest,this,this);
        CustomRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CustomRequest.setTag(REQUEST_TAG);
        mQueue.add(CustomRequest);
       // Toast.makeText(mActivity,"establish connection", Toast.LENGTH_SHORT).show();
    }

    public void showProgress(String message){
        mAlertDialog=new SweetAlertDialog(this.mActivity,SweetAlertDialog.PROGRESS_TYPE).setTitleText(message);
        mAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#398808"));
        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
    }

    public void dismissAlert(){
        if(mAlertDialog!=null&&mAlertDialog.isShowing()){
            mAlertDialog.dismiss();
        }
    }

    public void showError(String header,String message,final FragmentTransactionEnum mCallType){
        mAlertDialog=new SweetAlertDialog(this.mActivity,SweetAlertDialog.ERROR_TYPE).setTitleText(header).setContentText(message);
        mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                toFragment(mCallType);
                dismissAlert();
            }
        });
        mAlertDialog.show();
    }

    public void showSuccess(String header,String message,final FragmentTransactionEnum mCallType){
        mAlertDialog=new SweetAlertDialog(this.mActivity,SweetAlertDialog.SUCCESS_TYPE).setTitleText(header).setContentText(message);
        mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                toFragment(mCallType);
                dismissAlert();
            }
        });
        mAlertDialog.show();
    }

    public void showMessage(String header,String message){
        mAlertDialog=new SweetAlertDialog(this.mActivity,SweetAlertDialog.NORMAL_TYPE).setTitleText(header).setContentText(message);
        mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissAlert();
            }
        });
        mAlertDialog.show();
    }

    public void showWarning(String header, String message) {

        mAlertDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(header)
                .setContentText(message);

        mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                //toFragment(mCalltype);
                dismissAlert();

            }
        });
        mAlertDialog.show();
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity=(ConnectivityManager)this.mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity!=null){
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if(info!=null)
                for (int i=0;i<info.length;i++)
                    if(info[i].getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
        }
        return false;
    }

    public void shakeEditText(int widgetId){
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(this.mActivity.findViewById(widgetId));
        EditText mEditText=(EditText)this.mActivity.findViewById(widgetId);
        mEditText.setHintTextColor(Color.RED);
        mEditText.requestFocus();
    }

    public void shakeSpinner(int widgetId) {
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(this.mActivity.findViewById(widgetId));
        Spinner mSpinnertext = (Spinner) this.mActivity.findViewById(widgetId);
        mSpinnertext.setBackgroundResource(R.drawable.border_spinner_error);
        mSpinnertext.requestFocus();
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
           // mErrorMessage = "Unexpected error from server " + volleyError.getMessage();
        }
        this.mVolleyCallback.getExtras().put(this.mActivity.getResources().getString(R.string.string_server_response), mErrorMessage);
        this.mVolleyCallback.volleyOnError();
    }

    @Override
    public void onResponse(Object response) {
        this.mVolleyCallback.getExtras().put(mActivity.getResources().getString(R.string.string_server_response), response.toString());
      //  Toast.makeText(mActivity,mActivity.getResources().getString(R.string.string_server_response), Toast.LENGTH_SHORT).show();
        this.mVolleyCallback.volleyOnSuccess();
    }





    @Override
    public void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    public void toFragment(FragmentTransactionEnum mType) {
        switch (mType) {


            case NONE:
                break;

            /*case ADDPRODUCTS:
                mAlertCalback.loadAddProduct();
                break;*/
        }
    }

    public int getSharedvalueInteger(String key) {
        return appSharedPrefs.getInt(key, 0);

    }

    public void setSharedvalueInteger(String key, int value) {
        prefsEditor.putInt(key, value).commit();
    }


    public String getSharedvalueString(String key) {
        return appSharedPrefs.getString(key, "error");

    }

    public void setSharedvalueString(String key, String string) {
        prefsEditor.putString(key, string).commit();
    }

    public void clearSp() {
        this.appSharedPrefs = mActivity.getSharedPreferences(SHARED_DETAILS,
                Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.clear();
    }
}
