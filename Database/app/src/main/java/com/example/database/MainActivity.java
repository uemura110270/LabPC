package com.example.database;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editTextKey, editTextValue,editTextValue2;
    private TestOpenHelper helper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextKey = findViewById(R.id.edit_text_key);
        editTextValue = findViewById(R.id.edit_text_value);
        editTextValue2 = findViewById(R.id.edit_text_value2);
        textView = findViewById(R.id.text_view);

        Button insertButton = findViewById(R.id.button_insert);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(helper == null){
                    helper = new TestOpenHelper(getApplicationContext());
                }

                if(db == null){
                    db = helper.getWritableDatabase();
                }

                String key = editTextKey.getText().toString();
                String value = editTextValue.getText().toString();
                String value2 = editTextValue2.getText().toString();
                // 価格は整数を想定
                insertData(db, key, Float.parseFloat(value),Float.parseFloat(value2));
                //insertData(db, key, Integer.parseInt(value));
            }
        });

        Button readButton = findViewById(R.id.button_read);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });
    }


    private void readData(){
        if(helper == null){
            helper = new TestOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
            Log.d("debug","readData");

        }
        Log.d("debug","**********Cursor");

        Cursor cursor = db.query(
                "marker",
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

        Log.d("debug","**********"+sbuilder.toString());
        textView.setText(sbuilder.toString());
    }

    private void insertData(SQLiteDatabase db, String name, float longitude,float latitude){

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        db.insert("marker", null, values);
    }
}