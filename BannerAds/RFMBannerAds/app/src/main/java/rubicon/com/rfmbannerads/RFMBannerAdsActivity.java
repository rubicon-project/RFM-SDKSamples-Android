/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 * SDK Version: RFM SDK 4.0.0
 * Sample code to integrate BANNER Ad using RFM SDK
 */
package rubicon.com.rfmbannerads;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;

public class RFMBannerAdsActivity extends AppCompatActivity {
    private String LOG_TAG = "RFMBannerAdsActivity";

    private RFMAdRequest mAdRequest;
    private RFMAdView mAdView;

    private String mRfmServer = "http://mrp.rubiconproject.com";
    private String mRfmPubId = "111008";
    private String mRfmAppId = "281844F0497A0130031D123139244773";
    private String mRfmAdId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRFMAd();
            }
        });


        // Get the reference to RFM Ad View
        if (mAdView == null) {
            mAdView = (RFMAdView) findViewById(R.id.simplebanner_adview);
            // This is to enable HW acceleration for Videos
            mAdView.enableHWAcceleration(true);
        }

        // create the RFM Ad request
        createRFMAdRequest();

        // set optional RFM Ad view listener
        setRFMAdViewListener();

        // Request for an ad
        requestRFMAd();
    }

    /**
     * Utility method to create RFM Ad request
     *  - lists steps to create request using SDK API
     */
    private void createRFMAdRequest() {
        if (mAdRequest == null) {
            mAdRequest = new RFMAdRequest();
        }

        // Remove these calls before releasing the publisher app
        mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
        mAdRequest.setRFMTestAdId(mRfmAdId);

        mAdRequest.setRFMParams(mRfmServer, mRfmPubId, mRfmAppId);
    }

    /**
     * Utility method to request Ad using RFM SDK API
     *
     */
    private void requestRFMAd() {
        if (!mAdView.requestRFMAd(mAdRequest)) {
            Log.i(LOG_TAG, "ad request denied");
        } else {
            Log.i(LOG_TAG, "ad request accepted, waiting for ad");
        }
    }

    /**
     * Utility method to create Ad view listener
     */
    public void setRFMAdViewListener() {
        if (mAdView != null) {
            mAdView.setRFMAdViewListener(new RFMAdViewListener() {

                public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
                    mAdView.setVisibility(View.GONE);
                    Log.i(LOG_TAG, "RFM Ad: Requesting Url:" + requestUrl);
                }

                public void onAdReceived(RFMAdView adView) {
                    Log.i(LOG_TAG, "RFM Ad: Received");
                    mAdView.setVisibility(View.VISIBLE);
                }

                public void onAdFailed(RFMAdView adView) {
                    mAdView.setVisibility(View.INVISIBLE);
                    Log.i(LOG_TAG, "RFM Ad: Failed");
                }

                public void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event) {
                    switch (event) {
                        case FULL_SCREEN_AD_DISPLAYED:
                            Log.i(LOG_TAG, "RFM Ad: Full screen ad displayed");
                            break;

                        case FULL_SCREEN_AD_DISMISSED:
                            Log.i(LOG_TAG, "RFM Ad: Full screen ad dismissed");
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
                    Log.i(LOG_TAG, "RFM Ad: resized to width " + arg1 + ", height = " + arg2);
                }

                @Override
                public void didDisplayAd(RFMAdView arg0) {
                    Log.i(LOG_TAG, "RFM Ad: displayed ");
                }

                @Override
                public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
                    Log.i(LOG_TAG, "RFM Ad: Could not be displayed ");
                }

            });
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null)
            mAdView.rfmAdViewDestroy();

        super.onDestroy();

    }

}
