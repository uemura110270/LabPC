package com.example.test1;

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
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class CameraActivity extends AppCompatActivity {
    private final static int RESULT_CAMERA = 1001;
    private ImageView imageView,imageView2;
    private Uri cameraUri,uri;
    String fileName;
    String fileDate;
    private Bitmap bmp;
    public TestOpenHelper2 helper;
    public SQLiteDatabase db;
    Uri picpath,collection;
    String storename;
    private static final int RESULT_PICK_IMAGEFILE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Intent intent1 = getIntent();
        storename = intent1.getStringExtra("name");//設定したkeyで取り出す
        imageView = findViewById(R.id.image_view);
        imageView2 = findViewById(R.id.image_view2);
        Button cameraButton = findViewById(R.id.camera_button);
        Button galleryButton = findViewById(R.id.gallery_button);
        Button backButton=findViewById(R.id.back_button);
        cameraButton.setOnClickListener( v -> {
            if(isExternalStorageWritable()){
                cameraIntent();
            }
        });
        galleryButton.setOnClickListener((v->{
            if(isExternalStorageReadable()){
                galleryIntent();
            }
        }));
        backButton.setOnClickListener(v->{
            if(isExternalStorageWritable()){
                //setContentView(R.layout.activity_camera);
                Intent intent = new Intent(getApplication(),MapsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void cameraIntent(){
        Context context = getApplicationContext();
        File cFolder = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        fileDate = new SimpleDateFormat(
                "mmddHHmmss", Locale.US).format(new Date());
        fileName = String.format("CameraIntent_%s.jpg", fileDate);
        File cameraFile = new File(cFolder, fileName);
        cameraUri = FileProvider.getUriForFile(
                CameraActivity.this,
                context.getPackageName() + ".fileprovider",
                cameraFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_CAMERA);
    }
    private void galleryIntent(){
        Intent intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
        intentGallery.setType("*/*");
        startActivityForResult(intentGallery, RESULT_PICK_IMAGEFILE);
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
            savePicture();
        }else if(requestCode==RESULT_PICK_IMAGEFILE){
            if(intent.getData() != null){
                ParcelFileDescriptor pfDescriptor = null;
                try{
                    Uri uri = intent.getData();
                    pfDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    if(pfDescriptor != null){
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        imageView.setImageBitmap(bmp);
                    }
                    SimpleDateFormat timeStamp3 = new SimpleDateFormat("yyyyMMdd");
                    String[] projection = { MediaStore.Images.Media.TITLE,MediaStore.Images.Media.DATE_ADDED};
                    ContentResolver contentResolver2 = getContentResolver();
                    Cursor cursor = contentResolver2.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                    if (cursor != null) {
                        String name = null;
                        String name1 = null;
                        if (cursor.moveToFirst()) {
                            name = cursor.getString(0);
                            name1 = cursor.getString(1);
                            long epoctime= Long.parseLong(name1);
                            String date=timeStamp3.format(new Date(epoctime*1000));
                            if(isExternalStorageWritable()){
                                if(helper == null){
                                    helper = new TestOpenHelper2(getApplicationContext());
                                }
                                if(db == null){
                                    db = helper.getWritableDatabase();
                                }
                                Log.d("テスト","保存");
                                insertData(db, storename,name,date);
                            }
                        }
                        cursor.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try{
                        if(pfDescriptor != null){
                            pfDescriptor.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }
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
        //content://media/external_primary/images/media
        collection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY);

        Uri item = resolver.insert(collection, values);
        picpath= Uri.parse(collection+"/"+fileName);
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
                helper = new TestOpenHelper2(getApplicationContext());
            }
            if(db == null){
                db = helper.getWritableDatabase();
            }
            Log.d("テスト","保存");
            insertData(db, storename,timeStamp,timeStamp2);
            readData();
        }
        //readData();
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
            helper = new TestOpenHelper2(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        Log.d("テスト","camera");
        ///////////////////////////////////////エラー発見
        Cursor cursor = db.query(
                "picture",
                new String[] { "name", "picname","date" },
                null,
                null,
                null,
                null,
                null
        );
        Log.d("テスト","camera");

        cursor.moveToFirst();

        String picturename = null;
        Log.d("テスト","camera");
        for (int i = 0; i < cursor.getCount()-1; i++) {
            picturename = (cursor.getString(1));
            cursor.moveToNext();
            ContentResolver contentResolver2 = getContentResolver();
            Cursor cursor2 =
                    contentResolver2.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,             // UserDictionary.Words.CONTENT_URI, table
                            new String[] { MediaStore.Images.Media.TITLE,MediaStore.Images.Media.DATA,MediaStore.Images.Media.DATE_ADDED},      // The columns to return for each row
                            MediaStore.Images.Media.TITLE+"=?",       // Selection criteria
                            new String[]{picturename},
                            null);      // The sort order for the returned rows
            cursor2.moveToFirst();
            String picpath=null;
            for (int j = 0; j < cursor2.getCount(); j++) {
                picpath=(cursor2.getString(2));
                cursor2.moveToNext();
            }
            cursor2.close();
            Bitmap bmImg = BitmapFactory.decodeFile(picpath);
            imageView2.setImageBitmap(bmImg);
        }
        cursor.close();
    }
    private void insertData(SQLiteDatabase db, String name, String picname, String date){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("picname", String.valueOf(picname));
        values.put("date", date);
        db.insert("picture", null, values);
    }
}