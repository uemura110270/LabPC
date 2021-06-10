package com.example.mapcamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView,imageView2;
    private Uri cameraUri,uri;
    String fileName;
    String fileDate;
    private TextView textView;
    private Bitmap bmp;
    public TestOpenHelper helper;
    public SQLiteDatabase db;
    Uri picpath,collection;
    Bitmap bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        imageView2 = findViewById(R.id.image_view2);
        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener( v -> {
            if(isExternalStorageWritable()){
                cameraIntent();
            }
        });
    }
    private void cameraIntent(){
        Context context = getApplicationContext();
        File cFolder = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        Log.d("テスト","path: " + String.valueOf(cFolder));
        fileDate = new SimpleDateFormat(
                "mmddHHmmss", Locale.US).format(new Date());
        fileName = String.format("CameraIntent_%s.jpg", fileDate);
        File cameraFile = new File(cFolder, fileName);
        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                context.getPackageName() + ".fileprovider",
                cameraFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_CAMERA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RESULT_CAMERA) {
            if(cameraUri != null && isExternalStorageReadable()){
                imageView.setImageURI(cameraUri);
                bmp=(Bitmap)intent.getExtras().get("data");
            }
            else{
                Log.d("テスト","cameraUri == null");
            }
        }
        savePicture();
    }
    protected void savePicture(){
        ContentValues values = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp2 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName= timeStamp+".jpg";
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        ContentResolver resolver = getApplicationContext().getContentResolver();
        collection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);
        picpath= Uri.parse(collection+"/"+fileName);
        Log.d("テスト5", String.valueOf(picpath));
        try (OutputStream outstream = getContentResolver().openOutputStream(item)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, outstream);
            imageView.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.clear();
        //　排他的にアクセスの解除
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        if(isExternalStorageWritable()){
            if(helper == null){
                helper = new TestOpenHelper(getApplicationContext());
            }
            if(db == null){
                db = helper.getWritableDatabase();
            }
            insertData(db, "restaurant",timeStamp,timeStamp2);
        }
        readData();
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

    public void readData() {
        if(helper == null){
            helper = new TestOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        Log.d("debug","**********Cursor");
        SimpleDateFormat timeStamp3 = new SimpleDateFormat("yyyyMMdd");
        Cursor cursor = db.query(
                "picture",
                new String[] { "name", "picname","date" },
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        String picturename = null;
        String[] picnum=new String[cursor.getCount()];
        //レストランのデータベースから写真タイトルを取り出し、
        for (int i = 0; i < cursor.getCount()-1; i++) {
            picturename = (cursor.getString(1));
            String date = (cursor.getString(2));
            Log.d("テスト7",date);
            cursor.moveToNext();
            ContentResolver contentResolver2 = getContentResolver();
            Cursor cursor2 =
                    contentResolver2.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,             // UserDictionary.Words.CONTENT_URI, table
                            new String[] { MediaStore.Images.Media.TITLE,MediaStore.Images.Media.DATE_ADDED},      // The columns to return for each row
                            MediaStore.Images.Media.TITLE+"=?",       // Selection criteria
                            new String[]{picturename},
                            null);      // The sort order for the returned rows
            cursor2.moveToFirst();
            String picpath=null;
            Log.d("テスト3", "cursorcount"+String.valueOf(cursor2.getCount()));
            for (int j = 0; j < cursor2.getCount(); j++) {

                picpath=(cursor2.getString(1));
                long epoctime= Long.parseLong(picpath);

                timeStamp3.format(new Date(epoctime));
                Log.d("テスト4","epoc:"+ (epoctime));
                Log.d("テスト4","date:"+ (timeStamp3.format(new Date(epoctime*1000))));
                cursor2.moveToNext();
            }
            cursor2.close();
            Bitmap bmImg = BitmapFactory.decodeFile(picpath);
            imageView2.setImageBitmap(bmImg);
        }
        cursor.close();
        //Log.d("テスト2", String.valueOf(picnum.length));

    }
    private void insertData(SQLiteDatabase db, String name, String picname, String date){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("picname", String.valueOf(picname));
        values.put("date", date);
        db.insert("picture", null, values);
    }

}