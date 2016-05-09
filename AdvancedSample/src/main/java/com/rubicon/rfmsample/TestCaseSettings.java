/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class TestCaseSettings extends AppCompatActivity {

    private final String LOG_TAG = "TestCaseSettings";

    private Toolbar toolbar;
    private Context mContext;

    private CheckBox adWidthCheckbox;
    private EditText adWidthEditText;
    private CheckBox adHeightCheckbox;
    private EditText adHeightEditText;
    private EditText rfmAdIDEditText;
    private long adUnitId;
    private String adWidth = "";
    private String adHeight = "";
    private SwitchCompat testModeSwitch;
    private SwitchCompat fullscreenModeSwitch;
    private SwitchCompat cachedAdModeSwitch;
    private SwitchCompat videoAdSwitch;
    private HashMap<String, String> updatedUnitHashMap = new HashMap<String, String>();
    final static String DISABLE_SIZE_CHANGE = "DISABLE_SIZE_CHANGE";
    final static String DISABLE_FULLSCREEN = "DISABLE_FULLSCREEN";
    final static String DISABLE_CACHED_AD = "DISABLE_CACHED_AD";
    final static String DISABLE_VIDEO_ADS = "DISABLE_VIDEO_ADS";
    private boolean mDisableSizeChange = false;
    private boolean mDisableFullscreen = false;
    private boolean mDisableCachedAd = false;
    private boolean mDisableVideoAd = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_case_settings);
        mContext = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.white_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black);
        }

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.save_settings));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adUnitId = extras.getLong(RFMAd.ID);
            mDisableSizeChange = extras.getBoolean(DISABLE_SIZE_CHANGE);
            mDisableFullscreen = extras.getBoolean(DISABLE_FULLSCREEN);
            mDisableCachedAd = extras.getBoolean(DISABLE_CACHED_AD);
            mDisableVideoAd = extras.getBoolean(DISABLE_VIDEO_ADS);
        }

        adWidthEditText = (EditText) findViewById(R.id.ad_width_edittext);
        adWidthCheckbox = (CheckBox) findViewById(R.id.ad_width_checkbox);
        adHeightEditText = (EditText) findViewById(R.id.ad_height_edittext);
        adHeightCheckbox = (CheckBox) findViewById(R.id.ad_height_checkbox);
        testModeSwitch = (SwitchCompat) findViewById(R.id.test_mode_switch);
        fullscreenModeSwitch = (SwitchCompat) findViewById(R.id.fullscreen_switch);
        cachedAdModeSwitch = (SwitchCompat) findViewById(R.id.cached_ad_switch);
        videoAdSwitch = (SwitchCompat) findViewById(R.id.video_ad_switch);
        rfmAdIDEditText = (EditText) findViewById(R.id.rfm_ad_id_editext);
        LinearLayout sizeContainer = (LinearLayout) findViewById(R.id.size_container);
        RelativeLayout fullscreenModeSwitchContainer = (RelativeLayout) findViewById(R.id.fullscreen_switch_container);
        RelativeLayout cachedAdSwitchContainer = (RelativeLayout) findViewById(R.id.cached_ad_switch_container);
        RelativeLayout videoAdsSwitchContainer = (RelativeLayout) findViewById(R.id.video_ad_switch_container);

        if (mDisableSizeChange)
            sizeContainer.setVisibility(View.GONE);

        if (mDisableFullscreen)
            fullscreenModeSwitchContainer.setVisibility(View.GONE);

        if (mDisableCachedAd)
            cachedAdSwitchContainer.setVisibility(View.GONE);

        if (mDisableVideoAd)
            videoAdsSwitchContainer.setVisibility(View.GONE);

        adWidthCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    adWidthEditText.setVisibility(View.INVISIBLE);
                } else {
                    adWidthEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        adHeightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    adHeightEditText.setVisibility(View.INVISIBLE);
                } else {
                    adHeightEditText.setVisibility(View.VISIBLE);
                }
            }
        });


        initializeActivity();
    }

    private void initializeActivity() {

        RFMAdDataSource mRFMAdDataSource = RFMAdDataSource.getInstance(TestCaseSettings.this);
        RFMAd _rfmAd = mRFMAdDataSource.getRowById(adUnitId);
        long mAdWidth = _rfmAd.getAdWidth();
        if (mAdWidth == -1) {
            adWidthCheckbox.setChecked(true);
            adWidthEditText.setVisibility(View.INVISIBLE);
        } else {
            adWidthEditText.setText(String.valueOf(mAdWidth));
        }
        long mAdHeight = _rfmAd.getAdHeight();
        if (mAdHeight == -1) {
            adHeightCheckbox.setChecked(true);
            adHeightEditText.setVisibility(View.INVISIBLE);
        } else {
            adHeightEditText.setText(String.valueOf(mAdHeight));
        }

        boolean testMode = _rfmAd.getTestMode();
        testModeSwitch.setChecked(testMode);

        boolean fullscreenMode = _rfmAd.getFullscreenMode();
        fullscreenModeSwitch.setChecked(fullscreenMode);

        boolean cachedAdMode = _rfmAd.getCachedAdMode();
        cachedAdModeSwitch.setChecked(cachedAdMode);

        boolean videoAdMode = _rfmAd.getVideoAdMode();
        videoAdSwitch.setChecked(videoAdMode);

        String rfmAdId = _rfmAd.getAdId();
        if (rfmAdId != null) {
            rfmAdIDEditText.setText(rfmAdId);
        } else {
            rfmAdIDEditText.setText("");
        }

    }

    private void saveTestCaseSettings() {
        updatedUnitHashMap.clear();

        updateAdSize();
        updateTestMode();
        updateFullscreenMode();
        updateCachedAdMode();
        updateVideoAdMode();
        updateRFMAdId();

        RFMAdDataSource adUnitDS = RFMAdDataSource.getInstance(getBaseContext());
        adUnitDS.updateSampleAdUnit(adUnitId, updatedUnitHashMap);

        mContext.sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

        finish();
    }

    private void updateAdSize() {
        updatedUnitHashMap.put(RFMAd.AD_WIDTH, adWidth);
        updatedUnitHashMap.put(RFMAd.AD_HEIGHT, adHeight);
    }

    private void updateTestMode() {
        if (testModeSwitch.isChecked()) {
            updatedUnitHashMap.put(RFMAd.TEST_MODE, "1");
        } else {
            updatedUnitHashMap.put(RFMAd.TEST_MODE, "0");
        }
    }

    private void updateFullscreenMode() {
        if (fullscreenModeSwitch.isChecked()) {
            updatedUnitHashMap.put(RFMAd.FULLSCREEN_MODE, "1");
        } else {
            updatedUnitHashMap.put(RFMAd.FULLSCREEN_MODE, "0");
        }
    }

    private void updateCachedAdMode() {
        if (cachedAdModeSwitch.isChecked()) {
            updatedUnitHashMap.put(RFMAd.CACHED_AD_MODE, "1");
        } else {
            updatedUnitHashMap.put(RFMAd.CACHED_AD_MODE, "0");
        }
    }

    private void updateVideoAdMode() {
        if (videoAdSwitch.isChecked()) {
            updatedUnitHashMap.put(RFMAd.VIDEO_AD_MODE, "1");
        } else {
            updatedUnitHashMap.put(RFMAd.VIDEO_AD_MODE, "0");
        }
    }

    private void updateRFMAdId() {
        String rfmAdID = rfmAdIDEditText.getText().toString().trim();
        updatedUnitHashMap.put(RFMAd.AD_ID, rfmAdID);
    }

    private String valuesAreValid() {
        if (adWidthCheckbox.isChecked()) {
            adWidth = "-1";
        } else {
            adWidth = adWidthEditText.getText().toString().trim();
            if (adWidth.isEmpty()) {
                return "Enter a valid Width value";
            }
        }
        if (adHeightCheckbox.isChecked()) {
            adHeight = "-1";
        } else {
            adHeight = adHeightEditText.getText().toString().trim();
            if (adHeight.isEmpty()) {
                return "Enter a valid Height value";
            }
        }
        return "";
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
                    Utils.snackbar(TestCaseSettings.this, msg, false);
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

}
