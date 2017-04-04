/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMNativeAd;
import com.rfm.sdk.RFMNativeAdEventsListener;
import com.rfm.sdk.RFMNativeAdRequest;
import com.rfm.sdk.RFMNativeAdResponse;

import com.rfm.sdk.RFMNativeAssets;
import com.rfm.sdk.vast.views.RFMNativeMediaView;

import com.rfm.util.VideoCacheListener;
import com.rubicon.rfmsample.FeedData.FeedItem;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;

import com.squareup.picasso.Picasso;

public class NativeContentStreamList extends BaseActivity {
    private static final String LOG_TAG = "NativeContentStreamList";
    private int[] AD_POSITIONS = new int[]{4, 8};
    private SparseArray<RFMNativeAdResponse> adResponseMap = new SparseArray<>();
    private SparseArray<RFMNativeAd> adRequestMap = new SparseArray<>();
    private SparseArray<View> adViewMap = new SparseArray<>();
    private static final int AD_HEIGHT = 200;
    private static final int AD_WIDTH = 640;
    private ArrayList<FeedData.FeedItem> mFeedItems;

    private static final int NUM_FEED_ITEMS = 10;
    private ListView mContentStreamList;
    private ImageView adImgView=null;
    BaseAdapter originalAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_stream_list);
        Fresco.initialize(this.getApplicationContext());
        mFeedItems = FeedData.generateFeedItems(NUM_FEED_ITEMS);
        markAdPositions();

        originalAdapter = new FeedItemAdapter(this, mFeedItems);
        mContentStreamList = (ListView) findViewById(R.id.contentstream_list);
        mContentStreamList.setVisibility(View.VISIBLE);
        loadTestImages();
        setLoadAdAction();
        loadAd();
        mContentStreamList.setAdapter(originalAdapter);
        FloatingActionButton fetchAd = (FloatingActionButton) findViewById(R.id.fetch_ad);
        if (fetchAd != null) {
            fetchAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshAds();
                }
            });
        }
    }
    boolean refreshInProgress = false;

    private void refreshAds() {
        refreshInProgress = true;
        mContentStreamList.setSelection(0);
        originalAdapter.notifyDataSetChanged();
        cleanupAds();
        loadAd();
    }

    public void loadTestImages() {
        adImgView  = new ImageView(this.getApplicationContext());
        adImgView.setImageResource(R.color.cardview_light_background);
    }


    public void markAdPositions() {
        if(mFeedItems == null || mFeedItems.size() ==0) {
            return;
        }
        for(int i:AD_POSITIONS) {
            FeedItem adITem = new FeedItem("","","","","","");
            mFeedItems.add(i, adITem);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    public void preFetchAds() {
        for (int i=0;i< AD_POSITIONS.length;i++) {
            final int adPosition = AD_POSITIONS[i];
            // Created a native Ad instance
            RFMNativeAd nativeAd = new RFMNativeAd(this, new RFMNativeAd.RFMNativeAdListener() {
                @Override
                public void onAdRequested(String requestUrl, boolean adRequestSuccess) {
                    if(adRequestSuccess) {
                        Log.d(LOG_TAG, "Native ad requested successfully for position "+adPosition+" , with request URL = "+requestUrl);
                    } else {
                        Log.d(LOG_TAG, "Failed to request Native ad for position "+adPosition);
                    }
                }

                @Override
                public void onAdReceived(RFMNativeAdResponse adResponse) {
                    Log.d(LOG_TAG, "Native ad received for position "+adPosition);
                    placeNativeAds(adPosition, adResponse);
                }

                @Override
                public void onAdFailed(String appId, String errorMessage) {
                    Log.d(LOG_TAG, "Native ad failed for position "+adPosition+" , with error = "+errorMessage);
                }
            });

            // Created a native Ad request
            RFMNativeAdRequest nativeAdRequest = new RFMNativeAdRequest(mRfmServer, mRfmPubId, mRfmAppId);
            if(mRfmAppId.equalsIgnoreCase(RFMConstants.NATIVE_AD_PREDEFINED_PLACEMENT_ID)){
                nativeAdRequest.setRFMAdMode("native6");
            }
            // Request a native Ad
            nativeAd.requestRFMAd(nativeAdRequest);

            adRequestMap.put(adPosition, nativeAd);
        }
    }

    public class FeedItemAdapter extends ArrayAdapter<FeedItem> {
        private ArrayList<FeedItem> users;
        private LayoutInflater layoutInflater;
        private Context context;
        private final int TYPE_NOT_AD = 0;
        private final int TYPE_AD = 1;

        class ContentViewHolder {
            TextView title;
            TextView subtitle;
            TextView time_tt;
            TextView description_tt;
            SimpleDraweeView thumb_image;
            LinearLayout contentView;
            RelativeLayout adcontainer;
        }

        FeedItemAdapter(Context context, ArrayList<FeedData.FeedItem> users) {
            super(context, R.layout.news_feed_view, R.id.title, users);
            this.context = context;
            this.users = users;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = this.getItemViewType(position);
            FeedItem user= null;

            if(position<users.size()) {
                user = users.get(position);
            }

            switch (type) {

                case TYPE_NOT_AD:
                    ContentViewHolder viewHolder;
                    View rowView = convertView;

                    if (null == rowView || rowView.getTag() == null ) {
                        rowView = layoutInflater.inflate(R.layout.content_stream_item, parent, false);
                        viewHolder = new ContentViewHolder();
                        viewHolder.title = (TextView) rowView.findViewById(R.id.title);
                        viewHolder.subtitle = (TextView) rowView.findViewById(R.id.subtitle);
                        viewHolder.time_tt = (TextView) rowView.findViewById(R.id.time_tt);
                        viewHolder.description_tt = (TextView) rowView.findViewById(R.id.description_tt);
                        viewHolder.thumb_image = (SimpleDraweeView) rowView.findViewById(R.id.thumb_image);
                        viewHolder.contentView = (LinearLayout) rowView.findViewById(R.id.contentView);
                        viewHolder.adcontainer = (RelativeLayout) rowView.findViewById(R.id.adlayout);
                        rowView.setTag(viewHolder);
                    } else {
                        viewHolder = (ContentViewHolder) rowView.getTag();
                    }
                    viewHolder.contentView.setVisibility(View.VISIBLE);
                    viewHolder.adcontainer.setVisibility(View.GONE);
                    if (user != null) {
                        viewHolder.title.setText(user.getTitle() == null ? "" : user.getTitle());
                        viewHolder.subtitle.setText(user.getSubtitle() == null ? "" : user.getSubtitle());
                        viewHolder.time_tt.setText(user.getTimestamp());
                        viewHolder.description_tt.setText(user.getDescription() == null ? "" : user.getDescription());
                        Picasso.with(context)
                                .load(getResources().getIdentifier(user.getThumbImage(), "drawable", getPackageName()))
                                .into(viewHolder.thumb_image);
                    }
                    return rowView;
                case TYPE_AD:

                    try {
                        View adView = getNativeLayout(position, convertView, parent, layoutInflater);

                        if(adView != null) {
                            return adView;
                        } else {
                            return adImgView;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2; // Count of different layouts
        }

        public int getItemViewType(int position) {
            for(int i=0;i<AD_POSITIONS.length;i++) {
                if(position == AD_POSITIONS[i]) {
                    return TYPE_AD;
                }
            }
            return TYPE_NOT_AD;
        }
    }


    private void placeNativeAds(final int position, final RFMNativeAdResponse adResponse) {
        if(adResponseMap.get(position) != null) {
            try {
                RFMNativeAdResponse resp = adResponseMap.get(position);
                resp.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        adResponseMap.put(position, adResponse);
        if(adResponse.hasVideo()) {
            adResponse.preCacheVideo(this.getApplicationContext(), new VideoCacheListener() {
                @Override
                public void onVideoCached(Map<String, String> urlCacheMap) {
                    Log.d(LOG_TAG, "Native video cached to disk ");
                }

                @Override
                public void onVideoFailedToCache(String errorMessage) {
                    Log.d(LOG_TAG, "Failed to cache Native video, "+errorMessage);
                }
            });
         }
        refreshInProgress = false;
        originalAdapter.notifyDataSetChanged();
        mContentStreamList.setSelection(0);  // refresh view while ads get populated
        Log.d(LOG_TAG, "Updated native ad responses");
    }

    private View getNativeLayout(int position, View convertView, ViewGroup parent, LayoutInflater inflater) {
        if(refreshInProgress) {
            return null;
        }
        if(adViewMap.get(position) != null) {
            return adViewMap.get(position);
        } else {
            if(inflater == null) {
                return null;
            }
            try {
                final RFMNativeAdResponse adResponse = adResponseMap.get(position);
                if(adResponse==null){
                    return null;
                }
                View rowView = inflater.inflate(R.layout.content_stream_item, parent, false);

                TextView titleView = (TextView) rowView.findViewById(R.id.title);
                TextView ctaText = (TextView) rowView.findViewById(R.id.ctaText);
                TextView description_tt = (TextView) rowView.findViewById(R.id.description_tt);
                SimpleDraweeView thumb_image = (SimpleDraweeView) rowView.findViewById(R.id.thumb_image);
                SimpleDraweeView adChoiceIcon = (SimpleDraweeView) rowView.findViewById(R.id.adchoiceicon);
                LinearLayout contentView = (LinearLayout) rowView.findViewById(R.id.contentView);
                RelativeLayout adcontainer = (RelativeLayout) rowView.findViewById(R.id.adlayout);
                TextView sponsored = (TextView) rowView.findViewById(R.id.sponsored);
                TextView rating = (TextView) rowView.findViewById(R.id.rating);

                adChoiceIcon.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.VISIBLE);
                adcontainer.setVisibility(View.VISIBLE);
                titleView.setText(adResponse.getTitle().getTitle()) ;

                if(adResponse.getDataAsset(RFMNativeAssets.DATA_CTA) != null){
                    ctaText.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_CTA).getValue());
                }

                if(adResponse.getDataAsset(RFMNativeAssets.DATA_CTA).getLink() != null) {
                    ctaText.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            if (adResponse.getDataAsset(RFMNativeAssets.DATA_CTA).getLink() != null) {
                                intent.setData(Uri.parse(adResponse.getDataAsset(RFMNativeAssets.DATA_CTA).getLink().getURL()));
                                startActivity(intent);
                            }
                        }
                    });
                }

                if(adResponse.getDataAsset(RFMNativeAssets.DATA_DESC) != null){
                    description_tt.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_DESC).getValue());
                }

                if(adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED) != null){
                    sponsored.setVisibility(View.VISIBLE);
                    sponsored.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED).getValue());
                }

                if(adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED).getLink() != null){
                    sponsored.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            if (adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED).getLink() != null) {
                                intent.setData(Uri.parse(adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED).getLink().getURL()));
                                startActivity(intent);
                            }
                        }
                    });
                }

                if(adResponse.getDataAsset(RFMNativeAssets.DATA_RATING) != null){
                    rating.setVisibility(View.VISIBLE);
                    int numStars = Integer.parseInt(adResponse.getDataAsset(RFMNativeAssets.DATA_RATING).getValue());
                    String ratingStars = "";
                    for(int i = 0; i< numStars; i++){
                        ratingStars+="*";
                    }
                    rating.setText(ratingStars);
                }

                if(adResponse.getImage(RFMNativeAssets.IMAGE_ICON) != null){
                    thumb_image.setImageURI(Uri.parse(adResponse.getImage(RFMNativeAssets.IMAGE_ICON).getUrl()));
                }


                if(adResponse.hasVideo()) {
                    final RFMNativeMediaView vLayout = (RFMNativeMediaView) adResponse.getVideo().getMediaView();
                    vLayout.setAutoplay(true);
                    vLayout.setLoadVideoListener(new RFMNativeMediaView.LoadVideoListener() {
                        @Override
                        public void onVideoAdLoadSuccess() {
                            Log.d(LOG_TAG, "Native video successfully loaded ");
                            vLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVideoAdLoadFailure(String errorMessage) {
                            Log.d(LOG_TAG, "Native video failed");
                            vLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onVideoCompleted() {
                            Log.d(LOG_TAG, "Native video completed ..");
                            vLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVideoClicked() {
                            Log.d(LOG_TAG, "Native video clicked ..");
                        }
                    });
                    vLayout.loadVideo(AD_WIDTH * mDisplayDensity, AD_HEIGHT * mDisplayDensity);
                    adcontainer.addView(vLayout);
                }

                if(adResponse.getAdChoiceIconImage()!=null){
                    adChoiceIcon.setImageURI(Uri.parse(adResponse.getAdChoiceIconImage().getUrl()));
                }
                adChoiceIcon.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        if(adResponse.getAdChoicesOptURL() != null){
                            intent.setData(Uri.parse(adResponse.getAdChoicesOptURL()));
                            startActivity(intent);
                        }
                    }
                });

                adResponse.registerView(rowView, new RFMNativeAdEventsListener() {
                    @Override
                    public void onAdWasClicked() {
                        Log.d(LOG_TAG, "Ad was clicked");
                    }

                    @Override
                    public void onAdWillLeaveApplication() {
                        Log.d(LOG_TAG, "Ad will leave application ");
                    }
                });
                adViewMap.put(position, rowView);
                return rowView;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ContentStreamActivity", "Content Stream Activity destroyed ");
        cleanupAds();
    }

    @Override
    public void loadAd() {
        preFetchAds();
    }

    @Override
    public void updateAdView() {

    }

    public void cleanupAds() {
            int adposition;
            for(int i=0;i<AD_POSITIONS.length;i++) {
                adposition = AD_POSITIONS[i];
                try {
                    RFMNativeAd nativeAd = adRequestMap.get(adposition);
                    nativeAd.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ViewGroup adView = (ViewGroup) adViewMap.get(adposition);
                    if (adView != null) {
                        adView.removeAllViews();
                    }
                    adView = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    RFMNativeAdResponse resp = adResponseMap.get(adposition);
                    if (resp != null) {
                        resp.destroy();
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            adViewMap.clear();
            adRequestMap.clear();
            adResponseMap.clear();
        }
}

