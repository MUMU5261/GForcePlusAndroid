package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Interaction {
    private static final String TAG = "Interaction";
    int id;
    int p_id;
    int e_id;
    int type;
    int clt_id;

    ContentValues values = new ContentValues();
    public Interaction(int p_id, int e_id, int clt_id, int type) {
        this.id = -1;
        this.p_id = p_id;
        this.e_id = e_id;
        this.e_id = e_id;
        this.clt_id = clt_id;
        this.type = type;
    }
    public int insertInteraction(SQLiteDatabase db) {
        values.put("p_id", p_id);
        values.put("e_id", e_id);
        values.put("clt_id", clt_id);
        values.put("type", type);
        values.put("state", State.START);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Interaction", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            Log.e(TAG, e.getMessage());
            return id;
        }

    }

    public boolean updateState(SQLiteDatabase db, int state) {
        try {
            values.put("state", state);
//        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
            db.update("Interaction", values, "id=?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() {

        return true;
    }
    public int getState() {

        return 99;
    }
    public class Type {
        public static final int RELAX = 0 ;
        public static final int FIST = 1;
        public static final int FREE = 2;
        public static final int SMOOTH = 3;
        public static final int THICKNESS = 4;
        public static final int WARMTH = 5;
        public static final int FLEXIBILITY = 6;
        public static final int SOFTNESS = 7;
        public static final int ENJOYMENT = 8;

    }
    public class State {
        public static final int START = 0;
        public static final int FINISHED = 1;

    }
}
