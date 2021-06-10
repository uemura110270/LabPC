package com.example.camera3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView;
    private Uri cameraUri;
    String currentPhotoPath;
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

        String fileDate = new SimpleDateFormat(
                "ddHHmmss", Locale.US).format(new Date());
        // ファイル名
        String fileName = String.format("CameraIntent_%s.jpg", fileDate);

        File cameraFile = new File(cFolder, fileName);

        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                context.getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);

        Log.d("debug","startActivityForResult()");
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RESULT_CAMERA) {

            if(cameraUri != null && isExternalStorageReadable()){
                imageView.setImageURI(cameraUri);
            }
            else{
                Log.d("debug","cameraUri == null");
            }
        }
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