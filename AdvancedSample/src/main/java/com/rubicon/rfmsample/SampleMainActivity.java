/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleMainActivity extends AppCompatActivity {

    private String LOG_TAG = "SampleMainActivity";
    private RFMAdDataSource mRFMAdDataSource;
    private ListView mSampleAdsListView;
    private SampleListAdapter mSampleAdsListAdapter;
    private long firstAdUnitId = -1;
    private static final int READ_INPUT_JSON_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (toolbar != null) {
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setText(getResources().getString(R.string.main_activity_title));
        }

        mRFMAdDataSource = RFMAdDataSource.getInstance(this);

        mSampleAdsListView = (ListView) findViewById(R.id.dynamic_list);
        mSampleAdsListAdapter = new SampleListAdapter(this);
        mSampleAdsListView.setAdapter(mSampleAdsListAdapter);

        final SwipeDetector swipeDetector = new SwipeDetector();
        mSampleAdsListView.setOnTouchListener(swipeDetector);
        mSampleAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    /* Don't allow swiping the headers and predefined items. */
                    boolean shouldDelete = true;
                    if (!mSampleAdsListAdapter.isEnabled(position)) {
                        shouldDelete = false;
                    }
                    RFMAd rfmAd = (RFMAd) mSampleAdsListAdapter.getItem(position);
                    if (!rfmAd.isCustom()) {
                        shouldDelete = false;
                    }

                    if (shouldDelete) {
                        RFMAd _rfmAd = (RFMAd) mSampleAdsListAdapter.getItem(position);
                        deleteAdUnit(_rfmAd);
                    }
                } else {
                    try {
                        RFMAd rfmAd = (RFMAd) mSampleAdsListAdapter.getItem(position);
                        Intent i = new Intent(getApplicationContext(),
                                Class.forName(rfmAd.getActivityClass().getName()));
                        i.putExtras(rfmAd.toBundle());
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mSampleAdsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                boolean shouldDelete = true;
                if (!mSampleAdsListAdapter.isEnabled(pos)) {
                    shouldDelete = false;
                }
                RFMAd rfmAd = (RFMAd) mSampleAdsListAdapter.getItem(pos);
                if (!rfmAd.isCustom()) {
                    shouldDelete = false;
                }

                if (shouldDelete) {
                    RFMAd _rfmAd = (RFMAd) mSampleAdsListAdapter.getItem(pos);
                    editAdUnit(_rfmAd.getId());
                    return true;
                }
                return false;
            }
        });

        updateListUI();

        registerReceiver(mReceiver, new IntentFilter(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));

        setJsonToDBUploader();
    }

    private DatabaseChangedReceiver mReceiver = new DatabaseChangedReceiver() {
        public void onReceive(Context context, Intent intent) {
            updateListUI();
        }
    };

    private void editAdUnit(final long adUnitId) {
        RFMAdDataSource mRFMAdDataSource = RFMAdDataSource.getInstance(SampleMainActivity.this);
        final RFMAd _rfmAd = mRFMAdDataSource.getRowById(adUnitId);

        LayoutInflater layoutInflater = LayoutInflater.from(SampleMainActivity.this);

        final View promptView = layoutInflater.inflate(R.layout.dialog_add_test_case, null);
        final EditText testCaseNameEditText = (EditText) promptView.findViewById(R.id.test_case_name_edittext);
        final EditText serverNameEditText = (EditText) promptView.findViewById(R.id.rfm_server_name_editext);
        final EditText pubIdEditText = (EditText) promptView.findViewById(R.id.rfm_pub_id_edittext);
        final EditText appIdEditText = (EditText) promptView.findViewById(R.id.rfm_app_id_edittext);
        final EditText adIdEditText = (EditText) promptView.findViewById(R.id.rfm_ad_id_edittext);
        final Spinner adTypeSpinner = (Spinner) promptView.findViewById(R.id.ad_type_spinner);

        testCaseNameEditText.setText(_rfmAd.getTestCaseName());
        serverNameEditText.setText(_rfmAd.getRfmServer());
        pubIdEditText.setText(_rfmAd.getPubId());
        appIdEditText.setText(_rfmAd.getAppId());
        adIdEditText.setText(_rfmAd.getAdId());

        final RFMAd.AdType[] adTypes = RFMAd.AdType.values();
        final List<String> adTypeStrings = new ArrayList<String>(adTypes.length);

        for (final RFMAd.AdType adType : adTypes) {
            adTypeStrings.add(adType.getName());
        }

        adTypeSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, adTypeStrings));

        final RFMAd.AdType adType = _rfmAd.getAdType();
        adTypeSpinner.setSelection(adType.ordinal());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampleMainActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final String testCaseName = testCaseNameEditText.getText().toString();
                        if (testCaseName.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Test case Name is empty", false);
                            return;
                        }

                        final String serverName = serverNameEditText.getText().toString();
                        if (serverName.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Server Name is empty", false);
                            return;
                        }

                        final String appId = appIdEditText.getText().toString();
                        if (appId.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "App Id is empty", false);
                            return;
                        }

                        final String pubId = pubIdEditText.getText().toString();
                        if (pubId.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Pub Id is empty", false);
                            return;
                        }

                        String adId = adIdEditText.getText().toString();

                        final RFMAd.AdType adType = adTypes[adTypeSpinner.getSelectedItemPosition()];

                        HashMap<String, String> updatedUnitHashMap = new HashMap<String, String>();
                        updatedUnitHashMap.clear();

                        updatedUnitHashMap.put(RFMAd.TEST_CASE_NAME, testCaseName);
                        updatedUnitHashMap.put(RFMAd.RFM_SERVER, serverName);
                        updatedUnitHashMap.put(RFMAd.APP_ID, appId);
                        updatedUnitHashMap.put(RFMAd.PUB_ID, pubId);
                        updatedUnitHashMap.put(RFMAd.APP_ID, appId);
                        updatedUnitHashMap.put(RFMAd.AD_ID, adId);
                        updatedUnitHashMap.put(RFMAd.AD_TYPE, adType.getActivityClass().getName());

                        RFMAdDataSource adUnitDS = RFMAdDataSource.getInstance(getBaseContext());
                        adUnitDS.updateSampleAdUnit(adUnitId, updatedUnitHashMap);

                        dialog.dismiss();
                        updateListUI();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void addAdUnit(final RFMAd rfmAd) {
        RFMAd createdRFMAd = mRFMAdDataSource.createAdUnit(rfmAd);
        if (createdRFMAd != null) {
            Log.d(LOG_TAG, "Create new ad unit success!");
        }
        updateListUI();
    }

    private void deleteAdUnit(final RFMAd rfmAd) {
        mRFMAdDataSource.deleteSampleAdUnit(rfmAd);
        mSampleAdsListAdapter.remove(rfmAd);
        Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.item_deleted), true);
        updateListUI();
    }

    private void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SampleMainActivity.this);

        final View promptView = layoutInflater.inflate(R.layout.dialog_add_test_case, null);
        final EditText testCaseNameEditText = (EditText) promptView.findViewById(R.id.test_case_name_edittext);
        final EditText serverNameEditText = (EditText) promptView.findViewById(R.id.rfm_server_name_editext);
        final EditText pubIdEditText = (EditText) promptView.findViewById(R.id.rfm_pub_id_edittext);
        final EditText appIdEditText = (EditText) promptView.findViewById(R.id.rfm_app_id_edittext);
        final EditText adIdEditText = (EditText) promptView.findViewById(R.id.rfm_ad_id_edittext);
        final Spinner adTypeSpinner = (Spinner) promptView.findViewById(R.id.ad_type_spinner);

        final RFMAd.AdType[] adTypes = RFMAd.AdType.values();
        final List<String> adTypeStrings = new ArrayList<String>(adTypes.length);

        for (final RFMAd.AdType adType : adTypes) {
            adTypeStrings.add(adType.getName());
        }

        adTypeSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, adTypeStrings));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SampleMainActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final String testCaseName = testCaseNameEditText.getText().toString();
                        if (testCaseName.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Test case Name is empty", false);
                            return;
                        }

                        final String serverName = serverNameEditText.getText().toString();
                        if (serverName.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Server Name is empty", false);
                            return;
                        }

                        final String appId = appIdEditText.getText().toString();
                        if (appId.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "App Id is empty", false);
                            return;
                        }

                        final String pubId = pubIdEditText.getText().toString();
                        if (pubId.isEmpty()) {
                            Utils.snackbar(SampleMainActivity.this, "Pub Id is empty", false);
                            return;
                        }

                        String adId = adIdEditText.getText().toString();

                        final RFMAd.AdType adType = adTypes[adTypeSpinner.getSelectedItemPosition()];
                        final RFMAd rfmAd = new RFMAd(-1, testCaseName, serverName, adType,
                                1, 0, RFMAd.LocationType.NONE, "6", "0.0", "0.0",
                                "", 320, 50, true, false, false, false, adId, true, serverName, appId, pubId, 0);
                        addAdUnit(rfmAd);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void updateListUI() {
        boolean addedUserDefinedHeader = false;
        mSampleAdsListAdapter.clear();
        mSampleAdsListAdapter.add(new SampleListHeader(getResources().getString(R.string.preset)));
        final List<RFMAd> rfmAds = mRFMAdDataSource.getAllAdUnits();

        firstAdUnitId = rfmAds.get(0).getId();
        int count = 0;
        for (final RFMAd rfmAd : rfmAds) {
            if (rfmAd.isCustom() && !addedUserDefinedHeader) {
                addedUserDefinedHeader = true;
                mSampleAdsListAdapter.add(new SampleListHeader(getResources().getString(R.string.user_defined)));
            }
            count = count + 1;
            rfmAd.setCount(count);
            mSampleAdsListAdapter.add(rfmAd);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_testcase:
                showInputDialog();
                return true;
            case R.id.action_settings:
                Intent sampleSettingsIntent = new Intent(getApplicationContext(), SampleSettings.class);
                sampleSettingsIntent.putExtra(RFMAd.ID, firstAdUnitId);
                startActivity(sampleSettingsIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mRFMAdDataSource.cleanUp();
    }


    // ----  handle the click of transparent rect to upload data into DB ------
    private final String INPUT_JSON = "/rubicon/rfmsample/input.json";
    private static int clickCount = 0;
    private Handler mTimerHandler = new Handler();
    private Runnable counterResetRunnable = new Runnable() {
        @Override
        public void run() {
            clickCount = 0;
        }
    };

    private void resetCount() {
        mTimerHandler.removeCallbacks(counterResetRunnable);
        mTimerHandler.postDelayed(counterResetRunnable, 3000);
    }

    private void setJsonToDBUploader() {
        final View uploadFileToDB = findViewById(R.id.upload_file_to_db);
        if (uploadFileToDB != null) {
            uploadFileToDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCount();
                    if (clickCount == 10) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getPermissionToReadInputJson();
                        } else {
                            uploadJsonToDB();
                        }
                    }
                    clickCount++;
                }
            });
        }
    }

    private void getPermissionToReadInputJson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        READ_INPUT_JSON_PERMISSION_REQUEST);
            } else {
                uploadJsonToDB();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_INPUT_JSON_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadJsonToDB();
                } else {
                    Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.json_file_no_permission), false);
                }
                return;
            }
        }
    }

    private void uploadJsonToDB() {
        String inputJsonPath = Environment.getExternalStorageDirectory().getPath() + INPUT_JSON;
        File file = new File(inputJsonPath);
        if (!file.exists()) {
            Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.json_file_not_found), false);
            return;
        }

        String jsonString = JsonToSQLLite.readFileToString(inputJsonPath);
        boolean saveStatus = JsonToSQLLite.saveToDB(getApplicationContext(), jsonString);
        if (saveStatus) {
            Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.db_upload_success), true);
        } else {
            Utils.snackbar(SampleMainActivity.this, getResources().getString(R.string.db_upload_failure), false);
        }
        updateListUI();
    }
    // -------------------------------------------------------------------
}