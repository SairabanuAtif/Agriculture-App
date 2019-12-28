package com.triocodes.krishikkaran.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.triocodes.krishikkaran.Model.BarterProductModel;
import com.triocodes.krishikkaran.R;

import java.util.ArrayList;

/**
 * Created by admin on 31-03-16.
 */
public class BarterProductAdapter extends BaseAdapter {
    customButtonListener customListener;

    public interface customButtonListener{
        public void onBarterSendButtonClickListener(int position);
    }

    public void setCustomButtonListener(customButtonListener customListener) {
        this.customListener = customListener;
    }

    private static LayoutInflater inflater=null;
    private Activity mActivity;
    private ArrayList<BarterProductModel>List;

    public BarterProductAdapter(Activity activity,ArrayList<BarterProductModel>List){
        this.mActivity=activity;
        this.List=List;
        inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView=inflater.inflate(R.layout.frame_barter_product_click,null);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.txtId=(TextView)convertView.findViewById(R.id.text_barter_product_id);
        viewHolder.txtProduct=(TextView)convertView.findViewById(R.id.text_barter_product_product);
        viewHolder.txtButtonBarterSend=(TextView)convertView.findViewById(R.id.text_button_barter_product_send);

        viewHolder.txtId.setText(List.get(position).getmId());
        viewHolder.txtProduct.setText(List.get(position).getmProduct());
        viewHolder.txtButtonBarterSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customListener!=null){
                    customListener.onBarterSendButtonClickListener(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView txtId;
        TextView txtProduct;
        TextView txtButtonBarterSend;
    }
}
