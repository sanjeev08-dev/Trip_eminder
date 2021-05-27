package com.example.tripreminder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tripreminder.R;
import com.example.tripreminder.fragments.PermissionsFragment;
import com.example.tripreminder.fragments.UtilsSettingsFragment;
import com.example.tripreminder.utils.Controller;

import org.jetbrains.annotations.NotNull;

public class OnBoardActivity extends AppCompatActivity {

    private static final String TAG = "OnBoardActivity";
    private static final int REQUEST_CODE_BATTERY_OPTIMIZATION = 201;
    private static final int REQUEST_UPDATE_GPS_SETTINGS_MANUALLY = 202;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 203;
    private static final int ACCESS_BACKGROUND_LOCATION_REQUEST_CODE = 204;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        controller = new Controller(this);

        Log.d(TAG, "onCreate: GPS : " + controller.getIsGpsEnabled());
        Log.d(TAG, "onCreate: Network : " + controller.getIsNetworkEnabled());
        Log.d(TAG, "onCreate: Battery : " + controller.getIsBatteryOptimizationDisable());

        checkUtils();

    }

    public void checkUtils() {
        if (!controller.is_HAVE_ACCESS_FINE_LOCATION_PERMISSION() && !controller.is_HAVE_ACCESS_BACKGROUND_LOCATION_PERMISSION()){
            getSupportFragmentManager().beginTransaction().replace(R.id.onBoardFragment, new PermissionsFragment()).commit();
        }else if (!(controller.getIsGpsEnabled() && controller.getIsBatteryOptimizationDisable())){
            getSupportFragmentManager().beginTransaction().replace(R.id.onBoardFragment, new UtilsSettingsFragment()).commit();
        }else {
            startActivity(new Intent(OnBoardActivity.this,MapsActivity.class));
            finish();
        }
    }

    public void disableBatteryOptimization() {
        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_BATTERY_OPTIMIZATION:{
                if (resultCode == RESULT_OK){
                    checkUtils();
                    break;
                }
            }
            case REQUEST_UPDATE_GPS_SETTINGS_MANUALLY:{
                if (resultCode == RESULT_OK){
                    checkUtils();
                    break;
                }
            }
        }
    }

    public void enableGoogleAccuracyLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_UPDATE_GPS_SETTINGS_MANUALLY);
    }

    public void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ACCESS_FINE_LOCATION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkUtils();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            case ACCESS_BACKGROUND_LOCATION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkUtils();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void requestCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(OnBoardActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ACCESS_BACKGROUND_LOCATION_REQUEST_CODE);
        }
    }
}