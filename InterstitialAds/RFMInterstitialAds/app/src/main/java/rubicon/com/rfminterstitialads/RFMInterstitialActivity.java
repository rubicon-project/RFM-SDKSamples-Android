/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 * SDK Version: RFM SDK 4.0.0
 * Sample code to integrate INTERSTITIAL Ad using RFM SDK
 */
package rubicon.com.rfminterstitialads;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMInterstitialAd;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.util.RFMLog;

public class RFMInterstitialActivity extends AppCompatActivity {

    private String LOG_TAG = "RFMInterstitialActivity";

    private RFMAdRequest mAdRequest;
    private RFMInterstitialAd mInterstitialAd;

    private String mRfmServer = "http://mrp.rubiconproject.com";
    private String mRfmPubId = "111008";
    private String mRfmAppId = "FB4FB140C13601320C4722000B3510F7";
    private String mRfmAdId = "28854";

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
        mInterstitialAd = new RFMInterstitialAd(this);
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

        mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
        if (!"0".equalsIgnoreCase(mRfmAdId)) {
            mAdRequest.setRFMTestAdId(mRfmAdId);
        }
        mAdRequest.setRFMParams(mRfmServer, mRfmPubId, mRfmAppId);
        mAdRequest.setRFMAdAsInterstitial(true);
    }

    /**
     * Utility method to request Ad using RFM SDK API
     *
     */
    private void requestRFMAd() {

        if (!mInterstitialAd.requestRFMAd(mAdRequest)) {
            Log.i(LOG_TAG, "ad request denied");
        } else {
            Log.i(LOG_TAG, "ad request accepted, waiting for ad");
        }
    }

    /**
     * Utility method to create Ad view listener
     */
    public void setRFMAdViewListener() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setRFMInterstitialAdListener(new RFMInterstitialAdViewListener() {

                @Override
                public void onAdRequested(String requestUrl, boolean requestsuccess) {
                    Log.i(LOG_TAG, "RFM Ad: Requesting Url:"+requestUrl);
                }

                @Override
                public  void onAdReceived(RFMAdView adView) {
                    Log.i(LOG_TAG, "RFM Ad: Received");
                    if (mInterstitialAd!= null) {
                        mInterstitialAd.show();
                    }
                }

                @Override
                public  void onAdFailed(RFMAdView adView){
                    Log.i(LOG_TAG, "RFM Ad: Failed");
                }

                @Override
                public void onInterstitialAdWillDismiss(RFMAdView adView) {
                    Log.i(LOG_TAG, "RFM Ad: Interstitial will dismiss");
                }

                @Override
                public void onInterstitialAdDismissed(RFMAdView adView) {
                    Log.i(LOG_TAG, "RFM Ad: Interstitial ad dismissed");
                }

                @Override
                public void onAdStateChangeEvent(RFMAdView arg0, RFMAdViewEvent event) {
                    // For interstitial Ad this may not be exactly applicable
                    switch(event){
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
                    Log.i(LOG_TAG, "RFM Ad: Resized ");
                }

                @Override
                public void didDisplayAd(RFMAdView arg0) {
                    if(RFMLog.canLogVerbose()) {
                        Log.v(LOG_TAG, " Into didDisplayAd ");
                    }

                    Log.i(LOG_TAG, "RFM Ad: Ad displayed ");
                }

                @Override
                public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
                    Log.i(LOG_TAG, "RFM Ad: Failed to display Ad ");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if(mInterstitialAd != null) {
            mInterstitialAd.setRFMInterstitialAdListener(null);
            mInterstitialAd.reset();
            mInterstitialAd = null;
        }

        super.onDestroy();

    }

}
