/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;

import java.util.HashMap;

public class CachedAd extends BaseActivity {

    private final String LOG_TAG = "SimpleBanner";
    private TextView mDebugConsoleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demosimplebanner);

        mDebugConsoleView = (TextView) findViewById(R.id.log_text);
        if (mDebugConsoleView != null) {
            mDebugConsoleView.setBackgroundColor(Color.WHITE);
            mDebugConsoleView.setMovementMethod(new ScrollingMovementMethod());
        }

        if (mAdView == null) {
            mAdView = (RFMAdView) findViewById(R.id.bannerviewone);
            // This is to enable HW acceleration for Videos
            mAdView.enableHWAcceleration(true);

            //UI control for Sample App
            mAdView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Create Banner Ad Request
        createBannerRequest();
        // Add a listener
        setRFMAdViewListener();
        setLoadAdAction();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null)
            mAdView.rfmAdViewDestroy();

        Log.d(LOG_TAG, "SimpleBanner Destroyed");
        super.onDestroy();
    }

    /**
     * Implementation of abstract method to apply size change
     **/
    @Override
    public void updateAdView() {
        setAdRequestSize(mAdWidth, mAdHeight);
    }

    /**
     * Implementation of abstract method to request Ad
     **/
    @Override
    public void loadAd() {
        appendTextToConsole("\nRequesting Ad from RFM SDK");
        Log.v(LOG_TAG, " Request Settings: ServerName: " + mAdRequest.getRFMServerName() + " PubId: "
                + mAdRequest.getRFMPubId() + " AppId: " + mAdRequest.getRFMAppId());

        // Request Ad
        if (!mAdView.requestCachedRFMAd(mAdRequest)) {
            appendTextToConsole("ad request denied");
        } else {
            appendTextToConsole("ad request accepted, waiting for ad");
        }

        mNumberOfRequests = mNumberOfRequests + 1;
        updateCountersView();
    }

    /**
     * Utility method to initialize RFM Ad request parameters
     */
    private void createBannerRequest() {
        if (mAdRequest == null) {
            mAdRequest = new RFMAdRequest();
        }
        if (mRfmAdTestMode) {
            mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
        }

        if (!"0".equalsIgnoreCase(mRfmAdId)) {
            mAdRequest.setRFMTestAdId(mRfmAdId);
        }
        mAdRequest.setRFMParams(mRfmServer, mRfmPubId, mRfmAppId);
        setAdRequestSize(mAdWidth, mAdHeight);
    }

    /**
     * Utility method to set RFM AdView listener
     */
    private void setRFMAdViewListener() {
        // Optional listener for RFMAd status
        if (mAdView != null) {
            mAdView.setRFMAdViewListener(new RFMAdViewListener() {

                /*
                 * Sent when the ad request has been processed.
                 *
                 * @param adView -  RFMAdView instance that generated this method
                 * @param requestUrl - One of two possible values :
                 * a) Request URL if the ad request from client was accepted by SDK and a request will be potentially
                 * attempted.
                 * b) error message if the ad request from client was denied by SDK. Sent typically when the adview is in a
                 * state where it cannot accept new ad requests.
                 * @param adRequestSuccess -
                 * a) true if the ad request from client was accepted by SDK and a request will be potentially
                 * attempted.
                 * b) false if the ad request from client was denied by SDK. Sent typically when the adview is in a
                 * state where it cannot accept new ad requests.
                 *
                 */
                public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
                    mAdView.setVisibility(View.GONE);
                    appendTextToConsole("RFM Ad: Requesting Url:" + requestUrl);
                }

                /*
                 * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
                 * visible if it has been set to invisible till this time.
                 *
                 * @param adView - RFMAdView instance that generated this method
                 *
                 */
                public void onAdReceived(RFMAdView adView) {
                    appendTextToConsole("RFM Ad: Received");
                    CachedAd.this.mAdView.setVisibility(View.VISIBLE);
                    try {
                        adView.showCachedAd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                /*
                 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
                 * if it was set to visible.
                 *
                 * @param adView - RFMAdView instance that generated this method
                 *
                 */
                public void onAdFailed(RFMAdView adView) {
                    mAdView.setVisibility(View.INVISIBLE);
                    appendTextToConsole("RFM Ad: Failed");

                    mNumberOfFailures = mNumberOfFailures + 1;
                    updateCountersView();
                }

                /*
                 * Sent when an user interaction with ad results in a transition in view type from banner to full screen
                 * landing view or vice-versa
                 *
                 * @param adView - RFMAdView instance that generated this method
                 * @param event - User interaction event of type RFMAdViewEvent
                 *
                 */
                public void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event) {
                    switch (event) {
                        case FULL_SCREEN_AD_DISPLAYED:
                            appendTextToConsole("RFM Ad: Full screen ad displayed");
                            break;

                        case FULL_SCREEN_AD_DISMISSED:
                            appendTextToConsole("RFM Ad: Full screen ad dismissed");
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
                    appendTextToConsole("RFM Ad: resized to width " + arg1 + ", height = " + arg2);
                }

                @Override
                public void didDisplayAd(RFMAdView arg0) {
                    appendTextToConsole("RFM Ad: Ad displayed");
                    mNumberOfSuccess = mNumberOfSuccess + 1;
                    updateCountersView();
                }

                @Override
                public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
                    appendTextToConsole("RFM Ad: Could not be displayed");
                }

            });
        }
    }
}
