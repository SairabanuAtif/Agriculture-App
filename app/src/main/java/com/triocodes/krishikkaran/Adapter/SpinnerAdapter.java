package com.triocodes.krishikkaran.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.triocodes.krishikkaran.Model.SpinnerModel;
import com.triocodes.krishikkaran.R;

import java.util.ArrayList;

/**
 * Created by admin on 09-03-16.
 */
public class SpinnerAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<SpinnerModel>List;
    private static LayoutInflater inflater=null;

    public SpinnerAdapter(Activity activity,ArrayList<SpinnerModel>List){
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
            convertView=inflater.inflate(R.layout.frame_spinner,null);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.txtName=(TextView)convertView.findViewById(R.id.txt_item);
        viewHolder.txtName.setText(List.get(position).getText());
        return convertView;
    }

    static class ViewHolder {

        TextView txtName;

    }

}
