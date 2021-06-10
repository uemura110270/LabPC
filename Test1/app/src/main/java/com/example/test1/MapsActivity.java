package com.example.test1;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    private EditText editTextKey, editTextValue,editTextValue2;
    public TestOpenHelper helper;
    public TestOpenHelper2 helper2;
    public SQLiteDatabase db;
    public SQLiteDatabase db2;
    Button confirmButton,okButton,picButton,viewButton;
    Boolean confirmflag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onCreateの継承
        super.onCreate(savedInstanceState);
        //アクティビティにテキストやボタンといった部品を配置する
        setContentView(R.layout.activity_maps);
        Log.d("テスト","onCreate");
        //getSupportActionBar().setTitle("Map Location Activity");
        //クライアントを作成
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //FragmentManagerの呼び出し
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        okButton = findViewById(R.id.ok_button);
        //onMapReadyを呼び出し(onCreateを実行し終わってから）
        mapFrag.getMapAsync(this);
        editTextKey = findViewById(R.id.edit_text_name);
        confirmButton = findViewById(R.id.confirm_button);
        viewButton = findViewById(R.id.view_button);
        picButton = findViewById(R.id.picbutton);
        Log.d("テスト","onCreateEnd");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("テスト","onPause");
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("テスト","onMapReady");
        mGoogleMap = googleMap;
        //マップのタイプの設定
        //G_NORMAL_MAP 地図
        //G_SATELLITE_MAP 衛星写真
        //G_HYBRID_MAP 地図＋衛星写真
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //LocationRequestの設定
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng longpushLocation) {
                LatLng newlocation = new LatLng(longpushLocation.latitude, longpushLocation.longitude);
                mGoogleMap.addMarker(new MarkerOptions().position(newlocation).title(""+longpushLocation.latitude+" :"+ longpushLocation.longitude));
                okButton.setOnClickListener(v->{
                    if(isExternalStorageWritable()){
                        if(helper == null){
                            helper = new TestOpenHelper(getApplicationContext());
                        }

                        if(db == null){
                            db = helper.getWritableDatabase();
                        }
                        double latitude=longpushLocation.latitude;
                        double longitude=longpushLocation.longitude;
                        String key = editTextKey.getText().toString();
                        String value = String.valueOf(latitude);
                        String value2 = String.valueOf(longitude);
                        // 価格は整数を想定
                        insertData(db, key, Float.parseFloat(value),Float.parseFloat(value2));
                        Log.d("テスト","データベース保存");
                        editTextKey.getText().clear();
                    }
                });
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Button deleteButton = findViewById(R.id.delete_button);
                Log.d("テスト","選択");
                deleteButton.setOnClickListener(v->{
                    if(isExternalStorageWritable()){
                        marker.remove();
                        String name=marker.getTitle();
                        Cursor cursor = db.query(
                                "marker2",
                                null,
                                "name=?",
                                new String[]{name},
                                null,
                                null,
                                null
                        );
                        cursor.moveToFirst();
                        if(cursor.getCount()!=0){
                            Log.d("テスト","delete");
                            db.delete("marker2","name=?", new String[]{name});
                        }
                    }
                    Log.d("テスト","削除");
                });
                okButton.setOnClickListener(v->{
                    if(isExternalStorageWritable()){

                        String key = editTextKey.getText().toString();
                        // 価格は整数を想定
                        marker.setTitle(key);
                        editTextKey.getText().clear();
                    }
                });
                picButton.setOnClickListener(v->{
                    if(isExternalStorageWritable()){
                        //setContentView(R.layout.activity_camera);
                        Intent intent = new Intent(getApplication(),CameraActivity.class);
                        startActivity(intent);
                    }
                });
                viewButton.setOnClickListener(v->{
                    if(isExternalStorageWritable()){
                        String name=marker.getTitle();
                        Log.d("テスト",name);
                        readData2(name);
                    }
                });
                return false;
            }
        });
        confirmButton.setOnClickListener(v->{
            if(!confirmflag){
                if(isExternalStorageWritable()){
                    readData();
                    confirmflag=true;
                }
            }else{
                mGoogleMap.clear();
                confirmflag=false;
            }

        });
    }
    //デバイスの場所が変更されたとき、または決定できなくなったときに通知を受信するために使用されます
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        //requestLocationUpdatesで呼び出される？
        public void onLocationResult(LocationResult locationResult) {
            Log.d("テスト","onLocationResult");
            //locationResultに位置座標等が入っている
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //マーカーの設定
                MarkerOptions markerOptions = new MarkerOptions();
                //マーカーの位置座標
                markerOptions.position(latLng);
                //タイトル
                markerOptions.title("Current Position");
                //マーカー画像のセット
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                //マーカーの追加
                //mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        }
    };
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        Log.d("テスト","checkLocationPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("テスト","onRequestPermissionResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
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
    public void readData(){
        if(helper == null){
            helper = new TestOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        Cursor cursor = db.query(
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
            LatLng latLng = new LatLng(cursor.getFloat(1), cursor.getFloat(2));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(cursor.getString(0));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mGoogleMap.addMarker(markerOptions);
            cursor.moveToNext();
            Log.d("テスト","marker追加");
        }
        // 忘れずに！
        cursor.close();
        Log.d("debug","**********"+sbuilder.toString());
    }
    public void readData2(String name){
        if(helper2 == null){
            helper2 = new TestOpenHelper2(getApplicationContext());
        }
        if(db == null){
            db = helper2.getReadableDatabase();
            Log.d("debug","readData");
        }
        name="restaurant";
        Log.d("debug","**********Cursor");
        Cursor cursor = db.query(
                "picture",
                null,
                "name=?",
                new String[]{name},
                null,
                null,
                null
        );
        Log.d("テスト","ほげ");

        cursor.moveToFirst();
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            Log.d("テスト",cursor.getString(0));
        }
        // 忘れずに！
        cursor.close();
        Log.d("debug","**********"+sbuilder.toString());
    }
    private void insertData(SQLiteDatabase db, String name, float longitude, float latitude){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        db.insert("marker2", null, values);
    }

}