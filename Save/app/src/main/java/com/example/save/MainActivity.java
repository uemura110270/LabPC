package com.example.save;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CAMERA = 714;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private final static int RESULT_CAMERA = 1001;
    private Context context;
    private File file;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        Button cameraButton = findViewById(R.id.camera_button);
        Button saveButton = findViewById(R.id.save_button);
        // lambda式
        //Buttonを押すと、画面が遷移する
        cameraButton.setOnClickListener( v -> {
            //Intent Activityを画面を変える
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //遷移先から結果などのデータを受け取りたい場合で
            //onActivityResult(int, int, Intent) で受け取ります(呼び出される)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        });
        saveButton.setOnClickListener(v->{
            setUpWriteExternalStorage();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test","onActivityStart");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("test","onActivityEnd1");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            Log.d("test","onActivityEnd");
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName=timeStamp+".jpg";
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(path, fileName);
        Log.d("log","path: " + String.valueOf(path));
        if (!file.mkdirs()) {
            Log.e("log", "Directory not created");
        }
        setUpWriteExternalStorage();
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
    static final int REQUEST_TAKE_PHOTO = 1;

    private void setUpWriteExternalStorage(){
        Button buttonSave = findViewById(R.id.save_button);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName=timeStamp+".jpg";
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(path, fileName);
        Log.d("log","path: " + String.valueOf(path));
        if (!file.mkdirs()) {
            Log.e("log", "Directory not created");
        }
        // 外部ストレージに画像を保存する
        buttonSave.setOnClickListener( v -> {
            if(isExternalStorageWritable()){
                try(// assetsから画像ファイルを取り出し
                    InputStream inputStream =
                            getResources().getAssets().open(fileName);
                    // 外部ストレージに画像を保存
                    FileOutputStream output =
                            new FileOutputStream(file)) {

                    // バッファーを使って画像を書き出す
                    int DEFAULT_BUFFER_SIZE = 10240 * 4;
                    byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
                    int len;

                    while((len=inputStream.read(buf))!=-1){
                        output.write(buf,0, len);
                    }
                    output.flush();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
}