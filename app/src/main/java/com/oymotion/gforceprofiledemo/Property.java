package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 02/12/2021
 * Time: 22:54
 * Description:
 */
public class Property {
    private static final String TAG = "Property";
    ContentValues values = new ContentValues();
    int id = -1;

    int prj_id;
    String property;
    String polarLow;
    String polarHigh;

    public Property(int prj_id,String property, String low, String high) {
        this.prj_id = prj_id;
        this.property = property;
        this.polarLow = low;
        this.polarHigh = high;
    }

    public Property( int id, int prj_id, String property, String low, String high) {
        this.id = id;
        this.prj_id = prj_id;
        this.property = property;
        this.polarLow = low;
        this.polarHigh = high;
    }

    public String getProperty() {
        return property;
    }
    public String getPolarLow() {
        return polarLow;
    }
    public String getPolarHigh() { return polarHigh; }

    public int getId() {
        return id;
    }

    public int insertProperty(SQLiteDatabase db){
        values.put("prj_id", prj_id);
        values.put("property", property);
        values.put("polar_low", polarLow);
        values.put("polar_high", polarHigh);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Property", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public static boolean updateValue(SQLiteDatabase db, int id, String property, String low, String high){
        ContentValues values = new ContentValues();
        try {
            values.put("property", property);
            values.put("polar_low", low);
            values.put("polar_high", high);
            db.update("Property", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    public static boolean updateState(SQLiteDatabase db, int id, int state){
        ContentValues values = new ContentValues();
        try {
            values.put("state", state);;
            db.update("Property", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }

    public static boolean deleteProperty(SQLiteDatabase db, int ppt_id) {

        ContentValues values = new ContentValues();
        try {
            values.put("state", Participant.State.DELETED);
            db.update("Property", values, "id=?", new String[]{String.valueOf(ppt_id)});//
            db.update("Exploration", values, "ppt_id=?", new String[]{String.valueOf(ppt_id)});//
//            db.update("Interaction", values, "p_id=?", new String[]{String.valueOf(prj_id)});
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    static ArrayList<Property> getPropertyList(SQLiteDatabase db, int prj_id) {
        ArrayList<Property> propertyList = new ArrayList<>();
        try {
            Cursor result = db.query("Property",null,"prj_id = ? AND state != ?",new String[]{String.valueOf(prj_id),String.valueOf(State.DELETED)},null,null,null);
            while (result.moveToNext()){
                int id = result.getInt(0);
                Log.i(TAG, "getPropertyList: "+result.getColumnName(1));
                String property_name = result.getString(2);
                String low = result.getString(3);
                String high = result.getString(4);
                Property property = new Property(id,prj_id,property_name,low,high);
                propertyList.add(property);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return propertyList;
    }

    public class State {
        public static final int START = 0;
        public static final int COMPLETE = 1;
        public static final int DELETED = 2;
        public static final int SELECTED = 3;
    }


}
