package com.example.tripreminder.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.tripreminder.utils.Converter;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

@Entity(tableName = "trip")
public class Trip {
    @TypeConverters(Converter.class)
    @NonNull
    private LatLng latLng;
    private String workNote;
    private String address;
    @PrimaryKey
    private int id;
    @TypeConverters(Converter.class)
    private Date date;

    public Trip(int id,LatLng latLng, String workNote, String address, Date date) {
        this.id = id;
        this.latLng = latLng;
        this.workNote = workNote;
        this.address = address;
        this.date = date;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getWorkNote() {
        return workNote;
    }

    public void setWorkNote(String workNote) {
        this.workNote = workNote;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
