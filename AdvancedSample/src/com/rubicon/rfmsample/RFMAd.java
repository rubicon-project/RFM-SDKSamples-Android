/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.app.Activity;
import android.os.Bundle;

public class RFMAd implements Comparable<RFMAd> {

    public static final String ID = "id";
    public static final String TEST_CASE_NAME = "testCaseName";
    public static final String SITE_ID = "siteId";
    public static final String AD_TYPE = "adType";
    public static final String REFRESH_COUNT = "refreshCount";
    public static final String REFRESH_INTERVAL = "refreshInterval";
    public static final String LOCATION_TYPE = "locationType";
    public static final String LOCATION_PRECISION = "locationPrecision";
    public static final String LAT = "latitude";
    public static final String LONG = "longitude";
    public static final String TARGETING_KEY_VALUE = "targetingKeyValue";
    public static final String AD_WIDTH = "adWidth";
    public static final String AD_HEIGHT = "adHeight";
    public static final String TEST_MODE = "testMode";
    public static final String AD_ID = "adId";
    public static final String IS_CUSTOM = "isCustom";
    public static final String FULLSCREEN_MODE = "fullscreenMode";
    public static final String CACHED_AD_MODE = "cachedAdMode";
    public static final String VIDEO_AD_MODE = "videoAdMode";

    private long mId;
    private String mTestCaseName;
    private String mSiteId;
    private AdType mAdType;
    private int mRefreshCount;
    private int mRefreshInterval;
    private LocationType mLocationType;
    private String mLocationPrecision;
    private String mLat;
    private String mLong;
    private String mTargetingKeyValue;
    private int mAdWidth;
    private int mAdHeight;
    private boolean mTestMode;
    private String mAdId;
    private boolean mIsCustom;
    private boolean mFullscreenMode;
    private boolean mCachedAdMode;
    private boolean mVideoAdMode;
    private int mCount = 0;

    // RFM Sample specific
    public static final String RFM_SERVER = "rfmServer";
    public static final String APP_ID = "appId";
    public static final String PUB_ID = "pubId";
    private String mRFMServer;
    private String mAppId;
    private String mPubId;

    enum LocationType {
        NONE ("NONE"),
        FIXED ("FIXED"),
        IP_BASED ("IP_BASED"),
        GPS_BASED ("GPS_BASED");

        private final String locType;
        private LocationType(final String _locType) {
            this.locType = _locType;
        }

        String getLocType() {
            return locType;
        }

        static LocationType fromLocationName(final String locationName) {
            for (final LocationType locType : values()) {
                if (locType.getLocType().equals(locationName)) {
                    return locType;
                }
            }
            return null;
        }
    }

    enum AdType {
        BANNER ("Banner", SimpleBanner.class),
        INTERSTITIAL ("Interstitial", InterstitialAd.class),
        BANNER_IN_LIST ("BannerInList", BannerInList.class),
        CACHED_AD ("CachedAd", CachedAd.class);

        String getName() {
            return name;
        }

        private final String name;
        private final Class<? extends Activity> activityClass;

        private AdType(final String name, final Class<? extends Activity> fragmentClass) {
            this.name = name;
            this.activityClass = fragmentClass;
        }

        Class<? extends Activity> getActivityClass() {
            return activityClass;
        }

        static AdType fromActivityClassName(final String activityClassName) {
            for (final AdType adType : values()) {
                if (adType.activityClass.getName().equals(activityClassName)) {
                    return adType;
                }
            }

            return null;
        }
    }

    public RFMAd(long id, String _testCaseName, String _siteId, AdType _adType, int _refreshCount,
                 int _refreshInterval, LocationType _locationType, String _locationPrecision, String _lat, String _long,
                 String _targetingKeyValue, int _adWidth, int _adHeight,
                 boolean _testMode, boolean _fullscreenMode, boolean _cachedAdMode, boolean _videoAdMode, String _adId, boolean _isCustom,
                 String _rfmServer, String _appId, String _pubId) {
        mId = id;
        mTestCaseName = _testCaseName;
        mSiteId = _siteId;
        mAdType = _adType;
        mRefreshCount = _refreshCount;
        mRefreshInterval = _refreshInterval;
        mLocationType = _locationType;
        mLocationPrecision = _locationPrecision;
        mLat = _lat;
        mLong = _long;
        mTargetingKeyValue = _targetingKeyValue;
        mAdWidth = _adWidth;
        mAdHeight = _adHeight;
        mTestMode = _testMode;
        mFullscreenMode = _fullscreenMode;
        mCachedAdMode = _cachedAdMode;
        mVideoAdMode = _videoAdMode;
        mAdId = _adId;
        mIsCustom = _isCustom;

        mRFMServer = _rfmServer;
        mAppId = _appId;
        mPubId = _pubId;
    }

    public Class<? extends Activity> getActivityClass() {
        return mAdType.getActivityClass();
    }

    public String getTestCaseName() {
        return mTestCaseName;
    }

    public String getSiteId() {
        return mSiteId;
    }

    public int getRefreshCount() {
        return mRefreshCount;
    }

    public int getRefreshInterval() {
        return mRefreshInterval;
    }

    public LocationType getLocationType() {
        return mLocationType;
    }

    public String getLocationPrecision() {
        return mLocationPrecision;
    }

    public String getLat() {
        return mLat;
    }

    public String getLong() {
        return mLong;
    }

