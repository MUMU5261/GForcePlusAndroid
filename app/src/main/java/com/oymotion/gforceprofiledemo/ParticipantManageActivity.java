package com.oymotion.gforceprofiledemo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
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
 * Date: 19/11/2021
 * Time: 15:20
 * Description:
 */
public class ParticipantManageActivity extends AppCompatActivity implements AddParticipantDialog.NoticeDialogListener {
    private static final String TAG = "ParticipantManageActivity";

    @BindView(R.id.btn_add_participant)
    Button btn_add;

    private RecyclerView rv_participantList;
    private ParticipantListAdapter participantListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AddParticipantDialog addParticipantDialog;
    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    int prj_id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_manage);
        ButterKnife.bind(this);
        setTitle("Manage Participants");

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        btn_add = findViewById(R.id.btn_add_participant);
        initData();
        initView();

//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_add.setText("testtttt");
//            }
//        });


//        itemBackgroundColor = ContextCompat.getColor(ParticipantManageActivity.this, R.color.selected);
//        configureResultList();
    }


    private void initData() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        participantListAdapter = new ParticipantListAdapter(getData());
    }

    private void initView() {
        rv_participantList = (RecyclerView) findViewById(R.id.rv_participant_list);
        // 设置布局管理器
        rv_participantList.setLayoutManager(layoutManager);
        // 设置adapter
        rv_participantList.setAdapter(participantListAdapter);
    }

    @NotNull
    private ArrayList<Participant> getData() {
        ArrayList<Participant> data = new ArrayList<>();
        data = Participant.getParticipantList(db,prj_id);
        return data;
    }

    @OnClick(R.id.btn_add_participant)
    public void onAddClick() {
        FragmentManager fm = getSupportFragmentManager();
        addParticipantDialog = new AddParticipantDialog();
        addParticipantDialog.show(fm,"fragment_add_participant");

    }


//    private void configureResultList() {
//        rv_participantList.setHasFixedSize(true);
//        rv_participantList.setItemAnimator(null);
//        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
//        rv_participantList.setLayoutManager(recyclerLayoutManager);
//        participantListAdapter = new ParticipantListAdapter();
//        rv_participantList.setAdapter(participantListAdapter);
//        rv_participantList.setOnAdapterItemClickListener(view -> {
//            final int childAdapterPosition = rv_participantList.getChildAdapterPosition(view);
//            final ScanResult itemAtPosition = participantListAdapter.getItemAtPosition(childAdapterPosition);
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
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int p_id) {
        Log.i(TAG, "onDialogPositiveClick: ");
        participantListAdapter.updateData(getData());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();

    }
}
