package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triocodes.krishikkaran.Interface.TabFragmentListenerCallback;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import java.util.HashMap;

/**
 * Created by admin on 24-02-16.
 */
public class AddProductsFragment extends ParentFragment implements VolleyCallback,View.OnClickListener,TabFragmentListenerCallback {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items=2;

    String mYourProductsFragment;

    public void setmYourProductsFragment(String mYourProductsFragment) {
        this.mYourProductsFragment = mYourProductsFragment;
    }

    public String getmYourProductsFragment() {
        return mYourProductsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x=inflater.inflate(R.layout.add_products_fragment,container,false);
        tabLayout=(TabLayout)x.findViewById(R.id.tabs_add_product);
        viewPager=(ViewPager)x.findViewById(R.id.view_pager_add_products);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        int curntitm=viewPager.getCurrentItem();
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new AddProductTabFragment();
                case 1:
                    return new YourProductsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "ADD PRODUCTS";
                case 1:
                    return "YOUR PRODUCTS";
            }
            return null;
        }
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void volleyOnSuccess() {

    }

    @Override
    public void volleyOnError() {

    }

    @Override
    public HashMap<String, Object> getExtras() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getChildFragmentManager().getFragments())
        {
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void yourProductsFragment() {

    }
}
