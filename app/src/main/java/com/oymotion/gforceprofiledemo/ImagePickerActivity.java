package com.oymotion.gforceprofiledemo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.drjacky.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ImagePickerActivity extends AppCompatActivity {
    private static final String TAG = "ImagePickerActivity";
    int flag; // 0: take clothes image, 1: label image

    @BindView(R.id.btn_next)
    Button btn_next;
    TextView tv_title;
    ImageView cover;
    ImageView example_image;
//    @BindView(R.id.tv_clt_No)
    TextView tv_clt_No;
//    FloatingActionButton fab;
    Button fab;
    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentPhotoPath;
    String currentPhotoName;

    //information stored for image
    Clothes clothes;
    int p_id;
    int e_id;
    int clt_id = -1;
    int clt_count = -1;
    byte[] b_image;
    Bitmap imageBitmap = null;
    MyApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        ButterKnife.bind(this);
        this.setTitle("Taking Images");
        cover = findViewById(R.id.iv_cloth);
        example_image =  findViewById(R.id.iv_example);
        fab = findViewById(R.id.btn_take_pho);
        tv_title = findViewById(R.id.tv_photo_take);
        tv_clt_No = findViewById(R.id.tv_clt_No);
        tv_title.setText(Html.fromHtml("Take a photo of the <font color='#EE0000'><b>CLOTHES</b></font> (see the example below).", Typeface.BOLD));
//        cover.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.placeholder_image));
        app = (MyApplication) getApplication();
        flag = 0;
        b_image = null;
        btn_next.setEnabled(false);
        p_id = Participant.getIDFromPreference(this);
//        Intent intent = getIntent();
//        if(intent != null){
//            e_id = intent.getIntExtra("e_id",-1);
//        }

        app = (MyApplication) getApplication();
        e_id = app.getExperimentID();
        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        clt_count = app.getClothesCount();
        tv_clt_No.setText(String.valueOf(clt_count));

        cover.setImageResource(R.mipmap.placeholder_image);
        example_image.setImageResource(R.mipmap.example_clothes);


        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cover.setImageBitmap(imageBitmap);
                        b_image = BitmapHelper.getBytes(imageBitmap);
                        btn_next.setEnabled(true);

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
                fab.setText("Retake");

            }
        });

        // test restore data
//        reloadImg.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                String[] columns = {"id","p_id","img_cloth"};
//                String[] selectionArgs = {"4"};
//                Cursor cursor = db.query("Clothes", columns,"id=?",selectionArgs, null, null,null);
//                Log.v(TAG,cursor.toString());
//                if(cursor.moveToFirst()){
//                    do{
//                        String id = cursor.getString(cursor.getColumnIndex("id"));
//                        String p_id = cursor.getString(cursor.getColumnIndex("p_id"));
//                        byte[] b_img = cursor.getBlob(cursor.getColumnIndex("img_cloth"));
//                        Log.v(TAG,"id:" + id);
//                        Log.v(TAG,"p_id" + p_id);
//                        Bitmap imageBitmap;
//                        imageBitmap = BitmapHelper.getImage(b_img);
//                        cover.setImageBitmap(imageBitmap);
//                    }while(cursor.moveToNext());
//                }
//                cursor.close();
//            }
//        });
    }


    @OnClick(R.id.btn_next)
    public void onNextClick(){
        if(flag == 0) {
            // create an instance of clothes and store image of clothes
            Log.i(TAG, "image of clothes");

            clothes = new Clothes(-1,e_id, p_id, Clothes.State.START);
            clt_id = clothes.insertClothes(db);
            Log.i(TAG, "clt_id" + clt_id);
            currentPhotoName = createImageName();
            BitmapHelper.saveBitmap(currentPhotoName,imageBitmap,ImagePickerActivity.this);
            example_image.setImageResource(R.mipmap.example_label);
            setToLabelPicker();
            cover.setImageResource(R.mipmap.placeholder_image);
            fab.setText("Take photo");
        }else if (flag == 1) {
            // insert the image of the label of the clothes
            Log.i(TAG, "image of label");
            clothes.addLabelImg(db, b_image);
            currentPhotoName = createImageName();
            BitmapHelper.saveBitmap(currentPhotoName,imageBitmap,ImagePickerActivity.this);
            Intent intent = new Intent(ImagePickerActivity.this, InteractionActivity.class);
//            Intent intent = new Intent(ImagePickerActivity.this, EndActivity.class);

            app.setClothesID(clt_id);
            app.setClothesState(Clothes.State.START);
            app.setInteractionType(Interaction.Type.FREE);
            startActivity(intent);
            //pass clt_id
        }

    }

    private void setToLabelPicker(){
        flag = 1;
        imageBitmap = null;
        b_image = null;
        cover.setImageBitmap(null);
        tv_title.setText(Html.fromHtml("Take a photo of the <font color='#EE0000'><b>COMPOSITION LABEL</b></font> on the clothes " +
                "(see the example below).", Typeface.BOLD));
        btn_next.setEnabled(false);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = null;

        if(flag == 0) {
            imageFileName = "JPEG_" +"c"+clt_id+"_clothes_"+timeStamp + "_";
        }else if (flag == 1) {
            imageFileName = "JPEG_" +"c"+clt_id+"_label_"+timeStamp + "_";
        }
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
        String imageFileName = null;
        if(flag == 0) {
            imageFileName = "JPEG_" +"c"+clt_id+"_clothes_"+timeStamp + "_"+ ".jpg";
        }else if (flag == 1) {
            imageFileName = "JPEG_" +"c"+clt_id+"_label_"+timeStamp + "_"+ ".jpg";
        }
        return imageFileName;
    }



}
