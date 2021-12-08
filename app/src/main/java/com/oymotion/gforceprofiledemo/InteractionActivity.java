package com.oymotion.gforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.PrecomputedText;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oymotion.gforceprofile.DataNotificationCallback;
import com.oymotion.gforceprofile.GForceProfile;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InteractionActivity extends AppCompatActivity {

    private static final String TAG = "InteractionActivity";

    //layout/UI biding
    @BindView(R.id.tv_device_state_l)
    TextView tv_device_state_l;
    @BindView(R.id.tv_device_state_r)
    TextView tv_device_state_r;
    //    @BindView(R.id.btn_start)
//    Button btn_start_notifying;
    @BindView(R.id.btn_start_notifying)
    Button btn_start_notifying;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.btn_reConnect)
    Button btn_reConnect;
    @BindView(R.id.btn_reStart)
    Button btn_reStart;

    private CountDownTimer countDownTimerPre;
    private boolean countPre = false;
    private CountDownTimer countDownTimerExplore;
    private boolean countExplore = false;
    private TextView textViewQuaternion_l;
    private TextView textViewQuaternion_r;
    //    private TextView tv_countdown_start;
    private TextView tv_countdown_itr;
    private TextView tv_itr_statement;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    Intent intent;
    Handler handler;// if private or not
    //may need two handler, or use other way to listen state change, to improve the data collection rate
    Runnable runnable;
//    Runnable runnable_data_notify;

    Resources res;
    String[] statementList;

    int itr_id;//explore_id
    int explore_id;//explore_id
    String ppt_name;//explore_id
    int itr_type;//explore_id
    int ppt_id;//explore_id
    int p_id;
//    int prj_id;
    int clt_id;
    int prj_id;
    long explore_time;

    private GForceProfile.BluetoothDeviceStateEx state_l = GForceProfile.BluetoothDeviceStateEx.disconnected;
    private GForceProfile.BluetoothDeviceStateEx state_r = GForceProfile.BluetoothDeviceStateEx.disconnected;

    private GForceProfile gForceProfile_l;
    private GForceProfile gForceProfile_r;

    private String textErrorMsg = "";
    private boolean notifying = false;
    private MyApplication app;

    private Interaction interaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_interaction);
        ButterKnife.bind(this);
        this.setTitle("Data Gathering");

        textViewQuaternion_l = this.findViewById(R.id.tv_quaternion_l);
        textViewQuaternion_r = this.findViewById(R.id.tv_quaternion_r);
//        tv_countdown_start = this.findViewById(R.id.tv_countdown_start);
        tv_countdown_itr = this.findViewById(R.id.tv_countdown_itr);
        tv_itr_statement = this.findViewById(R.id.tv_itr_statement);
        btn_start_notifying.setEnabled(false);
        btn_next.setEnabled(false);
        btn_reStart.setEnabled(false);
        btn_reConnect.setEnabled(false);

        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();

        p_id = Participant.getIDFromPreference(this);
        prj_id = Project.getIDFromPreference(this);
        explore_time = Project.getTimeFromPreference(this) * 1000;
        tv_countdown_itr.setText(String.valueOf(Project.getTimeFromPreference(this))+"S");

//        itr_type = app.getInteractionType();
//        clt_id = app.getClothesID();

        intent = this.getIntent();
        clt_id = intent.getIntExtra("clt_id",-1);
        ppt_name = intent.getStringExtra("ppt_name");//
        itr_type = intent.getIntExtra("ppt_id",-1);//-2:Relax; -3:Fist,0:Free
        itr_type = intent.getIntExtra("explore_id",-1);//-2:Relax; -3:Fist,0:Free

        Log.i(TAG, "Initial Information: " + "prj_id:" + prj_id + "p_id:" + p_id + "itr_type:" + itr_type + "clt_id" + clt_id);

        getPrompt();

        //create new interaction
        interaction = new Interaction(prj_id, p_id, clt_id, itr_type);
        itr_id = interaction.insertInteraction(db);

        if (itr_id != -1) {
            Log.i(TAG, "insert interaction success");
            Toast.makeText(InteractionActivity.this, "insert interaction success", Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "insert interaction fail");
            Toast.makeText(InteractionActivity.this, "insert interaction fail", Toast.LENGTH_LONG).show();
        }

