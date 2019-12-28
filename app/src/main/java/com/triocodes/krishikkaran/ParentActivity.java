package com.triocodes.krishikkaran;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Volley.CustomJSONObjectRequest;
import com.triocodes.krishikkaran.Volley.CustomVolleyRequestQueue;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by admin on 26-02-16.
 */
public class ParentActivity extends AppCompatActivity {
    //implements Response.Listener,Response.ErrorListener
    private Activity mActivity;
    private VolleyCallback mVolleyCallback;
    SweetAlertDialog mAlertDialog ;
    private RequestQueue mQueue;
    Calendar myCalendar = Calendar.getInstance();
    private EditText mEditText;
    //private FragmentListenerCallback mAlertCallback;
    private CustomJSONObjectRequest CustomRequest;
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private static final String SHARED_DETAILS = "SHARED_DETAILS";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private final Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    public boolean emailValidation(String str) {
        if (str.equals("")
                || EMAIL_ADDRESS_PATTERN.matcher(str).matches() == false)
            return false;
        else
            return true;
    }
    public boolean mobileValidation(String strmob){
        // if((strmob.length()==10)&&(strmob.contains("[0-9]+")))
        if(strmob.matches("[0-9]{10}"))
            return true;
        else
            return false;
    }
    public boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        if(m.matches())
            return true;
        else
            return false;
    }
    public boolean isPercent(String strpercent) {

        if(Integer.parseInt(strpercent)<=100&&Integer.parseInt(strpercent)>=0)
            return true;
        else
            return false;
    }

    public  void initialize(Activity mActivity,VolleyCallback mVolleyCallback) {
        this.mActivity = mActivity;
        this.mVolleyCallback = mVolleyCallback;
//        this.mAlertCallback = (FragmentListenerCallback)mActivity;

      /*  this.appSharedPrefs = mActivity.getSharedPreferences(SHARED_DETAILS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();*/

    }
   /* public  void establishConnection(int method, String url, JSONObject jsonRequest)
    {

        mQueue = CustomVolleyRequestQueue.getInstance(this.mActivity)
                .getRequestQueue();
        CustomRequest = new CustomJSONObjectRequest(method, url,
                jsonRequest, this, this);
        CustomRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CustomRequest.setTag(REQUEST_TAG);
        mQueue.add(CustomRequest);
        // Toast.makeText(mActivity,"here",Toast.LENGTH_LONG).show();

    }*/

    public void dismissAlert() {

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public void showProgress(String message) {

        mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(message);
        mAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#398808"));
        mAlertDialog.show();
        mAlertDialog.setCancelable(false);


    }




    public void showError(String header, String message) {

        mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
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

    public void showMessage(String header, String message) {

        mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
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

    public void showWarning(String header, String message) {

        mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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

    public void showSuccess(String header, String message) {

        mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(header)
                .setContentText(message);
        mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {


                dismissAlert();
            }
        });
        mAlertDialog.show();
    }

    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;

    }

    public void shakeEdittext(int widgetId) {
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(this.findViewById(widgetId));
        EditText mEdittext = (EditText) this.findViewById(widgetId);
        mEdittext.setHintTextColor(Color.RED);
        mEdittext.requestFocus();
    }

    public void shakeSpinner(int widgetId) {
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(this.findViewById(widgetId));

        Spinner mSpinnertext = (Spinner) this.findViewById(widgetId);
        mSpinnertext.setBackgroundColor(Color.parseColor("#66f9b6c1"));
        mSpinnertext.requestFocus();

    }



  /*  @Override
    public void onErrorResponse(VolleyError volleyError) {
        String mErrorMessage;

        NetworkResponse response = volleyError.networkResponse;
        if(response != null && response.data != null){
            mErrorMessage = String.valueOf(response.statusCode);
            switch(response.statusCode){

                case HttpURLConnection.HTTP_NOT_FOUND:
                    mErrorMessage = mErrorMessage+" error page not found";
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    mErrorMessage = mErrorMessage+" error internal server error";
                    break;

            }

        }
        else
        {
            mErrorMessage ="Unexpected error from server "+ volleyError.getMessage().toString();


        }
        this.mVolleyCallback.getExtras().put(mActivity.getResources().getString(R.string.string_server_response), mErrorMessage);
        this.mVolleyCallback.volleyOnError();
    }

    @Override
    public void onResponse(Object response) {
        this.mVolleyCallback.getExtras().put(mActivity.getResources().getString(R.string.string_server_response), response.toString());
        this.mVolleyCallback.volleyOnSuccess();
    }
*/
    public void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    DatePickerDialog.OnDateSetListener date= new DatePickerDialog.OnDateSetListener()  {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    public void setDate(int widgetId) {
        mEditText = (EditText) this.findViewById(widgetId);
        new DatePickerDialog(this,date,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mEditText.setText(sdf.format(myCalendar.getTime()));
    }
}
