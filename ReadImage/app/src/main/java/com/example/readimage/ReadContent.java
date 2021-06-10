package com.example.readimage;


import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;


public class ReadContent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text_view);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;
        StringBuilder sb = null;

        // 例外を受け取る
        try {


            if (cursor != null && cursor.moveToFirst()) {
                String str =  String.format(
                        "MediaStore.Images = %s\n\n", cursor.getCount() );
                sb = new StringBuilder(str);
                do {
                    sb.append("ID: ");
                    sb.append(cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.Media._ID)));
                    sb.append("\n");
                    sb.append("Title: ");
                    sb.append(cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.Media.TITLE)));
                    sb.append("\n");
                    sb.append("Path: ");
                    sb.append(cursor.getString(cursor.getColumnIndex(
                            MediaStore.Images.Media.DATA)));
                    sb.append("\n\n");

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(this,
                    "例外が発生、Permissionを許可していますか？", Toast.LENGTH_SHORT);
            toast.show();

            //MainActivityに戻す
            finish();
        } finally{
            if(cursor != null){
                cursor.close();
            }
        }

        textView.setText(sb);
    }
}