package com.example.tripreminder.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripreminder.R;
import com.example.tripreminder.model.Trip;
import com.example.tripreminder.utils.TripEditCallback;
import com.example.tripreminder.viewModel.TripViewModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private TripViewModel tripViewModel;
    private Context context;
    private TripEditCallback callback;

    public TripAdapter(List<Trip> trips, TripViewModel tripViewModel, Context context, TripEditCallback callback) {
        this.trips = trips;
        this.tripViewModel = tripViewModel;
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TripViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.setData(trips.get(position), tripViewModel, context, callback);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {

        private TextView workText, addressText, dateText;
        private ImageView deleteButton, editButton;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            workText = itemView.findViewById(R.id.workText);
            addressText = itemView.findViewById(R.id.addressText);
            dateText = itemView.findViewById(R.id.dateText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }

        public void setData(Trip trip, TripViewModel tripViewModel, Context context, TripEditCallback callback) {
            workText.setText(trip.getWorkNote());
            addressText.setText(trip.getAddress());
            if (trip.getDate() != null){
                dateText.setText(new SimpleDateFormat("MMM dd , yyyy").format(trip.getDate()));
                dateText.setVisibility(View.VISIBLE);
            }
            else
                dateText.setVisibility(View.GONE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            tripViewModel.delete(context, trip);
                        }
                    });
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.edit(trip);
                }
            });
        }
    }

    public void refreshTrip(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }
}
