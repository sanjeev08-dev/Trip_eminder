package com.example.tripreminder.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripreminder.R;
import com.example.tripreminder.adapter.TripAdapter;
import com.example.tripreminder.model.Trip;
import com.example.tripreminder.utils.MyDialog;
import com.example.tripreminder.utils.TripCallback;
import com.example.tripreminder.utils.TripEditCallback;
import com.example.tripreminder.viewModel.TripViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AddReminderActivity extends AppCompatActivity {

    private static final String TAG = "AddReminderActivity";
    private RecyclerView toDoListRV;
    private LinearLayout emptyListLayout;
    private TextView addressText;
    private EditText workText;
    private TripAdapter adapter;
    private TripViewModel viewModel;
    private TripCallback callback;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    Double destinationLatitude, destinationLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        viewModel = new ViewModelProvider(this).get(TripViewModel.class);

        toDoListRV = findViewById(R.id.toDoListRV);
        emptyListLayout = findViewById(R.id.emptyListLayout);
        adapter = new TripAdapter(new ArrayList<>(), viewModel, this, new TripEditCallback() {
            @Override
            public void edit(Trip trip) {
                showTripPopUp(trip);
            }
        });
        toDoListRV.setLayoutManager(new LinearLayoutManager(this));
        toDoListRV.setHasFixedSize(true);
        toDoListRV.setAdapter(adapter);

        viewModel.getAllData(this).observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                if (trips.isEmpty()) {
                    emptyListLayout.setVisibility(View.VISIBLE);
                    toDoListRV.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "tripList: " + new Gson().toJson(trips));
                    emptyListLayout.setVisibility(View.GONE);
                    toDoListRV.setVisibility(View.VISIBLE);
                    adapter.refreshTrip(trips);
                }
            }
        });
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTripPopUp(null);
            }
        });
    }

    private void showTripPopUp(Trip trip) {
        MyDialog dialog = new MyDialog(this);
        dialog.showDialog();
        if (trip != null) {
            dialog.getAddressText().setText(trip.getAddress());
            dialog.getWorkText().setText(trip.getWorkNote());
            dialog.setTrip(trip);
            if (trip.getDate() != null) {
                dialog.setResult(trip.getDate());
                DateFormat simple = new SimpleDateFormat("MMM dd , yyyy");

                dialog.getChooseDateText().setText(simple.format(trip.getDate()));
            }
//            dialog.getTimeText().updateDate(Integer.parseInt(new SimpleDateFormat("yyyy").format(trip.getDate())),Integer.parseInt(new SimpleDateFormat("MM").format(trip.getDate())),Integer.parseInt(new SimpleDateFormat("DD").format(trip.getDate())));
        }
        addressText = dialog.getAddressText();
        workText = dialog.getWorkText();
        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressPicker(new TripCallback() {
                    @Override
                    public void add(Trip trip1) {
                        addressText.setText(trip1.getAddress());
                        destinationLatitude = trip1.getLatLng().latitude;
                        destinationLongitude = trip1.getLatLng().longitude;
                    }
                });
            }
        });

        dialog.getDone_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddReminderActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                } else if (workText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddReminderActivity.this, "Please Enter work", Toast.LENGTH_SHORT).show();
                } else {
                    if (trip == null) {
                        //add new
                        int id = randomNumber();
                        LatLng latLng = new LatLng(destinationLatitude, destinationLongitude);
                        Trip trip1 = new Trip(id, latLng, workText.getText().toString().trim(), addressText.getText().toString().trim(), dialog.getResult());
                        AsyncTask.execute(() -> viewModel.insert(AddReminderActivity.this, trip1));
                    } else {
                        LatLng latLng;
                        if (destinationLatitude != null) {
                            latLng = new LatLng(destinationLatitude, destinationLongitude);
                        } else {
                            latLng = trip.getLatLng();
                        }
                        Trip trip1 = new Trip(trip.getId(), latLng, workText.getText().toString().trim(), addressText.getText().toString().trim(), dialog.getResult());
                        AsyncTask.execute(() -> viewModel.update(AddReminderActivity.this, trip1));
                    }
                    dialog.hideDialog();
                }
            }
        });

        dialog.getCancel_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hideDialog();
            }
        });

    }

    public void showAddressPicker(TripCallback callback) {
        this.callback = callback;
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(AddReminderActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private int randomNumber() {
        return new Random().nextInt(10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            setAddress(place);
        }
    }

    public void setAddress(Place place) {
        Trip trip = new Trip(0, place.getLatLng(), "", place.getAddress(), null);
        callback.add(trip);
    }

}