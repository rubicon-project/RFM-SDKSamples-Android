/*
 * Copyright (C) 2012 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 * 
 */
package com.rubicon.rfmsample;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.ui.mediator.RFMBaseMediator;
import com.rubicon.rfmsample.SimpleGestureFilter.SimpleGestureListener;

public class BannerOffScreen extends BaseActivity implements SimpleGestureListener {

	//View components
    private Button mGetAdButton;
    private TextView mDebugConsoleView;
    private Button mClearConsoleButton;
    private Button mGetLocationButton;
    private SimpleGestureFilter detector;

    private static final String LOG_TAG = "BannerOffScreen";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demooffscreen);
        // Detect touched area
        detector = new SimpleGestureFilter(this,this);
        //set layout elements
        mGetAdButton = (Button) findViewById(R.id.getadbuttonoffscreen);
        
        mDebugConsoleView = (TextView) findViewById(R.id.debugconsoleviewswipe);
        mDebugConsoleView.setBackgroundColor(Color.WHITE);
        mDebugConsoleView.setMovementMethod(new ScrollingMovementMethod());

        //Layout banner ad view
        if(adView == null){
      	  adView = (RFMAdView) findViewById(R.id.bannerviewoffscreen);
		  adView.setVisibility(View.GONE);
		  adView.setBackgroundColor(Color.TRANSPARENT);
        }

        //Set Ad Request parameters
        if(mAdRequest == null) 
    	   mAdRequest = new RFMAdRequest();
       
        if(rfmAdTestMode)
       		mAdRequest.setRFMAdMode(RFMConstants.RFM_AD_MODE_TEST);
       
        if(!"0".equalsIgnoreCase(rfmAdId))
       		mAdRequest.setRFMTestAdId(rfmAdId);

        mAdRequest.setRFMParams(rfmServer,rfmPubId,rfmAppId);
        
        //Set ad dimensions(dp) in float. use -1 for fill_parent
        //mAdRequest.setAdDimensionParams(-1, 50);
		configureAdFromPrefs();
        mGetAdButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
	            	appendTextToConsole("\n\n****Get Ad Clicked****");	                
	            	appendTextToConsole("Requesting Ad from RFM SDK");
	            	configureAdFromPrefs();
	            	adView.enableHWAcceleration(true);
	            	//Request Ad
	                if(!adView.requestRFMAd(mAdRequest))
	            	{
	                	appendTextToConsole("ad request denied");
	                }
	                else
	                {
	                	appendTextToConsole("ad request accepted, waiting for ad");
	                }
	            }
	        });

        
        mClearConsoleButton =(Button) findViewById(R.id.clearconsoleswipe);
        mClearConsoleButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) {
     			clearDebugConsole();
     		}
        });
        
        //Obtain device location. Use fixed lat/long from settings menu if set.
        mGetLocationButton =(Button) findViewById(R.id.getlocationoffscreen);
        mGetLocationButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) {
     			configureLocation();
     		}
        });
        
        
        //Optional listener for RFMAd status
        if(adView != null){
     	   adView.setRFMAdViewListener(new RFMAdViewListener(){
     		   
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
     		    public void onAdRequested(RFMAdView adView, String requestUrl,boolean adRequestSuccess){
     		    	BannerOffScreen.this.adView.setVisibility(View.GONE);
     		    	appendTextToConsole("RFM Ad: Requesting Url:"+requestUrl);
     		    	
     		    }
     		    
     		   /* 
     		    * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
     			* visible if it has been set to invisible till this time.
     			* 
     			* @param adView - RFMAdView instance that generated this method
     			*  
     			*/   
     			public  void onAdReceived(RFMAdView adView){
     		    	appendTextToConsole("RFM Ad: Received");
     				BannerOffScreen.this.adView.setVisibility(View.VISIBLE);
     		    	BannerOffScreen.this.adView.displayAd();
     			}
     			
     			/*
     			 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
     			 * if it was set to visible.
     			 * 
     			 * @param adView - RFMAdView instance that generated this method
     			 * 
     			 */
     			public  void onAdFailed(RFMAdView adView){
     		    	BannerOffScreen.this.adView.setVisibility(View.GONE);
     				appendTextToConsole("RFM Ad: Failed");
     			}
     			
     			/*
     			 * Sent when an user interaction with ad results in a transition in view type from banner to full screen
     			 * landing view or vice-versa
     			 * 
     			 * @param adView - RFMAdView instance that generated this method
     			 * @param event - User interaction event of type RFMAdViewEvent
     			 */
     			public  void onAdStateChangeEvent(RFMAdView adView, RFMAdViewEvent event){
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
					appendTextToConsole("RFM Ad: resized to width "+arg1+", height = "+arg2);
				}

				@Override
				public void didDisplayAd(RFMAdView arg0) {
					appendTextToConsole("RFM Ad: displayed ");
				}

				@Override
				public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
					appendTextToConsole("RFM Ad: Could not be displayed ");
				}

		   });

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    final int left = 3000;
                    final int top = 0;
                    final int right = left + adView.getWidth();
                    final int bottom = top + adView.getHeight();
                    adView.layout(left, top, right, bottom);
                } else {
                    adView.setX(3000);
                }
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    final int left = -3000;
                    final int top = 0;
                    final int right = left + adView.getWidth();
                    final int bottom = top + adView.getHeight();
                    adView.layout(left, top, right, bottom);
                } else {
                    adView.setX(-3000);
                }
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up/Down";
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    final int left = 0;
                    final int top = 0;
                    final int right = left + adView.getWidth();
                    final int bottom = top + adView.getHeight();
                    adView.layout(left, top, right, bottom);
                } else {
                    adView.setX(0);
                }
                break;
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    private void clearDebugConsole(){
    	mDebugConsoleView.setText("RFM Sample App Ad Status \n--------------------------------\n");
    	mDebugConsoleView.bringPointIntoView(0);
    }

	public void updateLocationInfo(String locationData) {
		appendTextToConsole(locationData);
	}

    private void appendTextToConsole(String data){
    	mDebugConsoleView.append(data+"\n");
    	Log.d(LOG_TAG,data);
    	mDebugConsoleView.post(new Runnable(){
    		public void run(){
    		    final int scrollAmount = mDebugConsoleView.getLayout().getLineTop(mDebugConsoleView.getLineCount())
    		            -mDebugConsoleView.getHeight();
    		    // if there is no need to scroll, scrollAmount will be <=0
    		    if(scrollAmount>0)
    		    	mDebugConsoleView.scrollTo(0, scrollAmount);
    		    else
    		    	mDebugConsoleView.scrollTo(0,0);
    		}
    	});
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
    	//Required for RFM SDK integration
    	if(adView != null){
			adView.rfmAdViewDestroy();
    	}
    	RFMAdView.clearAds();
    	Log.d(LOG_TAG,"Demo Swipe Destroyed");
        super.onDestroy();
    }
    
    //Required for handling orientation changes
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(LOG_TAG,"Demo Swipe Config changed" );
        
        //Required for RFM SDK integration: activity must inform RFMAdView if it is handling
        //orientation change.
        if(adView != null)
        	adView.activityConfigurationChanged(newConfig);

        super.onConfigurationChanged(newConfig); 
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
	
	 public void configureAdFromPrefs() {
			// Set Size Params
	    	confureAdSize();

		 	// Set Location Params
		 	configureLocation();

	        // Set Orientation Params
	        configureOrientation();
	 }
}
