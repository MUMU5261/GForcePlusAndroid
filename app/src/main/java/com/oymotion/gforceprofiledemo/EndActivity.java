package com.oymotion.gforceprofiledemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;

import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;

public class EndActivity extends AppCompatActivity {
    MyApplication app;
    @BindView(R.id.btn_exit)
    Button btn_exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        this.setTitle("Login");
        app = (MyApplication) getApplication();
        app.setExperimentState(Experiment.State.FINISHED);
    }

    @OnClick(R.id.btn_exit)
    public void onNextClick() {
        finish();
        System.exit(0);
    }


}