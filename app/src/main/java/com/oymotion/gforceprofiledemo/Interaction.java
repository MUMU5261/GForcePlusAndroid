package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Interaction {
    private static final String TAG = "Interaction";
    long id;
    int p_id;
    int e_id;
    int type;

    ContentValues values = new ContentValues();
    public Interaction(int p_id, int e_id, int type) {
        this.p_id = p_id;
        this.e_id = e_id;
        this.type = type;

    }
    public boolean insertInteraction(SQLiteDatabase db) {
        values.put("p_id", p_id);
        values.put("e_id", e_id);
        values.put("type", type);
        values.put("state", 0);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = db.insert("Interaction", null, values);
            values.clear();
            return true;
        } catch (Exception e) {
            values.clear();
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    public boolean updateExpProgress(SQLiteDatabase db, String state) {
        values.put("state", state);
        values.put("timestamp", DatabaseUtil.getTimestamp());//may need a update time
        db.update("Experiment", values, "id", new String[Integer.parseInt(String.valueOf(id))]);//??
        return true;
    }
    public boolean updateExperiment() {

        return true;
    }
    public boolean deleteExperiment() {

        return true;
    }
    public int getState() {

        return 99;
    }
}