//set a transaction, if it's null, go connect
        app = (MyApplication) getApplication();

        try {
            gForceProfile_l = app.getProfileLeft();
            gForceProfile_r = app.getProfileRight();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // setup a thread to update status
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                updateState();
                handler.postDelayed(runnable, 1000);//repeat update the state of the device connecting
            }
        };
        handler.post(runnable);


    }

    private void getPrompt() {
        res = getResources();
        statementList = res.getStringArray(R.array.itr_statement2);
//        String html = statementList[itr_type];

        String html;
        switch (itr_type){
            case 0:
                html = statementList[2];
                break;
            case -2:
                html = statementList[0];
                break;
            case -3:
                html = statementList[1];
                break;
            default:
                html = "Please explore how <b>" + ppt_name + "</b> the material is.";
        }
        tv_itr_statement.setText(Html.fromHtml(html, Typeface.BOLD));
    }

    @OnClick(R.id.btn_start_notifying)
    public void onStartNotify() {
        // not work;
        if(countPre == true) {
            countDownTimerPre.cancel();
            countPre = false;
        }
        if(countExplore == true){
            countDownTimerExplore.cancel();
            countExplore = false;
        }


        if (notifying) {
            onStartClick();
        } else {
//            btn_start_notifying.setEnabled(false);
            try {
//            state_l = gForceProfile_l.getState();
//            state_r = gForceProfile_r.getState();
//            tv_device_state_l.setText(state_l.toString());
//            tv_device_state_r.setText(state_r.toString());
//            handler.removeCallbacks(runnable);
//            handler.postDelayed(runnable, 1000);
                countDownTimerPre =  new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btn_start_notifying.setText(String.valueOf(millisUntilFinished / 1000));
                        countPre = true;
//                    tv_countdown_start.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
//                    tv_countdown_start.setText("start!");
                        onStartClick();
                        countPre = false;
                        this.cancel();
                    }
                }.start();

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());

            }
        }


    }

    //    @OnClick(R.id.btn_start)
    public void onStartClick() {
        if (notifying) {
            btn_start_notifying.setText("Start");
            btn_start_notifying.setEnabled(true);
            gForceProfile_l.stopDataNotification();
            gForceProfile_r.stopDataNotification();
            notifying = false;
//            handler.removeCallbacks(runnable_data_notify);
//            btn_next.setEnabled(true);
        } else {
            if (state_l != GForceProfile.BluetoothDeviceStateEx.ready || state_r != GForceProfile.BluetoothDeviceStateEx.ready) {
                Toast.makeText(InteractionActivity.this, "not both devices are ready", Toast.LENGTH_SHORT).show();
                return;
            }

            dataNotification(gForceProfile_l, 0);
            dataNotification(gForceProfile_r, 1);

            btn_start_notifying.setEnabled(true);
            btn_start_notifying.setText("Stop");
            notifying = true;
//            runnable_data_notify = new Runnable() {
//                @Override
//                public void run() {
//                    onStartClick();
//                }
//            };
            countDownTimerExplore= new CountDownTimer(explore_time, 1000) {

                public void onTick(long millisUntilFinished) {
                    tv_countdown_itr.setText(String.valueOf(millisUntilFinished / 1000) +"S");
                    countExplore = true;
                }

                public void onFinish() {
                    tv_countdown_itr.setText("Done!");
                    onStartClick();
                    btn_next.setEnabled(true);
                    btn_reStart.setEnabled(true);
                    countExplore = false;
                    this.cancel();
                }
            }.start();
//            handler.postDelayed(runnable_data_notify, 5000);
        }
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
////        switch (itr_type) {
////            case Interaction.Type.FREE:
////            case Interaction.Type.SMOOTH:
////            case Interaction.Type.SOFT:
////            case Interaction.Type.WARMTH:
////            case Interaction.Type.THICKNESS:
////        }
//        if (interaction.updateState(db, Interaction.State.FINISHED)) {
//            app.setInteractionState(Interaction.State.FINISHED);
//        } else {
//            Toast.makeText(InteractionActivity.this, "Failed Exploration", Toast.LENGTH_LONG).show();
//        }
//
//        Intent intent;
//        switch (itr_type) {
//            case Interaction.Type.RELAX:
//                app.setInteractionState(Interaction.State.START);
//                app.setInteractionType(Interaction.Type.FIST);
//                intent = new Intent(InteractionActivity.this, InteractionActivity.class);
//                break;
//            case Interaction.Type.FIST:
////                app.setInteractionState(Interaction.State.START);
////                app.setInteractionType(Interaction.Type.FREE);
//                intent = new Intent(InteractionActivity.this, ImagePickerActivity.class);
//                break;
//            case Interaction.Type.FREE:
//                app.setInteractionState(Interaction.State.START);
//                app.setInteractionType(Interaction.Type.SMOOTH);
//                intent = new Intent(InteractionActivity.this, InteractionActivity.class);
//                break;
//            case Interaction.Type.ENJOYMENT:
//                intent = new Intent(InteractionActivity.this, SurveyEnjoymentActivity.class);//WENT TO ENJOYMENT SURVEY PAGE
//                Log.i(TAG, String.valueOf(itr_type));
//                break;
//            default:
//                intent = new Intent(InteractionActivity.this, SurveyActivity.class);
//                break;
//
//        }
//        startActivity(intent);
        if(itr_type >0){
            Exploration.finishExploration(db, Exploration.State.FINISHED, explore_id, itr_id);
        }
        finish();
    }
    @OnClick(R.id.btn_reStart)
    public void setBtn_reStart(){
        if (interaction.updateState(db, Interaction.State.FAILED)) {
            app.setInteractionState(Interaction.State.FAILED);
        } else {
            Toast.makeText(InteractionActivity.this, "Fail to update state failed ", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(InteractionActivity.this,InteractionActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.btn_reConnect)
    public void setBtn_reConnect(){
        Intent intent = new Intent(InteractionActivity.this, SetupDevicesActivity.class);
        startActivity(intent);
    }

    private void dataNotification(GForceProfile gForceProfile, int hand) {
        gForceProfile.startDataNotification(new DataNotificationCallback() {
            @Override
            public void onData(byte[] data) {
                Log.i(TAG, "data type: " + data[0] + ", len: " + data.length);

                if (data[0] == GForceProfile.NotifDataType.NTF_QUAT_FLOAT_DATA && data.length == 17) {
                    Log.i(TAG, "Quat data: " + Arrays.toString(data) + "\nhand use: " + hand);
                    Log.i(TAG, "hand use: " + hand);

                    byte[] W = new byte[4];
                    byte[] X = new byte[4];
                    byte[] Y = new byte[4];
                    byte[] Z = new byte[4];

                    System.arraycopy(data, 1, W, 0, 4);
                    System.arraycopy(data, 5, X, 0, 4);
                    System.arraycopy(data, 9, Y, 0, 4);
                    System.arraycopy(data, 13, Z, 0, 4);

                    float w = getFloat(W);
                    float x = getFloat(X);
                    float y = getFloat(Y);
                    float z = getFloat(Z);

                    if (hand == 0) {
                        Log.i(TAG, "#####LEFT QUAT####" + +w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
                    } else if (hand == 1) {
                        Log.i(TAG, "****RIGHT QUAT****" + +w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
                    }

                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("prj_id", prj_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("w", w);
                    values.put("x", x);
                    values.put("y", y);
                    values.put("z", z);
                    values.put("raw_data", data);//store raw data
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Quaternion", null, values));
                    values.clear();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (hand == 0) {
                                textViewQuaternion_l.setText("W: " + w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
                            } else if (hand == 1) {
                                textViewQuaternion_r.setText("W: " + w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);//use fragment to set up the update on sree
                            }
                        }
                    });

                } else if (data[0] == GForceProfile.NotifDataType.NTF_EMG_ADC_DATA && data.length == 129) {
                    Log.i("DeviceActivity", "EMG data: " + Arrays.toString(data) + "hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    ArrayList<Byte> CH0 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH1 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH2 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH3 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH4 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH5 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH6 = new ArrayList<Byte>(16);
                    ArrayList<Byte> CH7 = new ArrayList<Byte>(16);

                    byte[] raw_EMG = new byte[128];
                    System.arraycopy(data, 1, raw_EMG, 0, 128);
                    int count = 0;
                    for (byte i : raw_EMG) {
                        switch (count) {
                            case 0:
                                CH0.add(i);
                                count++;
                                break;
                            case 1:
                                CH1.add(i);
                                count++;
                                break;
                            case 2:
                                CH2.add(i);
                                count++;
                                break;
                            case 3:
                                CH3.add(i);
                                count++;
                                break;
                            case 4:
                                CH4.add(i);
                                count++;
                                break;
                            case 5:
                                CH5.add(i);
                                count++;
                                break;
                            case 6:
                                CH6.add(i);
                                count++;
                                break;
                            case 7:
                                CH7.add(i);
                                count = 0;
                                break;
                        }

                    }
                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("prj_id", prj_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("ch_01", getEMGList(CH0).toString());
                    values.put("ch_02", getEMGList(CH1).toString());
                    values.put("ch_03", getEMGList(CH2).toString());
                    values.put("ch_04", getEMGList(CH3).toString());
                    values.put("ch_05", getEMGList(CH4).toString());
                    values.put("ch_06", getEMGList(CH5).toString());
                    values.put("ch_07", getEMGList(CH6).toString());
                    values.put("ch_08", getEMGList(CH7).toString());
                    values.put("raw_data", data);//store raw data
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("EMG", null, values));
                    values.clear();


                    Log.i(TAG, "CH0" + getEMGList(CH0).toString());
                    Log.i(TAG, "CH1" + getEMGList(CH1).toString());
                    Log.i(TAG, "CH2" + getEMGList(CH2).toString());
                    Log.i(TAG, "CH3" + getEMGList(CH3).toString());
                    Log.i(TAG, "CH4" + getEMGList(CH4).toString());
                    Log.i(TAG, "CH5" + getEMGList(CH5).toString());
                    Log.i(TAG, "CH6" + getEMGList(CH6).toString());
                    Log.i(TAG, "CH7" + getEMGList(CH7).toString());

                } else if (data[0] == GForceProfile.NotifDataType.NTF_EULER_DATA && data.length == 13) {
                    Log.i("DeviceActivity", "NTF_EULER_DATA: " + Arrays.toString(data) + "\nhand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    byte[] b_pitch = new byte[4];
                    byte[] b_roll = new byte[4];
                    byte[] b_yaw = new byte[4];

                    System.arraycopy(data, 1, b_pitch, 0, 4);
                    System.arraycopy(data, 5, b_roll, 0, 4);
                    System.arraycopy(data, 9, b_yaw, 0, 4);

                    float pitch = getFloat(b_pitch);
                    float roll = getFloat(b_roll);
                    float yaw = getFloat(b_yaw);
                    Log.i("DeviceActivity", "NTF_EULER_DATA: " + " pitch:" + pitch + " roll:" + roll + " yaw:" + yaw);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ContentValues values = new ContentValues();
                            values.put("p_id", p_id);
                            values.put("prj_id", prj_id);
                            values.put("itr_id", itr_id);
                            values.put("itr_type", itr_type);
                            values.put("hand", hand);
                            values.put("clt_id", clt_id);
                            values.put("pitch", pitch);
                            values.put("roll", roll);
                            values.put("yaw", yaw);
                            values.put("raw_data", data);//store raw data
                            values.put("state", 0);
                            values.put("timestamp", DatabaseUtil.getTimestamp());
                            db.insert("Euler_Angle", null, values);
                            values.clear();
                        }
                    });

                } else if (data[0] == GForceProfile.NotifDataType.NTF_GYO_DATA && data.length == 13) {
                    Log.i("DeviceActivity", "NTF_GYO_DATA : " + Arrays.toString(data) + "hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    byte[] b_gyo_x = new byte[4];
                    byte[] b_gyo_y = new byte[4];
                    byte[] b_gyo_z = new byte[4];

                    System.arraycopy(data, 1, b_gyo_x, 0, 4);
                    System.arraycopy(data, 5, b_gyo_y, 0, 4);
                    System.arraycopy(data, 9, b_gyo_z, 0, 4);

                    System.out.println(Arrays.toString(b_gyo_x));
                    System.out.println(Arrays.toString(b_gyo_y));
                    System.out.println(Arrays.toString(b_gyo_z));

                    float gyo_x = getFloat2(b_gyo_x);
                    float gyo_y = getFloat2(b_gyo_y);
                    float gyo_z = getFloat2(b_gyo_z);

                    Log.i("DeviceActivity", " NTF_GYO_DATA:" + " gyo_x:" + gyo_x + " gyo_y:" + gyo_y + " gyo_z:" + gyo_z);
                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("prj_id", prj_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("x", gyo_x);
                    values.put("y", gyo_y);
                    values.put("z", gyo_z);
                    values.put("raw_data", data);//store raw data
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Gyroscope", null, values));
                    values.clear();

                } else if (data[0] == GForceProfile.NotifDataType.NTF_ACC_DATA && data.length == 13) {
                    Log.i("DeviceActivity", "NTF_ACC_DATA : " + Arrays.toString(data) + "hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    byte[] b_acc_x = new byte[4];
                    byte[] b_acc_y = new byte[4];
                    byte[] b_acc_z = new byte[4];

                    System.arraycopy(data, 1, b_acc_x, 0, 4);
                    System.arraycopy(data, 5, b_acc_y, 0, 4);
                    System.arraycopy(data, 9, b_acc_z, 0, 4);

                    System.out.println(Arrays.toString(b_acc_x));
                    System.out.println(Arrays.toString(b_acc_y));
                    System.out.println(Arrays.toString(b_acc_z));

                    float acc_x = getFloat2(b_acc_x);
                    float acc_y = getFloat2(b_acc_y);
                    float acc_z = getFloat2(b_acc_z);

                    Log.i("DeviceActivity", "NTF_ACC_DATA: " + " acc_x:" + acc_x + " acc_y:" + acc_y + " acc_z:" + acc_z);

                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("prj_id", prj_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("x", acc_x);
                    values.put("y", acc_y);
                    values.put("z", acc_z);
                    values.put("raw_data", data);//store raw data
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Acceletor", null, values));
                    values.clear();

                } else if (data[0] == GForceProfile.NotifDataType.NTF_MAG_DATA && data.length == 13) {
                    Log.i("DeviceActivity", "NTF_MAG_DATA : " + Arrays.toString(data));
                    Log.i("DeviceActivity", "hand use: " + hand + "hand use: " + hand);
                    byte[] b_mag_x = new byte[4];
                    byte[] b_mag_y = new byte[4];
                    byte[] b_mag_z = new byte[4];

                    System.arraycopy(data, 1, b_mag_x, 0, 4);
                    System.arraycopy(data, 5, b_mag_y, 0, 4);
                    System.arraycopy(data, 9, b_mag_z, 0, 4);

                    System.out.println(Arrays.toString(b_mag_x));
                    System.out.println(Arrays.toString(b_mag_y));
                    System.out.println(Arrays.toString(b_mag_z));

                    float mag_x = getFloat2(b_mag_x);
                    float mag_y = getFloat2(b_mag_y);
                    float mag_z = getFloat2(b_mag_z);
                    Log.i("DeviceActivity", "NTF_MAG_DATA: " + " mag_x:" + mag_x + " mag_y:" + mag_y + " mag_z:" + mag_z);
                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("prj_id", prj_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("x", mag_x);
                    values.put("y", mag_y);
                    values.put("z", mag_z);
                    values.put("raw_data", data);//store raw data
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Magnetometer", null, values));
                    values.clear();
                } else if (data[0] == GForceProfile.NotifDataType.NTF_ROTA_DATA && data.length == 37) {
                    Log.i("DeviceActivity", "NTF_ROTA_DATA : " + Arrays.toString(data) + "hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);

                }
            }
        });
    }

    void updateState() {
        GForceProfile.BluetoothDeviceStateEx newState_l = gForceProfile_l.getState();
        GForceProfile.BluetoothDeviceStateEx newState_r = gForceProfile_r.getState();

        if (state_l != newState_l) {
            runOnUiThread(new Runnable() {
                public void run() {
                    tv_device_state_l.setText(newState_l.toString());
                }
            });
            state_l = newState_l;
        }

        if (state_r != newState_r) {
            runOnUiThread(new Runnable() {
                public void run() {
                    tv_device_state_r.setText(newState_r.toString());
                }
            });
            state_r = newState_r;
        }

        if (state_l == GForceProfile.BluetoothDeviceStateEx.ready && state_r == GForceProfile.BluetoothDeviceStateEx.ready) {
            runOnUiThread(new Runnable() {
                public void run() {
                    btn_start_notifying.setEnabled(true);
                }
            });
        } else {
            btn_start_notifying.setEnabled(false);
            //LOSE connect, cancel countDownTimer
//            countDownTimerPre.cancel();
//            countDownTimerExplore.cancel();
            Toast.makeText(InteractionActivity.this, "lose connection", Toast.LENGTH_LONG).show();

            // update interaction fail
            if (interaction.updateState(db, Interaction.State.FAILED)) {
                app.setInteractionState(Interaction.State.FAILED);
            } else {
                Toast.makeText(InteractionActivity.this, "Fail to update state failed ", Toast.LENGTH_SHORT).show();
            }
            btn_reConnect.setEnabled(true);
            if(countPre == true){
                countDownTimerPre.cancel();
                countPre = false;
            }
            if(countExplore == true){
                countDownTimerExplore.cancel();
                countPre = false;
            }
            gForceProfile_r.stopDataNotification();
            gForceProfile_l.stopDataNotification();
            //restart the project or reconnect and restart this section.
        }
    }

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }

    // if correct
    /*
     The data stream in is coded in in q15 format,
      so we should divide the long value by 65536.0f to get real value in float format.
     */
    public static float getFloat2(byte[] b) {

        float value = 0;
        long va_l = getLong(b);
        value = va_l / 65536.0f;
        return value;

    }

    public static long getLong(byte[] b) {

        long accum = 0;
        accum = accum | (b[0] & 0xff) << 0;//不加0xffL看起来正常点
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;

        System.out.println(accum);
        return accum;

    }

//    public static long getLong(byte[] b) {
//
//        int accum = (b[0] & 0xff) << 0 | (b[1] & 0xff) | (b[2] & 0xff) << 16 | (b[3] & 0xff) << 24;
//
//        System.out.println(accum);
//        return accum;
//
//    }

    public static int getInt(byte b) {
        int accum = 0;
        accum = accum | b;

        return accum;
    }


    public static ArrayList<Integer> getEMGList(ArrayList<Byte> b) {
        ArrayList<Integer> EMGList = new ArrayList<Integer>();
        for (Byte data : b) {
            EMGList.add(toUnsignedInt(data));
        }
        return EMGList;
    }

    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    // when user leave this page,do???
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
//        handler.removeCallbacks(runnable_data_notify);
        // callback threads
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable);
//        handler.removeCallbacks(runnable_data_notify);
        Log.i(TAG, "I'm on destroying");
//        handler.removeCallbacks(runnable);
//        gForceProfile_l.disconnect();
//        gForceProfile_r.disconnect();
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable, 1000);
        super.onResume();
    }
}