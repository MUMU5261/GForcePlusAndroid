package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

public class Experiment {
    private static final String TAG = "Experiment";
    long id;
    int p_id;
    int phone_id;
    int armband_id_l;
    int armband_id_r;
    ContentValues values = new ContentValues();

    public Experiment(int p_id, int phone_id, int armband_id_l, int armband_id_r) {
        this.p_id = p_id;
        this.phone_id = phone_id;
        this.armband_id_l = armband_id_l;
        this.armband_id_r = armband_id_r;
    }

    public boolean insertExperiment(SQLiteDatabase db) {
        values.put("p_id", p_id);
        values.put("phone_id", phone_id);
        values.put("armband_id_l", armband_id_l);
        values.put("armband_id_r", armband_id_r);
        values.put("state", 0);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = db.insert("Experiment", null, values);
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
