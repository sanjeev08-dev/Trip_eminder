package com.example.tripreminder.utils;

import com.example.tripreminder.model.Trip;

import java.util.List;

public interface TripListCallback {
    void tripList(List<Trip> trips);
}
