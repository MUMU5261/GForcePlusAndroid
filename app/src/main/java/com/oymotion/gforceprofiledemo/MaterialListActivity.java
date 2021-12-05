package com.oymotion.gforceprofiledemo;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Date: 03/12/2021
 * Time: 08:54
 * Description:
 */
public class MaterialListActivity extends AppCompatActivity {

    private static final String TAG = "MaterialListActivity";

    private RecyclerView rv_materialList;
    private MaterialListAdapter materialListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences .Editor editor;
    private SharedPreferences preferences;

    int p_id;
    int prj_id;
    ArrayList<Property> propertyList = new ArrayList<>();
    ArrayList<Exploration> explorationList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list);
        ButterKnife.bind(this);
        setTitle("Material List");

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        initData();
        initView();
        updatePreference();
        propertyList = Property.getPropertyList(db,prj_id);

    }

    private void updatePreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        p_id = preferences.getInt("p_id", -1);
        prj_id = preferences.getInt("prj_id", -1);
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        materialListAdapter = new MaterialListAdapter(getData());
    }

    private void initView() {
        rv_materialList = (RecyclerView) findViewById(R.id.rv_material_list);
        // 设置布局管理器
        rv_materialList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_materialList.setAdapter(materialListAdapter);
    }

    @NotNull
    private ArrayList<Clothes> getData() {
        ArrayList<Clothes> data = new ArrayList<>();
        data = Clothes.getMaterialList(db,p_id);
        return data;
    }


    @OnClick(R.id.btn_add_clothes)
    public void onAddClothesClick(){
        Clothes clothes = new Clothes(prj_id, p_id);
        int clt_id = clothes.insertClothes(db);
        Exploration.createExplorationList(db,p_id,prj_id,clt_id,propertyList);

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
