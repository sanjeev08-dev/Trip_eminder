package com.example.tripreminder.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.tripreminder.database.Database;
import com.example.tripreminder.model.Trip;
import com.google.gson.Gson;

import java.util.List;

public class TripRepository {
    private static final String TAG = "TripRepository";
    private static Database database = null;

    static Database initializeDB(Context context){
        return Database.getDatabase(context);
    }

    public static void insert(Context context, Trip trip){
        database = initializeDB(context);
        database.dataDao().insert(trip);
    }

    public static LiveData<List<Trip>> getAllTrip(Context context){
        database = initializeDB(context);
        return database.dataDao().getAllTrips();
    }

    public static void delete(Context context, Trip trip){
        database = initializeDB(context);
        database.dataDao().delete(trip);
    }

    public static void update(Context context, Trip trip){
        Log.d(TAG, "update: "+new Gson().toJson(trip.getId()));
        database = initializeDB(context);
        database.dataDao().update(trip);
    }
}
