package com.oymotion.gforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    GForceDatabaseOpenHelper dbOpenHelper;
    SQLiteDatabase db;

    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;

    private String gender;
    private int gender_pos;
    private int p_id; //participant ID
    private List<Integer> p_ids = new ArrayList<Integer>();
    private Integer[] list = new Integer[] {123456,234567,345678};


    @BindView(R.id.et_p_id)
    EditText et_p_id;
    @BindView(R.id.spi_gender)
    Spinner spi_gender;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.cb_remember)
    CheckBox cb_remember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        dbOpenHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbOpenHelper.getReadableDatabase();

        gender = "select";
        loadSpinnerIdTypes();
        p_ids.add(123456);
        p_ids.add(234567);
        p_ids.add(345678);
        gender_pos = 0;
        p_id = 0;
        gender = "";

        gender_pos = 0;
        p_id = 0;
        gender = "";
        et_p_id.findViewById(R.id.et_p_id);
        spi_gender.findViewById(R.id.spi_gender);

        updatePreference();

    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        Log.i(TAG, "Element selected:");
        String id_str = et_p_id.getText().toString();
        boolean isCorrect = false;

        if(id_str.isEmpty()){
            Toast.makeText(this, "Please enter participant ID",Toast.LENGTH_LONG).show();
            return;
        }else {
            p_id = Integer.valueOf(id_str);
            isCorrect = p_ids.contains(Integer.valueOf(p_id));
            if(!isCorrect){
                Toast.makeText(this, "Wrong Participant ID.",Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(gender.equals("select")){
            Toast.makeText(this, "Please Select your gender.",Toast.LENGTH_LONG).show();
            return;
        }
        if(isCorrect){
            editor = preferences.edit();
            if(cb_remember.isChecked()){
                editor.putBoolean("isRemember",true);
                editor.putInt("p_id",p_id);
                editor.putInt("gender_pos",gender_pos);
            }else{editor.clear();}
            editor.apply();

            boolean isSuccess = DatabaseUtil.InsertParticipant(db,p_id,gender);//refactor to keep consist with the other entity
            if(!isSuccess){
                Toast.makeText(this, "Login fail.",Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Login success.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this,InstructionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("p_id",p_id);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Login fail.",Toast.LENGTH_LONG).show();
        }

    }

    @OnItemSelected(R.id.spi_gender)
    public void onGenderSelected(int position) {
        gender = spi_gender.getItemAtPosition(position).toString();
        gender_pos = position;
        Toast.makeText(this, "Element selected:" + gender, Toast.LENGTH_LONG).show();
    }

    private void loadSpinnerIdTypes() {
        String[] genItems = getResources().getStringArray(R.array.gender);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, genItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_gender.setAdapter(adapter);
    }

    private void updatePreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("isRemember",false);
        if (isRemember){
            p_id = preferences.getInt("p_id",0);
            gender_pos = preferences.getInt("gender_pos",0);
            et_p_id.setText(String.valueOf(p_id));
            spi_gender.setSelection(gender_pos);
            cb_remember.setChecked(true);
        }

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

