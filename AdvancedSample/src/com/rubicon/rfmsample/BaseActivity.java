package com.rubicon.rfmsample;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.util.RFMLog;


public abstract class BaseActivity extends Activity {
    //variables for holding preference settings
    protected SharedPreferences sharedPrefs; 
    protected String rfmServer;
    protected String rfmAppId;
    protected String rfmPubId;
    protected String rfmAdId;
    protected boolean rfmAdTestMode;    
    protected double rfmFixedLocationLatitude;
    protected double rfmFixedLocationLongitude;
    protected String sizeParams=SamplePreferences.ADSIZE_FILLPARENT_50;
    protected String locationParams=SamplePreferences.LOCATION_IP;
    protected String orientationParams= SamplePreferences.ORIENTATION_ALL;
    
    //variables for obtaining device location
    protected LocationManager mLocationManager;
    protected Location mCurrentLocation;
    protected String bestLocationProvider;
    protected String LOG_TAG = "BaseActivity";
    protected RFMAdView adView;
    protected RFMAdRequest mAdRequest;
    
    public void onCreate(Bundle savedInstance) {
    	super.onCreate(savedInstance);
        if(sharedPrefs == null) {
        	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            rfmAdId=sharedPrefs.getString(SamplePreferences.PREF_RFMADID, "0");
            rfmServer = sharedPrefs.getString(SamplePreferences.PREF_SERVERNAME, "Empty");
            rfmPubId= sharedPrefs.getString(SamplePreferences.PREF_RFMPUBID, SamplePreferences.DEFAULT_PUB_ID);
            rfmAppId = sharedPrefs.getString(SamplePreferences.PREF_RFMAPPID, SamplePreferences.DEFAULT_APP_ID);
            rfmAdTestMode = sharedPrefs.getBoolean(SamplePreferences.PREF_RFMTEST_MODE, true);
            sizeParams = sharedPrefs.getString(SamplePreferences.PREF_ADSIZE, SamplePreferences.ADSIZE_FILLPARENT_50);
            locationParams = sharedPrefs.getString(SamplePreferences.PREF_LOCATION, SamplePreferences.LOCATION_GPS);
            orientationParams = sharedPrefs.getString(SamplePreferences.PREF_ORIENTATION, SamplePreferences.ORIENTATION_ALL);
         }
        String fixedLat = sharedPrefs.getString(SamplePreferences.PREF_LOCATION_LAT,"0.0f");
        String fixedLng = sharedPrefs.getString(SamplePreferences.PREF_LOCATION_LONG,"0.0f");
		// Initialize objects required for GPS location
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		bestLocationProvider = mLocationManager.getBestProvider(new Criteria(), false);

        rfmFixedLocationLatitude = Float.valueOf(fixedLat);
        rfmFixedLocationLongitude = Float.valueOf(fixedLng);
    }

	// Abstract method to set Ad parameter, make sure mAdview and mAdRequest are initialized
	public abstract void configureAdFromPrefs();

