package com.oymotion.gforceprofiledemo;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oymotion.gforceprofile.GForceProfile;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 14/07/2021
 * Time: 14:31
 * Description: make shared values accessible to many values
 */
public class MyApplication extends Application{
    private GForceProfile gForceProfile_l;
    private GForceProfile gForceProfile_r;
    private static final String TAG = "MyApplication";





    @Override
    public void onCreate() {
        super.onCreate();
        try{
            final GForceProfile gForceProfile_int_l = new GForceProfile(new GForceProfile.GForceErrorCallback() {
                @Override
                public void onGForceErrorCallback(String errorMsg) {
                    Log.e(TAG, "error in creating instance(left): " + errorMsg);
                }
            });
            final GForceProfile gForceProfile_int_r = new GForceProfile(new GForceProfile.GForceErrorCallback() {
                @Override
                public void onGForceErrorCallback(String errorMsg) {
                    Log.e(TAG, "error in creating instance(left): " + errorMsg);
                }
            });
            setProfileLeft(gForceProfile_int_l); //初始化全局变量
            setProfileRight(gForceProfile_int_r); //初始化全局变量
        }catch (Exception e){
            Log.e("Tag", e.getMessage());
        }

    }

    public GForceProfile getProfileLeft() {

        Log.i(TAG, "getProfileLeft");
        return gForceProfile_l;
    }

    public void setProfileLeft(GForceProfile gForceProfile) {
        this.gForceProfile_l = gForceProfile;
        Log.i(TAG, "setProfileLeft");
    }

    public GForceProfile getProfileRight() {
        Log.i(TAG, "getProfileRight");
        return gForceProfile_r;
    }

    public void setProfileRight(GForceProfile gForceProfile) {

        Log.i(TAG, "setProfileRight");
        this.gForceProfile_r = gForceProfile;
    }





}