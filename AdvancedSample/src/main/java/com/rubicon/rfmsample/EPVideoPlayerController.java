package com.rubicon.rfmsample;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.rfm.sdk.epvast.CuePoint;
import com.rfm.sdk.epvast.EPAdsLoader;
import com.rfm.sdk.epvast.EPRFMAdsLoader;
import com.rfm.sdk.epvast.EPRFMAdDisplayContainer;
import com.rfm.sdk.EPRFMAd;
import com.rfm.sdk.RFMError;

import java.util.ArrayList;

class EPVideoPlayerController {

    interface Logger {
        void log(String message);
    }

    // The video player and ad UI container
    private EPRFMAdDisplayContainer mEPRFMAdDisplayContainer;

    // The AdsLoader instance to intract with the SDK
    private EPRFMAdsLoader mAdsLoader;

    // Ad-enabled video player.
    private EPVideoPlayerWithAdPlayback mEPVideoPlayerWithAdPlayback;

    private View mPlayPauseToggle;

    // Tracks if the SDK is playing an ad, since the SDK might not necessarily use the video
    // player provided to play the video ad.
    private boolean mIsAdPlaying;

    // View that we can write log messages to, to display in the UI.
    private Logger mLog;

    EPVideoPlayerController(final Context context,
                                EPVideoPlayerWithAdPlayback videoPlayerWithAdPlayback,
                                View playPauseToggle,
                            Logger log) {
        mEPVideoPlayerWithAdPlayback = videoPlayerWithAdPlayback;
        mPlayPauseToggle = playPauseToggle;
        mIsAdPlaying = false;
        mLog = log;

        mAdsLoader = new EPRFMAdsLoader(context);

        mAdsLoader.addAdManagerEventListener(new EPAdsLoader.AdManagerEventListener() {
            @Override
            public void onAdLoaded() {
                log("onAdLoaded");
                // the SDK will give call back to playAd or will call
                // resume content based on pre roll is configured or not
                mAdsLoader.start();
            }

            @Override
            public void onAdError(RFMError rfmError) {
                log("onAdError " + rfmError.toString());
                resumeContent();
            }

            @Override
            public void onPauseContent() {
                log("onPauseContent");
                pauseContent();
            }

            @Override
            public void onResumeContent() {
                log("onResumeContent");
                resumeContent();
            }

            @Override
            public void onAdPaused() {
                log("onAdPaused");
                mIsAdPlaying = false;
            }

            @Override
            public void onAdResumed() {
                log("onAdResumed");
                mIsAdPlaying = true;
            }

        });

        mEPVideoPlayerWithAdPlayback.setOnContentCompleteListener(
                new EPVideoPlayerWithAdPlayback.OnContentCompleteListener() {
                    // Event raised by VideoPlayerWithAdPlayback when content video is complete.
                    @Override
                    public void onContentComplete() {
                        mAdsLoader.contentComplete();
                    }
                });
    }

    private void log(String message) {
        if (mLog != null) {
            mLog.log(message + "\n");
        }
    }

    private void pauseContent() {
        mEPVideoPlayerWithAdPlayback.pauseContentForAdPlayback();
        mIsAdPlaying = true;
        setPlayPauseOnAdTouch();
    }

    private void resumeContent() {
        mEPVideoPlayerWithAdPlayback.resumeContentAfterAdPlayback();
        mIsAdPlaying = false;
        removePlayPauseOnAdTouch();
    }

    void requestAndPlayAds(RFMAd rfmAd) {
        // Create a parent container and set the video player and ad UI container on it
        // ad UI container will be used to show skip button
        createDisplayContainer();

        // Object to hold parent display container, publisher video progress provider and RFMAdRequest
        EPRFMAd epRFMAd = createExternalPlayerRFMAd(rfmAd);

        requestExternalPlayerVastAd(epRFMAd);
    }

    private void createDisplayContainer() {
        mEPRFMAdDisplayContainer = new EPRFMAdDisplayContainer();
        mEPRFMAdDisplayContainer.setPlayer(mEPVideoPlayerWithAdPlayback.getVideoAdPlayer());
        mEPRFMAdDisplayContainer.setAdContainer(mEPVideoPlayerWithAdPlayback.getAdUiContainer());
    }

    private EPRFMAd createExternalPlayerRFMAd(RFMAd rfmAd) {
        EPRFMAd epRFMAd = new EPRFMAd();
        epRFMAd.setAdDisplayContainer(mEPRFMAdDisplayContainer);
        epRFMAd.setContentProgressProvider(mEPVideoPlayerWithAdPlayback.getContentProgressProvider());
        epRFMAd.setCacheableAd(true);
        epRFMAd.setRFMTestAdId(rfmAd.getAdId());
        epRFMAd.setRFMParams(rfmAd.getRfmServer(), rfmAd.getPubId(), rfmAd.getAppId());
        epRFMAd.setRFMAdAsInterstitial(true);
        return epRFMAd;
    }

    private void requestExternalPlayerVastAd(EPRFMAd rfmAdRequest) {

        CuePoint preRoll = new CuePoint(CuePoint.Position.PRE_ROLL);
        CuePoint midRoll1 = new CuePoint(10); // time in seconds
        CuePoint midRoll2 = new CuePoint(20);

        ArrayList<CuePoint> cuePointArrayList = new ArrayList<CuePoint>();
        cuePointArrayList.add(preRoll);
        cuePointArrayList.add(midRoll1);
        cuePointArrayList.add(midRoll2);

        mAdsLoader.requestAdForExternalPlayer(rfmAdRequest, cuePointArrayList);
        // if the cuePointArrayList is passed as null CuePoint is defaulted to PRE roll.
    }

    // Set metadata about the content video. In more complex implementations, this might
    // more than just a URL and could trigger additional decisions regarding ad tag selection.
    public void setContentVideo(String videoPath) {
        mEPVideoPlayerWithAdPlayback.setContentVideoPath(videoPath);
    }

    // Save position of the video, whether content or ad. Can be called when the app is paused
    public void pause() {
        mEPVideoPlayerWithAdPlayback.savePosition();
        if (mAdsLoader != null && mEPVideoPlayerWithAdPlayback.getIsAdDisplayed()) {
            mAdsLoader.pause();
        } else {
            mEPVideoPlayerWithAdPlayback.pause();
        }
    }

    // Restore the previously saved progress location of the video. Can be called when the app is resumed.
    public void resume() {
        mEPVideoPlayerWithAdPlayback.restorePosition();
        if (mAdsLoader != null && mEPVideoPlayerWithAdPlayback.getIsAdDisplayed()) {
            mAdsLoader.resume();
            mEPVideoPlayerWithAdPlayback.play();
        } else {
            mEPVideoPlayerWithAdPlayback.play();
        }
    }

    // Touch to toggle play/pause during ad play instead of seeking.
    private void setPlayPauseOnAdTouch() {
        mPlayPauseToggle.setOnTouchListener(
                new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (mIsAdPlaying) {
                                mAdsLoader.pause();
                            } else {
                                mAdsLoader.resume();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
    }

    // Remove the play/pause on touch behavior.
    private void removePlayPauseOnAdTouch() {
        mPlayPauseToggle.setOnTouchListener(null);
    }

    public void destroy() {
        mAdsLoader.destroy();
    }

}
