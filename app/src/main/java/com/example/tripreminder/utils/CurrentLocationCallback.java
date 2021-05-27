package com.example.tripreminder.utils;

import android.location.Location;

import com.google.android.gms.location.LocationResult;

public interface CurrentLocationCallback {
    void currentLocation(LocationResult locationResult);
}
