package com.oymotion.gforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SurveyActivity extends AppCompatActivity {
    private static final String TAG = "SurveyActivity";

    @BindView(R.id.btn_next)
    Button btn_next;

    RadioGroup rg_liker;
//    RadioButton nan,nv ;
    RadioButton radioButton;


    TextView tv_question;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    Resources res;
    String[] questionList;
    MyApplication app;

    int itr_type;
    int clt_id;
    int likert;
    int clt_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(SurveyActivity.this);
        tv_question = findViewById(R.id.tv_question);
        rg_liker = findViewById(R.id.rg_likert);


        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();



        app = (MyApplication) getApplication();
        itr_type = app.getInteractionType();
        clt_id = app.getClothesID();
        likert = -1;

        res = getResources();
        questionList = res.getStringArray(R.array.survey);
        tv_question.setText(questionList[itr_type]);

        Log.i(TAG, "Initial Information: " + "clt_id:" + clt_id +  "itr_type:" + itr_type);
        btn_next.setEnabled(false);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        int radioButtonID = rg_liker.getCheckedRadioButtonId();
        radioButton = findViewById(radioButtonID);
        Toast.makeText(this, "Selected Radio Button: " + radioButton.getText(), Toast.LENGTH_LONG).show();
        Clothes.updateQuality(db,clt_id,itr_type,likert);
        Intent intentImg;
        Intent intentItr;
        Intent intentEnd;
        intentImg = new Intent(SurveyActivity.this,ImagePickerActivity.class);
        intentItr = new Intent(SurveyActivity.this,InteractionActivity.class);
        intentEnd = new Intent(SurveyActivity.this,EndActivity.class);

        clt_count = app.getClothesCount();
        app.setInteractionState(Interaction.State.START);


        switch (itr_type) {
            case Interaction.Type.SMOOTH:
                app.setInteractionType(Interaction.Type.THICKNESS);
                startActivity(intentItr);
                break;
            case Interaction.Type.THICKNESS:
                app.setInteractionType(Interaction.Type.WARMTH);
                startActivity(intentItr);
                break;
            case Interaction.Type.WARMTH:
                app.setInteractionType(Interaction.Type.FLEXIBILITY);
                startActivity(intentItr);
                break;
            case Interaction.Type.FLEXIBILITY:
                app.setInteractionType(Interaction.Type.SOFTNESS);
                startActivity(intentItr);
                break;
            case Interaction.Type.SOFTNESS:
                app.setInteractionType(Interaction.Type.ENJOYMENT);
                startActivity(intentItr);
                break;
//            case Interaction.Type.ENJOYMENT://move this to another activity;
//                app.setClothesState(Clothes.State.FINISHED);
//                if(clt_count == 6){
//                    startActivity(intentEnd);
//                }else {
//                    app.updateClothesCount(clt_count);
//                    startActivity(intentImg);
//                }
//                break;

                //default
        }
    }

    public void onRadioButtonClicked(View v) {
        int radioButtonID = rg_liker.getCheckedRadioButtonId();
        radioButton = findViewById(radioButtonID);
        String likertStr = (String) radioButton.getText();;
        likert = Integer.valueOf(likertStr);
        Log.i(TAG, "Selected Radio Button: " + radioButton.getText());
        btn_next.setEnabled(true);
    }
}