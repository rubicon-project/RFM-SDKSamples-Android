package com.rubicon.rfmsample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rfm.sdk.*;

public class NativeNewsFeedList extends BaseActivity{
    private static final String LOG_TAG = NativeNewsFeedList.class.getSimpleName();
    private Map<Integer, RFMNativeAdResponse> mAdResponseMap = new HashMap<>();
    private List<FeedData.FeedItem> mNewsFeedItemList = new ArrayList<>();
    private static final int[] AD_POSITIONS = new int[]{2, 6, 9, 15, 19};
    private NewsFeedAdapter originalAdapter;
    private RFMNativeAdAdapter rfmNativeAdAdapter;
    private ListView mNewsFeedList;
    private View footerView;
    private Map<Integer, View> adViewMap = new HashMap<>();
    @NonNull private NativeUIHandler nativeUIHandler;
    private LayoutInflater inflater;
    private TextView loadMoreButton;
    boolean loadMore = true;
    private final String serverName = "http://mrp.rubiconproject.com/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_feed_list);
        Fresco.initialize(this.getApplicationContext());

        mNewsFeedList = (ListView) findViewById(R.id.news_list);

        // Initialize Native News Feed list
        initializeFeed();
        initOrigAdapter(mNewsFeedItemList);

        // initialize RFM Native Adapter
        initRFMNativeAdapter();

        inflater = LayoutInflater.from(this.getApplicationContext());

        addLoadMoreFooter();
        setLoadAdAction();

/**
 *  Uncomment to verify if getOrigAdapterPosition() method returns correct position
 *
 */
//        if(mNewsFeedList != null) {
//            mNewsFeedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(NativeNewsFeedList.this, " Positions "+position+ " Adjusted Posistion "+rfmNativeAdAdapter.getOrigAdapterPosition(position), Toast.LENGTH_SHORT ).show();
//                }
//            });
//        }

        FloatingActionButton fetchAd = (FloatingActionButton) findViewById(R.id.fetch_ad);
        if (fetchAd != null) {
            fetchAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshAds();
                }
            });
        }

        loadAd();
    }

    @Override
    public void onDestroy() {
        // You must call this or the ad adapter may cause a memory leak.
        cleanupData();
        super.onDestroy();
    }

    /**
     * Method to initialize News List
     * @param feedList
     */
    public void initOrigAdapter(List<FeedData.FeedItem> feedList) {
        if(feedList != null && !feedList.isEmpty()) {
            originalAdapter = new NewsFeedAdapter(this, feedList);
        }
    }

    /**
     * Method to initialize RFM Native Adapter
     */
    public void initRFMNativeAdapter() {
        nativeUIHandler = new NativeUIHandler() {
            @Override
            public View getNativeLayout(int position, View view, ViewGroup viewGroup, RFMNativeAdResponse adResponse) {
                return getNativeView(position, view, viewGroup, adResponse);
            }
        };

        rfmNativeAdAdapter = new RFMNativeAdAdapter(this, originalAdapter, AD_POSITIONS, nativeUIHandler, RFMNativeAdAdapter.DEFAULT_REPEAT_INTERVAL);
        rfmNativeAdAdapter.setServerName(serverName);
        mNewsFeedList.setAdapter(rfmNativeAdAdapter);
    }

    /**
     * Method to load Ad
     */
    @Override
    public void loadAd() {
        if(rfmNativeAdAdapter != null) {
            rfmNativeAdAdapter.loadAds(RFMConstants.NATIVE_AD_PREDEFINED_PLACEMENT_ID, mRfmPubId);
        }
        if(footerView != null) {
            footerView.setEnabled(true);
            loadMoreButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateAdView() {
    }

    private void addLoadMoreFooter() {
        if(mNewsFeedItemList == null && inflater==null) {
            return;
        }
        footerView = inflater.inflate(R.layout.addmore_footer, null, false);
        loadMoreButton = (TextView) footerView.findViewById(R.id.loadmore);

        if(footerView != null) {
            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreFeed();
                }
            });
        }
        footerView.setEnabled(true);
        mNewsFeedList.setFooterDividersEnabled(true);
        mNewsFeedList.addFooterView(footerView);
    }

    private void loadMoreFeed() {
        if(loadMore) {
            mNewsFeedItemList.addAll(FeedData.loadMore(1));
            originalAdapter.updateData(mNewsFeedItemList);
            loadMore=false;
            loadMoreButton.setVisibility(View.INVISIBLE);
            footerView.setEnabled(false);
        }
    }

    private void initializeFeed() {
        mNewsFeedItemList = FeedData.generateFeedItems(20);
    }

    public void refreshAds() {
        cleanupData();
        originalAdapter.updateData(new ArrayList<FeedData.FeedItem>(0));

        // Initialize Native News Feed list
        initializeFeed();
        initOrigAdapter(mNewsFeedItemList);
        // initialize RFM Native Adapter
        initRFMNativeAdapter();
        loadAd();
        loadMore=true;
        // Setting new scroll position
        mNewsFeedList.setSelectionFromTop(0, 0);

    }
    class NativeViewHolder{
        TextView title;
        TextView description;
        TextView ctaText;
        TextView sponsored;
        TextView rating;
        SimpleDraweeView adChoicesIcon;
        SimpleDraweeView icon;
    }

    public class NewsFeedAdapter extends ArrayAdapter<FeedData.FeedItem> {

        private Context mContext;
        private LayoutInflater mInflater;
        private List<FeedData.FeedItem> mItems;

        class ViewHolder {
            TextView title;
            TextView ctaText;
            TextView description;
            TextView sponsored;
            TextView rating;
            SimpleDraweeView icon;
            SimpleDraweeView adChoicesIcon;
        }

        NewsFeedAdapter(Context context, List<FeedData.FeedItem> items) {
            super(context, R.layout.news_feed_view, items);
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mItems = items;
        }

        public void updateData(final List<FeedData.FeedItem> items) {
                mItems = items;
                notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeedData.FeedItem newsTileItem;
            if(position< mItems.size()){
                newsTileItem = mItems.get(position);
            }else{
                newsTileItem = mItems.get(mItems.size()-1);
            }
            ViewHolder viewHolder;
            View rowView = convertView;

            if (null == rowView || rowView.getTag() == null) {
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
                viewHolder.title.setText(newsTileItem.getTitle());
                viewHolder.icon.setImageURI(Uri.parse(newsTileItem.getThumbImage()));
                viewHolder.description.setText(newsTileItem.getDescription());
            }
            return rowView;
        }
    }

    public View getNativeView(int position, View convertView, ViewGroup parent,final RFMNativeAdResponse adResponse) {
        if (adViewMap.containsKey(position)) {
            return adViewMap.get(position);
        }
        if (inflater == null) {
            return null;
        }
        try {

            View rowView = inflater.inflate(R.layout.news_feed_view, parent, false);

            if (adResponse == null) {
                rowView.setVisibility(View.GONE);
                return rowView;
            }

            rowView.setVisibility(View.VISIBLE);
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
            mAdResponseMap.put(position, adResponse);
            adViewMap.put(position, rowView);
            return rowView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cleanupData() {
        for(RFMNativeAdResponse response:mAdResponseMap.values()) {
            try {
                response.unregisterView();
                response.destroy();
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed to clean up response "+e.getMessage());
            }
        }
        for(View view:adViewMap.values()) {
            try {
                view = null;
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed to clean up ad view "+e.getMessage());
            }
        }
        adViewMap.clear();
        rfmNativeAdAdapter.destroy();
        mNewsFeedItemList.clear();
        originalAdapter.clear();

    }
}