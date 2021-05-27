package com.example.tripreminder.utils;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tripreminder.R;
import com.example.tripreminder.model.Trip;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDialog {

    private final AlertDialog dialog;
    private final View view;
    private TextView addressText,chooseDateText;
    private DatePicker timeText;
    private EditText workText;
    private Button cancel_button, done_button;
    private ImageView dropIcon;
    private boolean isDatePickerShow = false;
    private ConstraintLayout dateTextLayout;
    private Date result;
    private Trip trip;

    public MyDialog(Context context) {
        isDatePickerShow = false;
        LayoutInflater factory = LayoutInflater.from(context);
        view = factory.inflate(R.layout.popup_item, null);
        dialog = new AlertDialog.Builder(context).create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setView(view);

        addressText = view.findViewById(R.id.addressText);
        workText = view.findViewById(R.id.workText);
        timeText = view.findViewById(R.id.timeText);
        dropIcon = view.findViewById(R.id.dropIcon);
        chooseDateText = view.findViewById(R.id.chooseDateText);
        dateTextLayout = view.findViewById(R.id.dateTextLayout);
        cancel_button = view.findViewById(R.id.cancel_button);
        done_button = view.findViewById(R.id.done_button);

        timeText.setMinDate(Calendar.getInstance().getTimeInMillis());

        dateTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDatePickerShow){
                    isDatePickerShow = false;
                    dropIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    timeText.setVisibility(View.GONE);
                }else {
                    isDatePickerShow = true;
                    dropIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    timeText.setVisibility(View.VISIBLE);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeText.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year,monthOfYear,dayOfMonth);
                    //chooseDateText.setText(calendar.getTimeInMillis()+"");
                    DateFormat simple = new SimpleDateFormat("MMM dd , yyyy");
                    result = new Date(calendar.getTimeInMillis());
                    chooseDateText.setText(simple.format(result));
                }
            });
        }
    }

    public void showDialog() {
        dialog.show();
    }

    public void hideDialog() {
        dialog.dismiss();
    }

    public TextView getAddressText() {
        return addressText;
    }

    public EditText getWorkText() {
        return workText;
    }

    public Button getCancel_button() {
        return cancel_button;
    }

    public Button getDone_button() {
        return done_button;
    }

    public DatePicker getTimeText() {
        return timeText;
    }

    public View getView() {
        return view;
    }

    public Date getResult() {
        return result;
    }

    public void setResult(Date result) {
        this.result = result;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public TextView getChooseDateText() {
        return chooseDateText;
    }

    public void setChooseDateText(TextView chooseDateText) {
        this.chooseDateText = chooseDateText;
    }
}
