/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.rfm.sdk.RFMAdView;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Context mContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.white_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black);
        }

        if (toolbar != null) {
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setText(getResources().getText(R.string.about));
        }

        RFMAdView mRFMBannerAdView = new RFMAdView(mContext);
        TextView rfmSdkVersionTextView = (TextView) findViewById(R.id.rfm_sdk_version_textview);
        if (rfmSdkVersionTextView != null)
            rfmSdkVersionTextView.setText(mRFMBannerAdView.getSDKVersion());

        TextView sampleAppVersionTextView = (TextView) findViewById(R.id.sample_version_textview);
        if (sampleAppVersionTextView != null)
            sampleAppVersionTextView.setText(mRFMBannerAdView.getSDKVersion());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}