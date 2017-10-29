package com.kaliviotis.efthymios.cowsensor.mobileapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Efthymios on 10/27/2017.
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener mListener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dlg = new DatePickerDialog(getActivity(), mListener, year, month, day);
        return dlg;
    }
}