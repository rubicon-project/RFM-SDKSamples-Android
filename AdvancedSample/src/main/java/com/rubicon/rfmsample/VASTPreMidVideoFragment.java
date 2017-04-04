package com.rubicon.rfmsample;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class VASTPreMidVideoFragment extends Fragment {

    private EPVideoPlayerController mVideoPlayerController;
    private LinearLayout mVideoExampleLayout;
    private final String mContentUrl = "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4";
    private RFMAd mRfmAd;

    private OnVideoFragmentCreatedListener mViewCreatedCallback;
    interface OnVideoFragmentCreatedListener {
        void onVideoFragmentCreated();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mViewCreatedCallback = (OnVideoFragmentCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnVideoFragmentCreatedListener.class.getName());
        }
        super.onAttach(activity);
    }

    private void requestVastAd() {
        mVideoPlayerController.setContentVideo(mContentUrl);
        mVideoPlayerController.requestAndPlayAds(mRfmAd);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        mRfmAd = RFMAd.fromBundle(extras);
        initUi(rootView);
        if (mViewCreatedCallback != null) {
            mViewCreatedCallback.onVideoFragmentCreated();
        }
        return rootView;
    }

    private void initUi(View rootView) {
        EPVideoPlayerWithAdPlayback mVideoPlayerWithAdPlayback = (EPVideoPlayerWithAdPlayback)
                rootView.findViewById(R.id.videoPlayerWithAdPlayback);
        mVideoExampleLayout = (LinearLayout) rootView.findViewById(R.id.videoExampleLayout);
        View playPauseToggle = rootView.findViewById(R.id.videoContainer);
        final TextView logText = (TextView) rootView.findViewById(R.id.logText);
        final ScrollView logScroll = (ScrollView) rootView.findViewById(R.id.logScroll);

        EPVideoPlayerController.Logger logger = new EPVideoPlayerController.Logger() {
            @Override
            public void log(String message) {
                Log.i("VastPreMidVideo", message);
                if (logText != null) {
                    logText.append(message);
                }
                if (logScroll != null) {
                    logScroll.post(new Runnable() {
                        @Override
                        public void run() {
                            logScroll.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        };

        mVideoPlayerController = new EPVideoPlayerController(getActivity(),
                mVideoPlayerWithAdPlayback, playPauseToggle, logger);

        requestVastAd();
    }

    public void makeFullscreen(boolean isFullscreen) {
        for (int i = 0; i < mVideoExampleLayout.getChildCount(); i++) {
            View view = mVideoExampleLayout.getChildAt(i);
            if (view.getId() != R.id.videoContainer) {
                if (isFullscreen) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public EPVideoPlayerController getVideoPlayerController() {
        return mVideoPlayerController;
    }

    @Override
    public void onPause() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.resume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.destroy();
        }
        super.onDestroy();
    }
}
