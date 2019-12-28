package com.triocodes.krishikkaran.Activity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.triocodes.krishikkaran.Fragment.LoginFragment;
import com.triocodes.krishikkaran.Fragment.RegistrationFragment;
import com.triocodes.krishikkaran.R;

public class BaseActivity extends AppCompatActivity {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;
    Toolbar mToolbar;
    FragmentManager mFragmentManager;


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LoginFragment();
                case 1:
                    return new RegistrationFragment();
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
                    return "LOGIN";
                case 1:
                    return "REGISTER";


            }
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);

        tabLayout = (TabLayout) this.findViewById(R.id.tabs_base_activity);
        viewPager = (ViewPager) this.findViewById(R.id.viewpager_base_activity);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        mFragmentManager = getSupportFragmentManager();
        ConnectivityManager check = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = check.getAllNetworkInfo();
    }

}
