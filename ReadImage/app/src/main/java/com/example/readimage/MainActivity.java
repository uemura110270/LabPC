package com.example.readimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isExternalStorageReadable()){
            checkPermission();
        }
        else{
            Log.e("log","ExternalStorage not Readable");
        }

    }

    // Permissionの確認
    public void checkPermission() {
        // 既に許可している
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
            readContentActivity();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }

    // 許可を求める
    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContentActivity();
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    // Intent でActivityへ移行
    private void readContentActivity() {
        Intent intent = new Intent(getApplication(), ReadContent.class);
        startActivity(intent);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
}