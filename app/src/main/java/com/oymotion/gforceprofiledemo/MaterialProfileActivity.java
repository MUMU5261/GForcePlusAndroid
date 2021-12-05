package com.oymotion.gforceprofiledemo;

import android.content.Intent;
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
 * Date: 03/12/2021
 * Time: 10:43
 * Description:
 */
public class MaterialProfileActivity extends AppCompatActivity {
    private static final String TAG = "MaterialProfileActivity";


    private RecyclerView rv_interactionList;
    private InteractionListAdapter interactionListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_profile);
        ButterKnife.bind(this);
        setTitle("Material no");

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
        interactionListAdapter = new InteractionListAdapter(getData());
    }

    private void initView() {
        rv_interactionList = (RecyclerView) findViewById(R.id.rv_interaction_list);
        // 设置布局管理器
        rv_interactionList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_interactionList.setAdapter(interactionListAdapter);
    }

    @NotNull
    private ArrayList<Interaction> getData() {
        ArrayList<Interaction> data = new ArrayList<>();
        data = Interaction.getInteractionList(db);
        return data;
    }

    @OnClick(R.id.btn_upload_photo)
    public void onUploadPhotoClick() {
        Intent intent = new Intent(MaterialProfileActivity.this,ImagePickerActivity.class);
//        what is set flag
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("p_id",p_id);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_answer_question)
    public void onAnswerQuestionClick() {
        Intent intent = new Intent(MaterialProfileActivity.this,ExperimentSettingMenuActivity.class);
//        what is set flag
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("p_id",p_id);
        startActivity(intent);
        finish();
    }



//    private void configureResultList() {
//        rv_interactionList.setHasFixedSize(true);
//        rv_interactionList.setItemAnimator(null);
//        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
//        rv_interactionList.setLayoutManager(recyclerLayoutManager);
//        interactionListAdapter = new ParticipantListAdapter();
//        rv_interactionList.setAdapter(interactionListAdapter);
//        rv_interactionList.setOnAdapterItemClickListener(view -> {
//            final int childAdapterPosition = rv_interactionList.getChildAdapterPosition(view);
//            final ScanResult itemAtPosition = interactionListAdapter.getItemAtPosition(childAdapterPosition);
//            onAdapterItemClick(itemAtPosition);
//
//            //when selected, item view is highlighted
//            view.setBackgroundColor(itemBackgroundColor);
//        });
//    }
//
//    private void onAdapterItemClick(ScanResult scanResults) {
////        final Intent intent = new Intent(this, DeviceActivity.class);
////        intent.putExtra(DeviceActivity.EXTRA_DEVICE_NAME, scanResults.getBluetoothDevice().getName());
////        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, scanResults.getBluetoothDevice().getAddress());
////        startActivity(intent);
//
//        //test for save bluetooth devices info and transfer
//        String extra_device_name = scanResults.getBluetoothDevice().getName();
//        String extra_mac_address = scanResults.getBluetoothDevice().getAddress();
//        int hand = Device.checkDeviceLR(db, extra_mac_address);
//        if(!isExist(extra_mac_address)) {
//            if (hand == 0){
//
//                leftSelected.setBackground(getDrawable(R.drawable.round_purple));
//            }else if(hand == 1){
//                rightSelected.setBackground(getDrawable(R.drawable.round_purple));
//            }
//            btList.add(new Bluetooth(extra_device_name,extra_mac_address));
//            count_selected ++;
//        }
//        if(count_selected == 2){
//            nextButton.setEnabled(true);
//        }
//
//    }


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
