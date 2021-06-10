package com.example.camera2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);

        Button cameraButton = findViewById(R.id.camera_button);
        // lambda式
        cameraButton.setOnClickListener( v -> {
            Log.d("test","onCreate");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Log.d("test","Caputure0");
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                Log.d("test","Caputure1");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d("test","onActivity");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("test","onActivity");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


}