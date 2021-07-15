package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.util.Date;

public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";
//
//    private GForceDatabaseOpenHelper dbOpenHelper = new GForceDatabaseOpenHelper(this, "GForce.db",null,1);
//    private static SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

    public static String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = sdf.format(new Date());
        return timestamp;
    }

    public static boolean InsertParticipant(SQLiteDatabase db, int p_id, String gender){
       Cursor cursor = db.query("Participant",new String[]{"p_id"},null,null,null,null,null,null);
       while (cursor.moveToNext()){
           int id = cursor.getInt(cursor.getColumnIndex("p_id"));
           if(p_id == id){
               return true;//add update code to update gender
           }
       }
        ContentValues values = new ContentValues();
        values.put("p_id", p_id);
        values.put("gender", gender);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            db.insert("Participant", null, values);
            values.clear();
            return true;
        }catch (Exception e){
            values.clear();
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public static boolean insertAcc(SQLiteDatabase db, int p_id, String gender){
        ContentValues values = new ContentValues();
        values.put("p_id", p_id);
        values.put("gender", gender);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            db.insert("Participant", null, values);
            values.clear();
            return true;
        }catch (Exception e){
            values.clear();
            Log.e(TAG, e.getMessage());
            return false;
        }

    }
}
