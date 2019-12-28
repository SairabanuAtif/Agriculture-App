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
import com.triocodes.krishikkaran.Model.BidModel;
import com.triocodes.krishikkaran.Model.VegetablesModel;
import com.triocodes.krishikkaran.R;

import java.util.ArrayList;

/**
 * Created by admin on 30-03-16.
 */
public class BidAdapter extends BaseAdapter {
    customButtonListener customListener;

    public interface customButtonListener {
        public void OnBidThisProductClickListener(int position);
    }

    public void setCustomButtonListener(customButtonListener listener) {
        this.customListener = listener;
    }

    private static LayoutInflater inflater = null;
    private Activity mActivity;
    private ArrayList<BidModel> List;

    public BidAdapter(Activity activity, ArrayList<BidModel> List) {
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
            convertView = inflater.inflate(R.layout.frame_bid_layout, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtProductName = (TextView) convertView.findViewById(R.id.text_bid_product_name);
        viewHolder.txtName = (TextView) convertView.findViewById(R.id.text_bid_name_of_seller);
        viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.text_bid_address_of_seller);
        viewHolder.txtQuantity = (TextView) convertView.findViewById(R.id.text_bid_product_quantity);
        viewHolder.txtMobile = (TextView) convertView.findViewById(R.id.text_bid_seller_contact);
        viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.text_bid_amount);
        viewHolder.imgProduct = (ImageView) convertView.findViewById(R.id.image_bid_product_image);
        viewHolder.btnBidThisProduct = (TextView) convertView.findViewById(R.id.text_button_bid_product);

        viewHolder.txtProductName.setText(List.get(position).getmProductName());
        viewHolder.txtName.setText(List.get(position).getmSellerName());
        viewHolder.txtAddress.setText(List.get(position).getmSellerAddress());
        viewHolder.txtQuantity.setText(List.get(position).getmQuantity() + " " + List.get(position).getmQuantityUnit());
        viewHolder.txtMobile.setText(List.get(position).getmSellerMobile());

        viewHolder.txtMobile.setPaintFlags(viewHolder.txtMobile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.txtAmount.setText("Rs " + List.get(position).getmAmount());
        Picasso.with(mActivity)
                .load(List.get(position).getmThumbnailUrl())
                .placeholder(R.drawable.noimage).skipMemoryCache()
                .into(viewHolder.imgProduct);
        viewHolder.btnBidThisProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.OnBidThisProductClickListener(position);
                }
            }
        });

        viewHolder.txtMobile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                String phone_no = List.get(position).getmSellerMobile().toString().trim();
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



        return convertView;
    }

    private int checkSelfPermission(String callPhone) {
        return 0;
    }

    static class ViewHolder{
        TextView txtName;
        TextView txtAddress;
        TextView txtQuantity;
        TextView txtMobile;
        TextView txtProductName;
        TextView txtAmount;
        TextView btnBidThisProduct;
        ImageView imgProduct;
    }
}
