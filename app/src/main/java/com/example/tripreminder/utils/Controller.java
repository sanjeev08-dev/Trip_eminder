package com.example.tripreminder.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.tripreminder.activity.OnBoardActivity;
import com.example.tripreminder.model.Trip;

import java.util.List;

public class Controller extends Application {
    public static final String RECEIVER_FILTER = "ReceiverFilter";
    public static final String LAT = "lat";
    public static final String LAN = "lan";
    private Context context;

    public Controller(Context context) {
        this.context = context;
    }

    public boolean getIsBatteryOptimizationDisable(){
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }else {
            return true;
        }
    }

    public boolean getIsGpsEnabled(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean getIsNetworkEnabled(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean is_HAVE_ACCESS_FINE_LOCATION_PERMISSION(){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean is_HAVE_ACCESS_BACKGROUND_LOCATION_PERMISSION(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }else {
            return true;
        }
    }
}
