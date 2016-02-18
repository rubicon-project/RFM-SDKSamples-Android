/*
 * Copyright (C) 2012 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 * 
 */

package com.rubicon.rfmsample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class built on base adapter for providing data to be displayed in list items.
 * Each list item view has a title and description for the demo test case.
 */
public class InfoListAdapter extends BaseAdapter {

	Activity context;
	String description = "Rubicon Project ";
	String adSizeStr;
	boolean firstTimeAdrequested = false;
	private RFMAdView mBanner;
	private RFMAdRequest mAdRequest;
	private SharedPreferences sharedPrefs;
	private String rfmServer;
	private String rfmAppId;
	private String rfmPubId;
	private String rfmAdId;
	private boolean rfmAdTestMode;
	private static String LOG_TAG = "InfoListAdapter";
	protected RFMAdViewListener adViewListener;
	private int TYPE_NOT_AD = 0;
	private int TYPE_AD = 1;
	static int AD_ROW_1 = 2;
	static int AD_ROW_2 = 20;
	static int AD_ROW_3 = 30;
	static int AD_ROW_4 = 50;
	static int AD_ROW_5 = 70;
	static int AD_ROW_6 = 90;
	AdViewHolder adViewHolder;
	private List<String> data = new ArrayList<>();

	public InfoListAdapter(Activity context, int listSize, String adSizeStrVal, RFMAdViewListener _adViewListener, RFMAdRequest _adRequest) {
		super();

		for (int i=0; i < listSize; i++)
			data.add("Demo Cell " + i);

		this.context = context;
		adSizeStr = adSizeStrVal;
		if(_adRequest != null) {
			mAdRequest = _adRequest;
		} else {
			mAdRequest = new RFMAdRequest();
		}
		adViewListener = _adViewListener;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == AD_ROW_1 || position == AD_ROW_2
				|| position == AD_ROW_3 || position == AD_ROW_4
				|| position == AD_ROW_5 || position == AD_ROW_6) {
			return TYPE_AD;
		} else {
			return TYPE_NOT_AD;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2; // Count of different layouts
	}

	private static class NonAdViewHolder {
		public TextView mainText;
		public TextView descriptionText;
	}

	private static class AdViewHolder {
		public RFMAdView rfmAdView;
	}

	/* (non-Javadoc)
 	* @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
 	*/
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int viewType = this.getItemViewType(position);

		switch(viewType)
		{
			// non ad type publisher list item
			case 0:

				NonAdViewHolder nonAdViewHolder;

				View v = convertView;
				if (v == null) {
					LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.list_item, parent, false);

					nonAdViewHolder = new NonAdViewHolder();
					nonAdViewHolder.mainText = (TextView) v.findViewById(R.id.maintitle);
					nonAdViewHolder.descriptionText = (TextView) v.findViewById(R.id.description);
					v.setTag(nonAdViewHolder);
				} else {
					nonAdViewHolder = (NonAdViewHolder) v.getTag();
				}

				// set up the list item
				if (nonAdViewHolder.mainText != null) {
					nonAdViewHolder.mainText.setText(data.get(position));
				}
				if (nonAdViewHolder.descriptionText != null) {
					nonAdViewHolder.descriptionText.setText(description + position);
				}

				// return the created view
				return v;

			// ad type list item
			case 1:

				AdViewHolder adViewHolder;
				View v2 = convertView;
				if (v2 == null) {
					LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v2 = vi.inflate(R.layout.list_item_ad, parent, false);

					adViewHolder = new AdViewHolder();
					adViewHolder.rfmAdView = (RFMAdView) v2.findViewById(R.id.adview);
					v2.setTag(adViewHolder);
				}
				else {
					adViewHolder = (AdViewHolder) v2.getTag();
				}

