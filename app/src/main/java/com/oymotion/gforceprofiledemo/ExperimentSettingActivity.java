package com.oymotion.gforceprofiledemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 29/11/2021
 * Time: 16:11
 * Description:
 */
public class ExperimentSettingActivity extends AppCompatActivity {

    private static final String TAG = "ExperimentSettingActivi";

//    @BindView(R.id.et_cloth_number)
//    EditText et_cloth_number;
    @BindView(R.id.et_explore_time)
    EditText et_explore_time;
    @BindView(R.id.et_rating_scale)
    EditText et_rating_scale;

    @BindView(R.id.btn_edit)
    Button btn_edit;
    @BindView(R.id.btn_properties_edit)
    Button btn_properties_edit;
    @BindView(R.id.btn_open_question_edit)
    Button btn_open_question_edit;

    boolean isEditing = false;
    int prj_id;

    Intent intent;


//    int clothesNo;
    int exploreTime;
    int scale;

    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_setting);
        ButterKnife.bind(this);
        this.setTitle("Experiment Setting");

        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();
////        et_cloth_number.setFocusable(false);
        et_explore_time.setFocusable(false);
        et_rating_scale.setFocusable(false);

        intent = getIntent();
        prj_id = intent.getIntExtra("prj_id", -1);

        updatePreference();

    }

    private void updatePreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

//        clothesNo = preferences.getInt("clothes_no", 3);
        exploreTime = preferences.getInt("explore_time", 15);
        scale = preferences.getInt("scale", 7);

//        et_cloth_number.setFocusableInTouchMode(false);
        et_explore_time.setFocusableInTouchMode(false);
        et_rating_scale.setFocusableInTouchMode(false);
//        et_cloth_number.setFocusable(false);
        et_explore_time.setFocusable(false);
        et_rating_scale.setFocusable(false);

    }
    private void writePreference() {
        editor = preferences.edit();
        editor.putInt("explore_time", exploreTime);
        editor.putInt("scale", scale);
        editor.apply();
        editor.clear();
        Project.updateProjectTimeScale(db,prj_id,exploreTime,scale);
    }


    @OnClick(R.id.btn_edit)
    public void onEditClick() {
        if(isEditing){

            editor = preferences.edit();
//            String clothesNo_str = et_cloth_number.getText().toString();
            String exploreTime_str = et_explore_time.getText().toString();
            String rating_str = et_rating_scale.getText().toString();
            if(exploreTime_str.isEmpty()||rating_str.isEmpty()){
                Toast.makeText(this, "Field can't be empty",Toast.LENGTH_LONG).show();
                return;
            }else{
                isEditing = false;
                btn_edit.setText("Edit");
//                et_cloth_number.setFocusableInTouchMode(false);
                et_explore_time.setFocusableInTouchMode(false);
                et_rating_scale.setFocusableInTouchMode(false);

//                et_cloth_number.setFocusable(false);
                et_explore_time.setFocusable(false);
                et_rating_scale.setFocusable(false);
//                clothesNo = Integer.valueOf(clothesNo_str);
                exploreTime = Integer.valueOf(exploreTime_str);
                scale = Integer.valueOf(rating_str);
                writePreference();

            }
        }else{
            isEditing = true;
            btn_edit.setText("Save");
//            et_cloth_number.setFocusableInTouchMode(true);
            et_explore_time.setFocusableInTouchMode(true);
            et_rating_scale.setFocusableInTouchMode(true);

//            et_cloth_number.setFocusable(true);
            et_explore_time.setFocusable(true);
            et_rating_scale.setFocusable(true);

//            et_cloth_number.requestFocus();
            et_explore_time.requestFocus();
            et_rating_scale.requestFocus();
        }

    }

    @OnClick(R.id.btn_properties_edit)
    public void onEditPropertyClick() {
        intent = new Intent(ExperimentSettingActivity.this,EditPropertiesActivity.class);
        intent.putExtra("prj_id", prj_id);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_open_question_edit)
    public void onEditOpenQuestionClick() {
        intent = new Intent(ExperimentSettingActivity.this,EditOpenQuestionsActivity.class);
        intent.putExtra("prj_id", prj_id);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
