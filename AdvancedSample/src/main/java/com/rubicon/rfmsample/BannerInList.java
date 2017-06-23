/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;

public class BannerInList extends BaseActivity {

	private final String LOG_TAG = "BannerInList";
	private ListView mListView = null;
	private InfoListAdapter infoAdapter = null;
	private String sizeParams = "fillparent x 50dp";
	static DisplayMetrics dm = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demolistview);

		dm = fetchScreenSize(this);

		mListView = (ListView) findViewById(R.id.listobj);
		mListView.setVisibility(View.VISIBLE);

		if (mAdRequest == null) {
			mAdRequest = new RFMAdRequest();
		}

		if (mRfmAdTestMode)
			mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);

		if (!"0".equalsIgnoreCase(mRfmAdId))
			mAdRequest.setRFMTestAdId(mRfmAdId);
		mAdRequest.setRFMParams(mRfmServer, mRfmPubId, mRfmAppId);

		infoAdapter = new InfoListAdapter(this, 100, sizeParams, new AdRFMAdViewListener(), mAdRequest);
		mListView.setAdapter(infoAdapter);
		mAdView = infoAdapter.getAdview();

		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				final ListView listView = mListView;

				if (scrollState == SCROLL_STATE_IDLE) {

					Log.d(LOG_TAG, "onScrollStateChanged  first " + listView.getFirstVisiblePosition() + "  last  " + listView.getLastVisiblePosition());

					// This window is screen's visible area, if required you can move the window above or below the viewable screen to have the ad ready
					if (infoAdapter != null &&
							(InfoListAdapter.AD_ROW_1 > listView.getFirstVisiblePosition() && InfoListAdapter.AD_ROW_1 < listView.getLastVisiblePosition())
							|| (InfoListAdapter.AD_ROW_2 > listView.getFirstVisiblePosition() && InfoListAdapter.AD_ROW_2 < listView.getLastVisiblePosition())
							|| (InfoListAdapter.AD_ROW_3 > listView.getFirstVisiblePosition() && InfoListAdapter.AD_ROW_3 < listView.getLastVisiblePosition())
							|| (InfoListAdapter.AD_ROW_4 > listView.getFirstVisiblePosition() && InfoListAdapter.AD_ROW_4 < listView.getLastVisiblePosition())
							|| (InfoListAdapter.AD_ROW_5 > listView.getFirstVisiblePosition() && InfoListAdapter.AD_ROW_5 < listView.getLastVisiblePosition())
							|| (InfoListAdapter.AD_ROW_6 > listView.getFirstVisiblePosition() && InfoListAdapter.AD_ROW_6 < listView.getLastVisiblePosition())
							) {

						Log.d(LOG_TAG, "onScrollStateChanged requesting ad");

						if (infoAdapter.getAdview() != null && !infoAdapter.requestAd()) {
							appendTextToConsole("ad request denied");
						} else {
							appendTextToConsole("ad request accepted, waiting for ad");
						}
					}

				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}

		});

		setLoadAdAction();
	}

	@Override
	public void loadAd() {

	}

	@Override
	public void updateAdView() {
		if (mAdWidth != 0)
			mAdView.getLayoutParams().width = mAdWidth * mDisplayDensity;
		if (mAdHeight != 0) {
			if (mAdHeight == -1) {
				mAdView.getLayoutParams().height = mDisplayHeight;
				// will help to redraw view after setting it to device screen size
				//loadAd();
			} else {
				mAdView.getLayoutParams().height = mAdHeight * mDisplayDensity;
			}
		}
	}

	private static DisplayMetrics fetchScreenSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics;
	}


	private class AdRFMAdViewListener implements RFMAdViewListener {
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
         *
         */
		public void onAdRequested(String requestUrl, boolean adRequestSuccess) {
			Log.v(LOG_TAG, "onAdRequested being invoked");
			appendTextToConsole("RFM Ad: Requesting Url:" + requestUrl);
			if (BannerInList.this.mAdView == null) {
				BannerInList.this.mAdView = BannerInList.this.infoAdapter.getAdview();
			}
		}

		/*
		* Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
		* visible if it has been set to invisible till this time.
		*
		* @param adView - RFMAdView instance that generated this method
		*
		*/
		public void onAdReceived(RFMAdView adView) {
			Log.v(LOG_TAG, "Ad Received");
			appendTextToConsole("RFM Ad: Received");
			if (BannerInList.this.mAdView == null) {
				BannerInList.this.mAdView = BannerInList.this.infoAdapter.getAdview();
			}
			BannerInList.this.mAdView.setVisibility(View.VISIBLE);
			//BannerInList.this.mAdView.displayAd();
		}

		/*
		 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
		 * if it was set to visible.
		 * 
		 * @param adView - RFMAdView instance that generated this method
		 * 
		 */
		public void onAdFailed(RFMAdView adView) {
			appendTextToConsole("RFM Ad: Failed");
			if (BannerInList.this.mAdView == null) {
				BannerInList.this.mAdView = BannerInList.this.infoAdapter.getAdview();
			}
			BannerInList.this.mAdView.setVisibility(View.GONE);
		}

		/*
		 * Sent when an user interaction with ad results in a transition in view type from banner to full screen
		 * landing view or vice-versa
		 * 
		 * @param adView - RFMAdView instance that generated this method
		 * @param event - User interaction event of type RFMAdViewEvent
		 */
		public void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event) {
			switch (event) {
				case FULL_SCREEN_AD_DISPLAYED:
					Log.v(LOG_TAG, "FULL SCREEN Ad Displayed ");
					break;

				case FULL_SCREEN_AD_DISMISSED:
					Log.v(LOG_TAG, "FULL SCREEN Ad Dismissed ");
					break;

				case AD_CLICKED:
					Log.v(LOG_TAG, "Ad clicked ");
					break;

				default:
					break;
			}
		}

		@Override
		public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
			Log.v(LOG_TAG, "RFM Ad: resized to width " + arg1 + ", height = " + arg2);
			appendTextToConsole("RFM Ad: Ad Resized");
		}

		@Override
		public void didDisplayAd(RFMAdView arg0) {
			Log.v(LOG_TAG, "RFM Ad: Ad displayed");
			appendTextToConsole("RFM Ad: Ad displayed");
		}

		@Override
		public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
			appendTextToConsole("RFM Ad: Failed to display");
			Log.v(LOG_TAG, "RFM Ad: Could not be displayed ");
		}

	}

	@Override
	public void onDestroy() {
		RFMAdView.clearAds();
		super.onDestroy();
	}
}
