package com.example.tripreminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tripreminder.R;
import com.example.tripreminder.activity.OnBoardActivity;
import com.example.tripreminder.utils.Controller;

import org.jetbrains.annotations.NotNull;

public class PermissionsFragment extends Fragment {

    private AppCompatButton requestFineLocationButton, requestCoarseLocationButton;
    private Controller controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permissions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = new Controller(getActivity());
        requestFineLocationButton = view.findViewById(R.id.requestFineLocationButton);
        requestCoarseLocationButton = view.findViewById(R.id.requestCoarseLocationButton);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!controller.is_HAVE_ACCESS_FINE_LOCATION_PERMISSION()) {
            requestFineLocationButton.setBackgroundResource(R.drawable.failure_bg);
            requestFineLocationButton.setText("Please Enabled Fine Location Permission");
            requestFineLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((OnBoardActivity) getActivity()).requestFineLocationPermission();
                }
            });
        } else {
            requestFineLocationButton.setBackgroundResource(R.drawable.success_bg);
            requestFineLocationButton.setText("Fine Location Permission is enabled");
        }

        if (!controller.is_HAVE_ACCESS_BACKGROUND_LOCATION_PERMISSION()) {
            requestCoarseLocationButton.setBackgroundResource(R.drawable.failure_bg);
            requestCoarseLocationButton.setText("Please Enabled Background Location Permission");
            requestCoarseLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((OnBoardActivity) getActivity()).requestCoarseLocationPermission();
                }
            });
        } else {
            requestCoarseLocationButton.setBackgroundResource(R.drawable.success_bg);
            requestCoarseLocationButton.setText("Background Location Permission is enabled");
        }
    }
}