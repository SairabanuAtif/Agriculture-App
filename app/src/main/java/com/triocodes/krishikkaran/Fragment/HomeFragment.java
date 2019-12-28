package com.triocodes.krishikkaran.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.triocodes.krishikkaran.Activity.AnimalsActivity;
import com.triocodes.krishikkaran.Activity.DairyProductsActivity;
import com.triocodes.krishikkaran.Activity.EquipmentsActivity;
import com.triocodes.krishikkaran.Activity.FlowersActivity;
import com.triocodes.krishikkaran.Activity.FruitsActivity;
import com.triocodes.krishikkaran.Activity.HandloomActivity;
import com.triocodes.krishikkaran.Activity.HerbsAndCerealsActivity;
import com.triocodes.krishikkaran.Activity.LandForSaleActivity;
import com.triocodes.krishikkaran.Activity.OthersActivity;
import com.triocodes.krishikkaran.Activity.SeedsAndPlantsActivity;
import com.triocodes.krishikkaran.Activity.SpicesActivity;
import com.triocodes.krishikkaran.Interface.VolleyCallback;
import com.triocodes.krishikkaran.ParentFragment;
import com.triocodes.krishikkaran.R;
import com.triocodes.krishikkaran.Activity.VegetablesAvtivity;

import java.util.HashMap;

/**
 * Created by admin on 19-07-16.
 */
public class HomeFragment extends ParentFragment implements View.OnClickListener,VolleyCallback {
    ImageView mImageVegetables,mImageFruits,mImageSpices,mImageSeedsAndPlants,mImageFlowers,mImageHerbsAndCereals,mImageDairyProducts,
             mImageAnimal,mImageEquipment,mImageHandloom,mImageLandForSale,mImageOthers;
    Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_page_layout,container,false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.initialize(mActivity, HomeFragment.this);
        mImageVegetables=(ImageView)mActivity.findViewById(R.id.home_vegetables);
        mImageFruits=(ImageView)mActivity.findViewById(R.id.home_fruits);
        mImageSpices=(ImageView)mActivity.findViewById(R.id.home_spices);
        mImageSeedsAndPlants=(ImageView)mActivity.findViewById(R.id.home_seeds_and_plants);
        mImageDairyProducts=(ImageView)mActivity.findViewById(R.id.home_dairy_products);
        mImageAnimal=(ImageView)mActivity.findViewById(R.id.home_animal);
        mImageEquipment=(ImageView)mActivity.findViewById(R.id.home_equipments);
        mImageHandloom=(ImageView)mActivity.findViewById(R.id.home_handloom);
        mImageLandForSale=(ImageView)mActivity.findViewById(R.id.home_land_for_sale);
        mImageOthers=(ImageView)mActivity.findViewById(R.id.home_others);
        mImageFlowers=(ImageView)mActivity.findViewById(R.id.home_flowers);
        mImageHerbsAndCereals=(ImageView)mActivity.findViewById(R.id.home_herbs_and_cereals);

        mImageVegetables.setOnClickListener(this);
        mImageFlowers.setOnClickListener(this);
        mImageHandloom.setOnClickListener(this);
        mImageHerbsAndCereals.setOnClickListener(this);
        mImageOthers.setOnClickListener(this);
        mImageAnimal.setOnClickListener(this);
        mImageDairyProducts.setOnClickListener(this);
        mImageEquipment.setOnClickListener(this);
        mImageLandForSale.setOnClickListener(this);
        mImageFruits.setOnClickListener(this);
        mImageSpices.setOnClickListener(this);
        mImageSeedsAndPlants.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_vegetables:{
                startActivity(new Intent(mActivity, VegetablesAvtivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
            }
            case R.id.home_fruits:{
                startActivity(new Intent(mActivity, FruitsActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
            }
            case R.id.home_spices:{
                startActivity(new Intent(mActivity, SpicesActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
            }
            case R.id.home_seeds_and_plants:{
                startActivity(new Intent(mActivity, SeedsAndPlantsActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_flowers:{
                startActivity(new Intent(mActivity, FlowersActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_herbs_and_cereals:{
                startActivity(new Intent(mActivity, HerbsAndCerealsActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_dairy_products:{
                startActivity(new Intent(mActivity, DairyProductsActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_animal:{
                startActivity(new Intent(mActivity, AnimalsActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_equipments:{
                startActivity(new Intent(mActivity, EquipmentsActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_handloom:{
                startActivity(new Intent(mActivity, HandloomActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_land_for_sale:{
                startActivity(new Intent(mActivity, LandForSaleActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }
            case R.id.home_others:{
                startActivity(new Intent(mActivity, OthersActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                break;
            }


        }
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
