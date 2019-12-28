package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.triocodes.krishikkaran.Activity.MainActivity;
import com.triocodes.krishikkaran.Adapter.SpinnerAdapter;
import com.triocodes.krishikkaran.Constants.Constants;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Enum.FragmentTransactionEnum;
import com.triocodes.krishikkaran.Enum.ServiceCallEnum;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.Model.SpinnerModel;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegistrationFragment extends ParentFragment implements View.OnClickListener, VolleyCallback, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private Activity mActivity;
    HashMap<String, Object> extras;
    Handler mhandler;
    ServiceCallEnum mServiceCallEnum;
    TextView mTextImageUpload, mTextRegister;
    EditText mEditFirstName, mEditLastName, mEditMobile, mEditLand, mEditAddress, mEditEmail, mEditPassword, mEditConfirmPassword;
    AutoCompleteTextView mEditLocation;
    Spinner mSpinnerState, mSpinnerDistrict, mSpinnerPostOffice;
    ImageView mImageUser;
    private static int RESULT_LOAD_IMAGE = 1;
    private String selectedImagePath;
    ArrayList<SpinnerModel> mSpinnerStateList, mSpinnerDistrictList, mSpinnerPostOfficeList;
    int SpinnerSelectedId;
    SweetAlertDialog mAlertDialog;
    FragmentManager mFragmentManager;
    private Bitmap bitmap;

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCBdSI0UAEoiFYcb5btQrGYGOd9NsgjD-E";


    private Uri mImageCaptureUri;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, RegistrationFragment.this);
        extras = new HashMap<String, Object>();
        mhandler = new Handler();
        mFragmentManager = getFragmentManager();

        mTextImageUpload = (TextView) mActivity.findViewById(R.id.text_register_upload_image);
        mTextRegister = (TextView) mActivity.findViewById(R.id.text_register);
        mTextImageUpload.setOnClickListener(this);
        mTextRegister.setOnClickListener(this);

        mEditFirstName = (EditText) mActivity.findViewById(R.id.edittext_register_firstname);
        //  mEditLastName = (EditText) mActivity.findViewById(R.id.edittext_register_lastname);
        mEditAddress = (EditText) mActivity.findViewById(R.id.edittext_register_address);
        mEditMobile = (EditText) mActivity.findViewById(R.id.edittext_register_mobile_number);
        mEditLand = (EditText) mActivity.findViewById(R.id.edittext_register_land_number);
        mEditEmail = (EditText) mActivity.findViewById(R.id.edittext_register_email);
        mEditLocation = (AutoCompleteTextView) mActivity.findViewById(R.id.edittext_register_location);
        mEditPassword = (EditText) mActivity.findViewById(R.id.edittext_register_password);
        mImageUser = (ImageView) mActivity.findViewById(R.id.image_register);
        mSpinnerState = (Spinner) mActivity.findViewById(R.id.spinner_register_state);
        mSpinnerDistrict = (Spinner) mActivity.findViewById(R.id.spinner_register_district);
        mSpinnerPostOffice = (Spinner) mActivity.findViewById(R.id.spinner_register_post_office);

        mSpinnerState.setOnItemSelectedListener(this);
        mSpinnerDistrict.setOnItemSelectedListener(this);
        mSpinnerPostOffice.setOnItemSelectedListener(this);

        mEditLocation.setAdapter(new GooglePlacesAutocompleteAdapter(mActivity, R.layout.list_locations));
        mEditLocation.setOnItemClickListener(this);

        mServiceCallEnum = ServiceCallEnum.STATE;
        passDataforState();
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
    }

    public ArrayList autocomplete(String input) {

        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;

            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {

                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {

            return resultList;
        } catch (IOException e) {

            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {

        }
        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return String.valueOf(resultList.get(index));
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };

            return filter;
        }

    }


    private void passDataforState() {
        JSONObject mStateObject = new JSONObject();
        try {
            mStateObject.put("CountryId", 1);
            super.establishConnection(Request.Method.POST, Constants.mStateUrl, mStateObject);
        } catch (Exception e) {

        }
    }

    public void setmSpinnerState(String data) {
        mSpinnerStateList = new ArrayList<>();
        try {
            JSONObject mStateObject = new JSONObject(data);
            String mStateString = mStateObject.getString("d");
            JSONArray mStateArray = new JSONArray(mStateString);
            for (int i = 0; i < mStateArray.length(); i++) {
                JSONObject json_obj = mStateArray.getJSONObject(i);
                int mId = json_obj.getInt("StateId");
                String mName = json_obj.getString("State");

                SpinnerModel mSpinnerModel = new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);

                mSpinnerStateList.add(mSpinnerModel);
            }


        } catch (Exception e) {

        }
        Collections.sort(mSpinnerStateList);
        SpinnerAdapter adapterLevel = new SpinnerAdapter(mActivity, mSpinnerStateList);
        mSpinnerState.setAdapter(adapterLevel);
    }

    public void setmSpinnerDistrict(String data) {
        mSpinnerDistrictList = new ArrayList<>();
        try {
            JSONObject mDistrictObject = new JSONObject(data);
            String mDistrictString = mDistrictObject.getString("d");
            JSONArray mDistrictArray = new JSONArray(mDistrictString);
            for (int i = 0; i < mDistrictArray.length(); i++) {
                JSONObject json_obj = mDistrictArray.getJSONObject(i);
                int mId = json_obj.getInt("DistrictId");
                String mName = json_obj.getString("District");

                SpinnerModel mSpinnerModel = new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);
                mSpinnerDistrictList.add(mSpinnerModel);
            }
        } catch (Exception e) {

        }

        Collections.sort(mSpinnerDistrictList);
        SpinnerAdapter adapterLevel = new SpinnerAdapter(mActivity, mSpinnerDistrictList);
        mSpinnerDistrict.setAdapter(adapterLevel);
    }

    public void setmSpinnerPostOffice(String data) {
        mSpinnerPostOfficeList = new ArrayList<>();
        try {
            JSONObject mPostOfficeObject = new JSONObject(data);
            String mPostOfficeString = mPostOfficeObject.getString("d");
            JSONArray mPostOfficeArray = new JSONArray(mPostOfficeString);
            for (int i = 0; i < mPostOfficeArray.length(); i++) {
                JSONObject json_obj = mPostOfficeArray.getJSONObject(i);
                int mId = json_obj.getInt("PostOfficeId");
                String mName = json_obj.getString("PostOffice");

                SpinnerModel mSpinnerModel = new SpinnerModel();
                mSpinnerModel.setId(mId);
                mSpinnerModel.setText(mName);
                mSpinnerPostOfficeList.add(mSpinnerModel);
            }
        } catch (Exception e) {

        }
        Collections.sort(mSpinnerPostOfficeList);
        SpinnerAdapter adapterLevel = new SpinnerAdapter(mActivity, mSpinnerPostOfficeList);
        mSpinnerPostOffice.setAdapter(adapterLevel);
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
            case R.id.text_register: {
                if (isConnectingToInternet()) {
                    if (nullChecker()) {
                        if (mobileValidation(mEditMobile.getText().toString())) {
                            if (mEditEmail.getText().toString().trim().length() != 0) {
                                if (emailValidation(mEditEmail.getText().toString())) {
                                    connectToServer();
                                } else {
                                    showMessage("Content not correct", "Email should be valid");
                                }
                            } else {
                                connectToServer();
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
            case R.id.text_register_upload_image: {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        }
    }

    private boolean nullChecker() {
        if (mEditFirstName.getText().toString().trim().length() == 0 || mEditFirstName.getText().toString() == "") {
            shakeEditText(R.id.edittext_register_firstname);
            return false;
        }  else if (mEditAddress.getText().toString().trim().length() == 0 || mEditAddress.getText().toString() == "") {
            shakeEditText(R.id.edittext_register_address);
            return false;
        } else if (mEditMobile.getText().toString().trim().length() == 0 || mEditMobile.getText().toString() == "") {
            shakeEditText(R.id.edittext_register_mobile_number);
            return false;
        }else if (mSpinnerPostOfficeList.isEmpty()) {
            shakeSpinner(R.id.spinner_register_state);
            showError("Missing contents", "Please select your state", FragmentTransactionEnum.NONE);
            return false;
        } else if (mEditLocation.getText().toString().trim().length() == 0 || mEditLocation.getText().toString() == "") {
            shakeEditText(R.id.edittext_register_location);
            return false;
        } else if (mEditPassword.getText().toString().trim().length() == 0 || mEditPassword.getText().toString() == "") {
            shakeEditText(R.id.edittext_register_password);
            return false;
        }
        return true;
    }

    private void connectToServer() {
        JSONObject mRegistrationDetails = new JSONObject();
        try {

            mRegistrationDetails.put("Name", mEditFirstName.getText().toString());
            mRegistrationDetails.put("Address", mEditAddress.getText().toString());
            mRegistrationDetails.put("Email", mEditEmail.getText().toString());
            mRegistrationDetails.put("MobileNumber", mEditMobile.getText().toString());
            mRegistrationDetails.put("LandNumber", mEditLand.getText().toString());
            mRegistrationDetails.put("Location", mEditLocation.getText().toString());
            mRegistrationDetails.put("Password", mEditPassword.getText().toString());
            mRegistrationDetails.put("StateId", mSpinnerStateList.get(mSpinnerState.getSelectedItemPosition()).getId());
            mRegistrationDetails.put("DistrictId", mSpinnerDistrictList.get(mSpinnerDistrict.getSelectedItemPosition()).getId());
            mRegistrationDetails.put("PostOffice", mSpinnerPostOfficeList.get(mSpinnerPostOffice.getSelectedItemPosition()).getId());
            mRegistrationDetails.put("CreatedBy", "Admin");
            if (bitmap != null) {
                mRegistrationDetails.put("img", getStringImage(bitmap));
            } else {
                mRegistrationDetails.put("img", getString(R.string.default_reg_string));
            }

            showProgress("Please Wait");
            mServiceCallEnum = ServiceCallEnum.REGISTRATION;
            super.establishConnection(Request.Method.POST, Constants.mRegistrationUrl, mRegistrationDetails);
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
                        if (mServiceCallEnum == ServiceCallEnum.REGISTRATION) {
                            JSONObject mJsonRegisterObject = new JSONObject(responseType);
                            String mJsonRegisterString = mJsonRegisterObject.getString("d");
                            JSONArray mJsonRegisterArray = new JSONArray(mJsonRegisterString);

                            JSONObject mJsonRegister = new JSONObject(String.valueOf(mJsonRegisterArray.get(0)));
                            String mStatus = mJsonRegister.getString("status");
                            String mRegistrationId = mJsonRegister.getString("RegistrationID");
                            if (mStatus.equalsIgnoreCase("true")) {
                                DataBaseQueryHelper.getInstance().insertEntryRegister(mEditMobile.getText().toString(), mEditPassword.getText().toString(), mRegistrationId);
                                showSuccessHere("Success", "Registration Successfull");
                                clearData();
                            } else {
                                showMessage("Registration Unsuccessfull", "You are already regestered, Please Login");
                            }
                        } else if (mServiceCallEnum == ServiceCallEnum.STATE) {
                            setmSpinnerState(responseType);
                        } else if (mServiceCallEnum == ServiceCallEnum.DISTRICT) {
                            setmSpinnerDistrict(responseType);
                        } else {
                            setmSpinnerPostOffice(responseType);
                        }
                    } catch (Exception e) {

                    }
                }

            }
        });

    }

    private void clearData() {
        mEditFirstName.getText().clear();
        mEditAddress.getText().clear();
        mEditMobile.getText().clear();
        mEditLand.getText().clear();
        mEditLocation.getText().clear();
        mEditPassword.getText().clear();

        mImageUser.setImageBitmap(null);
        mImageUser.setImageResource(android.R.color.transparent);

        mEditFirstName.setHintTextColor(Color.parseColor("#398808"));
        mEditAddress.setHintTextColor(Color.parseColor("#398808"));
        mEditMobile.setHintTextColor(Color.parseColor("#398808"));
        mEditLand.setHintTextColor(Color.parseColor("#398808"));
        mEditLocation.setHintTextColor(Color.parseColor("#398808"));
        mEditPassword.setHintTextColor(Color.parseColor("#398808"));

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
        switch (parent.getId()) {
            case R.id.spinner_register_state: {
                SpinnerSelectedId = mSpinnerStateList.get(mSpinnerState.getSelectedItemPosition()).getId();
                passDataForDistrict(SpinnerSelectedId);
                break;
            }
            case R.id.spinner_register_district: {
                SpinnerSelectedId = mSpinnerDistrictList.get(mSpinnerDistrict.getSelectedItemPosition()).getId();
                passDataForCity(SpinnerSelectedId);
                break;
            }

        }

    }

    private void passDataForDistrict(int spinnerSelectedId) {
        JSONObject mDistrictDetails = new JSONObject();
        try {
            mDistrictDetails.put("StateId", spinnerSelectedId);
            mServiceCallEnum = ServiceCallEnum.DISTRICT;
            super.establishConnection(Request.Method.POST, Constants.mDistrictUrl, mDistrictDetails);
        } catch (Exception e) {

        }

    }

    private void passDataForCity(int spinnerSelectedId) {
        JSONObject mCityDetails = new JSONObject();
        try {
            mCityDetails.put("DistrictId", spinnerSelectedId);
            mServiceCallEnum = ServiceCallEnum.POSTOFFICE;
            super.establishConnection(Request.Method.POST, Constants.mPostOfficeUrl, mCityDetails);
        } catch (Exception e) {

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != mActivity.RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                /**
                 * After cropping the image, get the bitmap of the cropped image and
                 * display it on imageview.
                 */
                if (extras != null) {
                    bitmap = extras.getParcelable("data");

                    mImageUser.setImageBitmap(bitmap);
                }
                File f = new File(mImageCaptureUri.getPath());
                /**
                 * Delete the temporary image
                 */
                if (f.exists())
                    f.delete();

                break;
        }

    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showSuccessHere(String header, String message) {
        mAlertDialog = new SweetAlertDialog(this.mActivity, SweetAlertDialog.SUCCESS_TYPE).setTitleText(header).setContentText(message);
        mAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                startActivity(new Intent(mActivity, MainActivity.class));
                dismissAlertHere();
            }
        });
        mAlertDialog.show();
    }

    public void dismissAlertHere() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);
            mOptions = options;

            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);

                return convertView;
            }

            return null;
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        /**
         * Open image crop app by starting an intent
         * �com.android.camera.action.CROP�.
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        /**
         * Check if there is image cropper app installed.
         */
        List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(
                intent, 0);

        int size = list.size();

        /**
         * If there is no image cropper app, display warning message
         */
        if (size == 0) {

            Toast.makeText(mActivity, "Can not find image crop app",
                    Toast.LENGTH_SHORT).show();

            return;
        } else {
            /**
             * Specify the image path, crop dimension and scale
             */
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            /**
             * There is posibility when more than one image cropper app exist,
             * so we have to check for it first. If there is only one app, open
             * then app.
             */

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                /**
                 * If there are several app exist, create a custom chooser to
                 * let user selects the app.
                 */
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = mActivity.getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = mActivity.getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(
                        mActivity.getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        CROP_FROM_CAMERA);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            mActivity.getContentResolver().delete(mImageCaptureUri, null,
                                    null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }
}
