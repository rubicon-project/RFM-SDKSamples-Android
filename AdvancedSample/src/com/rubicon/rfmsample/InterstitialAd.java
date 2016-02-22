/*
= * Copyright (C) 2012 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 */

package com.rubicon.rfmsample;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMException;
import com.rfm.sdk.RFMInterstitialAdViewListener;
import com.rfm.sdk.adissue.RFMAdForensicsStatusListener;
import com.rfm.util.RFMLog;

/**
 * Demo Case #1:<br>
 *  1. Demo with RFMAdView created in XML.<br>
 *  2. Banner Size: fill_parent width and 50dp height.<br>
 *  3. Banner Position: below an ad request button.<br>
 *  4. Device Orientation: Activity handles orientation change to prevent view reload.<br>
 *  5. Optional APIs: GPS location passed by application.<br>
 */
public class InterstitialAd extends BaseActivity {

	//View components
    private Button mGetAdButton;
    private TextView mDebugConsoleView;
    private Button mClearConsoleButton;
    private Button mGetLocationButton;
    private Button mDisplayButton;
    private CheckBox mFullScreenCheckBox;
    private CheckBox mCachedAdCheckBox;
    private int ad_width = 650;
    private int ad_height = 500;
    CountDownTimer myCountdownTimer = null;
    private boolean rfmFullScreenMode;
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demointerstitial);
        LOG_TAG = "InterstitialAd";
        //set layout elements
        mGetAdButton = (Button) findViewById(R.id.getadbuttonone);
        mDisplayButton = (Button) findViewById(R.id.displayadbutton);
        mDebugConsoleView = (TextView) findViewById(R.id.debugconsoleviewone);
        mFullScreenCheckBox = (CheckBox) findViewById(R.id.checkFullScreen);
        mCachedAdCheckBox = (CheckBox) findViewById(R.id.getCachedAd);
        mDebugConsoleView.setBackgroundColor(Color.WHITE);
        mDebugConsoleView.setMovementMethod(new ScrollingMovementMethod());

        //Layout banner ad view
        if(adView == null){
      	  adView = (RFMAdView) findViewById(R.id.bannerviewone);
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
        mAdRequest.setRFMParams(rfmServer, rfmPubId, rfmAppId);


		// set this to have a fullscreen vast and publisher video playback
		//adView.setFullScreenForInterstitial(true);
		//mAdRequest.setVideoContent(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"), RFMConstants.RFM_VIDEO_PREROLL);

		// If the placement type is banner or interstitial
		// the publisher app can use this api to get a VAST ad
		// If the placement is in-stream this call is not required
		// the boolean argument can be set to false to disable the VAST ad video auto play behaviour
		//mAdRequest.setRFMAdAsVideo(true);


		this.configureAdFromPrefs();
		mGetAdButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
	            	appendTextToConsole("\n\n**** INTERSTITIAL Ad Requested **** ");	                
	            	appendTextToConsole("Requesting Ad from RFM SDK");
	            	if(mDisplayButton != null) {
	            		mDisplayButton.setBackgroundColor(Color.GRAY);
	            	}
	            	InterstitialAd.this.configureAdFromPrefs();
	            	if(rfmFullScreenMode) {
	            		//Set ad dimensions(dp) in float. use -1 for fill_parent
	                    // This is optional for while requesting for fullScreen ads
	                    mAdRequest.setAdDimensionParams(-1, -1);
	               } else {
	            		//Set ad dimensions(dp) in float. use -1 for fill_parent
	                    // This is optional and will impact your AdRequest criteria only (based on these ads get qualified from server). All ads
	            	    // which are created with width=320 and heigh=250 will be qualified and one of them will be returned
	            	    // Its upto app developer to control the size of the AdView. It will be based on Width and Height set in your layout.xml/code controlling the view
	                    mAdRequest.setAdDimensionParams(320, 250);
	            	}
	            	
	            	//setHardwareAcceleration for Videos
	            	adView.enableHWAcceleration(true);
	            	//adView.setBackgroundColor(Color.RED);
	            	boolean success = false;
	            	if(mCachedAdCheckBox.isChecked()) {
	            		// Invoke request as per user selection
	            		mAdRequest.setRFMAdAsInterstitial(rfmFullScreenMode);
	            		success = adView.requestCachedRFMAd(mAdRequest);
	            		
	            	} else {
	            		mAdRequest.setRFMAdAsInterstitial(rfmFullScreenMode);
	            		success = adView.requestRFMAd(mAdRequest);
	            	}
	            	//Request Ad
	                if(success)
	                {
	                	appendTextToConsole("ad request accepted, waiting for ad");
	                }
	                else
	                {
	                	appendTextToConsole("ad request denied");
	                }
	            }
	        });

        
        mCachedAdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mDisplayButton.setVisibility(View.VISIBLE);
				} else {
					mDisplayButton.setVisibility(View.GONE);
				}
			}
		});
        
        mCachedAdCheckBox.setChecked(false);
        mDisplayButton.setVisibility(View.GONE);
        
        mFullScreenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
 			@Override
 			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 				rfmFullScreenMode = isChecked;
 				if(rfmFullScreenMode) {
 					// If user requires full Screen Ad set width and height of AdView
 					LinearLayout.LayoutParams fullScreenLayoutP = new LinearLayout.LayoutParams(
 							getFillParentLP(),
 							getFillParentLP());
 					adView.setLayoutParams(fullScreenLayoutP);

 				} else {
 					// If user requires a fixed Interstitial, setting up some Width and Height, can be easily changed for demo
 					LinearLayout.LayoutParams fullScreenLayoutP = new LinearLayout.LayoutParams(
 							ad_width,
 							ad_height);
 					adView.setLayoutParams(fullScreenLayoutP);
 					
 				}
				adView.setFullScreenForInterstitial(rfmFullScreenMode);
 			}
 		});
         
        mFullScreenCheckBox.setChecked(true);
         
        rfmFullScreenMode = true;
        mClearConsoleButton =(Button) findViewById(R.id.clearconsoleone);
        mClearConsoleButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) {
     			clearDebugConsole();
     		}
        });
        
        //Obtain device location. Use fixed lat/long from settings menu if set.
        mGetLocationButton =(Button) findViewById(R.id.getlocationone);
        mGetLocationButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) {
     			configureLocation();
     		}
        });
        

        adView.setScrollContainer(true);
        
        //Optional listener for RFMAd status
        if(adView != null){
     	   adView.setRFMAdViewListener(new RFMInterstitialAdViewListener(){
     		   
     		    public void onAdRequested(RFMAdView adView, String requestUrl,boolean adRequestSuccess){
    		    	//adView.setVisibility(View.GONE);
    		    	appendTextToConsole("RFM Ad: Requesting Url:"+requestUrl);
    		    	mDisplayButton.setBackgroundColor(Color.LTGRAY);
    		    }
    		    
    		   /* 
    		    * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
    			* visible if it has been set to invisible till this time.
    			* 
    			* @param adView - RFMAdView instance that generated this method
    			*  
    			*/   
    			public  void onAdReceived(RFMAdView adView){
    		    	//adView.setVisibility(View.VISIBLE);
    		    	appendTextToConsole("RFM Ad: Received");
    		    	if(mCachedAdCheckBox.isChecked()) {
    		    		// If it is a Cached Ad allow display Button to handle visibility
    		    		mDisplayButton.setBackgroundColor(Color.DKGRAY);
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
    		    	//adView.setVisibility(View.GONE);

    				appendTextToConsole("RFM Ad: Failed");
    			}
    			
    			public void onInterstitialAdWillDismiss(RFMAdView adView) {
    				appendTextToConsole("RFM Ad: Interstitial will dismiss");
    			}
    			
    			public void onInterstitialAdDismissed(RFMAdView adView) {
    				InterstitialAd.this.adView.setVisibility(View.GONE);
    				appendTextToConsole("RFM Ad: Interstitial ad dismissed");
    				InterstitialAd.this.mDisplayButton.setBackgroundColor(Color.LTGRAY);
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
				}

				@Override
				public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
					appendTextToConsole("RFM Ad: Failed to display Ad ");
				}

		   });

        }

		// optional listener for ad issue data upload events
		adView.setRFMAdForensicsStatusListener(new RFMAdForensicsStatusListener() {
			@Override
			public void rfmAdForensicsReportStarted() {
				appendTextToConsole("Ad Issue rfmAdForensicsReportStarted");
			}

			@Override
			public void rfmAdForensicsReportCompleted(boolean status, String errorMessage) {
				appendTextToConsole("rfmAdForensicsReportCompleted status: " + status + " errorMessage: " + errorMessage);
			}
		});
        
        /**
         * Just trying to depict that once App gets to know ad is preloaded, it can display it whenever
         */
        myCountdownTimer = new CountDownTimer(10000, 1000) {

        	 public void onTick(long millisUntilFinished) {
        	     //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
        	     // Kick off your AsyncTask here.
        		 appendTextToConsole("A = Preloaded ad will display in "+ 
        				 millisUntilFinished+ " milliseconds ");
        	 }

        	 public void onFinish() {
        	    if(adView != null) {
        	    	adView.setVisibility(View.VISIBLE);
        	    }
        	     // the 30 seconds is up now so do make any checks you need here.
        	 }
        };
        
        mDisplayButton.setBackgroundColor(Color.GRAY);
        mDisplayButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) { 
     			handleAdVisibility();
     		}
        });
        
    }

    private void handleAdVisibility() {
    	if(adView.isAdAvailableToDisplay()) {
 			adView.setVisibility(View.VISIBLE);
 			if(mCachedAdCheckBox != null && mCachedAdCheckBox.isChecked()) {
	        	try {
					adView.showCachedAd();
				} catch (RFMException e) {
					e.printStackTrace();
					Log.v(LOG_TAG, "Error Code = "+e.getExceptionCode()+" | Error Message = "+e.getMessage());
				}
 			}
 		} else {
 			appendTextToConsole("RFM Ad: Ad is not available to display");
 		}
    }
    
	public void configureAdFromPrefs() {
		// Do not change layout based on Size for Interstitial
	    // Set Orientation Params
	    configureOrientation();

		// Set Location Params
		configureLocation();
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
    protected void onDestroy() {
    	//Required for RFM SDK integration
    	if(adView != null) {
			adView.rfmAdViewDestroy();
		}

    	super.onDestroy();
    }
    
	@SuppressWarnings("deprecation")
	public static int getFillParentLP(){
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.FROYO){
			return ViewGroup.LayoutParams.MATCH_PARENT;
		}else{
			return ViewGroup.LayoutParams.FILL_PARENT;		
		}
	}
}
