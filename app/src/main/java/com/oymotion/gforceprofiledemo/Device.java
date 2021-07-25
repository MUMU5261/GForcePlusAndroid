package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 20/07/2021
 * Time: 02:05
 * Description:
 */
public class Device {
    private static final String TAG = "Device";
    public static boolean insertDeviceInfo(SQLiteDatabase db, int d_id, String name, String mac_address, int type){
        ContentValues values = new ContentValues();
        values.put("d_id",d_id);
        values.put("name",name);
        values.put("mac_address",mac_address);
        values.put("type",type);//0; ARMBAND_l, 1: ARMBAND_r,2:phone
        values.put("timestamp",DatabaseUtil.getTimestamp());
        try {
            db.insert("Device", null, values);
            values.clear();
            return true;
        }catch (Exception e){
            values.clear();
            Log.e(TAG, e.getMessage());
            return false;
        }
        
    }

    public static int checkDeviceLR(SQLiteDatabase db, String mac_address) {
        Cursor cursor = db.query("Device", new String[]{"type"}, "mac_address = ?", new String[]{String.valueOf(mac_address)}, null, null, null, null);
        int type = -1;
        while (cursor.moveToNext()) {
            type = cursor.getInt(cursor.getColumnIndex("type"));
        }
        return type;
    }
}
