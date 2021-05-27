package com.example.tripreminder.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripreminder.model.Trip;
import com.example.tripreminder.repository.TripRepository;

import java.util.List;

public class TripViewModel extends ViewModel {
    public void insert(Context context, Trip trip) {
        TripRepository.insert(context, trip);
    }

    public LiveData<List<Trip>> getAllData(Context context) {
        return TripRepository.getAllTrip(context);
    }


    public void delete(Context context, Trip trip) {
        TripRepository.delete(context, trip);
    }

    public void update(Context context, Trip trip) {
        TripRepository.update(context, trip);
    }
}
