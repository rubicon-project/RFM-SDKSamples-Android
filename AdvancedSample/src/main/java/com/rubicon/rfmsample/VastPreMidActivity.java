package com.rubicon.rfmsample;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class VastPreMidActivity extends AppCompatActivity implements
        VASTPreMidVideoFragment.OnVideoFragmentCreatedListener {

    private Fragment fragment = null;
    private FragmentManager fragmentManager;
    private AppBarLayout appBarLayout;
    private RFMAd rfmAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vast_pre_mid_main);

        appBarLayout = (AppBarLayout) findViewById(R.id.sample_header_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (toolbar != null) {
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setText(getResources().getString(R.string.vast_pre_mid));
        }

        Bundle extras = getIntent().getExtras();
        rfmAd = RFMAd.fromBundle(extras);

        fragment = new VASTPreMidVideoFragment();
        fragment.setArguments(toBundle());

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragment = null;
        fragmentManager = null;
    }

    @Override
    public void onVideoFragmentCreated() {
        handleOrientation();
    }

    private void handleOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        boolean isLandscape = (orientation == Configuration.ORIENTATION_LANDSCAPE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        VASTPreMidVideoFragment videoFragment = (VASTPreMidVideoFragment) fragmentManager
                .findFragmentById(R.id.fragment_container);

        if (videoFragment != null) {
            videoFragment.makeFullscreen(isLandscape);
            if (isLandscape) {
                hideStatusBar();
            } else {
                showStatusBar();
            }
        }
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            appBarLayout.setVisibility(View.GONE);
        }
    }

    private void showStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        handleOrientation();
    }

    private Bundle toBundle() {
        if (rfmAd == null) {
            Log.d("VastPreMidActivity", "rfmAd is null!!!");
            return null;
        }

        Bundle bundle = new Bundle();
        bundle.putString(RFMAd.APP_ID, rfmAd.getAppId());
        bundle.putString(RFMAd.RFM_SERVER, rfmAd.getRfmServer());
        bundle.putString(RFMAd.PUB_ID, rfmAd.getPubId());
        bundle.putString(RFMAd.AD_ID, rfmAd.getAdId());
        return bundle;
    }
}
