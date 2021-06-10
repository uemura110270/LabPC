package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class SubActivity extends AppCompatActivity {
    Button confirmButton;
    public MapsActivity mapsActivity;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);
        confirmButton = findViewById(R.id.confirm_button);
        textView = findViewById(R.id.text_view);
        confirmButton.setOnClickListener(v->{
            if(isExternalStorageWritable()){
                mapsActivity.readData();
            }
            Log.d("テスト","削除");
        });
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
    public void readData(){
        if(mapsActivity.helper == null){
            mapsActivity.helper = new TestOpenHelper(getApplicationContext());
        }

        if(mapsActivity.db == null){
            mapsActivity.db = mapsActivity.helper.getReadableDatabase();
            Log.d("debug","readData");

        }
        Log.d("debug","**********Cursor");

        Cursor cursor = mapsActivity.db.query(
                "marker2",
                new String[] { "name", "longitude","latitude" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            sbuilder.append(cursor.getString(0));
            sbuilder.append(": ");
            sbuilder.append(cursor.getFloat(1));
            sbuilder.append(": ");
            sbuilder.append(cursor.getFloat(2));
            sbuilder.append("\n");
            cursor.moveToNext();

        }

        // 忘れずに！
        cursor.close();

        Log.d("テスト","**********"+sbuilder.toString());
        textView.setText(sbuilder.toString());
    }

}