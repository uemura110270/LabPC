package com.example.camera4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.UserDictionary;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView;
    private Uri cameraUri;
    String fileName;
    String fileDate;
    private TextView textView;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        Log.d("debug","cameraUri == null");
        Button cameraButton = findViewById(R.id.camera_button);
        Button readButton = findViewById(R.id.read_button);
        // lambda式
        cameraButton.setOnClickListener( v -> {
            if(isExternalStorageWritable()){
                cameraIntent();
            }
        });
        readButton.setOnClickListener( v -> {
            if(isExternalStorageWritable()){
                cameraIntent();
            }
        });
    }

    private void cameraIntent(){
        Context context = getApplicationContext();
        // 保存先のフォルダー
        File cFolder = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        Log.d("log","path: " + String.valueOf(cFolder));

        fileDate = new SimpleDateFormat(
                "mmddHHmmss", Locale.US).format(new Date());
        // ファイル名
        fileName = String.format("CameraIntent_%s.jpg", fileDate);

        File cameraFile = new File(cFolder, fileName);

        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                context.getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);

        Log.d("debug","startActivityForResult()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("debug","onActivityResult");
        if (requestCode == RESULT_CAMERA) {
            if(cameraUri != null && isExternalStorageReadable()){
                imageView.setImageURI(cameraUri);
                bmp=(Bitmap)intent.getExtras().get("data");
                Log.d("debug","Picture");
            }
            else{
                Log.d("debug", String.valueOf(cameraUri));
                Log.d("debug","cameraUri == null");
            }
        }
        Log.d("debug","CameraEnd");
        savePicture();
    }
    protected void savePicture(){
        Log.d("debug","Save");
        ContentValues values = new ContentValues();
        // コンテンツ クエリの列名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName= timeStamp+".jpg";
        // ファイル名
        //fileName = String.format("CameraIntent_%s.jpg", fileDate);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        // マイムの設定
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // 書込み時にメディア ファイルに排他的にアクセスする
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        Log.d("debug","Save2");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);
        try (OutputStream outstream = getContentResolver().openOutputStream(item)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, outstream);
            imageView.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.clear();
        //　排他的にアクセスの解除
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        resolver.update(item, values, null, null);

    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
}