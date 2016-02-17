package com.rubicon.rfmsample;

import java.util.HashMap;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.rfm.sdk.ui.mediator.RFMBaseMediator;
// Required for RFM SDK integration


/**
 *Timer based periodic ad requests:<br>
		". Demo with RFMAdView created in XML.\n" +
		". Banner Position: below an ad request button.\n" +
		". Ad ids selected from a hardcoded ad list array\n",
**/
public class TimerBasedBanner extends BaseActivity {

	//View components
    private Button mStartAdsButton;
    private TextView mDebugConsoleView;
    private TextView mStatsConsoleView;
    private Button mClearConsoleButton;
    private Button mStopAdsButton;
    
    private int numAdRequestAttempts = 0;
    private int numAdRequestSuccess=0;
    private int numAdRequestFailure=0;
    private int numAdRequestsDenied=0;
    
    private static final int AD_FETCH_TIMER_SECS = 2;
    private Handler mHandler;
    private boolean shouldContinueTimer = false;

    @SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demotimedads);
        LOG_TAG = "TimerBasedBanner";
        
        //set layout elements
        mStartAdsButton = (Button) findViewById(R.id.startadbuttonfive);
        mStartAdsButton.setEnabled(true);
        mDebugConsoleView = (TextView) findViewById(R.id.debugconsoleviewfive);
        mDebugConsoleView.setBackgroundColor(Color.WHITE);
        mDebugConsoleView.setMovementMethod(new ScrollingMovementMethod());
        
        mStatsConsoleView = (TextView) findViewById(R.id.statsconsoleviewfive);
        mStatsConsoleView.setBackgroundColor(Color.WHITE);
        mStatsConsoleView.setMovementMethod(new ScrollingMovementMethod());
        
        //Layout banner ad view
        if(adView == null){
      	  adView = (RFMAdView) findViewById(R.id.bannerviewfive);
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
        
        mHandler = new Handler();
        shouldContinueTimer = false;
        configureAdFromPrefs();
        mStartAdsButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mStartAdsButton.setEnabled(false);
					mStopAdsButton.setEnabled(true);
					resetAdStats();
	            	appendTextToConsole("\n\n****Start Ads Clicked****");	                
	            	appendTextToConsole("Requesting Ad from RFM SDK\n");
	            	shouldContinueTimer = true;
	            	requestNewAd(mAdRequest);
	            }
	        });

        
        mClearConsoleButton =(Button) findViewById(R.id.clearconsolefive);
        mClearConsoleButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) {
     			clearDebugConsole();
     		}
        });
        
        //Obtain device location. Use fixed lat/long from settings menu if set.
        mStopAdsButton =(Button) findViewById(R.id.stopadbuttonfive);
        mStopAdsButton.setEnabled(false);
        mStopAdsButton.setOnClickListener(new OnClickListener(){
     		public void onClick(View v) {
     			appendTextToConsole("\nStop Ad requests");
     			//Change button settings
     			mStartAdsButton.setEnabled(true);
     			mStopAdsButton.setEnabled(false);
            	shouldContinueTimer = false;
     			//End update timer cycle
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
     		    	appendTextToConsole("RFM Ad: Requesting Url:"+requestUrl);
     		    	TimerBasedBanner.this.adView.setVisibility(View.GONE);

     		    }
     		    
     		   /* 
     		    * Sent when an ad request loaded an ad; this is a good opportunity to make the ad view
     			* visible if it has been set to invisible till this time.
     			* 
     			* @param adView - RFMAdView instance that generated this method
     			*  
     			*/   
     			public  void onAdReceived(RFMAdView adView){
     		    	TimerBasedBanner.this.adView.setVisibility(View.VISIBLE);
     		    	TimerBasedBanner.this.adView.displayAd();
     				appendTextToConsole("RFM Ad: Received");
     				numAdRequestSuccess++;
     				updateStatsConsoleView();
     				
     				//Set timer for next ad request
     				if (shouldContinueTimer)
     					mHandler.postDelayed(mUpdateTimeTask, ( AD_FETCH_TIMER_SECS * 1000)) ; //AD_FETCH_TIMER_SECS second delay
     				else
     			    	mHandler.removeCallbacks(mUpdateTimeTask);
     			}
     			
     			/*
     			 * Sent when an ad request failed to load an ad. Client can choose to set the view to invisible
     			 * if it was set to visible.
     			 * 
     			 * @param adView - RFMAdView instance that generated this method
     			 * 
     			 */
     			public  void onAdFailed(RFMAdView adView){
     		    	TimerBasedBanner.this.adView.setVisibility(View.GONE);

     				appendTextToConsole("RFM Ad: Failed");
     				numAdRequestFailure++;
     				updateStatsConsoleView();
     				//set timer for next ad request
     				if (shouldContinueTimer)
     					mHandler.postDelayed(mUpdateTimeTask, ( AD_FETCH_TIMER_SECS * 1000)) ; // AD_FETCH_TIMER_SECS second delay
     				else
         			    mHandler.removeCallbacks(mUpdateTimeTask);
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
             			    mHandler.removeCallbacks(mUpdateTimeTask);
               				break;
               				
                     	  case FULL_SCREEN_AD_DISMISSED:
                 				appendTextToConsole("RFM Ad: Full screen ad dismissed");
                 				mHandler.postDelayed(mUpdateTimeTask, ( AD_FETCH_TIMER_SECS * 1000)) ; // AD_FETCH_TIMER_SECS second delay
                 				break;
						  default:
								break;
		               }
     			}

				@Override
				public void onAdResized(RFMAdView arg0, int arg1, int arg2) {
					appendTextToConsole("RFM Ad is resized");
				}

				@Override
				public void didDisplayAd(RFMAdView arg0) {
					appendTextToConsole("RFM Ad is displayed ");
				}

				@Override
				public void didFailedToDisplayAd(RFMAdView arg0, String arg1) {
					appendTextToConsole("RFM Ad could not be displayed ");
				}

		   });

        }
        
    }

    private Runnable mUpdateTimeTask = new Runnable() {
    	   public void run() {
    		   //Call ad request here
    		   if(shouldContinueTimer)
    			   requestNewAd(mAdRequest);
    	   }
    	};
    
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
    	mDebugConsoleView.post(new Runnable() {
			public void run() {
				final int scrollAmount = mDebugConsoleView.getLayout().getLineTop(mDebugConsoleView.getLineCount())
						- mDebugConsoleView.getHeight();
				// if there is no need to scroll, scrollAmount will be <=0
				if (scrollAmount > 0)
					mDebugConsoleView.scrollTo(0, scrollAmount);
				else
					mDebugConsoleView.scrollTo(0,0);
    		}
    	});
    }
    
    private void updateStatsConsoleView(){
    	mStatsConsoleView.setText("Sample App Stats\n---------------\nNum Attempts:" + numAdRequestAttempts +
				"\nNum Success:" + numAdRequestSuccess +
				"\nNum Failures:" + numAdRequestFailure +
				"\nNum Request Denied:" + numAdRequestsDenied);

    }
    
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onPause(){
	    mHandler.removeCallbacks(mUpdateTimeTask);

		super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	
    	//Required for RFM SDK integration
    	if(adView != null)
			adView.rfmAdViewDestroy();
    	shouldContinueTimer = false;
	    mHandler.removeCallbacks(mUpdateTimeTask);

		Log.d(LOG_TAG, "Demo Five Destroyed");
    	
        super.onDestroy();
    }
	
	private void requestNewAd(RFMAdRequest request){
    	appendTextToConsole("\n\nRequest Attempt : "+(numAdRequestAttempts+1)+"\n-----------\n");
    	mHandler.removeCallbacks(mUpdateTimeTask);

    	//Request Ad
    	numAdRequestAttempts++;
        if(!adView.requestRFMAd(mAdRequest))
        {
        	numAdRequestsDenied++;
        	appendTextToConsole("ad request denied");
        	updateStatsConsoleView();
        }
        else
        {
        	appendTextToConsole("ad request accepted, waiting for ad");
        }
	}
	
	public void resetAdStats(){
	  	numAdRequestAttempts = 0;
	  	numAdRequestSuccess=0;
	  	numAdRequestFailure=0;
	  	numAdRequestsDenied=0;
	  	updateStatsConsoleView();
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
