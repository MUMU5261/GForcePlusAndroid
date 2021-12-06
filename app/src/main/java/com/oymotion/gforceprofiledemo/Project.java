package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import kotlin.reflect.KProperty;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 04/12/2021
 * Time: 22:51
 * Description:
 */
public class Project {
    private static final String TAG = "Project";
    ContentValues values = new ContentValues();
    int id = -1;

    int prj_id;
    String prj_name;
    String researcher;
    int state = 0;


    public Project( int prj_id, String prj_name, String researcher) {
        this.prj_id = prj_id;
        this.prj_name = prj_name;
        this.researcher = researcher;
    }

    public Project(int id, int prj_id, String prj_name, String researcher, int state) {
        this.id = id;
        this.prj_id = prj_id;
        this.prj_name = prj_name;
        this.researcher = researcher;
        this.state = state;
    }


    public int getPrj_id() {
        return prj_id;
    }
    public String getPrj_name() {
        return prj_name;
    }
    public String getResearcher() { return researcher; }
    public int getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public int insertProject(SQLiteDatabase db){
        values.put("prj_id", prj_id);
        values.put("prj_name", prj_name);
        values.put("researcher", researcher);
        values.put("state", state);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Project", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public static boolean updateValue(SQLiteDatabase db, int id, String prj_id, String prj_name, String researcher){
        ContentValues values = new ContentValues();
        try {
            values.put("prj_id", prj_id);
            values.put("prj_name", prj_name);
            values.put("researcher", researcher);
            db.update("Project", values, "id=?", new String[]{String.valueOf(id)});//
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
            db.update("Project", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }

    static Project getProject(SQLiteDatabase db, int prj_id) {
        Project project = new Project(-1,null,null);
        Cursor result = null;
        try { 
            result = db.query("Project",null,"prj_id = ?",new String[]{String.valueOf(prj_id)},null,null,null);
            
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        if(result.getCount() == 0){
            return null;
        }else{
            result.moveToNext();
            int id = result.getInt(0);
            int prjId = result.getInt(1);
            String prj_name = result.getString(2);
            String researcher = result.getString(3);
            int state = result.getInt(4);
            project = new Project(id,prj_id,prj_name,researcher,state);
            result.close();
            return project;
        }
        
    }

    static ArrayList<Project> getProjectList(SQLiteDatabase db) {
        ArrayList<Project> projectList = new ArrayList<>();
        try {
            Cursor result = db.query("Project",null,"state != ?",new String[]{String.valueOf(State.DELETED)},null,null,null);
            while (result.moveToNext()){
                int id = result.getInt(0);
                int prj_id = result.getInt(1);
                String prj_name = result.getString(2);
                String researcher = result.getString(3);
                int state = result.getInt(4);
                Project project = new Project(id,prj_id,prj_name,researcher,state);
                projectList.add(project);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return projectList;
    }

    public boolean updateProject(SQLiteDatabase db, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("prj_name", prj_name);
            values.put("researcher", researcher);
            db.update("Project", values, "prj_id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    public static boolean deleteProject(SQLiteDatabase db, int id) {
        ContentValues values = new ContentValues();
        try {
            values.put("state", Project.State.DELETED);
            db.update("Project", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Participant", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Property", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Question", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Answer", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Clothes", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Exploration", values, "prj_id=?", new String[]{String.valueOf(id)});//
            db.update("Interaction", values, "prj_id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    static boolean isIDExist(SQLiteDatabase db, int prj_id){
        try {
            Cursor result = db.query("Project",new String[]{"prj_id"},null,null,null,null,null);
//            Cursor result = db.query("Project",new String[]{"prj_id"},"state != ?", new String[]{String.valueOf(State.DELETED)},null,null,null);
            while (result.moveToNext()){
                int id = result.getInt(0);
                if(prj_id == id){
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
        public static final int SELECTED = 3;
    }
}
