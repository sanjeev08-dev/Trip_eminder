package com.example.tripreminder.viewModel;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapsActivityViewModel extends ViewModel {
    public List<LatLng> latLngList;

    public List<LatLng> getLatLngList() {
        if (latLngList != null)
            return latLngList;
        else
            return new ArrayList<>();
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }
}
