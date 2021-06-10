package com.example.mapapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
}

public abstract class BaseFragment extends Fragment
        implements MyLocationManager.OnLocationResultListener {
    private MyLocationManager locationManager;

    @Override
    public void onResume() {
        super.onResume();

        locationManager = new MyLocationManager(getContext(), this);
        locationManager.startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.stopLocationUpdates();
        }
    }
}

public class MyLocationManager extends LocationCallback {
    private static final int LOCATION_REQUEST_CODE = 1;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private OnLocationResultListener mListener;

    public interface OnLocationResultListener {
        void onLocationResult(LocationResult locationResult);
    }


    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        mListener.onLocationResult(locationResult);
    }

    public void startLocationUpdates() {
        // パーミッションの確認
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Logger.d("Permission required.");
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, LOCATION_REQUEST_CODE);

            return;
        }

        // 端末の位置情報サービスが無効になっている場合、設定画面を表示して有効化を促す
        if (!isGPSEnabled()) {
            showLocationSettingDialog();
            return;
        }

        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(request, this,null);
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(this);
    }

    private Boolean isGPSEnabled() {
        android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

}