package com.example.mapapp4;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.PointOfInterest;


import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener{
    private FusedLocationProviderClient fusedLocationClient;
    private MapsActivity mapsActivity;
    private GoogleMap mMap;
    private LocationResult result;
    private MyLocationCallback myLocationCallback = new MyLocationCallback();
    private LocationCallback locationCallback;
    ContentResolver cr = getContentResolver();
    Marker mCurrLocationMarker;
    Location mLastLocation;
    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFrag;
    String[] projection = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("デバッグ", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // 位置情報取得開始
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        locationCallback=new LocationCallback();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("デバッグ", "onMapReady");
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnPoiClickListener(this);
        Log.d("デバッグ","Click1");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                fusedLocationClient.requestLocationUpdates(mLocationRequest, myLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
            }
        } else {
            fusedLocationClient.requestLocationUpdates(mLocationRequest, myLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }

    }
    @Override
    public void onPoiClick(PointOfInterest
                                       poi) {
        Toast.makeText(this, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        MarkerOptions markerOptions = new MarkerOptions();
        //マーカーの位置座標
        markerOptions.position(poi.latLng);
        //タイトル
        markerOptions.title(poi.name);
        //マーカー画像のセット
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //マーカーの追加
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        Log.d("デバッグ","Click2");
    }
    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d("デバッグ","onLocationResult");
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                //マーカーの位置座標
                markerOptions.position(latLng);
                //タイトル
                markerOptions.title("Current Position");
                //マーカー画像のセット
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                //マーカーの追加
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            }
        };
    }
}