	/**
	 * Method to change AdView's Width and Height based on preferences
	 */
    protected void confureAdSize(){
    	
    	if(adView == null) {
    		return;
    	}
    	try {
			WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);

			if (sizeParams == null || adView == null) {
				Log.v(LOG_TAG, "Cannot configureAdSize, invalid Preferences");
				return;
			} else {
				Log.v(LOG_TAG, "Into configureAdSize, with sizeParams = " + sizeParams);
			}

			if (sizeParams.equalsIgnoreCase(SamplePreferences.ADSIZE_FILLPARENT_50)) {
				LayoutParams bannerLayout = (LayoutParams) adView.getLayoutParams();
				bannerLayout.width = LinearLayout.LayoutParams.FILL_PARENT;
				adView.setLayoutParams(bannerLayout);
				if (mAdRequest != null) {
					mAdRequest.setAdDimensionParams(-1, 50);
				}
			} else if (sizeParams.equalsIgnoreCase(SamplePreferences.ADSIZE_320_50)) {
				LayoutParams bannerLayout = (LayoutParams) adView.getLayoutParams();
				bannerLayout.width = (int) (320 * metrics.density);
				adView.setLayoutParams(bannerLayout);
				if (mAdRequest != null) {
					mAdRequest.setAdDimensionParams(320, 50);
				}
			} else if (sizeParams.equalsIgnoreCase(SamplePreferences.ADSIZE_300_100)) {
				LayoutParams bannerLayout = (LayoutParams) adView.getLayoutParams();
				bannerLayout.width = (int) (300 * metrics.density);
				bannerLayout.height = (int) (100 * metrics.density);
				adView.setLayoutParams(bannerLayout);
				if (mAdRequest != null) {
					mAdRequest.setAdDimensionParams(300, 100);
				}
			}
			adView.invalidate();
			adView.requestLayout();
		} catch (Exception e) {
			Log.v(LOG_TAG, "Problems while configuring layout size based on preferences "+e.getMessage());
			e.printStackTrace();
		}
    }

	/**
	 * Method to configure orientation property of Acitivty
	 */
    protected void configureOrientation() {
    	if(orientationParams ==null) {
    		Log.v(LOG_TAG, "Orientation params not provided, Activity will behave as defined in layout");
    		return;
    	}
		try {
			if (orientationParams.equalsIgnoreCase(SamplePreferences.ORIENTATION_ALL)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			} else if (orientationParams.equalsIgnoreCase(SamplePreferences.ORIENTATION_PORTRAIT)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (orientationParams.equalsIgnoreCase(SamplePreferences.ORIENTATION_LANDSCAPE)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} catch(Exception e) {
			Log.v(LOG_TAG, "Problems while configuring Orientation based on preferences "+e.getMessage());
			e.printStackTrace();
		}
    }

	/**
	 * Method to configure Location
	 */
	protected void configureLocation() {
		if(locationParams == null || mAdRequest ==null) {
			Log.v(LOG_TAG, "Cannot configureLocation, invalid Preferences");
			return;
		} else {
			Log.v(LOG_TAG, "Into configureLocation, with sizeParams = "+locationParams);
		}
		if(mLocationManager == null) {
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		try {
			if (locationParams.equalsIgnoreCase(SamplePreferences.LOCATION_FIXED)) {
				updateLocationInfo("Using Fixed Location from Settings: Lat: "+
						rfmFixedLocationLatitude+" Long: "+rfmFixedLocationLongitude);
				bestLocationProvider = mLocationManager.getBestProvider(new Criteria(), false);
				if (mCurrentLocation == null) {
					mCurrentLocation = new Location(bestLocationProvider);
				}
				mCurrentLocation.setLatitude(rfmFixedLocationLatitude);
				mCurrentLocation.setLongitude(rfmFixedLocationLongitude);
				mAdRequest.setLocationDetectType(RFMConstants.RFM_LOCATION_AUTO);
				mAdRequest.setLocation(mCurrentLocation);
				mAdRequest.setLocationConfigured(true);

			} else if (locationParams.equalsIgnoreCase(SamplePreferences.LOCATION_IP)) {
				//Optional API for setting IP-based location targeting by RFM sever.
				updateLocationInfo("Using IP Address " + getLocalIpAddress());
				mAdRequest.setLocationDetectType(RFMConstants.RFM_LOCATION_IP);
				mAdRequest.setLocationConfigured(false);
			} else if (locationParams.equalsIgnoreCase(SamplePreferences.LOCATION_GPS)) {
				// GPS location
				try {
					mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if (mCurrentLocation == null)
						mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				} catch (SecurityException e) {
					RFMLog.v(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Failed to get location, check location access");
					updateLocationInfo("Failed to get location, check location access " + e.getMessage());
				} catch (IllegalArgumentException e) {
					RFMLog.d(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Failed to get location, device has no GPS provider");
					updateLocationInfo("Failed to get location, device has no GPS provider " + e.getMessage());
				} catch (Exception e) {
					RFMLog.d(LOG_TAG, RFMLog.LOG_EVENT_ERROR, "Failed to get location, device has no GPS provider");
					updateLocationInfo("Failed to get location, error retrieving location " + e.getMessage());
				}
				if(mCurrentLocation != null) {
					updateLocationInfo("GPS Location : " + mCurrentLocation.getLatitude() + "::" + mCurrentLocation.getLongitude());
				} else {
					updateLocationInfo("Failed to get location!");
					Log.v(LOG_TAG, "Current Location is not yet set, please check if location is enabled on device");
				}

				mAdRequest.setLocationDetectType(RFMConstants.RFM_LOCATION_GPS);
				mAdRequest.setLocation(mCurrentLocation);
				mAdRequest.setLocationConfigured(true);

			} else {
				updateLocationInfo("location detect default");
				mAdRequest.setLocationConfigured(false);
				mAdRequest.setLocationDetectType(RFMAdRequest.RFM_LOCATION_DETECT_DEFAULT);
			}
		} catch(Exception e) {
			Log.v(LOG_TAG, "Problems while configuring Location based on preferences "+e.getMessage());
			e.printStackTrace();
		}
	}

	public String getLocalIpAddress() {
		try {
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress()) {
		                return inetAddress.getHostAddress().toString();
		            }
		        }
		    }
		} catch (SocketException ex) {
			Log.e(LOG_TAG, ex.toString());
		}
		return null;
	}

	public abstract void updateLocationInfo(String locationData);

	@Override
	protected void onDestroy() {
		super.onDestroy();

//		
//		
	}
}
