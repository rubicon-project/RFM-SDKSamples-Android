package com.rubicon.rfmsample;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;

public class BannerInList extends BaseActivity {

	private ListView mListView = null;
	private Button getDemoAd = null;
	private InfoListAdapter infoAdapter = null;
	public static DisplayMetrics dm = null;
	private Button mClearConsoleButton;
	private Button mGetLocationButton;
	private TextView mDebugConsoleView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demolistview);

		LOG_TAG = "BannerInList";
		dm = fetchScreenSize(this);

		mListView = (ListView) findViewById(R.id.listobj);
		mListView.setVisibility(View.VISIBLE);
		mDebugConsoleView = (TextView) findViewById(R.id.debugconsoleviewone);
		mDebugConsoleView.setBackgroundColor(Color.WHITE);
		mDebugConsoleView.setMovementMethod(new ScrollingMovementMethod());

		if (mAdRequest == null) {
			mAdRequest = new RFMAdRequest();
		}

		configureAdFromPrefs();
		getDemoAd = (Button) findViewById(R.id.getadbuttonone);
		// hidden as we are fetching ads when the list stops scrolling
		getDemoAd.setVisibility(View.GONE);
		getDemoAd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (infoAdapter == null) {
					return;
				}

				appendTextToConsole("\n\n****Get Ad Clicked****");
				appendTextToConsole("Requesting Ad from RFM SDK");

				if (infoAdapter.getAdview() != null && !infoAdapter.requestAd()) {
					appendTextToConsole("ad request denied");
				} else {
					appendTextToConsole("ad request accepted, waiting for ad");
				}
			}
		});

		mClearConsoleButton = (Button) findViewById(R.id.clearconsoleone);
		mClearConsoleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearDebugConsole();
			}
		});

		//Obtain device location. Use fixed lat/long from settings menu if set.
		mGetLocationButton = (Button) findViewById(R.id.getlocationone);
		mGetLocationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				configureLocation();
			}
		});

		infoAdapter = new InfoListAdapter(this, 100, sizeParams, new AdRFMAdViewListener(), mAdRequest);
		mListView.setAdapter(infoAdapter);
		adView = infoAdapter.getAdview();

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

	}

	private void clearDebugConsole() {
		mDebugConsoleView.setText("RFM Sample App Ad Status \n--------------------------------\n");
		mDebugConsoleView.bringPointIntoView(0);
	}

	public static DisplayMetrics fetchScreenSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics;
	}

	public void updateLocationInfo(String locationData) {
		appendTextToConsole(locationData);
	}

	private void appendTextToConsole(String data) {
		mDebugConsoleView.append(data + "\n");
		Log.d(LOG_TAG, data);
		mDebugConsoleView.post(new Runnable() {
			public void run() {
				final int scrollAmount = mDebugConsoleView.getLayout().getLineTop(mDebugConsoleView.getLineCount())
						- mDebugConsoleView.getHeight();
				// if there is no need to scroll, scrollAmount will be <=0
				if (scrollAmount > 0)
					mDebugConsoleView.scrollTo(0, scrollAmount);
				else
					mDebugConsoleView.scrollTo(0, 0);
			}
		});
	}

	public void configureAdFromPrefs() {
		// Set Orientation Params
		configureOrientation();

		// Set Location Params
		configureLocation();
	}

	class AdRFMAdViewListener implements RFMAdViewListener {
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
		public void onAdRequested(RFMAdView adView, String requestUrl, boolean adRequestSuccess) {
			Log.v(LOG_TAG, "onAdRequested being invoked");
			appendTextToConsole("RFM Ad: Requesting Url:" + requestUrl);
			if (BannerInList.this.adView == null) {
				BannerInList.this.adView = BannerInList.this.infoAdapter.getAdview();
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
			if (BannerInList.this.adView == null) {
				BannerInList.this.adView = BannerInList.this.infoAdapter.getAdview();
			}
			BannerInList.this.adView.setVisibility(View.VISIBLE);
			BannerInList.this.adView.displayAd();
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
			if (BannerInList.this.adView == null) {
				BannerInList.this.adView = BannerInList.this.infoAdapter.getAdview();
			}
			BannerInList.this.adView.setVisibility(View.GONE);
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
				default:
					break;
			}
		}

		@Override
		public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			Log.v(LOG_TAG, "RFM Ad: resized to width " + arg1 + ", height = " + arg2);
			appendTextToConsole("RFM Ad: Ad Resized");
		}

		@Override
		public void didDisplayAd(RFMAdView arg0) {
			Log.v(LOG_TAG, "RFM Ad: displayed ");
			appendTextToConsole("RFM Ad: Displayed");
		}

		@Override
		public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
			appendTextToConsole("RFM Ad: Failed to display");
			Log.v(LOG_TAG, "RFM Ad: Could not be displayed ");
		}

	}


	@Override
	public void onDestroy() {
//		if (infoAdapter != null) {
//			infoAdapter.destroyAdView();
//		}
		RFMAdView.clearAds();
		super.onDestroy();
	}

}
