package com.oymotion.gforceprofiledemo;

import android.app.LauncherActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Script;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ImagePickerActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageActivity";//shortcut:logt + enter
    @BindView(R.id.btn_next)
    Button btn_next;

    ImageView cover;
    FloatingActionButton fab;
    Button reloadImg;
    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentPhotoPath;
    String currentPhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        ButterKnife.bind(this);
        cover = findViewById(R.id.iv_cloth);
        fab = findViewById(R.id.floatingActionButton);
        reloadImg =findViewById(R.id.btn_take_pho);


        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            System.out.println(e);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();
                        Bitmap imageBitmap = null;
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentPhotoName = createImageName();
                        cover.setImageBitmap(imageBitmap);
                        byte[] b_image = BitmapHelper.getBytes(imageBitmap);
                        addEntry(b_image);
                        BitmapHelper.saveBitmap(currentPhotoName,imageBitmap,ImagePickerActivity.this);

//                        // Use the uri to load the image
//                        cover.setImageURI(uri);
//                        cover.setImageBitmap(imageBitmap);
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });


        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(ImagePickerActivity.this)
                        .crop()
                        .cropOval()
                        .maxResultSize(512, 512, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        }));

            }
        });

        reloadImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String[] columns = {"id","p_id","img_cloth"};
                String[] selectionArgs = {"4"};
                Cursor cursor = db.query("Clothes", columns,"id=?",selectionArgs, null, null,null);
                Log.v(TAG,cursor.toString());
                if(cursor.moveToFirst()){
                    do{
                        String id = cursor.getString(cursor.getColumnIndex("id"));
                        String p_id = cursor.getString(cursor.getColumnIndex("p_id"));
                        byte[] b_img = cursor.getBlob(cursor.getColumnIndex("img_cloth"));
                        Log.v(TAG,"id:" + id);
                        Log.v(TAG,"p_id" + p_id);
                        Bitmap imageBitmap;
                        imageBitmap = BitmapHelper.getImage(b_img);
                        cover.setImageBitmap(imageBitmap);
                    }while(cursor.moveToNext());
                }
                cursor.close();
            }
        });


    }


    @OnClick(R.id.btn_next)
    public void onNextClick(){
        Intent intent = new Intent(ImagePickerActivity.this, InteractionActivity.class);
        startActivity(intent);

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String createImageName() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"+ ".jpg";
        return imageFileName;
    }




    public void addEntry(byte[] image) throws SQLiteException {
        ContentValues values = new ContentValues();
        int p_id = 31010101;
        values.put("p_id", p_id);
        values.put("img_cloth", image);
//        values.put("img_label", z);
//        values.put("c_soft", user_id);
//        values.put("c_warmth", user_id);
//        values.put("c_thickness", user_id);
//        values.put("c_smooth", user_id);
//        values.put("c_enjoyment", section);
        values.put("timestamp", df.format(new Date()));
        System.out.println(db.insert("Clothes", null, values));
        values.clear();

    }


}
