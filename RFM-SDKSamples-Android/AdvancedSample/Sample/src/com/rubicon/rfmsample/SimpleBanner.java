/*
 * Copyright (C) 2012 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 * 
 */


package com.rubicon.rfmsample;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rfm.sdk.RFMAdRequest;
import com.rfm.sdk.RFMAdView;
import com.rfm.sdk.RFMAdViewListener;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.adissue.RFMAdForensicsStatusListener;
// Required for RFM SDK integration


/**
 * Demo Case #1:<br>
 *  1. Demo with RFMAdView created in XML.<br>
 *  2. Banner Size: fill_parent width and 50dp height.<br>
 *  3. Banner Position: below an ad request button.<br>
 *  4. Device Orientation: Activity handles orientation change to prevent view reload.<br>
 *  5. Optional APIs: GPS location passed by application.<br>
 */
public class SimpleBanner extends BaseActivity {

	//View components
    private Button mGetAdButton;
    private TextView mDebugConsoleView;
    private Button mClearConsoleButton;
    private Button mGetLocationButton;

    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG_TAG = "SimpleBanner";
		setContentView(R.layout.demosimplebanner);
  
        //set layout elements
        mGetAdButton = (Button) findViewById(R.id.getadbuttonone);
        mDebugConsoleView = (TextView) findViewById(R.id.debugconsoleviewone);
        mDebugConsoleView.setBackgroundColor(Color.WHITE);
        mDebugConsoleView.setMovementMethod(new ScrollingMovementMethod());

        //Layout banner ad view
        if(adView == null) {
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
		//mAdRequest.setVideoContent(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"), RFMConstants.RFM_VIDEO_MIDROLL);

		// If the placement type is banner or interstitial
		// the publisher app can use this api to get a VAST ad
		// If the placement is in-stream this call is not required
		// the boolean argument can be set to false to disable the VAST ad video auto play behaviour
		//mAdRequest.setRFMAdAsVideo(false);

		configureAdFromPrefs(); // Needs to called only after AdView and AdRequest objects are initialized
        mGetAdButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
	            	appendTextToConsole("\n\n****Get Ad Clicked****");	                
	            	appendTextToConsole("Requesting Ad from RFM SDK");
	            	
	            	configureAdFromPrefs();

					adView.enableHWAcceleration(true);
	        		Log.v(LOG_TAG, " Information from Preferences "+mAdRequest.getRFMServerName()+" PUB "+mAdRequest.getRFMPubId()+ " appId "+mAdRequest.getRFMAppId() );
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
     		    	SimpleBanner.this.adView.setVisibility(View.GONE);
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
     				SimpleBanner.this.adView.setVisibility(View.VISIBLE);
     		    	//adView.displayAd();
     			}
     			
     			/*
     			 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
     			 * if it was set to visible.
     			 * 
     			 * @param adView - RFMAdView instance that generated this method
     			 * 
     			 */
     			public  void onAdFailed(RFMAdView adView){
     		    	SimpleBanner.this.adView.setVisibility(View.GONE);
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

    }
    
    @Override
    protected void onDestroy() {
    	if(adView != null)
			adView.rfmAdViewDestroy();

		Log.d(LOG_TAG, "SimpleBanner Destroyed");
		super.onDestroy();
    }
    
	 public void configureAdFromPrefs() {
			// Set Size Params
		 	confureAdSize();

		 	// Set Location Params
		 	configureLocation();

	        // Set Orientation Params
	        configureOrientation();
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

}
