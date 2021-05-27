package com.example.tripreminder.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripreminder.R;
import com.example.tripreminder.geoFenceHelper.GeofenceHelper;
import com.example.tripreminder.model.Trip;
import com.example.tripreminder.services.LocationService;
import com.example.tripreminder.utils.Controller;
import com.example.tripreminder.viewModel.TripViewModel;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_UPDATE_GPS_SETTINGS_MANUALLY = 108;
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private TripViewModel viewModel;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 106;
    private final float RADIUS = 500;
    private GeofenceHelper geofenceHelper;
    private Intent intent;
    private final int ACCESS_BACKGROUND_LOCATION_REQUEST_CODE = 107;
    private static TextToSpeech textToSpeech;

    public static void speak(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        Places.initialize(getApplicationContext(), "AIzaSyCYgXDnaCYklP13v-heW6bsRtDzAIFAbiQ");
        viewModel = new ViewModelProvider(this).get(TripViewModel.class);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });
        findViewById(R.id.openReminderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AddReminderActivity.class));
            }
        });

        intent = new Intent(getApplicationContext(), LocationService.class);
        startService(intent);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        viewModel.getAllData(this).observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                mMap.clear();
                if (Build.VERSION.SDK_INT >= 29) {
                    //we need background permission
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        addedTrips(trips);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ACCESS_BACKGROUND_LOCATION_REQUEST_CODE);
                        } else {
                            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ACCESS_BACKGROUND_LOCATION_REQUEST_CODE);
                        }
                    }
                } else {
                    addedTrips(trips);
                }
            }
        });
    }

    private void addedTrips(List<Trip> trips) {
        if (!trips.isEmpty()) {
            List<Trip> todayTrips = new ArrayList<>();
            for (Trip trip : trips) {
                addMarker(trip);
                addCircle(trip);
                if (trip.getDate() == null) {
                    todayTrips.add(trip);
                } else {
                    if (new SimpleDateFormat("MMM dd , yyyy").format(trip.getDate()).equalsIgnoreCase(new SimpleDateFormat("MMM dd , yyyy").format(Calendar.getInstance().getTime()))) {
                        todayTrips.add(trip);
                    }

                }
            }

            if (!todayTrips.isEmpty())
                addGeofence(todayTrips);
        } else {
            Toast.makeText(this, "No Trip Reminder Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void addGeofence(List<Trip> trips) {
        List<LatLng> latLngList = new ArrayList<>();
        for (Trip trip : trips) {
            latLngList.add(trip.getLatLng());
        }
        List<Geofence> geofenceList = geofenceHelper.getGeofence("GEOFENCING_ID", latLngList, RADIUS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofenceList);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MapsActivity.this, "Geofence is added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception exception) {
                Toast.makeText(MapsActivity.this, "Turn of Google Accuracy Location", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_UPDATE_GPS_SETTINGS_MANUALLY);
            }
        });
    }

    private void addCircle(Trip trip) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(trip.getLatLng())
                .radius(RADIUS)
                .strokeColor(Color.argb(255, 0, 255, 0))
                .fillColor(Color.argb(64, 0, 255, 0))
                .strokeWidth(4);

        mMap.addCircle(circleOptions);
    }

    private void addMarker(Trip trip) {
        MarkerOptions markerOptions = new MarkerOptions().position(trip.getLatLng()).title(trip.getWorkNote());
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;

        enableUserLocation();
    }


    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for display why the permission is needed and then ask for permmission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have permission
            }
        }
        if (requestCode == ACCESS_BACKGROUND_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can add geofences", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Need Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == REQUEST_UPDATE_GPS_SETTINGS_MANUALLY && resultCode == RESULT_OK) {
            Toast.makeText(this, "GeoFence Permission added 1", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            double latitude = Double.valueOf(intent.getStringExtra(Controller.LAT));
            double longitude = Double.valueOf(intent.getStringExtra(Controller.LAN));

            LatLng latLng = new LatLng(latitude, longitude);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        }
    };
}