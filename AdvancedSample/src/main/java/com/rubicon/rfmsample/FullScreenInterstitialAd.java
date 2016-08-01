/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMInterstitialAd;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.util.RFMLog;

public class FullScreenInterstitialAd extends BaseActivity {

	private final String LOG_TAG = "FullScreenInterstitial";
	private Button displayButton;
	private RFMInterstitialAd rfmInterstitialAd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreendemointerstitial);

		// display button used by the sample app
		displayButton = (Button) findViewById(R.id.displayad);
		if (displayButton != null) {
			// This will be displayed only for Cached Ads
			displayButton.setVisibility(View.GONE);
			displayButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					displayButton.setVisibility(View.GONE);
					rfmInterstitialAd.show();
				}
			});
		}

		// Create object of RFMInterstitialAd
		rfmInterstitialAd = new RFMInterstitialAd(this);
		// Create Interstitial Ad Request
		createInterstitialRequest();
		// Add a listener
		setRFMAdViewListener();

		setLoadAdAction();
	}

	@Override
	public void onDestroy() {
		//Required for RFM SDK integration
		if (mAdView != null) {
			mAdView.rfmAdViewDestroy();
		}
		if(rfmInterstitialAd != null) {
			rfmInterstitialAd.setRFMInterstitialAdListener(null);
			rfmInterstitialAd.reset();
			rfmInterstitialAd = null;
		}
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

	/**
	 * Implementation of abstract method to request Ad
	 */
	@Override
	public void loadAd() {
		appendTextToConsole("\nRequesting Ad from RFM SDK");

		mAdRequest.setRFMAdAsInterstitial(mFullscreenMode);

		if(mVideoAdMode) {
			// Fetch video ads and force VAST type Video ads
			mAdRequest.fetchVideoAds(true);
		} else {
			mAdRequest.fetchVideoAds(false);
		}

		boolean success;
		if (mCachedAdMode) {
			// For interstitial ads requestRFMAd function will also cache the ad
			displayButton.setText(R.string.displaycachedad);
			success = rfmInterstitialAd.requestCachedRFMAd(mAdRequest);
		} else {
			displayButton.setText(R.string.displayad);
			success = rfmInterstitialAd.requestRFMAd(mAdRequest);
		}

		if (success) {
			appendTextToConsole("ad request accepted, waiting for ad");
		} else {
			appendTextToConsole("ad request denied");
		}

		mNumberOfRequests = mNumberOfRequests + 1;
		updateCountersView();
	}

	// Utility method to initialize RFM Ad request parameters
	public void createInterstitialRequest() {
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

	// Utility method to set RFM AdView listener
	public void setRFMAdViewListener() {
		if (rfmInterstitialAd == null) {
			return;
		}

		rfmInterstitialAd.setRFMInterstitialAdListener(new RFMInterstitialAdViewListener() {

			public void onAdRequested(RFMAdView adView, String requestUrl,boolean adRequestSuccess) {
				appendTextToConsole("RFM Ad: Requesting Url:"+requestUrl);
			}

			public  void onAdReceived(RFMAdView adView) {
				appendTextToConsole("RFM Ad: Received");
				displayButton.setVisibility(View.VISIBLE);
			}

			public  void onAdFailed(RFMAdView adView){
				appendTextToConsole("RFM Ad: Failed");

				mNumberOfFailures = mNumberOfFailures + 1;
				updateCountersView();
			}

			public void onInterstitialAdWillDismiss(RFMAdView adView) {
				appendTextToConsole("RFM Ad: Interstitial will dismiss");
			}

			public void onInterstitialAdDismissed(RFMAdView adView) {
				appendTextToConsole("RFM Ad: Interstitial ad dismissed");
			}

			@Override
			public void onAdStateChangeEvent(RFMAdView arg0, RFMAdViewEvent event) {
				switch(event) {
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
				appendTextToConsole("RFM Ad: Resized ");
			}

			@Override
			public void didDisplayAd(RFMAdView arg0) {
				if(RFMLog.canLogVerbose()) {
					Log.v(LOG_TAG, "Into didDisplayAd");
				}

				appendTextToConsole("RFM Ad: Ad displayed");
				mNumberOfSuccess = mNumberOfSuccess + 1;
				updateCountersView();
			}

			@Override
			public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
				appendTextToConsole("RFM Ad: Failed to display Ad");
				mNumberOfFailures = mNumberOfFailures + 1;
				updateCountersView();
			}
		});

	}

}
