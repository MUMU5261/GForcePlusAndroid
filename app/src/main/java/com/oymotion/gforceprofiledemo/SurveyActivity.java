package com.oymotion.gforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
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
    TextView tv_low;
    TextView tv_high;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    Resources res;
    String[] questionList;
    String[] wordPairList;
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
        this.setTitle("");
        tv_question = findViewById(R.id.tv_question);
        rg_liker = findViewById(R.id.rg_likert);
        tv_low = findViewById(R.id.tv_low);
        tv_high = findViewById(R.id.tv_high);


        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();



        app = (MyApplication) getApplication();
        itr_type = app.getInteractionType();
        clt_id = app.getClothesID();
        likert = -1;

        res = getResources();
        questionList = res.getStringArray(R.array.survey2);
        tv_question.setText(Html.fromHtml(questionList[itr_type], Typeface.BOLD));
        String low = "low";
        String high = "high";
        switch (itr_type){
            case Interaction.Type.SMOOTH:
                wordPairList = res.getStringArray(R.array.smooth);
                break;
            case Interaction.Type.THICKNESS:
                wordPairList = res.getStringArray(R.array.thick);
                break;
            case Interaction.Type.WARMTH:
                wordPairList = res.getStringArray(R.array.warm);
                break;
            case Interaction.Type.FLEXIBILITY:
                wordPairList = res.getStringArray(R.array.flexible);
                break;
            case Interaction.Type.SOFTNESS:
                wordPairList = res.getStringArray(R.array.soft);
                break;
            case Interaction.Type.ENJOYMENT:
                wordPairList = new String[]{"a", "b"};
                break;
        }
//        hide for version2
//        tv_low.setText(wordPairList[0]);
//        tv_high.setText(wordPairList[1]);

        Log.i(TAG, "Initial Information: " + "clt_id:" + clt_id +  "itr_type:" + itr_type);
        btn_next.setEnabled(false);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        int radioButtonID = rg_liker.getCheckedRadioButtonId();
        radioButton = findViewById(radioButtonID);
        Toast.makeText(this, "Selected Radio Button: " + radioButton.getText(), Toast.LENGTH_LONG).show();
//        Clothes.updateQuality(db,clt_id,itr_type,likert);
        Intent intentItr;
        intentItr = new Intent(SurveyActivity.this,InteractionActivity.class);

        clt_count = app.getClothesCount();
        app.setInteractionState(Interaction.State.START);

        switch (itr_type) {
            case Interaction.Type.SMOOTH:
                app.setInteractionType(Interaction.Type.THICKNESS);
                startActivity(intentItr);
                break;
            case Interaction.Type.THICKNESS:
                app.setInteractionType(Interaction.Type.ENJOYMENT);
                startActivity(intentItr);
                break;
//            case Interaction.Type.WARMTH:
//                app.setInteractionType(Interaction.Type.FLEXIBILITY);
//                startActivity(intentItr);
//                break;
//            case Interaction.Type.FLEXIBILITY:
//                app.setInteractionType(Interaction.Type.SOFTNESS);
//                startActivity(intentItr);
//                break;
//            case Interaction.Type.SOFTNESS:
//                app.setInteractionType(Interaction.Type.ENJOYMENT);
//                startActivity(intentItr);
//                break;
//

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