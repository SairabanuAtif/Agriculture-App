package com.triocodes.krishikkaran.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.triocodes.krishikkaran.Data.DataBaseHelper;
import com.triocodes.krishikkaran.Fragment.AddProductsFragment;
import com.triocodes.krishikkaran.Fragment.AllProductsFragment;
import com.triocodes.krishikkaran.Fragment.BarterFragment;
import com.triocodes.krishikkaran.Fragment.BidFragment;
import com.triocodes.krishikkaran.Fragment.FeedbackFragment;
import com.triocodes.krishikkaran.Fragment.HomeFragment;
import com.triocodes.krishikkaran.Fragment.YourRequirementsFragment;
import com.triocodes.krishikkaran.Interface.FragmentListenerCallback;
import com.triocodes.krishikkaran.R;

public class MainActivity extends AppCompatActivity implements FragmentListenerCallback,View.OnClickListener {

    public MainActivity() {
        super();
    }

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Toolbar toolbar;
    ImageView mImgLogout;
    DataBaseHelper dbhelper;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationDrawerItemTitles;
    int c;
    int selectedPosition=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        c = 1;

        setSupportActionBar(toolbar);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mImgLogout=(ImageView)findViewById(R.id.image_logout);
        mImgLogout.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationview);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_catagory) {
                    int position=0;
                    selectedPosition=position;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();//HomeFragment()CatagoriesFragment()
                    setTitle(mNavigationDrawerItemTitles[position]);
                }

                if (menuItem.getItemId() == R.id.nav_item_all_products) {
                    int position=1;
                    selectedPosition=position;
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new AllProductsFragment()).commit();
                    setTitle(mNavigationDrawerItemTitles[position]);
                }

                if (menuItem.getItemId() == R.id.nav_item_add_products) {
                    int position=2;
                    selectedPosition=position;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new AddProductsFragment()).commit();
                    setTitle(mNavigationDrawerItemTitles[position]);
                }
                if (menuItem.getItemId() == R.id.nav_item_your_requirements) {
                    int position=3;
                    selectedPosition=position;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new YourRequirementsFragment()).commit();
                    setTitle(mNavigationDrawerItemTitles[position]);
                }
                if (menuItem.getItemId() == R.id.nav_item_bid) {
                    int position=4;
                    selectedPosition=position;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new BidFragment()).commit();
                    setTitle(mNavigationDrawerItemTitles[position]);
                }
                if (menuItem.getItemId() == R.id.nav_item_barter) {
                    int position=5;
                    selectedPosition=position;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new BarterFragment()).commit();
                    setTitle(mNavigationDrawerItemTitles[position]);
                }
                if (menuItem.getItemId() == R.id.nav_item_feedback) {
                    int position=6;
                    selectedPosition=position;
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new FeedbackFragment()).commit();
                    setTitle(mNavigationDrawerItemTitles[position]);
                }
                return false;

            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        toolbar.setTitle(mTitle);
    }



    @Override
    public void loadCatagory() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();
    }

    @Override
    public void loadAddProduct() {
        Fragment mAddProductsFragment = new AddProductsFragment();
         mFragmentManager.beginTransaction().replace(R.id.containerView, mAddProductsFragment).commit();
    }

    public void setDrawerState(boolean isEnabled){
        if ( isEnabled ) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();
            this.getActionBar().setHomeButtonEnabled(true);
        }
        else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_logout: {
                alert();
                break;
            }
        }
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dbhelper = new DataBaseHelper(MainActivity.this);
                                dbhelper.DeleteDatabase();

                                ActivityCompat.finishAffinity(MainActivity.this);
                               /* Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);*/
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (selectedPosition != 0) {
            FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
            xfragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();//HomeFragment()CatagoriesFragment()
            setTitle(mNavigationDrawerItemTitles[0]);
            selectedPosition=0;
        } else {
            ActivityCompat.finishAffinity(this);
        }
    }
}
