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
 * Time: 20:21
 * Description:
 */
public class EditPropertiesActivity extends AppCompatActivity implements AddPropertyDialog.NoticeDialogListener{

    private static final String TAG = "EditPropertiesActivity";

    @BindView(R.id.btn_add_property)
    Button btn_add_property;

    private RecyclerView rv_propertyList;
    private PropertyListAdapter propertyListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddPropertyDialog addPropertyDialog;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_properties);
        ButterKnife.bind(this);
        setTitle("Manage Property");
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
        propertyListAdapter = new PropertyListAdapter(getData());
    }

    private void initView() {
        rv_propertyList = (RecyclerView) findViewById(R.id.rv_property);
        // 设置布局管理器
        rv_propertyList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_propertyList.setAdapter(propertyListAdapter);
    }

    @NotNull
    private ArrayList<Property> getData() {
        ArrayList<Property> data = new ArrayList<>();
        data = Property.getPropertyList(db);
        return data;
    }

    @OnClick(R.id.btn_add_property)
    public void onAddPropertyClick() {
        Log.i(TAG, "onAddClick: tetstt");
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }


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
