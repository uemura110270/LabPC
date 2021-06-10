package com.example.myapplication

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import java.util.jar.Manifest

class MainActivity<FusedLocationProviderClient> : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1234
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestPermission() {
        val permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

            if (backgroundLocationPermissionApproved) {
                // フォアグラウンドとバックグランドのバーミッションがある
            } else {
                // フォアグラウンドのみOKなので、バックグラウンドの許可を求める
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // 位置情報の権限が無いため、許可を求める
            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
            )
        }
    }
}