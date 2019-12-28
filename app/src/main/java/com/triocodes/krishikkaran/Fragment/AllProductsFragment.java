package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by admin on 23-02-16.
 */
public class AllProductsFragment extends ParentFragment implements VolleyCallback,View.OnClickListener {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.all_products_fragment_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs_all_products);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager_all_products);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager childFragmentManager) {
            super(childFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ProductsForSaleFragment();
                case 1:
                    return new SearchFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PRODUCTS FOR SALE";
                case 1:
                    return "SEARCH";


            }
            return null;
        }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

}
