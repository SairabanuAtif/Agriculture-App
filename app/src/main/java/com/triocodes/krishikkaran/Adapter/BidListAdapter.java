package com.triocodes.krishikkaran.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.triocodes.krishikkaran.Model.BidListModel;
import com.triocodes.krishikkaran.R;

import java.util.ArrayList;

/**
 * Created by admin on 31-03-16.
 */
public class BidListAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<BidListModel>List;
    private static LayoutInflater inflater=null;

    public BidListAdapter(Activity activity,ArrayList<BidListModel>List){
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
            convertView=inflater.inflate(R.layout.frame_bid_list_activity_layout,null);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.txtName=(TextView)convertView.findViewById(R.id.text_bid_list_name);
        viewHolder.txtMobile=(TextView)convertView.findViewById(R.id.text_bid_list_mobile);
        viewHolder.txtPlace=(TextView)convertView.findViewById(R.id.text_bid_list_place);
        viewHolder.txtAmount=(TextView)convertView.findViewById(R.id.text_bid_list_amount);
        viewHolder.txtBiddedOn=(TextView)convertView.findViewById(R.id.text_bid_list_bidded_on);

        viewHolder.txtName.setText(List.get(position).getmName());
        viewHolder.txtMobile.setText(List.get(position).getmMobilenum());
        viewHolder.txtAmount.setText(List.get(position).getmAmount());
        viewHolder.txtPlace.setText(List.get(position).getmPlace());
        viewHolder.txtBiddedOn.setText(List.get(position).getmBiddedOn());

        viewHolder.txtMobile.setPaintFlags(viewHolder.txtMobile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        viewHolder.txtMobile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                String phone_no = List.get(position).getmMobilenum().toString().trim();
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
        TextView txtMobile;
        TextView txtPlace;
        TextView txtAmount;
        TextView txtBiddedOn;
    }
}
