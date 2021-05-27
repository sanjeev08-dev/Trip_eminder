package com.example.tripreminder.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tripreminder.model.Trip;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM trip ORDER BY date DESC")
    LiveData<List<Trip>> getAllTrips();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Trip trip);

    @Delete
    void delete(Trip trip);

    @Update
    void update(Trip trip);
}
