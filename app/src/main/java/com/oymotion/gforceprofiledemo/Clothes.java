package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Clothes {
    private static final String TAG = "Clothes";
    int p_id;
    int e_id;
    byte[] image;
    ContentValues values = new ContentValues();

    int state = 0;
    int id = -1;
    public Clothes(int p_id, int e_id, byte[] image) {
        this.p_id = p_id;
        this.image = image;
        this.e_id = e_id;
    }

    public int insertClothes(SQLiteDatabase db){
        values.put("p_id", p_id);
        values.put("e_id", e_id);
        values.put("img_cloth", image);
        values.put("state", state);
//        values.put("img_label", z);
//        values.put("c_soft", user_id);
//        values.put("c_warmth", user_id);
//        values.put("c_thickness", user_id);
//        values.put("c_smooth", user_id);
//        values.put("c_enjoyment", section);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Clothes", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public boolean addLabelImg(SQLiteDatabase db, byte[] image){
        try {
            Log.i(TAG, String.valueOf(id));
            Log.i(TAG, "before update");
            values.put("img_label", image);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            values.clear();
            return false;
        }
    }
    public static boolean updateState(SQLiteDatabase db, int id, int state){
        ContentValues values = new ContentValues();
        try {
            values.put("state", state);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    public static void updateQuality(SQLiteDatabase db, int id, int property,int likert){
        ContentValues values = new ContentValues();
        try {
            switch (property){
                case Interaction.Type.SMOOTH:
                    values.put("c_smooth", likert);
                    break;
                case Interaction.Type.THICKNESS:
                    values.put("c_thickness", likert);
                    break;
                case Interaction.Type.WARMTH:
                    values.put("c_warmth", likert);
                    break;
                case Interaction.Type.FLEXIBILITY:
                    values.put("c_flexibility", likert);
                    break;
                case Interaction.Type.SOFTNESS:
                    values.put("c_soft", likert);
                    break;
                case Interaction.Type.ENJOYMENT:
                    values.put("c_enjoyment", likert);
                    break;
            }
//            values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
            values.clear();
        } catch (NumberFormatException e) {
            values.clear();
            e.printStackTrace();
        }
    }

    public static void updateEnjoyText(SQLiteDatabase db, int id, String str){
        ContentValues values = new ContentValues();
        try {
            values.put("c_enjoy_touch", str);
            db.update("Clothes", values, "id=?", new String[]{String.valueOf(id)});//??
            values.clear();
        } catch (NumberFormatException e) {
            values.clear();
            e.printStackTrace();
        }
    }

    public class State {
        public static final int START = 0;
        public static final int FINISHED = 1;

    }
}
