package com.example.tripreminder.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tripreminder.dao.TripDao;
import com.example.tripreminder.model.Trip;

@androidx.room.Database(entities = Trip.class,version = 1)
public abstract class Database extends RoomDatabase {

    public abstract TripDao dataDao();

    private static final String DATABASE_NAME = "TripDatabase";

    private static Database database = null;

    public static Database getDatabase(Context context){
        if (database == null){
            synchronized (Database.class){
                if (database == null){
                    database = Room.databaseBuilder(context,
                            Database.class, DATABASE_NAME).build();
                }
            }
        }
        return database;
    }
}
