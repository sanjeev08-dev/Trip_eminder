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

public class UtilsSettingsFragment extends Fragment {
    private AppCompatButton disableBOButton, enableGALButton, startButton;
    private Controller controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_utils_settings, container, false);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = new Controller(getActivity());
        disableBOButton = view.findViewById(R.id.disableBOButton);
        enableGALButton = view.findViewById(R.id.enableGALButton);
        startButton = view.findViewById(R.id.startButton);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (controller.getIsBatteryOptimizationDisable()) {
            disableBOButton.setBackgroundResource(R.drawable.success_bg);
            disableBOButton.setText("Battery Optimization is disabled");
        } else {
            disableBOButton.setBackgroundResource(R.drawable.failure_bg);
            disableBOButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((OnBoardActivity) getActivity()).disableBatteryOptimization();
                }
            });
        }

        if (!controller.getIsGpsEnabled()) {
            enableGALButton.setBackgroundResource(R.drawable.failure_bg);
            enableGALButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((OnBoardActivity) getActivity()).enableGoogleAccuracyLocation();
                }
            });
        } else {
            enableGALButton.setBackgroundResource(R.drawable.success_bg);
            enableGALButton.setText("Google Accuracy Location is turn on");
        }

        if (controller.getIsGpsEnabled() && controller.getIsBatteryOptimizationDisable()) {
            startButton.setVisibility(View.VISIBLE);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((OnBoardActivity) getActivity()).checkUtils();
                }
            });
        } else {
            startButton.setVisibility(View.GONE);
        }

    }
}