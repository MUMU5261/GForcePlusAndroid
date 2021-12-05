package com.oymotion.gforceprofiledemo;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 05/12/2021
 * Time: 02:30
 * Description:
 */
public class ProjectListActivity extends AppCompatActivity implements AddProjectDialog.NoticeDialogListener {
    private static final String TAG = "ProjectManageActivity";

    @BindView(R.id.btn_add_project)
    Button btn_add;


    private RecyclerView rv_projectList;
    private ProjectListAdapter projectListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddProjectDialog addProjectDialog;
    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;

    int prj_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        ButterKnife.bind(this);
        setTitle("Manage Projects");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prj_id = preferences.getInt("prj_id", -1);

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        initData();
        initView();
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        projectListAdapter = new ProjectListAdapter(getData());
        btn_add = findViewById(R.id.btn_add_project);
    }

    private void initView() {
        rv_projectList = (RecyclerView) findViewById(R.id.rv_project_list);
        // 设置布局管理器
        rv_projectList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_projectList.setAdapter(projectListAdapter);
    }

    @NotNull
    private ArrayList<Project> getData() {
        ArrayList<Project> data = new ArrayList<>();
        data = Project.getProjectList(db);
        return data;
    }

    @OnClick(R.id.btn_add_project)
    public void onAddClick() {
        FragmentManager fm = getSupportFragmentManager();
        addProjectDialog = new AddProjectDialog();
        addProjectDialog.show(fm,"fragment_add_project");

    }

    @OnClick(R.id.btn_change_current_project)
    public void onChangeProjectClick() {
        prj_id = 1;
        editor = preferences.edit();
        editor.putInt("prj_id", prj_id);
        this.recreate();//replace with update
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


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG, "onDialogPositiveClick: ");
        projectListAdapter.updateData(getData());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }
}
