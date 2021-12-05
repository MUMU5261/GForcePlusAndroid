package com.oymotion.gforceprofiledemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 03/12/2021
 * Time: 04:24
 * Description:
 */
public class Question {
    private static final String TAG = "Question";
    ContentValues values = new ContentValues();
    int id = -1;

    String content;
    int prj_id;

    public Question(int id, int prj_id, String content) {
        this.id = id;
        this.prj_id = prj_id;
        this.content = content;
    }

    public Question(int prj_id,String content) {
        this.prj_id = prj_id;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int insertProperty(SQLiteDatabase db){
        values.put("prj_id", prj_id);
        values.put("content", content);
        values.put("state", 0);
        values.put("timestamp", DatabaseUtil.getTimestamp());
        try {
            id = (int) db.insert("Question", null, values);
            values.clear();
            return id;
        } catch (Exception e) {
            values.clear();
            e.printStackTrace();
            return id;
        }
    }

    public static boolean updateValue(SQLiteDatabase db, int id, String question){
        ContentValues values = new ContentValues();
        try {
            values.put("question", question);
            db.update("Question", values, "id=?", new String[]{String.valueOf(id)});//
            values.clear();
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            values.clear();
            return false;
        }
    }
    static ArrayList<Question> getQuestionList(SQLiteDatabase db, int prj_id) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            Cursor result = db.query("Question",null,"prj_id = ? AND state != ?",new String[]{String.valueOf(prj_id),String.valueOf(State.DELETED)},null,null,null);
            while (result.moveToNext()){
                int id = result.getInt(0);
                Log.i(TAG, "getQuestionList: "+result.getColumnName(1));
                String question_con = result.getString(2);
                Question question = new Question(id,question_con);
                questionList.add(question);
            }
            result.close();
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        return questionList;
    }

    public static boolean deleteQuestion(SQLiteDatabase db, int id) {
        try {
            db.delete("Question","id=?", new String[]{String.valueOf(id)});//
            db.delete("Answer","qst_id=?", new String[]{String.valueOf(id)});//
            return true;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public class State{
        public static final int START = 0;
        public static final int DELETED = 2;
    }

}
