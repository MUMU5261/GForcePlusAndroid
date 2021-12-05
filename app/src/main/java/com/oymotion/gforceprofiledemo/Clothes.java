package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Clothes {
    private static final String TAG = "Clothes";
    int p_id;
    int e_id;
    byte[] image;
    int prj_id;
    ContentValues values = new ContentValues();

    int state = 0;
    int id = -1;
    public Clothes(int id, int e_id, int p_id,int state) {
        this.id = id;
        this.p_id = p_id;
        this.e_id = e_id;
        this.state = state;
    }

    public Clothes(int prj_id, int p_id) {
        this.prj_id = prj_id;
        this.p_id = p_id;
    }

    public int getClothesId() {
        return id;
    }
    public int getState() { return state; }


    public int insertClothes(SQLiteDatabase db){
        values.put("p_id", p_id);
        values.put("prj_id",prj_id);
//        values.put("img_cloth", image);
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

    public boolean addClothesImg(SQLiteDatabase db, byte[] image){
        try {
            Log.i(TAG, String.valueOf(id));
            values.put("img_cloth", image);
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

    public static ArrayList<Clothes> getMaterialList(SQLiteDatabase db,int p_id) {
        ArrayList<Clothes> materialList = new ArrayList<>();
        Cursor result = db.query("Clothes",null,"p_id=? AND state != ?",new String[]{String.valueOf(p_id),String.valueOf(State.DELETED)},null,null,null);
        Log.i(TAG, "getMaterialList: "+result);
        while (result.moveToNext()) {
            Log.i(TAG, "getMaterialList: " + result.getColumnName(1));
            int id_n = result.getInt(0);
            int e_id_n = result.getInt(1);
            int p_id_n = result.getInt(2);
            int state = result.getInt(3);
            Clothes clothes = new Clothes(id_n, e_id_n, p_id_n, state);
            materialList.add(clothes);

        }
        result.close();
        return materialList;
    }


    public class State {
        public static final int START = 0;
        public static final int FINISHED = 1;
        public static final int DELETED = 2;

    }
}
