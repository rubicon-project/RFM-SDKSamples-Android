/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMRewardedVideo;

public class RewardedVideoAd extends BaseActivity {

    private Button displayButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreendemointerstitial);

        // Create Rewarded Ad Request
        createInterstitialRequest();

        setLoadAdAction();

        // display button used by the sample app
        displayButton = (Button) findViewById(R.id.displayad);
        if (displayButton == null)
            return;

        displayButton.setVisibility(View.GONE);
        displayButton.setText(R.string.displaycachedad);
        displayButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                displayButton.setVisibility(View.GONE);

                RFMRewardedVideo.show(mRfmAppId, new RFMRewardedVideo.ShowRewardedVideoListener() {
                    @Override
                    public void onRewardedVideoAdStarted(String appId) {
                        appendTextToConsole("RFM Ad: Video Ad started");
                    }

                    @Override
                    public void onRewardedVideoAdPlaybackError(String appId, String errorMessage) {
                        appendTextToConsole("RFM Ad: Ad error " + errorMessage);
                    }

                    @Override
                    public void onRewardedVideoAdCompleted(String appId, String reward) {
                        appendTextToConsole("RFM Ad: Ad completed with reward "+reward);
                        mNumberOfSuccess = mNumberOfSuccess + 1;
                        updateCountersView();
                    }

                    @Override
                    public void onRewardedVideoAdClosed(String appId) {
                        appendTextToConsole("RFM Ad: Ad closed");
                    }
                });

            }
        });
    }

    @Override
    public void onDestroy() {
        RFMRewardedVideo.destroy(mRfmAppId);
        RFMRewardedVideo.destroyAll();
        super.onDestroy();
    }

    /**
     * Implementation of abstract method to apply size change
     **/
    @Override
    public void updateAdView() {
        if (mFullscreenMode) {
            setAdRequestSize(-1, -1);
        } else {
            setAdRequestSize(mAdWidth, mAdHeight);
        }
    }

    // Implementation of abstract method to request Ad
    @Override
    public void loadAd() {
        appendTextToConsole("\nRequesting Ad from RFM SDK");

        mAdRequest.setRFMAdAsInterstitial(mFullscreenMode);

        if (mVideoAdMode) {
            // Fetch video ads and force VAST type Video ads
            mAdRequest.fetchVideoAds(true);
        } else {
            mAdRequest.fetchVideoAds(false);
        }

        // Request RewardedVideo Ad
        boolean success = RFMRewardedVideo.requestAd(this, mAdRequest, new RFMRewardedVideo.LoadRewardedVideoListener() {
            @Override
            public void onRewardedVideoAdLoadSuccess(String appId) {
                displayButton.setVisibility(View.VISIBLE);
                appendTextToConsole("RFM Ad: Received");
            }

            @Override
            public void onRewardedVideoAdLoadFailure(String appId, String errorMessage) {
                appendTextToConsole("RFM Ad: Failed " + errorMessage);
                mNumberOfFailures = mNumberOfFailures + 1;
                updateCountersView();
            }
        });

        if (success) {
            appendTextToConsole("ad request accepted, waiting for ad");
        } else {
            appendTextToConsole("ad request denied");
        }

        mNumberOfRequests = mNumberOfRequests + 1;
        updateCountersView();
    }

    private void createInterstitialRequest() {
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
        mAdRequest.setRFMAdAsInterstitial(mFullscreenMode);

        updateAdView();
    }

}
