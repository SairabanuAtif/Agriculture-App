package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 25-02-16.
 */
public class AddProductTabFragment extends ParentFragment implements VolleyCallback,View.OnClickListener,AdapterView.OnItemSelectedListener {
    Activity mActivity;
    HashMap<String,Object>extras;
    Handler mhandler;
    TextView mTextUploadImage,mTextSave,mTextBarter,mTextCancel;
    EditText mEditProductName,mEditTotalQuantity,mEditAmountPerUnit;
    CheckBox mCheckInterestedInBarter;
    Spinner mSpinnerQuantityUnit,mSpinnerCatagory;
    private ServiceCallEnum mServiceCallEnum;
    ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    private String selectedImagePath;
    String RegistrationId,RegistrationIdToPass;
    ArrayList<SpinnerModel>mSpinnerQuantityUnitList, mSpinnerCatagoryList;
    String mProductId=null;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private Bitmap bitmap;

    private Uri mImageCaptureUri;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_products_tab_layout,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, AddProductTabFragment.this);
        extras=new HashMap<String, Object>();
        mhandler=new Handler();

        mTextSave=(TextView)mActivity.findViewById(R.id.text_button_add_product_save);
        mTextUploadImage=(TextView)mActivity.findViewById(R.id.text_button_add_product_upload_image);
        mEditProductName=(EditText)mActivity.findViewById(R.id.edittext_add_product_product_name);
        mEditAmountPerUnit=(EditText)mActivity.findViewById(R.id.edittext_add_product_amount_per_unit);
        mEditTotalQuantity=(EditText)mActivity.findViewById(R.id.edittext_add_product_total_quantity);
        mSpinnerCatagory=(Spinner)mActivity.findViewById(R.id.spinner_add_product_catagory);
        mSpinnerQuantityUnit=(Spinner)mActivity.findViewById(R.id.spinner_add_product_quantity_unit);


        mTextBarter=(TextView)mActivity.findViewById(R.id.text_button_add_product_barter);
        mTextBarter.setOnClickListener(this);
        mTextCancel=(TextView)mActivity.findViewById(R.id.text_button_add_product_cancel);
        mTextCancel.setOnClickListener(this);

        RegistrationId=DataBaseQueryHelper.getInstance().getRegisterIdRegister();

        if(RegistrationId==null) {
            RegistrationIdToPass=DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        }else{
           RegistrationIdToPass=RegistrationId;
        }
        imageView = (ImageView) mActivity.findViewById(R.id.image_add_product);

        mTextSave.setOnClickListener(this);
        mTextUploadImage.setOnClickListener(this);
        mSpinnerQuantityUnit.setOnItemSelectedListener(this);
        mSpinnerCatagory.setOnItemSelectedListener(this);

        mServiceCallEnum=ServiceCallEnum.CATAGORY;
        super.establishConnection(Request.Method.POST,Constants.mCatagoryUrl,new JSONObject());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_button_add_product_save:{
                if(isConnectingToInternet()){
                    if(nullChecker()){
                        ConnectToServer();
                    }
                }else{
                    showError("Connectivity Issue","No internet connection available", FragmentTransactionEnum.NONE);
                }
                break;
            }
            case R.id.text_button_add_product_upload_image:{
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                getParentFragment().startActivityForResult(i, PICK_FROM_FILE);
                break;
            }
            case R.id.text_button_add_product_barter:{
                if(isConnectingToInternet()){
                    if(nullChecker()){
                        if (mProductId==null) {
                            showMessage("Sorry", "Please save your product first");
                        }else {
                            ConnectToBarter();
                        }
                    }
                }else{
                    showError("Connectivity Issue","No internet connection available", FragmentTransactionEnum.NONE);
                }
                break;
            }
            case R.id.text_button_add_product_cancel:{
                clearData();
            }

        }
    }

    private void clearData() {
        mEditProductName.getText().clear();
        mEditTotalQuantity.getText().clear();
        mEditAmountPerUnit.getText().clear();
        imageView.setImageBitmap(null);
        imageView.setImageResource(android.R.color.transparent);
        bitmap=null;

        mEditProductName.setHintTextColor(Color.parseColor("#398808"));
        mEditTotalQuantity.setHintTextColor(Color.parseColor("#398808"));
        mEditAmountPerUnit.setHintTextColor(Color.parseColor("#398808"));
    }




    private boolean nullChecker() {
        if(mEditProductName.getText().toString().trim().length()==0||mEditProductName.getText().toString().trim()==""){
           super.shakeEditText(R.id.edittext_add_product_product_name);
            return false;
        }

        else if(mEditTotalQuantity.getText().toString().trim().length()==0||mEditTotalQuantity.getText().toString().trim()==""){
            super.shakeEditText(R.id.edittext_add_product_total_quantity);
            return false;
        }

        else if (mEditAmountPerUnit.getText().toString().trim().length()==0||mEditAmountPerUnit.getText().toString()==""){
            super.shakeEditText(R.id.edittext_add_product_amount_per_unit);
            return false;
        }
        return true;
    }

    private void ConnectToServer() {
        JSONObject mAddProductDetails=new JSONObject();
        try{
            mAddProductDetails.put("ProductName",mEditProductName.getText().toString());
            mAddProductDetails.put("CategoryId",mSpinnerCatagoryList.get(mSpinnerCatagory.getSelectedItemPosition()).getId());
            mAddProductDetails.put("TotalQuantity",mEditTotalQuantity.getText().toString());
            mAddProductDetails.put("QuantityId",mSpinnerQuantityUnitList.get(mSpinnerQuantityUnit.getSelectedItemPosition()).getId());
            mAddProductDetails.put("amount",mEditAmountPerUnit.getText().toString());
            mAddProductDetails.put("RegistrationId",RegistrationIdToPass);

            mAddProductDetails.put("Status","1");
            mAddProductDetails.put("CreatedBy","Admin");
            mAddProductDetails.put("ActualPrice",mEditAmountPerUnit.getText().toString());
            mAddProductDetails.put("TaxPercentage","0.00");
            mAddProductDetails.put("DiscountPercentage","0.00");
            mAddProductDetails.put("SalesPrice",mEditAmountPerUnit.getText().toString());
            mAddProductDetails.put("ActualSalesPrice",mEditAmountPerUnit.getText().toString());
            if(bitmap!=null){
                mAddProductDetails.put("img",getStringImage(bitmap));
            }
            else{
                mAddProductDetails.put("img",getString(R.string.default_image_string));
            }
            super.showProgress("Please Wait");
            mServiceCallEnum=ServiceCallEnum.ADDPRODUCT;
            super.establishConnection(Request.Method.POST, Constants.mAddProductUrl,mAddProductDetails);
        }catch (Exception e){

        }
    }
    private void ConnectToBarter() {
        JSONObject mBarterDetails=new JSONObject();
        try{

            mBarterDetails.put("productID",mProductId);
            mBarterDetails.put("RegistrationId",RegistrationIdToPass);
            mBarterDetails.put("UserProductName", mEditProductName.getText().toString());
            super.showProgress("Please Wait");
            mServiceCallEnum=ServiceCallEnum.BARTER;
            super.establishConnection(Request.Method.POST,Constants.mBarterInterestUrl,mBarterDetails);

        }catch (Exception e){

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
                        if (mServiceCallEnum == ServiceCallEnum.CATAGORY) {
                            setmSpinnerCatagory(responseType);
                            mServiceCallEnum = ServiceCallEnum.QUANTITY;
                            establishConnection(Request.Method.POST, Constants.mQuantityUnitUrl, new JSONObject());
                        } else if (mServiceCallEnum == ServiceCallEnum.QUANTITY) {
                            setmSpinnerQuantityUnit(responseType);
                        } else if (mServiceCallEnum == ServiceCallEnum.ADDPRODUCT) {
                            JSONObject mJsonObject = new JSONObject(responseType);
                            String mString = mJsonObject.getString("d");
                            JSONArray mJSonArray = new JSONArray(mString);
                            JSONObject mJson = new JSONObject(String.valueOf(mJSonArray.get(0)));
                            String Status = mJson.getString("status");
                            String mProductIdd = mJson.getString("ProductID");
                            mProductId = mProductIdd;
                            if (Status.equalsIgnoreCase("true")) {
                                showSuccess("Success", "Product added succesfully", FragmentTransactionEnum.ADDPRODUCT);
                                clearData();
                            } else {
                                showMessage("Sorry", "Could not add your product");
                                clearData();
                            }
                        } else if (mServiceCallEnum == ServiceCallEnum.BARTER) {
                            JSONObject mJsonObject = new JSONObject(responseType);
                            String mString = mJsonObject.getString("d");
                            JSONArray mJsonArray = new JSONArray(mString);
                            JSONObject mJson = new JSONObject(String.valueOf(mJsonArray.get(0)));
                            String mStatus = mJson.getString("status");
                            if (mStatus.equalsIgnoreCase("true")) {
                                showSuccess("Success", "Product added to barter", FragmentTransactionEnum.ADDPRODUCT);
                            } else {
                                showMessage("Sorry", "Could not add your product to barter");
                            }
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    public void Refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AddProductTabFragment mFragment = new AddProductTabFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(mFragment).attach(mFragment).commit();


               /* YourProductsFragment mFragment = new YourProductsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(mFragment).attach(mFragment).commit();*/
                //swipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
    }

    private void setmSpinnerQuantityUnit(String responseType) {
        mSpinnerQuantityUnitList=new ArrayList<>();
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

                mSpinnerQuantityUnitList.add(mSpinnerModel);
            }
        }catch (Exception e){

        }
        Collections.sort(mSpinnerQuantityUnitList);
        SpinnerAdapter mSpinnerAdapter=new SpinnerAdapter(mActivity,mSpinnerQuantityUnitList);
        mSpinnerQuantityUnit.setAdapter(mSpinnerAdapter);
    }

    private void setmSpinnerCatagory(String responseType) {
        mSpinnerCatagoryList=new ArrayList<>();
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

                mSpinnerCatagoryList.add(mSpinnerModel);
            }
        } catch (Exception e){

        }
        Collections.sort(mSpinnerCatagoryList);
        SpinnerAdapter mSpinnertAdapter=new SpinnerAdapter(mActivity, mSpinnerCatagoryList);
        mSpinnerCatagory.setAdapter(mSpinnertAdapter);
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
                        showMessage("Sorry", "Something Went Wrong");
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

                    imageView.setImageBitmap(bitmap);
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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

   public class CropOptionAdapter extends ArrayAdapter<CropOption>{
       private ArrayList<CropOption> mOptions;
       private LayoutInflater mInflater;
       public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
           super(context,  R.layout.crop_selector, options);
           mOptions = options;

           mInflater = LayoutInflater.from(context);
       }
       @Override
       public View getView(int position, View convertView, ViewGroup group)
       {
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