				// set up the list item
				if (adViewHolder.rfmAdView != null) {
					mBanner = adViewHolder.rfmAdView;
					LayoutParams bannerLayout = (LayoutParams) mBanner.getLayoutParams();
					bannerLayout.width = LinearLayout.LayoutParams.FILL_PARENT;
					bannerLayout.height = (int)(50* BannerInList.dm.density);
					bannerLayout.gravity = Gravity.CENTER;
					mBanner.setLayoutParams(bannerLayout);
					if(mAdRequest != null) {
						mAdRequest.setAdDimensionParams(-1, 50);
					}
					mBanner.setClickable(true);
					mBanner.requestFocus();
					adViewHolder.rfmAdView = mBanner;
				}
				if(!firstTimeAdrequested) {
					firstTimeAdrequested = true;
					this.requestAd();
				}
				// return the created view
				return v2;
			default:
				return null;
		}

	}

	public RFMAdView getAdview() {
		return mBanner;
	}

	protected boolean requestAd() {
		initAdView();
		Log.v(LOG_TAG, "Requesting Ad with Server " + mAdRequest.getRFMServerName() + " PUB " + mAdRequest.getRFMPubId() + " appId " + mAdRequest.getRFMAppId());
		addListener();
		// Location info
		Log.v(LOG_TAG, "Requesting Ad with Server " + mAdRequest.isLocationConfigured() + " Location " + mAdRequest.getLocation() + " Type = " + mAdRequest.getLocationDetectType());

		mBanner.setVisibility(View.GONE);
		return mBanner.requestRFMAd(mAdRequest);
	}

	private void initAdView() {
		if(sharedPrefs == null) {
			sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		rfmAdId=sharedPrefs.getString("rfmad_id", "0");
		rfmServer = sharedPrefs.getString("rfmserver_name", "Empty");
		rfmPubId= sharedPrefs.getString("rfmpub_id", SamplePreferences.DEFAULT_PUB_ID);
		rfmAppId = sharedPrefs.getString("rfmapp_id", SamplePreferences.DEFAULT_APP_ID);
		rfmAdTestMode = sharedPrefs.getBoolean("rfmtest_mode", false);

		mBanner.enableHWAcceleration(true);
		mBanner.setBackgroundColor(Color.TRANSPARENT);

		if(rfmAdTestMode) {
			mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
		}

		if(!"0".equalsIgnoreCase(rfmAdId))
			mAdRequest.setRFMTestAdId(rfmAdId);

		Log.v(LOG_TAG, " Setting Information from APP "+rfmServer+" PUB "+rfmPubId+ " appId "+rfmAppId );
		mAdRequest.setRFMParams(rfmServer,rfmPubId,rfmAppId);
	}

	private void addListener() {
		//Optional listener for RFMAd status
		if(adViewListener != null) {
			mBanner.setRFMAdViewListener(adViewListener);
			return;
		}
		// If AdViewListener is not available add one
		if(mBanner != null){
			mBanner.setRFMAdViewListener(new RFMAdViewListener(){

				public void onAdRequested(RFMAdView adView, String requestUrl,boolean adRequestSuccess){
					Log.v(LOG_TAG, "onAdRequested being invoked");
				}

				public void onAdReceived(RFMAdView adView){
					Log.v(LOG_TAG, "Ad Received");
					mBanner.setVisibility(View.VISIBLE);
					mBanner.displayAd();
					InfoListAdapter.this.notifyDataSetChanged();
				}

				public  void onAdFailed(RFMAdView adView){
					mBanner.setVisibility(View.GONE);
				}

				public  void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event){
					switch(event){
						case FULL_SCREEN_AD_DISPLAYED:
							break;
						case FULL_SCREEN_AD_DISMISSED:
							break;
						default:
							break;
					}
				}

				@Override
				public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					Log.v(LOG_TAG, "RFM Ad: resized to width " + arg1 + ", height = " + arg2);

				}

				@Override
				public void didDisplayAd(RFMAdView arg0) {
					Log.v(LOG_TAG, "RFM Ad: displayed ");
				}

				@Override
				public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
					Log.v(LOG_TAG, "RFM Ad: Could not be displayed ");
				}

			});
		}
	}

	public void destroyAdView() {
		if(mBanner != null) {
			mBanner.rfmAdViewDestroy();
		}
		if(adViewListener != null) {
			adViewListener = null;
		}
	}
}
