/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdNewsFeedList extends BaseActivity {
    private static final String LOG_TAG = NativeAdNewsFeedList.class.getSimpleName();
    private Map<Integer, RFMNativeAdResponse> adResponseMap = new HashMap<>();
    private Map<Integer, RFMNativeAd> adRequestMap = new HashMap<>();
    private Map<Integer, View> adViewMap = new HashMap<>();
    private List<NewsFeedItem> mNewsFeedItemList = new ArrayList<>();
    private static final int[] AD_POSITIONS = new int[]{2, 8, 11, 14, 18};
    private NewsFeedAdapter mNewsFeedAdapter;
    private ListView mNewsFeedList;
    private static final String NEWS_FEED_URL = "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=30&q=http://rss.nytimes.com/services/xml/rss/nyt/World.xml";
    private static final String IMAGE_URL = "http://www.darrickbynum.com/wp-content/uploads/2014/07/News729x6581.jpg";
    private ImageView adImgView=null;


    @Override
    public void updateAdView() {
    }
    @NonNull
    private final Handler mHandler = new Handler();

    static final class NewsFeedItem {
        String title;
        String imageUrl;
        String description;
        String landingUrl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_feed_list);
        Fresco.initialize(this.getApplicationContext());
        getNewsHeadlines();
        mNewsFeedAdapter = new NewsFeedAdapter(this.getApplicationContext(), mNewsFeedItemList);
        mNewsFeedList = (ListView)findViewById(R.id.news_list);
        mNewsFeedList.setVisibility(View.VISIBLE);
        mNewsFeedList.setAdapter(mNewsFeedAdapter);
        loadTestImages();
        setLoadAdAction();
        FloatingActionButton fetchAd = (FloatingActionButton) findViewById(R.id.fetch_ad);
        if (fetchAd != null) {
            fetchAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cleanupAds();
                    mNewsFeedAdapter.notifyDataSetChanged();
                    loadAd();
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    public void loadTestImages() {
        adImgView  = new ImageView(this);
        adImgView.setImageResource(R.drawable.loadingicon);
    }

    private void getNewsHeadlines() {
        new NewsHeadlineFetcher().getFeed(NEWS_FEED_URL, new NewsHeadlineFetcher.OnFetchCompletedListener() {
            @Override
            public void onFetchCompleted(@Nullable final String data, @Nullable final String message) {
                if (null == data) {
                    Log.e(LOG_TAG, "Fetching headlines failed with error: " + message);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadNewsHeadlines(data);
                        }
                    });
                }
            }
        });
    }

    private void loadNewsHeadlines(String data) {
        try {
            JSONArray feed = new JSONObject(data).
                    getJSONObject("responseData").
                    getJSONObject("feed").
                    getJSONArray("entries");
            for (int i = 0; i < feed.length(); i++) {
                JSONObject item = feed.getJSONObject(i);
                Log.v(LOG_TAG, item.toString());
                NewsFeedItem feedEntry = new NewsFeedItem();
                try {
                    feedEntry.title = item.getString("title");
                    if (item.isNull("mediaGroups")) {
                        feedEntry.imageUrl = IMAGE_URL;
                    } else {
                        feedEntry.imageUrl = item.getJSONArray("mediaGroups").getJSONObject(0).getJSONArray("contents").getJSONObject(0).getString("url");
                    }
                    feedEntry.landingUrl = item.getString("link");
                    feedEntry.description = item.getString("contentSnippet");
                    mNewsFeedItemList.add(feedEntry);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
            mNewsFeedAdapter.notifyDataSetChanged();
            markAdPositions();
            loadAd();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAd() {
        preFetchAds();
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
                nativeAdRequest.setRFMAdMode("native3");
            }
            // Request a native Ad
            nativeAd.requestRFMAd(nativeAdRequest);
            adRequestMap.put(adPosition, nativeAd);
        }
    }

    public void markAdPositions() {
        if(mNewsFeedItemList == null || mNewsFeedItemList.size() ==0) {
            return;
        }
        for(int i=0;i<AD_POSITIONS.length;i++) {
            int adposition = AD_POSITIONS[i];
            NewsFeedItem adITem = new NewsFeedItem();
            mNewsFeedItemList.add(adposition, adITem);
        }
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
        mNewsFeedAdapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "Updated native ad responses");
    }


    public class NewsFeedAdapter extends ArrayAdapter<NewsFeedItem> {

        private Context mContext;
        private LayoutInflater mInflater;
        private List<NewsFeedItem> mItems;
        private final int TYPE_NOT_AD = 0;
        private final int TYPE_AD = 1;

        class ViewHolder {
            TextView title;
            TextView ctaText;
            TextView description;
            TextView sponsored;
            TextView rating;
            SimpleDraweeView icon;
            SimpleDraweeView adChoicesIcon;
        }

        public NewsFeedAdapter(Context context, List<NewsFeedItem> items) {
            super(context, R.layout.news_feed_view, items);
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mItems = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = this.getItemViewType(position);
            final NewsFeedItem newsTileItem = mItems.get(position);
            switch (type) {

                case TYPE_NOT_AD:
                    ViewHolder viewHolder;
                    View rowView = convertView;

                    if (null == rowView || rowView.getTag() == null ) {
                        rowView = mInflater.inflate(R.layout.news_feed_view, parent, false);
                        viewHolder = new ViewHolder();
                        viewHolder.title = (TextView) rowView.findViewById(R.id.title);
                        viewHolder.description = (TextView) rowView.findViewById(R.id.description);
                        viewHolder.ctaText = (TextView) rowView.findViewById(R.id.ctaText);
                        viewHolder.icon = (SimpleDraweeView) rowView.findViewById(R.id.icon);
                        viewHolder.sponsored = (TextView) rowView.findViewById(R.id.sponsored);
                        viewHolder.rating = (TextView) rowView.findViewById(R.id.rating);
                        viewHolder.adChoicesIcon = (SimpleDraweeView) rowView.findViewById(R.id.adchoiceicon);
                        rowView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) rowView.getTag();
                    }
                    viewHolder.rating.setVisibility(View.GONE);
                    viewHolder.sponsored.setVisibility(View.GONE);
                    viewHolder.adChoicesIcon.setVisibility(View.GONE);
                    if (newsTileItem != null) {
                        viewHolder.description.setVisibility(View.VISIBLE);
                        viewHolder.title.setText(newsTileItem.title);
                        viewHolder.icon.setImageURI(Uri.parse(newsTileItem.imageUrl));
                        viewHolder.description.setText(newsTileItem.description);
                    }
                    return rowView;
                case TYPE_AD:

                    try {
                        View adView = getNativeLayout(position, convertView, parent, mInflater);
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
                    View rowView = inflater.inflate(R.layout.news_feed_view, parent, false);

                    TextView title = (TextView) rowView.findViewById(R.id.title);
                    TextView description = (TextView) rowView.findViewById(R.id.description);
                    TextView ctaText = (TextView) rowView.findViewById(R.id.ctaText);
                    SimpleDraweeView icon = (SimpleDraweeView) rowView.findViewById(R.id.icon);
                    TextView sponsored = (TextView) rowView.findViewById(R.id.sponsored);
                    TextView rating = (TextView) rowView.findViewById(R.id.rating);
                    SimpleDraweeView adChoicesIcon = (SimpleDraweeView) rowView.findViewById(R.id.adchoiceicon);

                    adChoicesIcon.setVisibility(View.VISIBLE);
                    description.setVisibility(View.GONE);
                    ctaText.setVisibility(View.VISIBLE);
                    title.setText(adResponse.getTitle().getTitle());

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
                        description.setText(adResponse.getDataAsset(RFMNativeAssets.DATA_DESC).getValue());
                    }

                    if (adResponse.getDataAsset(RFMNativeAssets.DATA_RATING) != null) {
                        if(adResponse.getDataAsset(RFMNativeAssets.DATA_RATING) != null){
                            rating.setVisibility(View.VISIBLE);
                            int numStars = Integer.parseInt(adResponse.getDataAsset(RFMNativeAssets.DATA_RATING).getValue());
                            String ratingStars = "";
                            for(int i = 0; i< numStars; i++){
                                ratingStars+="*";
                            }
                            rating.setText(ratingStars);
                        }
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
                        icon.setImageURI(Uri.parse(adResponse.getImage(RFMNativeAssets.IMAGE_ICON).getUrl()));
                    }

                    if (adResponse.getAdChoiceIconImage() != null) {
                        adChoicesIcon.setImageURI(Uri.parse(adResponse.getAdChoiceIconImage().getUrl()));
                    }
                    adChoicesIcon.setOnClickListener(new View.OnClickListener() {

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
        Log.d("NewsFeedList", "News Feed destroyed ");
        cleanupAds();
    }
}
