package com.oymotion.gforceprofiledemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 02/12/2021
 * Time: 20:22
 * Description:
 */
public class EditOpenQuestionsActivity extends AppCompatActivity implements AddOpenQuestionDialog.NoticeDialogListener{

    private static final String TAG = "EditOpenQuestionsActivity";
    @BindView(R.id.btn_add_question)
    Button btn_add_question;

    private RecyclerView rv_openQuestionList;
    private OpenQuestionListAdapter openQuestionListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private AddOpenQuestionDialog addOpenQuestionDialog;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_open_questions);
        ButterKnife.bind(this);
        setTitle("Manage Open Question");
        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        initData();
        initView();

    }


    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        openQuestionListAdapter = new OpenQuestionListAdapter(getData());
    }

    private void initView() {
        rv_openQuestionList = (RecyclerView) findViewById(R.id.rv_open_question_list);
        // 设置布局管理器
        rv_openQuestionList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_openQuestionList.setAdapter(openQuestionListAdapter);
    }

    @NotNull
    private ArrayList<Question> getData() {
        ArrayList<Question> data = new ArrayList<>();
        data = Question.getQuestionList(db);
        return data;
    }

    @OnClick(R.id.btn_add_question)
    public void onAddOpenQuestionClick() {
        Log.i(TAG, "onAddClick: tetstt");
        FragmentManager fm = getSupportFragmentManager();
        addOpenQuestionDialog = new AddOpenQuestionDialog();
        addOpenQuestionDialog.show(fm,"fragment_add_open_question");

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

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG, "onDialogPositiveClick: ");
        openQuestionListAdapter.updateData(getData());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }
}
