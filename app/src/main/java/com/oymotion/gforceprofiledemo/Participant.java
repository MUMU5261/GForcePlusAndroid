package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 16/07/2021
 * Time: 00:16
 * Description:
 */
public class Participant {

    private static final String TAG = "Participant";
    int p_id;
    ContentValues values = new ContentValues();
    int state = 0;
    int id = -1;
    int prj_id;

    public Participant(int id, int p_id, int prj_id, int state) {
        this.id = id;
        this.p_id = p_id;
        this.prj_id = prj_id;
        this.state = state;
    }
    public Participant(int p_id, int prj_id, int state) {
        this.p_id = p_id;
        this.prj_id = prj_id;
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getP_id() {
        return p_id;
    }

    public ContentValues getValues() {
        return values;
    }

    public int getId() {
        return id;
    }

    public int getPrj_id() {
        return prj_id;
    }

    public int insertParticipant(SQLiteDatabase db){
        values.put("p_id", p_id);
        values.put("prj_id", prj_id);
        values.put("state", state);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Participant", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public static boolean updateState(SQLiteDatabase db, int p_id, int state){
        ContentValues values = new ContentValues();
        try {
            values.put("state", state);
            db.update("Participant", values, "p_id=?", new String[]{String.valueOf(p_id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    static ArrayList<Participant> getParticipantList(SQLiteDatabase db, int prj_id) {
        ArrayList<Participant> participantList = new ArrayList<>();
        try {
            Cursor result = db.query("Participant",null,"prj_id = ? AND state != ?",new String[]{String.valueOf(prj_id),String.valueOf(Project.State.DELETED)},null,null,null);
            Log.i(TAG, "getParticipantList: "+result);
            while (result.moveToNext()){
                Log.i(TAG, "getParticipantList: "+result.getColumnName(1));
                int mprj_id = result.getInt(1);
                int p_id = result.getInt(2);
                int state = result.getInt(3);
                Participant participant = new Participant(p_id,mprj_id,state);
                participantList.add(participant);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return participantList;
    }

    public static boolean deleteParticipant(SQLiteDatabase db, int p_id) {

        ContentValues values = new ContentValues();
        try {
            values.put("state", State.DELETED);
            db.update("Participant", values, "p_id=?", new String[]{String.valueOf(p_id)});//
            db.update("Answer", values, "p_id=?", new String[]{String.valueOf(p_id)});//
            db.update("Exploration", values, "p_id=?", new String[]{String.valueOf(p_id)});//
            db.update("Clothes", values, "p_id=?", new String[]{String.valueOf(p_id)});//
            db.update("Interaction", values, "p_id=?", new String[]{String.valueOf(p_id)});//

            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }

    public boolean updateParticipant(SQLiteDatabase db, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("p_id", p_id);
            db.update("Participant", values, "p_id=? and state != ?", new String[]{String.valueOf(id),String.valueOf(State.DELETED)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }

    static Participant getParticipant(SQLiteDatabase db, int p_id) {
        Participant participant = new Participant(-1,-1,-1);
        Cursor result = null;
        try {
            result = db.query("Participant",null,"p_id = ?",new String[]{String.valueOf(p_id)},null,null,null);

        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        if(result.getCount() == 0){
            return null;
        }else{
            result.moveToNext();
            int id = result.getInt(0);
            int mp_id = result.getInt(1);
            int prj_id = result.getInt(2);
            int state = result.getInt(3);
            participant = new Participant(id,mp_id,prj_id,state);
            result.close();
            return participant;
        }

    }

    public static int getIDFromPreference(Context context){
        SharedPreferences preferences;
        SharedPreferences .Editor editor;
        int p_id = -1;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        p_id = preferences.getInt("p_id",-1);
        return p_id;
    }

    static boolean isIDExist(SQLiteDatabase db, int prj_id,int p_id){
        try {
            Cursor result = db.query("Participant",new String[]{"p_id"},"prj_id = ? and p_id = ? and state != ?",
                    new String[]{String.valueOf(prj_id),String.valueOf(p_id),String.valueOf(State.DELETED)},null,null,null);
            while (result.moveToNext()){
                int id = result.getInt(0);
                if(p_id == id){
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public class State {
        public static final int START = 0;
        public static final int COMPLETE = 1;
        public static final int DELETED = 2;

    }


}
