package com.oymotion.gforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EndActivity extends AppCompatActivity {
    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        app = (MyApplication) getApplication();
        app.setExperimentState(Experiment.State.FINISHED);
    }
}