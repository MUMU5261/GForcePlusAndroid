package com.oymotion.gforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
    @BindView(R.id.btn_start)
    Button btn_start_notifying;
    @BindView(R.id.btn_next)
    Button btn_next;

    private TextView textViewQuaternion_l;
    private TextView textViewQuaternion_r;
    private TextView tv_countdown_start;
    private TextView tv_countdown_itr;
    private TextView tv_itr_statement;

    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    Handler handler;// if private or not
    //may need two handler, or use other way to listen state change, to improve the data collection rate
    Runnable runnable;
//    Runnable runnable_data_notify;

    Resources res;
    String[] statementList;

    int itr_type;
    int itr_id;
    int p_id;
    int e_id;
    int clt_id;

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
        textViewQuaternion_l = this.findViewById(R.id.tv_quaternion_l);
        textViewQuaternion_r = this.findViewById(R.id.tv_quaternion_r);
        tv_countdown_start = this.findViewById(R.id.tv_countdown_start);
        tv_countdown_itr = this.findViewById(R.id.tv_countdown_itr);
        tv_itr_statement = this.findViewById(R.id.tv_itr_statement);
        btn_start_notifying.setEnabled(false);
//        btn_next.setEnabled(false);

        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();

        p_id = Participant.getIDFromPreference(this);
        app = (MyApplication) getApplication();
        itr_type = app.getInteractionType();
        e_id = app.getExperimentID();
        clt_id = app.getClothesID();
        Log.i(TAG, "Initial Information: " + "p_id:" + p_id + "e_id:" + e_id + "itr_type:" + itr_type+"clt_id"+clt_id);

        res = getResources();
        statementList = res.getStringArray(R.array.itr_statement);
        tv_itr_statement.setText(statementList[itr_type]);

        interaction = new Interaction(p_id, e_id, clt_id,itr_type);
        itr_id = interaction.insertInteraction(db);
        if(itr_id != -1){
            Log.i(TAG,"insert interaction success");
            Toast.makeText(InteractionActivity.this,"insert interaction success", Toast.LENGTH_LONG).show();
        }else {
            Log.e(TAG,"insert interaction fail");
            Toast.makeText(InteractionActivity.this,"insert interaction fail", Toast.LENGTH_LONG).show();
        }

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

    @OnClick(R.id.btn_update_state)
    public void onStartNotify() {
        try {
//            state_l = gForceProfile_l.getState();
//            state_r = gForceProfile_r.getState();
//            tv_device_state_l.setText(state_l.toString());
//            tv_device_state_r.setText(state_r.toString());
//            handler.removeCallbacks(runnable);
//            handler.postDelayed(runnable, 1000);
            new CountDownTimer(4000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tv_countdown_start.setText("seconds remaining: " + millisUntilFinished / 1000);
                }
                public void onFinish() {
                    tv_countdown_start.setText("start!");
                    onStartClick();
                    this.cancel();
                }
            }.start();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        }

    }

    @OnClick(R.id.btn_start)
    public void onStartClick() {
        if (notifying) {
            btn_start_notifying.setText("Start Data Notification");
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

            btn_start_notifying.setText("Stop Data Notification");
            notifying = true;
//            runnable_data_notify = new Runnable() {
//                @Override
//                public void run() {
//                    onStartClick();
//                }
//            };
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tv_countdown_itr.setText("seconds remaining: " + millisUntilFinished / 1000);
                }
                public void onFinish() {
                    tv_countdown_itr.setText("done!");
                    onStartClick();
                    if(interaction.updateState(db,Interaction.State.FINISHED)){
                        app.setInteractionState(Interaction.State.FINISHED);
                    }else{
                        Toast.makeText(InteractionActivity.this,"Failed Exploration",Toast.LENGTH_LONG).show();
                    }
                    btn_next.setEnabled(true);
                    this.cancel();
                }
            }.start();
