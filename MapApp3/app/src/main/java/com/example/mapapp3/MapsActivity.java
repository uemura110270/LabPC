package com.example.mapapp3;
import java.text.SimpleDateFormat;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {

    private GoogleMap map;
    private LocationClient locationClient;
    private LocationListener locationListener;
    private LocationRequest locationRequest;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 第２引数と第３引数は、以降に記述するコールバックメソッド
        locationClient = new LocationClient(this, connectionCallbacks, onConnectionFailedListener);

    }

    // LocationClient()の第２引数に指定しているコールバックメソッド
    private GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {

        // GooglePlayServiceからの切断時に呼び出される
        @Override
        public void onDisconnected() {
            Toast.makeText(MainActivity.this, "切断されました。", Toast.LENGTH_SHORT).show();
        }

        // GooglePlayServiceへの接続時に呼び出される
        @Override
        public void onConnected(Bundle bundle) {
            Toast.makeText(MainActivity.this, "接続しました。", Toast.LENGTH_SHORT).show();

            if (locationClient.isConnected()) {

                // リスナーの登録
                locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {

                        if (location != null) {

                            TextView textLat = (TextView) findViewById(R.id.textLat);
                            TextView textLng = (TextView) findViewById(R.id.textLng);
                            TextView textTime = (TextView) findViewById(R.id.textTime);

                            double lat = location.getLatitude();
                            double lng = location.getLongitude();

                            textTime.setText("時間：" + sdf.format(location.getTime()));
                            textLat.setText("緯度：" + String.valueOf(lat));
                            textLng.setText("経度：" + String.valueOf(lng));

                            LatLng latLng = new LatLng(lat, lng);
                            map.addMarker(new MarkerOptions().position(latLng));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                            Toast.makeText(MainActivity.this, "値を更新しました。",  Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "値を取得できませんでした。", Toast.LENGTH_SHORT).show();
                        }
                    }

                };

                // LocationRequestの生成と各種処理（定期的に位置情報を取得するため）
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(50000);// LocationRequestの時間間隔をミリ秒単位で指定
                locationClient.requestLocationUpdates(locationRequest, locationListener);

            } else {

                Toast.makeText(MainActivity.this, "接続されていません", Toast.LENGTH_SHORT).show();

            }

        }

    };

    // LocationClient()の第３引数に指定しているコールバックメソッド
    private GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener = new GooglePlayServicesClient.OnConnectionFailedListener() {

        // GooglePlayServiceへの接続に失敗した際に呼び出される
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(MainActivity.this, "接続に失敗しました。", Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapFragment1);
        map = mapFragment.getMap();

        Toast.makeText(MainActivity.this, "接続を開始します。", Toast.LENGTH_SHORT).show();
        locationClient.connect();// GooglePlayServiceへの接続
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationClient.removeLocationUpdates(locationListener);// リスナーの解除
        locationClient.disconnect();// GooglePlayServiceからの切断
        map.clear();
    }

}