package com.example.camerasave;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);
        imageView = findViewById(R.id.image_view);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image);

        Button button = findViewById(R.id.button);
        button.setOnClickListener( v -> {
            if(isExternalStorageWritable()) {
                savePicture();

            }
        });
    }
    protected void savePicture(){
        ContentValues values = new ContentValues();
        // コンテンツ クエリの列名
        // ファイル名
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "SampeImage.jpg");
        // マイムの設定
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // 書込み時にメディア ファイルに排他的にアクセスする
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);

        try (OutputStream outstream = getContentResolver().openOutputStream(item)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, outstream);
            textView.setText(R.string.saved);
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
}