//            handler.postDelayed(runnable_data_notify, 5000);
        }
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
//        switch (itr_type) {
//            case Interaction.Type.FREE:
//            case Interaction.Type.SMOOTH:
//            case Interaction.Type.SOFT:
//            case Interaction.Type.WARMTH:
//            case Interaction.Type.THICKNESS:
//        }
        Intent intent;
        switch (itr_type){
            case Interaction.Type.RELAX:
                app.setInteractionState(Interaction.State.START);
                app.setInteractionType(Interaction.Type.FIST);
                intent = new Intent(InteractionActivity.this, InteractionActivity.class);
                break;
            case Interaction.Type.FIST:
                app.setInteractionState(Interaction.State.START);
                app.setInteractionType(Interaction.Type.FREE);
                intent = new Intent(InteractionActivity.this, InteractionActivity.class);
                break;
            case Interaction.Type.FREE:
                app.setInteractionState(Interaction.State.START);
                app.setInteractionType(Interaction.Type.SMOOTH);
                intent = new Intent(InteractionActivity.this, InteractionActivity.class);
                break;
            case Interaction.Type.ENJOYMENT:
                intent = new Intent(InteractionActivity.this, SurveyEnjoymentActivity.class);//WENT TO ENJOYMENT SURVEY PAGE
                break;
            default:
                intent = new Intent(InteractionActivity.this, SurveyActivity.class);
                break;

        }
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
                    values.put("e_id", e_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("w", w);
                    values.put("x", x);
                    values.put("y", y);
                    values.put("z", z);
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
                    values.put("e_id", e_id);
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
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("EMG", null, values));
                    values.clear();


                    Log.i(TAG, "CH0" +getEMGList(CH0).toString());
                    Log.i(TAG, "CH1" +getEMGList(CH1).toString());
                    Log.i(TAG, "CH2" +getEMGList(CH2).toString());
                    Log.i(TAG, "CH3" +getEMGList(CH3).toString());
                    Log.i(TAG, "CH4" +getEMGList(CH4).toString());
                    Log.i(TAG, "CH5" +getEMGList(CH5).toString());
                    Log.i(TAG, "CH6" +getEMGList(CH6).toString());
                    Log.i(TAG, "CH7" +getEMGList(CH7).toString());

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
                            values.put("e_id", e_id);
                            values.put("itr_id", itr_id);
                            values.put("itr_type", itr_type);
                            values.put("hand", hand);
                            values.put("clt_id", clt_id);
                            values.put("pitch", pitch);
                            values.put("roll", roll);
                            values.put("yaw", yaw);
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

                    long gyo_x = getLong(b_gyo_x);
                    long gyo_y = getLong(b_gyo_y);
                    long gyo_z = getLong(b_gyo_z);

                    Log.i("DeviceActivity", " NTF_GYO_DATA:" + " gyo_x:" + gyo_x + " gyo_y:" + gyo_y + " gyo_z:" + gyo_z);
                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("e_id", p_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("x", gyo_x);
                    values.put("y", gyo_y);
                    values.put("z", gyo_z);
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

                    long acc_x = getLong(b_acc_x);
                    long acc_y = getLong(b_acc_y);
                    long acc_z = getLong(b_acc_z);

                    Log.i("DeviceActivity", "NTF_ACC_DATA: " + " acc_x:" + acc_x + " acc_y:" + acc_y + " acc_z:" + acc_z);

                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("e_id", e_id);
                    values.put("itr_id", itr_type);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("x", acc_x);
                    values.put("y", acc_y);
                    values.put("z", acc_z);
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

                    long mag_x = getLong(b_mag_x);
                    long mag_y = getLong(b_mag_y);
                    long mag_z = getLong(b_mag_z);
                    Log.i("DeviceActivity", "NTF_MAG_DATA: " + " mag_x:" + mag_x + " mag_y:" + mag_y + " mag_z:" + mag_z);
                    ContentValues values = new ContentValues();
                    values.put("p_id", p_id);
                    values.put("e_id", e_id);
                    values.put("itr_id", itr_id);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("clt_id", clt_id);
                    values.put("x", mag_x);
                    values.put("y", mag_y);
                    values.put("z", mag_z);
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
            Toast.makeText(InteractionActivity.this, "lose connection", Toast.LENGTH_LONG).show();
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
    public static long getLong(byte[] b) {

        long accum = 0;
        accum = accum | (b[0] & 0xffL) << 0;
        accum = accum | (b[1] & 0xffL) << 8;
        accum = accum | (b[2] & 0xffL) << 16;
        accum = accum | (b[3] & 0xffL) << 24;

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

        return  accum;
    }


    public static ArrayList<Integer> getEMGList( ArrayList<Byte> b) {
        ArrayList<Integer> EMGList = new ArrayList<Integer>();
        for(Byte data: b){
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