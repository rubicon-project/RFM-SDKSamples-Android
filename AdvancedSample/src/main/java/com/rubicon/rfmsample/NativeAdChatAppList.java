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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rfm.sdk.RFMConstants;
import com.rfm.sdk.RFMNativeAd;
import com.rfm.sdk.RFMNativeAdEventsListener;
import com.rfm.sdk.RFMNativeAdRequest;
import com.rfm.sdk.RFMNativeAdResponse;
import com.rfm.sdk.RFMNativeAssets;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NativeAdChatAppList extends BaseActivity{
    private static final String LOG_TAG = "NativeAdChatAppList";
    private int[] AD_POSITIONS = new int[]{4, 8};
    private Map<Integer, RFMNativeAdResponse> adResponseMap = new HashMap<>();
    private Map<Integer, RFMNativeAd> adRequestMap = new HashMap<>();
    private Map<Integer, View> adViewMap = new HashMap<>();
    private ArrayList<FeedData.FeedItem> mFeedItems;
    private ImageView adImgView=null;

    private static final int NUM_FEED_ITEMS = 10;
    private ListView mChatAppList;
    BaseAdapter mChatAppAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_app_list);
        Fresco.initialize(this.getApplicationContext());
        mFeedItems = FeedData.generateChatItems(NUM_FEED_ITEMS);
        markAdPositions();
        mChatAppAdapter = new FeedItemAdapter(this, mFeedItems);
        mChatAppList = (ListView) findViewById(R.id.chat_list);
        mChatAppList.setVisibility(View.VISIBLE);
        loadTestImages();
        setLoadAdAction();
        loadAd();
        mChatAppList.setAdapter(mChatAppAdapter);
        FloatingActionButton fetchAd = (FloatingActionButton) findViewById(R.id.fetch_ad);
        if (fetchAd != null) {
            fetchAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cleanupAds();
                    mChatAppAdapter.notifyDataSetChanged();
                    loadAd();
                }
            });
        }
    }

    public void loadTestImages() {
        adImgView  = new ImageView(this);
        adImgView.setImageResource(R.color.cardview_light_background);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    @Override
    public void updateAdView() {
    }


    public void markAdPositions() {
        if(mFeedItems == null || mFeedItems.size() ==0) {
            return;
        }
        for(int i=0;i<AD_POSITIONS.length;i++) {
            int adposition = AD_POSITIONS[i];
            FeedData.FeedItem adITem = new FeedData.FeedItem("","","","","","");
            mFeedItems.add(adposition, adITem);
        }
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
                nativeAdRequest.setRFMAdMode("native4");
            }
            // Request a native Ad
            nativeAd.requestRFMAd(nativeAdRequest);
            adRequestMap.put(adPosition, nativeAd);
        }
    }

    @Override
    public void loadAd() {
       preFetchAds();
    }

    private void placeNativeAds(final int position, final RFMNativeAdResponse adResponse) {
        if(adResponseMap.containsKey(position)) {
            try {
                RFMNativeAdResponse resp = adResponseMap.get(position);
                resp.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adResponseMap.put(position, adResponse);
        mChatAppAdapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "Updated native ad responses");
    }


    public class FeedItemAdapter extends ArrayAdapter<FeedData.FeedItem> {
        private ArrayList<FeedData.FeedItem> users;
        private LayoutInflater layoutInflater;
        private Context context;
        private final int TYPE_NOT_AD = 0;
        private final int TYPE_AD = 1;

        class ChatAppHolder {
            TextView title;
            TextView description_tt;
            SimpleDraweeView thumb_image;
            TextView timestamp;
        }

        public FeedItemAdapter(Context context, ArrayList<FeedData.FeedItem> users) {
            super(context, R.layout.chat_list_view, R.id.title, users);
            this.context = context;
            this.users = users;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = this.getItemViewType(position);
            FeedData.FeedItem user= null;

            if(position<users.size()) {
                user = users.get(position);
            }

            switch (type) {

                case TYPE_NOT_AD:
                    ChatAppHolder viewHolder;
                    View rowView = convertView;

                    if (null == rowView || rowView.getTag() == null ) {
                        rowView = layoutInflater.inflate(R.layout.chat_list_view, parent, false);
                        viewHolder = new ChatAppHolder();
                        viewHolder.title = (TextView) rowView.findViewById(R.id.title);
                        viewHolder.description_tt = (TextView) rowView.findViewById(R.id.description);
                        viewHolder.thumb_image = (SimpleDraweeView) rowView.findViewById(R.id.icon);
                        viewHolder.timestamp = (TextView) rowView.findViewById(R.id.timestamp);
                        rowView.setTag(viewHolder);
                    } else {
                        viewHolder = (ChatAppHolder) rowView.getTag();
                    }
                    if (user != null) {
                        viewHolder.title.setText(user.getTitle() == null ? "" : user.getTitle());
                        viewHolder.timestamp.setText(user.getTimestamp() == null ? "" : user.getTimestamp());
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
                        }else{
                            return adImgView;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

            return convertView;
        }


        private View getNativeLayout(int position, View convertView, ViewGroup parent, LayoutInflater inflater) {
            if (adViewMap.containsKey(position)) {
                return adViewMap.get(position);
            } else {
                if (inflater == null) {
                    return null;
                }
                try {
                    final RFMNativeAdResponse adResponse = adResponseMap.get(position);
                    if (adResponse == null) {
                        return null;
                    }
                    View rowView = inflater.inflate(R.layout.chat_list_view, parent, false);

                    TextView titleView = (TextView) rowView.findViewById(R.id.title);
                    TextView ctaText = (TextView) rowView.findViewById(R.id.ctaText);
                    TextView description_tt = (TextView) rowView.findViewById(R.id.description);
                    SimpleDraweeView thumb_image = (SimpleDraweeView) rowView.findViewById(R.id.icon);
                    SimpleDraweeView adChoiceIcon = (SimpleDraweeView) rowView.findViewById(R.id.adchoiceicon);
                    TextView sponsored = (TextView) rowView.findViewById(R.id.sponsored);
                    TextView timestamp = (TextView) rowView.findViewById(R.id.timestamp);

                    adChoiceIcon.setVisibility(View.VISIBLE);
                    description_tt.setVisibility(View.GONE);
                    timestamp.setVisibility(View.GONE);
                    titleView.setText(adResponse.getTitle().getTitle());

                    if (adResponse.getDataAsset(RFMNativeAssets.DATA_CTA) != null) {
                        ctaText.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_CTA).getValue());
                    }

                    if (adResponse.getDataAsset(RFMNativeAssets.DATA_CTA).getLink() != null) {
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

                    if (adResponse.getDataAsset(RFMNativeAssets.DATA_DESC) != null) {
                        description_tt.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_DESC).getValue());
                    }

                    if (adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED) != null) {
                        sponsored.setVisibility(View.VISIBLE);
                        sponsored.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED).getValue());
                    }

                    if (adResponse.getDataAsset(RFMNativeAssets.DATA_SPONSORED).getLink() != null) {
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

                    if (adResponse.getImage(RFMNativeAssets.IMAGE_ICON) != null) {
                        thumb_image.setImageURI(Uri.parse(adResponse.getImage(RFMNativeAssets.IMAGE_ICON).getUrl()));
                    }

                    if (adResponse.getAdChoiceIconImage() != null) {
                        adChoiceIcon.setImageURI(Uri.parse(adResponse.getAdChoiceIconImage().getUrl()));
                    }
                    adChoiceIcon.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            if (adResponse.getAdChoicesOptURL() != null) {
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
        public int getViewTypeCount() {
            return 2; // Count of different layouts
        }

        //@Override
        public int getItemViewType(int position) {
            for(int i=0;i<AD_POSITIONS.length;i++) {
                if(position == AD_POSITIONS[i]) {
                    return TYPE_AD;
                }
            }

            return TYPE_NOT_AD;
        }
    }

    public void cleanupAds() {
        int adposition = 0;
        if(adViewMap != null) {
            for(int i=0;i<AD_POSITIONS.length;i++) {
                try {
                    ViewGroup adView = (ViewGroup) adViewMap.get(i);
                    if (adView != null) {
                        adView.removeAllViews();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(adRequestMap != null) {
            for(int i=0;i<AD_POSITIONS.length;i++) {
                adposition = AD_POSITIONS[i];
                try {
                    RFMNativeAd nativeAd = adRequestMap.get(adposition);
                    nativeAd.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        adViewMap.clear();
        adRequestMap.clear();
        adResponseMap.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ChatListApp", "Chat List destroyed ");
        cleanupAds();
    }
}
