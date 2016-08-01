/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.util.RFMLog;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseActivity extends AppCompatActivity {

    protected String LOG_TAG = "BaseActivity";

    private String mAdUnitTitle;
    private String rowNumber;
    private long mAdUnitId;
    protected int mAdWidth;
    protected int mAdHeight;
    String mRfmServer;
    String mRfmAppId;
    String mRfmPubId;
    String mRfmAdId;
    private LocationManager mLocationManager;
    private RFMAd.LocationType mLocationType;
    private String mLatitude;
    private String mLongitude;
    private String bestLocationProvider;
    private Location mCurrentLocation;

    private int mRefreshCount = 0;
    private int mRefreshInterval = 0;

    int mDisplayDensity = 1;
    int mDisplayHeight = 100;

    int mNumberOfRequests = 0;
    int mNumberOfSuccess = 0;
    int mNumberOfFailures = 0;

    private TextView mCountersTextViewContent;
    private String mCountersStr = "";

    RFMAdView mAdView;
    RFMAdRequest mAdRequest;
    boolean mRfmAdTestMode;
    boolean mFullscreenMode;
    boolean mCachedAdMode;
    boolean mVideoAdMode;
    private StringBuffer mLogData = new StringBuffer();
    private TextView mLogText;

    private boolean isCounterViewExpanded = true;
    private boolean isLogsViewExpanded = true;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Bundle extras = getIntent().getExtras();

        if (extras == null)
            return;

        RFMAd rfmAd = RFMAd.fromBundle(extras);

        mAdUnitTitle = rfmAd.getTestCaseName();
        rowNumber = String.valueOf(rfmAd.getCount());
        mAdUnitId = rfmAd.getId();
        mRfmAdId = rfmAd.getAdId();
        mRfmServer = rfmAd.getRfmServer();
        mRfmPubId = rfmAd.getPubId();
        mRfmAppId = rfmAd.getAppId();
        mRfmAdTestMode = rfmAd.getTestMode();
        mFullscreenMode = rfmAd.getFullscreenMode();
        mCachedAdMode = rfmAd.getCachedAdMode();
        mVideoAdMode = rfmAd.getVideoAdMode();

        mAdWidth = rfmAd.getAdWidth();
        mAdHeight = rfmAd.getAdHeight();

        mRefreshCount = rfmAd.getRefreshCount();
        mRefreshInterval = rfmAd.getRefreshInterval();

        mLocationType = rfmAd.getLocationType();

        mLatitude = rfmAd.getLat();
        mLongitude = rfmAd.getLong();

        mDisplayDensity = (int)getResources().getDisplayMetrics().density;
        WindowManager mWinMgr = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mDisplayHeight = mWinMgr.getDefaultDisplay().getHeight();

        registerReceiver(mReceiver, new IntentFilter(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.testcase_settings_actionbar_item:
                Intent i = new Intent(getApplicationContext(), TestCaseSettings.class);
                i.putExtra(RFMAd.ID, mAdUnitId);
                i.putExtra(TestCaseSettings.DISABLE_VIDEO_ADS, false);
                String parentClass = getLocalClassName();
                if (parentClass.equals("FullScreenInterstitialAd") || parentClass.equals("VastAd"))
                    i.putExtra(TestCaseSettings.DISABLE_FULLSCREEN, false);
                else
                    i.putExtra(TestCaseSettings.DISABLE_FULLSCREEN, true);

                if (!parentClass.equals("FullScreenInterstitialAd"))
                    i.putExtra(TestCaseSettings.DISABLE_CACHED_AD, true);

                startActivity(i);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    private DatabaseChangedReceiver mReceiver = new DatabaseChangedReceiver() {
        public void onReceive(Context context, Intent intent) {
            RFMAdDataSource mRFMAdDataSource = RFMAdDataSource.getInstance(BaseActivity.this);
            final List<RFMAd> rfmAds = mRFMAdDataSource.getAllAdUnits();
            for (RFMAd _rfmAd : rfmAds) {
                if (_rfmAd.getId() == mAdUnitId) {
                    mAdWidth = _rfmAd.getAdWidth();
                    mAdHeight = _rfmAd.getAdHeight();
                    //rfmAdId = _rfmAd.getAdId();
                    mRfmAdTestMode = _rfmAd.getTestMode();
                    mFullscreenMode = _rfmAd.getFullscreenMode();
                    mCachedAdMode = _rfmAd.getCachedAdMode();
                    mVideoAdMode = _rfmAd.getVideoAdMode();
                    updateAdView();
                }
            }
        }

    };

    void setLoadAdAction() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null)
            collapsingToolbar.setTitle("(" + rowNumber + ") " + mAdUnitTitle);

        TextView backdropSubtext = (TextView) findViewById(R.id.backdrop_subtext);
        if (backdropSubtext != null)
            backdropSubtext.setText(getResources().getString(R.string.app_id, mRfmAppId));

        FloatingActionButton fetchAd = (FloatingActionButton) findViewById(R.id.fetch_ad);
        if (fetchAd != null) {
            fetchAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadAd();
                }
            });
        }

        // create timer with refresh count and interval and call loadAd();
        startTimer();

        mLogText = (TextView) findViewById(R.id.log_text);

        String parentClass = getLocalClassName();
        if (parentClass.equals("BannerInList")) {
            mLogText.setMovementMethod(new ScrollingMovementMethod());
        }

        RelativeLayout counters_text_view_title = (RelativeLayout) findViewById(R.id.expand_list_container);
        mCountersTextViewContent = (TextView) findViewById(R.id.counters_text_view_content);
        if (counters_text_view_title != null) {
            counters_text_view_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView expandButton = (ImageView) findViewById(R.id.expand_button);
                    if (isCounterViewExpanded && expandButton != null) {
                        expandButton.setImageResource(R.drawable.ic_arrow_up_white);
                        ExpandCollapseHelper.collapse(mCountersTextViewContent);
                    } else if (expandButton != null) {
                        expandButton.setImageResource(R.drawable.ic_arrow_down_white);
                        ExpandCollapseHelper.expand(mCountersTextViewContent);
                    }
                    isCounterViewExpanded = !isCounterViewExpanded;
                }
            });
        }

        RelativeLayout logs_text_view_title = (RelativeLayout) findViewById(R.id.logs_text_view_content);
        final TextView logText = (TextView) findViewById(R.id.log_text);

        ImageView expandButton2 = (ImageView) findViewById(R.id.expand_button_2);
        if (parentClass.equals("BannerInList") && expandButton2 != null) {
            expandButton2.setVisibility(View.GONE);
        } else {
            if (logs_text_view_title != null) {
                logs_text_view_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView expandButton2 = (ImageView) findViewById(R.id.expand_button_2);
                        if (isLogsViewExpanded && expandButton2 != null) {
                            expandButton2.setImageResource(R.drawable.ic_arrow_up_white);
                            ExpandCollapseHelper.collapse(logText);
                        } else if (expandButton2 != null) {
                            expandButton2.setImageResource(R.drawable.ic_arrow_down_white);
                            ExpandCollapseHelper.expand(logText);
                        }
                        isLogsViewExpanded = !isLogsViewExpanded;

                    }
                });
            }
        }

        ImageView clearButton = (ImageView) findViewById(R.id.clear_button);
        if (clearButton != null) {
            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNumberOfRequests = 0;
                    mNumberOfSuccess = 0;
                    mNumberOfFailures = 0;
                    updateCountersView();
                }
            });
        }

        ImageView clearButton2 = (ImageView) findViewById(R.id.clear_button_2);
        if (clearButton2 != null) {
            clearButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearLogsView();
                }
            });
        }

        updateCountersView();
        clearLogsView();
        setAdRequestLocation();
    }

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    private int mNumberOfRefreshes = 0;

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer(){
        if (mRefreshCount == 0 || mRefreshInterval == 0) {
            return;
        }

        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        if (mNumberOfRefreshes >= mRefreshCount) {
                            stopTimer();
                        } else {
                            mNumberOfRefreshes = mNumberOfRefreshes + 1;
                            loadAd();
                        }
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, mRefreshInterval, mRefreshInterval);
    }

    void updateCountersView() {
        mCountersStr = "Requests : " + mNumberOfRequests +  "\n" +
                "Success : " + mNumberOfSuccess + "\n" +
                "Failure : " + mNumberOfFailures;
        if (mCountersTextViewContent!=null)
            mCountersTextViewContent.setText(mCountersStr);
    }

    private void clearLogsView() {
        mLogData.setLength(0);
        mLogData.append(getResources().getString(R.string.default_stats_console_text));
        mLogText.setText(mLogData);

        String parentClass = getLocalClassName();
        if (parentClass.equals("BannerInList")) {
            mLogText.bringPointIntoView(0);
        }
    }

    void appendTextToConsole(String data) {
        mLogData.append(data + "\n");
        mLogText.setText(mLogData);

        String parentClass = getLocalClassName();
        if (parentClass.equals("BannerInList")) {
            mLogText.post(new Runnable() {
                public void run() {
                    final int scrollAmount = mLogText.getLayout().getLineTop(mLogText.getLineCount())
                            - mLogText.getHeight();
                    // if there is no need to scroll, scrollAmount will be <=0
                    if (scrollAmount > 0)
                        mLogText.scrollTo(0, scrollAmount);
                    else
                        mLogText.scrollTo(0, 0);
                }
            });
        }
    }

    // Method to change AdRequest location information
    private void setAdRequestLocation() {
        if (mLocationType == null || mAdRequest ==null) {
            Log.v(LOG_TAG, "Cannot setAdRequestLocation, invalid Preferences");
            return;
        } else {
            Log.v(LOG_TAG, "Into setAdRequestLocation, with sizeParams = " + mLocationType);
        }
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            if (mLocationType == RFMAd.LocationType.FIXED) {
                appendTextToConsole("Using Fixed Location from Settings: Lat: " + mLatitude + " Long: " + mLongitude);
                bestLocationProvider = mLocationManager.getBestProvider(new Criteria(), false);
                if (mCurrentLocation == null) {
                    mCurrentLocation = new Location(bestLocationProvider);
                }
                mCurrentLocation.setLatitude(Double.parseDouble(mLatitude));
                mCurrentLocation.setLongitude(Double.parseDouble(mLongitude));
                mAdRequest.setLocationDetectType(RFMConstants.RFM_LOCATION_AUTO);
                mAdRequest.setLocation(mCurrentLocation);
                mAdRequest.setLocationConfigured(true);
            } else if (mLocationType == RFMAd.LocationType.IP_BASED) {
                //Optional API for setting IP-based location targeting by RFM sever.
                appendTextToConsole("Using IP Address " + getLocalIpAddress());
                mAdRequest.setLocationDetectType(RFMConstants.RFM_LOCATION_IP);
                mAdRequest.setLocationConfigured(false);
            } else if (mLocationType == RFMAd.LocationType.GPS_BASED) {
                // GPS location
                try {
                    mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (mCurrentLocation == null)
                        mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e) {
                    RFMLog.v(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Failed to get location, check location access");
                    appendTextToConsole("Failed to get location, check location access " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    RFMLog.d(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Failed to get location, device has no GPS provider");
                    appendTextToConsole("Failed to get location, device has no GPS provider " + e.getMessage());
                } catch (Exception e) {
                    RFMLog.d(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Failed to get location, device has no GPS provider");
                    appendTextToConsole("Failed to get location, error retrieving location " + e.getMessage());
                }
                if(mCurrentLocation != null) {
                    appendTextToConsole("GPS Location : " + mCurrentLocation.getLatitude() + "::" + mCurrentLocation.getLongitude());
                } else {
                    appendTextToConsole("Failed to get location!");
                    RFMLog.v(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Current Location is not yet set, please check if location is enabled on device");
                }

                mAdRequest.setLocationDetectType(RFMConstants.RFM_LOCATION_GPS);
                mAdRequest.setLocation(mCurrentLocation);
                mAdRequest.setLocationConfigured(true);

            } else {
                appendTextToConsole("location detect default");
                mAdRequest.setLocationConfigured(false);
                mAdRequest.setLocationDetectType(RFMAdRequest.RFM_LOCATION_DETECT_DEFAULT);
            }
        } catch(Exception e) {
            Log.v(LOG_TAG, "Problems while configuring Location based on preferences "+e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to change AdView's Width and Height based on preferences
    void setAdRequestSize(int adwidth, int adheight){

        if(mAdView == null) {
            return;
        }
        try {
            Log.v(LOG_TAG, "Into setAdRequestSize, Width = " + adwidth+" | Height = "+adheight);
            LinearLayout.LayoutParams bannerLayout = (LinearLayout.LayoutParams) mAdView.getLayoutParams();

            if (bannerLayout == null)
                return;

            if (adwidth == -1  || adwidth < -1) {
                bannerLayout.width = LinearLayout.LayoutParams.FILL_PARENT;
            } else {
                bannerLayout.width = (int) (adwidth * mDisplayDensity);
            }
            if (adheight == -1 || adheight < -1) {
                bannerLayout.height = LinearLayout.LayoutParams.FILL_PARENT;
            } else {
                bannerLayout.height = (int) (adheight * mDisplayDensity);
            }

            mAdView.setLayoutParams(bannerLayout);
            if (mAdRequest != null) {
                mAdRequest.setAdDimensionParams(adwidth, adheight);
            }

            mAdView.invalidate();
            mAdView.requestLayout();
        } catch (Exception e) {
            Log.v(LOG_TAG, "Problems while configuring layout size based on preferences "+e.getMessage());
            e.printStackTrace();
        }
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }

    public abstract void loadAd();

    public abstract void updateAdView();

}
