package com.oymotion.gforceprofiledemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 16/07/2021
 * Time: 00:16
 * Description:
 */
public class Participant {

    public static int getIDFromPreference(Context context){
        SharedPreferences preferences;
        SharedPreferences .Editor editor;
        int p_id = -1;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        p_id = preferences.getInt("p_id",-1);
        return p_id;
    }
}
