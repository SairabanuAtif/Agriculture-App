package com.triocodes.krishikkaran.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.triocodes.krishikkaran.Model.YourProductsModel;
import com.triocodes.krishikkaran.R;

import java.util.ArrayList;

/**
 * Created by admin on 10-03-16.
 */
public class YourProductsAdapter extends BaseAdapter {
    customButtonListener customListner;

    public interface customButtonListener {
        public void onModifyClickListner(int position);
        public void onDeleteClickListener(int position);
        public void onBidClickListener(int position);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private static LayoutInflater inflater = null;
    private Activity mActivity;
    private ArrayList<YourProductsModel> List;


    public YourProductsAdapter(Activity activity,ArrayList<YourProductsModel>List){
        this.mActivity=activity;
        this.List=List;
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
        ViewHolder viewHolder=new ViewHolder();
        if(convertView==null){
            convertView=inflater.inflate(R.layout.frame_your_products_layout,null);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.txtProductCode=(TextView)convertView.findViewById(R.id.text_your_products_product_code);
        viewHolder.txtProductName=(TextView)convertView.findViewById(R.id.text_your_products_product_name);
        viewHolder.txtTotalQuantity=(TextView)convertView.findViewById(R.id.text_your_products_total_quantity);
        viewHolder.txtModify=(TextView)convertView.findViewById(R.id.text_your_products_modify);
        viewHolder.txtDelete=(TextView)convertView.findViewById(R.id.text_your_products_delete);
        viewHolder.txtBid=(TextView)convertView.findViewById(R.id.text_your_products_bid);

        viewHolder.txtProductCode.setText(String.valueOf(List.get(position).getmProductCode()));
        viewHolder.txtProductName.setText(List.get(position).getmProductName());
        viewHolder.txtTotalQuantity.setText(List.get(position).getmTotalQuantity());

        viewHolder.txtModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onModifyClickListner(position);
                }
            }
        });
        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onDeleteClickListener(position);
                }
            }
        });
        viewHolder.txtBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onBidClickListener(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtProductCode;
        TextView txtProductName;
        TextView txtTotalQuantity;
        TextView txtModify;
        TextView txtDelete;
        TextView txtBid;
    }
}
