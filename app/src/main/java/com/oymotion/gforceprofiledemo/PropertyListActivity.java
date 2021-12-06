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
 * Date: 05/12/2021
 * Time: 03:31
 * Description:
 */
public class PropertyListActivity extends AppCompatActivity implements AddPropertyDialog.NoticeDialogListener {
    private static final String TAG = "PropertyManageActivity";

    @BindView(R.id.btn_add_property)
    Button btn_add;

    private RecyclerView rv_propertyList;
    private PropertyListAdapter propertyListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddPropertyDialog addPropertyDialog;
    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    int prj_id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        ButterKnife.bind(this);
        setTitle("Manage Properties");

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        btn_add = findViewById(R.id.btn_add_property);
        initData();
        initView();
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        propertyListAdapter = new PropertyListAdapter(getData());
    }

    private void initView() {
        rv_propertyList = (RecyclerView) findViewById(R.id.rv_project_list);
        // 设置布局管理器
        rv_propertyList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_propertyList.setAdapter(propertyListAdapter);
    }

    @NotNull
    private ArrayList<Property> getData() {
        ArrayList<Property> data = new ArrayList<>();
        data = Property.getPropertyList(db,prj_id);
        return data;
    }

    @OnClick(R.id.btn_add_property)
    public void onAddClick() {
        FragmentManager fm = getSupportFragmentManager();
        addPropertyDialog = new AddPropertyDialog();
        addPropertyDialog.show(fm,"fragment_add_property");

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
        propertyListAdapter.updateData(getData());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }
}
