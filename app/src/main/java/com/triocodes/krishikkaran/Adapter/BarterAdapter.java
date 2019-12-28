package com.triocodes.krishikkaran.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.triocodes.krishikkaran.Data.DataBaseQueryHelper;
import com.triocodes.krishikkaran.Model.BarterModel;
import com.triocodes.krishikkaran.R;

import java.util.ArrayList;

/**
 * Created by admin on 30-03-16.
 */
public class BarterAdapter extends BaseAdapter {
    String RegistrationIdToPass,RegistrationId;

    customButtonListener customListner;

    public interface customButtonListener {
        public void OnBarterProductClickListener(int position);
    }

    public void setCustomButtonListner(customButtonListener customListner) {
        this.customListner = customListner;
    }

    private static LayoutInflater inflater = null;
    private Activity mActivity;
    private ArrayList<BarterModel> List;

    public BarterAdapter(Activity activity, ArrayList<BarterModel> List) {
        this.mActivity = activity;
        this.List = List;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.frame_barter_layout, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RegistrationId = DataBaseQueryHelper.getInstance().getRegisterIdRegister();
        if (RegistrationId == null) {
            RegistrationIdToPass = DataBaseQueryHelper.getInstance().getRegisterIdLogin();
        } else {
            RegistrationIdToPass = RegistrationId;
        }


        viewHolder.txtProductName = (TextView) convertView.findViewById(R.id.text_barter_product_name);
        viewHolder.txtName = (TextView) convertView.findViewById(R.id.text_barter_name_of_seller);
        viewHolder.txtLocation = (TextView) convertView.findViewById(R.id.text_barter_address_of_seller);
        viewHolder.txtMobile = (TextView) convertView.findViewById(R.id.text_barter_seller_contact);
        viewHolder.txtEmail = (TextView) convertView.findViewById(R.id.text_barter_seller_email);
        viewHolder.txtQuantity = (TextView) convertView.findViewById(R.id.text_barter_product_quantity);
        viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.text_barter_amount);
        viewHolder.imgProduct = (ImageView) convertView.findViewById(R.id.image_barter_product_image);
        viewHolder.txtBarterProduct = (TextView) convertView.findViewById(R.id.text_button_barter_product);

        viewHolder.txtProductName.setText(List.get(position).getmProductName());
        viewHolder.txtName.setText(List.get(position).getmName());
        viewHolder.txtLocation.setText(List.get(position).getmLocation());
        viewHolder.txtMobile.setText(List.get(position).getmMobile());
        viewHolder.txtMobile.setPaintFlags(viewHolder.txtMobile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.txtEmail.setText(List.get(position).getmEmail());
        viewHolder.txtAmount.setText("Rs " + List.get(position).getmAmount());
        viewHolder.txtQuantity.setText(List.get(position).getmQuantity() + " " + List.get(position).getmQuantityUnit());
        Picasso.with(mActivity)
                .load(List.get(position).getmImageUrl())
                .placeholder(R.drawable.noimage).skipMemoryCache()
                .into(viewHolder.imgProduct);

        if(List.get(position).getmRegId().equalsIgnoreCase(RegistrationIdToPass)){
            viewHolder.txtBarterProduct.setVisibility(View.GONE);
        }else {
            viewHolder.txtBarterProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customListner != null) {
                        customListner.OnBarterProductClickListener(position);
                    }
                }
            });
        }
        viewHolder.txtMobile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                String phone_no = List.get(position).getmMobile().toString().trim();
                intent.setData(Uri.parse("tel:" + phone_no));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                mActivity.startActivity(intent);
            }
        });

        viewHolder.txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                mActivity.startActivity(emailIntent);
            }
        });



        return convertView;
    }

    private int checkSelfPermission(String callPhone) {
        return 0;
    }

    static class ViewHolder{
        TextView txtName;
        TextView txtProductName;
        TextView txtLocation;
        TextView txtMobile;
        TextView txtEmail;
        TextView txtQuantity;
        TextView txtAmount;
        TextView txtBarterProduct;
        ImageView imgProduct;
    }
}
