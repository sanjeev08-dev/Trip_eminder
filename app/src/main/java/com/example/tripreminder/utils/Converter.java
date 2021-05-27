package com.example.tripreminder.utils;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

public class Converter {

    @TypeConverter
    public Date toListDateJson(String dateString) {
        if (dateString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Date>() {
        }.getType();
        return gson.fromJson(dateString, type);
    }

    @TypeConverter
    public String fromListDateJson(Date date) {
        if (date == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Date>() {
        }.getType();
        return gson.toJson(date, type);
    }
    @TypeConverter
    public LatLng toListLatLngJson(String latLngString) {
        if (latLngString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<LatLng>() {
        }.getType();
        return gson.fromJson(latLngString, type);
    }

    @TypeConverter
    public String fromListLatLngJson(LatLng latLng) {
        if (latLng == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<LatLng>() {
        }.getType();
        return gson.toJson(latLng, type);
    }

}
