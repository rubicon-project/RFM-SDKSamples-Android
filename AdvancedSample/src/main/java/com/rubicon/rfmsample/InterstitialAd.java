/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMException;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.util.RFMLog;

public class InterstitialAd extends BaseActivity {

	private final String LOG_TAG = "InterstitialAd";
    private Button displayButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.demointerstitial);

		displayButton = (Button) findViewById(R.id.displayad);
        if (mAdView == null) {
			mAdView = (RFMAdView) findViewById(R.id.interstitialviewone);
			// This is to enable HW acceleration for Videos
			mAdView.enableHWAcceleration(true);

			//UI control for Sample App
			mAdView.setVisibility(View.GONE);
			mAdView.setBackgroundColor(Color.TRANSPARENT);
			mAdView.setScrollContainer(true);
        }

 		if (displayButton != null) {
			// This will be displayed only for Cached Ads
			displayButton.setVisibility(View.GONE);
			displayButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					displayButton.setVisibility(View.GONE);
					handleAdVisibility();
				}
			});
		}

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
		//createInterstitialRequest();
		// set this to have a fullscreen vast and publisher video playback
		mAdView.setFullScreenForInterstitial(mFullscreenMode);

		if(mVideoAdMode) {

			//mAdView.setFullScreenForInterstitial(true);

			// If the placement type is banner or interstitial
			// the publisher app can use this api to get a VAST ad
			// If the placement is in-stream this call is not required
			// the boolean argument is to stop the VAST ad video auto play behaviour
			mAdRequest.fetchVideoAds(true);
			//mAdRequest.setVideoContent(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"), RFMConstants.RFM_VIDEO_PREROLL);
			//mAdRequest.setRFMAdAsVideo();
		} else {
			mAdRequest.fetchVideoAds(false);
		}


		boolean success;
		if (mCachedAdMode) {
			// Invoke request as per user selection
			success = mAdView.requestCachedRFMAd(mAdRequest);
		} else {
			success = mAdView.requestRFMAd(mAdRequest);
		}

		if (success) {
			appendTextToConsole("ad request accepted, waiting for ad");
		} else {
			appendTextToConsole("ad request denied");
		}

		mNumberOfRequests = mNumberOfRequests + 1;
		updateCountersView();
	}

	/**
	 * Utility method to initialize RFM Ad request parameters
	 */
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

	/**
	 * Utility method to set RFM AdView listener
	 */
	public void setRFMAdViewListener() {
		// Optional listener for RFMAd status
		if (mAdView != null) {

			mAdView.setRFMAdViewListener(new RFMInterstitialAdViewListener() {

				public void onAdRequested(RFMAdView adView, String requestUrl,boolean adRequestSuccess) {
					appendTextToConsole("RFM Ad: Requesting Url:"+requestUrl);
				}

				/*
                    * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
                    * visible if it has been set to invisible till this time.
                    *
                    * @param adView - RFMAdView instance that generated this method
                    *
                    */
				public  void onAdReceived(RFMAdView adView) {
					appendTextToConsole("RFM Ad: Received");
					if(mCachedAdMode) {
						// If it is a Cached Ad allow display Button to handle visibility
						displayButton.setVisibility(View.VISIBLE);
					} else {
						handleAdVisibility();
					}
				}

				/*
                 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
                 * if it was set to visible.
                 *
                 * @param adView - RFMAdView instance that generated this method
                 *
                 */
				public  void onAdFailed(RFMAdView adView){
					appendTextToConsole("RFM Ad: Failed");

					mNumberOfFailures = mNumberOfFailures + 1;
					updateCountersView();
//					if(mAdRequest.getVideoPlaybackUri() != null) {
//						mAdView.setVisibility(View.VISIBLE);
//					}
				}

				public void onInterstitialAdWillDismiss(RFMAdView adView) {
					appendTextToConsole("RFM Ad: Interstitial will dismiss");
				}

				public void onInterstitialAdDismissed(RFMAdView adView) {
					mAdView.setVisibility(View.GONE);
					appendTextToConsole("RFM Ad: Interstitial ad dismissed");
				}

				@Override
				public void onAdStateChangeEvent(RFMAdView arg0, RFMAdViewEvent event) {
					// For interstitial Ad this may not be exactly applicable
					switch(event){
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
						Log.v(LOG_TAG, " Into didDisplayAd ");
					}

					appendTextToConsole("RFM Ad: Ad displayed ");
					mNumberOfSuccess = mNumberOfSuccess + 1;
					updateCountersView();
				}

				@Override
				public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
					appendTextToConsole("RFM Ad: Failed to display Ad ");
					mNumberOfFailures = mNumberOfFailures + 1;
					updateCountersView();
				}
			});
		}
	}

    private void handleAdVisibility() {
    	if (mAdView.isAdAvailableToDisplay()) {
			mAdView.setVisibility(View.VISIBLE);
 			if (mCachedAdMode) {
	        	try {
					mAdView.showCachedAd();
				} catch (RFMException e) {
					e.printStackTrace();
					Log.v(LOG_TAG, "Error Code = "+e.getExceptionCode()+" | Error Message = "+e.getMessage());
				}
 			}
 		} else {
 			appendTextToConsole("RFM Ad: Ad is not available to display");
 		}
    }
}
