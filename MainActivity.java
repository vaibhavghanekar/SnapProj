package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    @InjectView(R.id.spinnerYear)
    com.example.myapplication.NoDefaultSpinner spinnerYear;
    @InjectView(R.id.spinnerMonth)
    com.example.myapplication.NoDefaultSpinner spinnerMonth;
    @InjectView(R.id.listview)
    ListView listview;

    private static final String TAG = MainActivity.class.getSimpleName() + "_";
    private Activity activity;
    private ArrayList<Object> mYearList = new ArrayList<>();
    private ArrayList<Object> mMonthList = new ArrayList<>();
    private Integer[] mYear = {2015, 2016, 2017, 2018, 2019, 2020};
    private String[] mMonth = {"jan", "feb", "march", "apr", "may", "june", "july", "aug", "sep", "oct", "nov", "dec"};
    private boolean isSpinnerTouched;

    private Integer[] days31Array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
    private Integer[] days30Array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
    private Integer[] daysFeb29Array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};
    private Integer[] daysFeb28Array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};

    private int yearSelected = 0;
    private int monthSelected = 0;
    private int numberOfDaysTobeListed = 0;
    private boolean isLeapYear;
    private ArrayAdapter listviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        activity = MainActivity.this;
        mYearList.add("Select year");
        mYearList.addAll(Arrays.asList(mYear));
        mMonthList.add("Select month");
        mMonthList.addAll(Arrays.asList(mMonth));

        spinnerYear.setOnItemSelectedListener(this);
        spinnerMonth.setOnItemSelectedListener(this);
        spinnerYear.setOnTouchListener(this);
        spinnerMonth.setOnTouchListener(this);

        setupSpinners();
    }

    private void setupSpinners() {
        ArrayAdapter<Object> monthArrayAdapter = new ArrayAdapter<Object>(activity, R.layout.support_simple_spinner_dropdown_item, mMonthList);
        spinnerMonth.setAdapter(monthArrayAdapter);

        ArrayAdapter<Object> yearArrayAdapter = new ArrayAdapter<Object>(activity, R.layout.support_simple_spinner_dropdown_item, mYearList);
        spinnerYear.setAdapter(yearArrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isSpinnerTouched) return;

        if (null != parent && parent.getId() == R.id.spinnerYear) {
            yearSelected = mYear[position - 1];
            if (yearSelected % 4 == 0) {
                isLeapYear = true;
            } else {
                isLeapYear = false;
            }
            spinnerMonth.setSelection(monthSelected);
        } else {
            monthSelected = position;
            if (monthSelected == 1 || monthSelected == 3 || monthSelected == 5 || monthSelected == 7 || monthSelected == 8 || monthSelected == 10 || monthSelected == 12) {
                numberOfDaysTobeListed = 31;
            } else if (monthSelected != 2) {
                numberOfDaysTobeListed = 30;
            } else if (isLeapYear) {
                numberOfDaysTobeListed = 29;
            } else {
                numberOfDaysTobeListed = 28;
            }
        }

        if (yearSelected != 0 && monthSelected != 0) {
            populateListview();
        }
    }

    private void populateListview() {
        switch (numberOfDaysTobeListed) {
            case 28:
                listviewAdapter = new ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, daysFeb28Array);
                break;
            case 29:
                listviewAdapter = new ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, daysFeb29Array);
                break;
            case 30:
                listviewAdapter = new ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, days30Array);
                break;
            case 31:
                listviewAdapter = new ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, days31Array);
                break;
        }
        listview.setAdapter(listviewAdapter);
        //listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(activity, "Day: " + (position + 1) + "/" + monthSelected + "/" + yearSelected, Toast.LENGTH_SHORT).show();
                for (int a = 0; a < parent.getChildCount(); a++) {
                    parent.getChildAt(a).setBackgroundColor(Color.WHITE);
                }

                view.setBackgroundColor(Color.RED);

                SPUtils.put(activity, Constants.SP_KEY_DAY, String.valueOf(position + 1));
                SPUtils.put(activity, Constants.SP_KEY_MONTH, String.valueOf(monthSelected));
                SPUtils.put(activity, Constants.SP_KEY_YEAR, String.valueOf(yearSelected));

                Log.d(TAG, "onItemClick: " + SPUtils.getSP(activity).getString(Constants.SP_KEY_DAY, ""));
                Log.d(TAG, "onItemClick: " + SPUtils.getSP(activity).getString(Constants.SP_KEY_MONTH, ""));
                Log.d(TAG, "onItemClick: " + SPUtils.getSP(activity).getString(Constants.SP_KEY_YEAR, ""));
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        isSpinnerTouched = true;
        return false;
    }
}
