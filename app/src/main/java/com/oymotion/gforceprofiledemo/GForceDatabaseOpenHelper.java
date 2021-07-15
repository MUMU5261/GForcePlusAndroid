package com.oymotion.gforceprofiledemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class GForceDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "GForceDatabaseOpenHelpe";
    public static final String Create_Participant = "create table Participant ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "gender text NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_Experiment = "create table Experiment ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "phone_id integer NOT NULL,"
            + "armband_id_l integer NOT NULL,"
            + "armband_id_r integer NOT NULL,"
            + "state integer default 0,"  // "0" : setup, "1":doing, "2": finished "3":failed
            + "timestamp text NOT NULL)";

    public static final String Create_Clothes = "create table Clothes ("
            + "id integer primary key autoincrement,"
            + "e_id integer NOT NULL,"
            + "p_id integer NOT NULL,"
            + "img_cloth blob,"
            + "img_label blob,"
            + "c_soft int,"
            + "c_warmth int,"
            + "c_thickness int,"
            + "c_smooth int,"
            + "c_enjoyment int,"
            + "timestamp text NOT NULL)";

    public static final String Create_Interaction = "create table Interaction ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "type integer NOT NULL,"  //0: smoothness, 1:warmth, 2:softness, 3:thickness 4:free
            + "state integer default 0," // "0" : setup, "1":doing, "2": finished "3":failed
            + "timestamp text NOT NULL)";

    public static final String Create_EMG = "create table EMG ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "itr_id integer NOT NULL,"
            + "itr_type integer NOT NULL,"
            + "hand integer NOT NULL," //0: left, 1:right
            + "ch_01 real NOT NULL,"
            + "ch_02 real NOT NULL,"
            + "ch_03 real NOT NULL,"
            + "ch_04 real NOT NULL,"
            + "ch_05 real NOT NULL,"
            + "ch_06 real NOT NULL,"
            + "ch_07 real NOT NULL,"
            + "ch_08 real NOT NULL,"
            + "state integer NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_Quaternion = "create table Quaternion ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "itr_id integer NOT NULL,"
            + "itr_type integer NOT NULL," //0: smoothness, 1:warmth, 2:softness, 3:thickness 4:free
            + "hand integer NOT NULL," //0: left, 1:right
            + "w real NOT NULL,"
            + "x real NOT NULL,"
            + "y real NOT NULL,"
            + "z real NOT NULL,"
            + "state integer NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_ACC = "create table Acceletor ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "itr_id integer NOT NULL,"
            + "itr_type integer NOT NULL," //0: smoothness, 1:warmth, 2:softness, 3:thickness 4:free
            + "hand integer NOT NULL," //0: left, 1:right
            + "x real NOT NULL,"
            + "y real NOT NULL,"
            + "z real NOT NULL,"
            + "state integer NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_Gyro = "create table Gyroscope ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "itr_id integer NOT NULL,"
            + "itr_type integer NOT NULL," //0: smoothness, 1:warmth, 2:softness, 3:thickness 4:free
            + "hand integer NOT NULL," //0: left, 1:right
            + "x real NOT NULL,"
            + "y real NOT NULL,"
            + "z real NOT NULL,"
            + "state integer NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_Mag = "create table Magnetometer ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "itr_id integer NOT NULL,"
            + "itr_type integer NOT NULL," //0: smoothness, 1:warmth, 2:softness, 3:thickness 4:free
            + "hand integer NOT NULL," //0: left, 1:right
            + "x real NOT NULL,"
            + "y real NOT NULL,"
            + "z real NOT NULL,"
            + "state integer NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_Euler_Angle = "create table Euler_Angle ("
            + "id integer primary key autoincrement,"
            + "p_id integer NOT NULL,"
            + "e_id integer NOT NULL,"
            + "itr_id integer NOT NULL,"
            + "itr_type integer NOT NULL," //0: smoothness, 1:warmth, 2:softness, 3:thickness 4:free
            + "hand integer NOT NULL," //0: left, 1:right
            + "pitch real NOT NULL,"
            + "roll real NOT NULL,"
            + "yaw real NOT NULL,"
            + "state integer NOT NULL,"
            + "timestamp text NOT NULL)";

    public static final String Create_Device = "create table Device ("
            + "id integer primary key autoincrement,"
            + "d_id integer NOT NULL,"
            + "name text NOT NULL,"
            + "mac_address text NOT NULL,"
            + "type integer NOT NULL)";


    private Context mContext;
    public GForceDatabaseOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Device);
        db.execSQL(Create_Participant);
        db.execSQL(Create_Experiment);
        db.execSQL(Create_Clothes);
        db.execSQL(Create_Interaction);
        db.execSQL(Create_EMG);
        db.execSQL(Create_Quaternion);
        db.execSQL(Create_ACC);
        db.execSQL(Create_Gyro);
        db.execSQL(Create_Mag);
        db.execSQL(Create_Euler_Angle);
        Toast.makeText(mContext,"create succeed", Toast.LENGTH_SHORT).show();
        Log.i(TAG,"Create database successful");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