    public String getTargetingKeyValue() {
        return mTargetingKeyValue;
    }

    public int getAdWidth() {
        return mAdWidth;
    }

    public int getAdHeight() {
        return mAdHeight;
    }

    public boolean getTestMode() {
        return mTestMode;
    }

    public boolean getFullscreenMode() {
        return mFullscreenMode;
    }

    public boolean getCachedAdMode() {
        return mCachedAdMode;
    }

    public boolean getVideoAdMode() {
        return mVideoAdMode;
    }

    public String getAdId() {
        return mAdId;
    }

    public boolean getAdIsCustom() {
        return mIsCustom;
    }


    public String getRfmServer() {
        return mRFMServer;
    }
    public String getAppId() {
        return mAppId;
    }
    public String getPubId() {
        return mPubId;
    }

    public AdType getAdType() {
        return mAdType;
    }

    public String getAdTypeName() {
        return mAdType.getActivityClass().getName();
    }

    public long getId() {
        return mId;
    }


    public String getActivityClassName() {
        return mAdType.getActivityClass().getName();
    }

    public String getHeaderName() {
        return mAdType.name;
    }

    public boolean isCustom() {
        return mIsCustom;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putLong(ID, mId);
        bundle.putString(TEST_CASE_NAME, mTestCaseName);
        bundle.putString(SITE_ID, mSiteId);
        bundle.putSerializable(AD_TYPE, mAdType);
        bundle.putInt(REFRESH_COUNT, mRefreshCount);
        bundle.putInt(REFRESH_INTERVAL, mRefreshInterval);
        bundle.putSerializable(LOCATION_TYPE, mLocationType);
        bundle.putString(LOCATION_PRECISION, mLocationPrecision);
        bundle.putString(LAT, mLat);
        bundle.putString(LONG, mLong);
        bundle.putString(TARGETING_KEY_VALUE, mTargetingKeyValue);
        bundle.putInt(AD_WIDTH, mAdWidth);
        bundle.putInt(AD_HEIGHT, mAdHeight);
        bundle.putBoolean(TEST_MODE, mTestMode);
        bundle.putBoolean(FULLSCREEN_MODE, mFullscreenMode);
        bundle.putBoolean(CACHED_AD_MODE, mCachedAdMode);
        bundle.putBoolean(VIDEO_AD_MODE, mVideoAdMode);
        bundle.putString(AD_ID, mAdId);
        bundle.putBoolean(IS_CUSTOM, mIsCustom);

        // RFM Sample specific
        bundle.putString(RFM_SERVER, mRFMServer);
        bundle.putString(APP_ID, mAppId);
        bundle.putString(PUB_ID, mPubId);

        return bundle;
    }

    public static RFMAd fromBundle(final Bundle bundle) {
        final Long id = bundle.getLong(ID, -1L);
        final String testCaseName = bundle.getString(TEST_CASE_NAME);
        final String siteId = bundle.getString(SITE_ID);
        final AdType adType = (AdType) bundle.getSerializable(AD_TYPE);
        final int refreshCount = bundle.getInt(REFRESH_COUNT);
        final int refreshInterval = bundle.getInt(REFRESH_INTERVAL);
        final LocationType locationType = (LocationType) bundle.getSerializable(LOCATION_TYPE);
        final String locationPrecision = bundle.getString(LOCATION_PRECISION);
        final String lat = bundle.getString(LAT);
        final String lng = bundle.getString(LONG);
        final String targetingKeyValue = bundle.getString(TARGETING_KEY_VALUE);
        final int adWidth = bundle.getInt(AD_WIDTH);
        final int adHeight = bundle.getInt(AD_HEIGHT);
        final boolean testMode = bundle.getBoolean(TEST_MODE, true);
        final boolean fullscreenMode = bundle.getBoolean(FULLSCREEN_MODE, false);
        final boolean cachedAdMode = bundle.getBoolean(CACHED_AD_MODE, false);
        final boolean videoAdMode = bundle.getBoolean(VIDEO_AD_MODE, false);
        final String adId = bundle.getString(AD_ID);
        final boolean isCustom = bundle.getBoolean(IS_CUSTOM, false);

        // RFM Sample specific
        final String rfmServer = bundle.getString(RFM_SERVER);
        final String appId = bundle.getString(APP_ID);
        final String pubId = bundle.getString(PUB_ID);

        final RFMAd rfmAd = new RFMAd(id, testCaseName, siteId, adType, refreshCount, refreshInterval,
                                locationType, locationPrecision, lat, lng, targetingKeyValue, adWidth, adHeight, testMode,
                                fullscreenMode, cachedAdMode, videoAdMode, adId, isCustom, rfmServer, appId, pubId);
        return rfmAd;
    }

    @Override
    public int compareTo(RFMAd ad) {
        if (mAdType != ad.mAdType) {
            return mAdType.ordinal() - ad.mAdType.ordinal();
        }

        return mAppId.compareTo(ad.mAppId);
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + mAdType.ordinal();
        result = 31 * result + mAppId.hashCode();
        result = 31 * result + mAdId.hashCode();
        result = 31 * result + (mIsCustom ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof RFMAd)) {
            return false;
        }

        final RFMAd that = (RFMAd) o;

        return that.mAdType.equals(this.mAdType) &&
                that.mAppId.equals(this.mAppId) &&
                that.mAdId.equals(this.mAdId) &&
                that.mIsCustom == this.mIsCustom;
    }
}
