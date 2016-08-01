/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleSettings extends AppCompatActivity {

    private final String LOG_TAG = "SampleSettings";
    private Toolbar toolbar;
    private Context mContext;
    private EditText refreshCountEditText;
    private EditText refreshIntervalEditText;
    private LinearLayout locationLatLongContainer;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Spinner locationTypeSpinner;
    private long adUnitId;

    private HashMap<String, String> updatedUnitHashMap = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_settings);
        mContext = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black);
        }
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.settings));

        locationTypeSpinner = (Spinner) findViewById(R.id.location_type_spinner);
        refreshCountEditText = (EditText) findViewById(R.id.refresh_count_editext);
        refreshIntervalEditText = (EditText) findViewById(R.id.refresh_interval_editext);
        locationLatLongContainer = (LinearLayout) findViewById(R.id.location_lat_long_container);
        latitudeEditText = (EditText) findViewById(R.id.location_latitude_editext);
        longitudeEditText = (EditText) findViewById(R.id.location_longitude_editext);

        Bundle extras = getIntent().getExtras();
        adUnitId = extras.getLong(RFMAd.ID);

        initSampleSettings();

    }

    private void initSampleSettings() {
        RFMAdDataSource rfmAdDataSource = RFMAdDataSource.getInstance(mContext);
        RFMAd rfmAd;

        rfmAd = rfmAdDataSource.getRowById(adUnitId);

        final List<String> locationTypeStrings = new ArrayList<String>();
        locationTypeStrings.add(RFMAd.LocationType.NONE.getLocType());
        locationTypeStrings.add(RFMAd.LocationType.FIXED.getLocType());
        locationTypeStrings.add(RFMAd.LocationType.IP_BASED.getLocType());
        locationTypeStrings.add(RFMAd.LocationType.GPS_BASED.getLocType());

        locationTypeSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, locationTypeStrings));

        locationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == RFMAd.LocationType.FIXED.ordinal()) {
                    locationLatLongContainer.setVisibility(View.VISIBLE);
                } else {
                    locationLatLongContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (rfmAd.getLocationType()!=null) {
            locationTypeSpinner.setSelection(rfmAd.getLocationType().ordinal());
            if (rfmAd.getLocationType() == RFMAd.LocationType.FIXED) {
                locationLatLongContainer.setVisibility(View.VISIBLE);
                latitudeEditText.setText(String.valueOf(rfmAd.getLat()));
                longitudeEditText.setText(String.valueOf(rfmAd.getLong()));
            } else {
                locationLatLongContainer.setVisibility(View.GONE);
            }
        }

        refreshCountEditText.setText(String.valueOf(rfmAd.getRefreshCount()));
        refreshIntervalEditText.setText(String.valueOf(rfmAd.getRefreshInterval()));

    }

    private String valuesAreValid() {
        if (locationTypeSpinner.getSelectedItemPosition() == RFMAd.LocationType.FIXED.ordinal()) {
            if (latitudeEditText.getText().toString().isEmpty())
                return "Enter a valid latitude value!";
            if (longitudeEditText.getText().toString().isEmpty())
                return "Enter a valid longitude value!";
        }

        if (refreshCountEditText.getText().toString().isEmpty())
            return "Enter a valid Number of requests";
        if (refreshIntervalEditText.getText().toString().isEmpty())
            return "Enter a valid Refresh interval";

        return "";
    }

    private void saveTestCaseSettings() {
        updatedUnitHashMap.clear();

        updateRefreshFields();
        updateLocType();

        RFMAdDataSource adUnitDS = RFMAdDataSource.getInstance(getBaseContext());

        // sample settings table is configured to work at ad unit level if required
        // but for current implementation we are using it at sample app level
        // we will updated all rows with same value
        adUnitDS.updateSampleAdUnit(-1, updatedUnitHashMap);

        mContext.sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

        finish();
    }

    private void updateRefreshFields() {
        updatedUnitHashMap.put(RFMAd.REFRESH_COUNT, refreshCountEditText.getText().toString());
        updatedUnitHashMap.put(RFMAd.REFRESH_INTERVAL, refreshIntervalEditText.getText().toString());
    }

    private void updateLocType() {
        final RFMAd.LocationType[] locTypes = RFMAd.LocationType.values();
        RFMAd.LocationType locationType = locTypes[locationTypeSpinner.getSelectedItemPosition()];

        updatedUnitHashMap.put(RFMAd.LOCATION_TYPE, locationType.getLocType());
        if (locationType == RFMAd.LocationType.FIXED) {
            updatedUnitHashMap.put(RFMAd.LAT, latitudeEditText.getText().toString());
            updatedUnitHashMap.put(RFMAd.LONG, longitudeEditText.getText().toString());
        } else {
            updatedUnitHashMap.put(RFMAd.LAT, "");
            updatedUnitHashMap.put(RFMAd.LONG, "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_sample_settings:
                String msg = valuesAreValid();
                if (msg.equals(""))
                    saveTestCaseSettings();
                else
                    Utils.snackbar(SampleSettings.this, msg, false);
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
