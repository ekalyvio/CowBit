package com.kaliviotis.efthymios.cowsensor.mobileapp;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CowDetailsActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "CowDetailsActivity";

    String cowName;
    String cowId;
    Button pickDateButton;
    TextView cowNameTextView;
    int popupSelectedItemId;
    ChildEventListener graphValuesListener;
    Query graphValuesQuery;

    BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_details);

        initGraphValuesListener();
        graphValuesQuery = null;
        pickDateButton = findViewById(R.id.pickDateButton);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        cowName = intent.getStringExtra(CowListingActivity.Message_CowName);
        cowId = intent.getStringExtra(CowListingActivity.Message_CowID);

        cowNameTextView = findViewById(R.id.cowNameTextView);

        if (cowName.isEmpty())
            cowNameTextView.setText(cowId);
        else
            cowNameTextView.setText(cowName);

        popupSelectedItemId = R.id.month;

        // in this example, a LineChart is initialized from xml
        chart = (BarChart) findViewById(R.id.chart);


/*        entries.add(new BarEntry(2, 2));
        entries.add(new BarEntry(3, 5));
        entries.add(new BarEntry(4, 4));
        BarEntry entr = new BarEntry(1, 10);
        entries.add(entr);*/

        /*List<BarEntry> graphEntries;
        graphEntries = ;*/
        BarDataSet dataSet = new BarDataSet(new ArrayList(), ""); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLUE); // styling, ...

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        chart.getDescription().setEnabled(false);

        Calendar calendar = Calendar.getInstance();
        selectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (graphValuesQuery != null)
            graphValuesQuery.addChildEventListener(graphValuesListener);
    }

    @Override
    protected void onPause() {
        if (graphValuesQuery != null)
            graphValuesQuery.removeEventListener(graphValuesListener);

        super.onPause();
    }

    public void pickDateButtonClick(View view) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(CowDetailsActivity.this, pickDateButton);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.date_popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                popupSelectedItemId = item.getItemId();
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setListener(CowDetailsActivity.this);

/*                Bundle args = new Bundle();
                args.putInt("num", num);
                newFragment.setArguments(args);*/

                newFragment.show(getFragmentManager(), "datePickr");

                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public void changeNameButtonClick(View view) {
        //(Context context, int title, int message, int inputType, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener)
        AppDialogs.DisplayGetInput(this, R.string.enter_name_title, R.string.enter_name_message, InputType.TYPE_CLASS_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String val = AppDialogs.GetValue();
                String path = String.format("/clients/%s/cows/%s/name", FirebaseHelper.getInstance().getClientID(), cowId);
                FirebaseHelper.getInstance().getDatabase().getReference(path).setValue(val);
                cowNameTextView.setText(val);
            }
        }, null);
    }

    private BarData getBarData() {
        BarData data = chart.getData();
/*        IBarDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            return null;
        }*/
        return data;
    }

    private void initGraphValuesListener() {
        graphValuesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int st = 0;
                if (dataSnapshot.child("total_steps").exists())
                    st = Integer.valueOf(dataSnapshot.child("total_steps").getValue().toString());

                try {
                    int key = Integer.valueOf(dataSnapshot.getKey());

                    getBarData().addEntry(new BarEntry(key, st), 0);
                    chart.notifyDataSetChanged();
                    chart.invalidate();

/*                    CowDetailsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            chart.notifyDataSetChanged();
                            chart.invalidate();
                        }
                    });*/
                } catch (NumberFormatException e) {
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int st = 0;
                if (dataSnapshot.child("total_steps").exists())
                    st = Integer.valueOf(dataSnapshot.child("total_steps").getValue().toString());

                try {
                    int key = Integer.valueOf(dataSnapshot.getKey());

                    IBarDataSet set = getBarData().getDataSetByIndex(0);

                    for(int i=0; i<set.getEntryCount(); ++i) {
                        if (set.getEntryForIndex(i).getX() == key) {
                            set.getEntryForIndex(i).setY(st);
                            chart.notifyDataSetChanged();
                            chart.invalidate();
                            return;
                        }
                    }
                } catch (NumberFormatException e) {
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            // TODO: implement the ChildEventListener methods as documented above
            // ...
        };
    }

    private void dataSortedOnDay(int year, int month, int dayOfMonth) {
        if (graphValuesQuery != null)
            graphValuesQuery.removeEventListener(graphValuesListener);

        XAxis xaxis = chart.getXAxis();
        //xaxis.setTextSize(18f);
        xaxis.setDrawLabels(true);
        xaxis.setAxisMinimum(-0.5f);
        xaxis.setAxisMaximum(23.5f);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        chart.getData().getDataSetByIndex(0).clear();
        getBarData().getDataSetByIndex(0).setLabel(getString(R.string.hour_of_day_string));

        String path = String.format("/cow_data/%s/%d/%d/%d", cowId, year, month, dayOfMonth);
        graphValuesQuery = FirebaseHelper.getInstance().getDatabase().getReference().child(path).orderByKey();

        graphValuesQuery.addChildEventListener(graphValuesListener);
    }

    private void dataSortedOnMonth(int year, int month) {
        if (graphValuesQuery != null)
            graphValuesQuery.removeEventListener(graphValuesListener);

        int iMonth = month - 1; // 1 (months begin with 0)

        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(year, iMonth, 1);

        // Get the number of days in that month
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        XAxis xaxis = chart.getXAxis();
        //xaxis.setTextSize(18f);
        xaxis.setDrawLabels(true);
        xaxis.setAxisMinimum(0.5f);
        xaxis.setAxisMaximum(daysInMonth + 0.5f);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        chart.getData().getDataSetByIndex(0).clear();
        getBarData().getDataSetByIndex(0).setLabel(getString(R.string.day_of_month_string));

        String path = String.format("/cow_data/%s/%d/%d", cowId, year, month);
        graphValuesQuery = FirebaseHelper.getInstance().getDatabase().getReference().child(path).orderByKey();

        graphValuesQuery.addChildEventListener(graphValuesListener);
    }

    private void dataSortedOnYear(int year) {
        if (graphValuesQuery != null)
            graphValuesQuery.removeEventListener(graphValuesListener);

        XAxis xaxis = chart.getXAxis();
        //xaxis.setTextSize(18f);
        xaxis.setDrawLabels(true);
        xaxis.setAxisMinimum(0.5f);
        xaxis.setAxisMaximum(12.5f);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        chart.getData().getDataSetByIndex(0).clear();
        getBarData().getDataSetByIndex(0).setLabel(getString(R.string.month_of_year_string));

        String path = String.format("/cow_data/%s/%d", cowId, year);
        graphValuesQuery = FirebaseHelper.getInstance().getDatabase().getReference().child(path).orderByKey();

        graphValuesQuery.addChildEventListener(graphValuesListener);
    }

    public void selectDate(int year, int month, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = null;

        switch (popupSelectedItemId) {
            case R.id.day:
                simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                dataSortedOnDay(year, month + 1, dayOfMonth);
                break;
            case R.id.month:
                simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
                dataSortedOnMonth(year, month + 1);
                break;
            case R.id.year:
                simpleDateFormat = new SimpleDateFormat("yyyy");
                dataSortedOnYear(year);
                break;
        }

        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth, 0, 0);
        String timestamp = simpleDateFormat.format(c.getTime());

        pickDateButton.setText(timestamp);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectDate(year, month, dayOfMonth);
    }
}
