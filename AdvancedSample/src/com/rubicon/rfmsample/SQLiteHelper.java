/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String SAMPLE_ADS_TABLE = "sampleads";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEST_CASE_NAME = "testCaseName";
    public static final String COLUMN_SITE_ID = "siteId";
    public static final String COLUMN_AD_TYPE = "adType";
    public static final String COLUMN_REFRESH_COUNT = "refreshCount";
    public static final String COLUMN_REFRESH_INTERVAL = "refreshInterval";
    public static final String COLUMN_LOCATION_TYPE = "locationType";
    public static final String COLUMN_LOCATION_PRECISION = "locationPrecision";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_TARGETING_KEY_VALUE = "targetingKeyValue";
    public static final String COLUMN_AD_WIDTH = "adWidth";
    public static final String COLUMN_AD_HEIGHT = "adHeight";
    public static final String COLUMN_TEST_MODE = "testMode";
    public static final String COLUMN_FULL_SCREEN_MODE = "fullscreenMode";
    public static final String COLUMN_CACHED_AD_MODE = "cachedAdMode";
    public static final String COLUMN_VIDEO_AD_MODE = "videoAdMode";
    public static final String COLUMN_AD_ID = "adId";
    public static final String COLUMN_IS_CUSTOM = "isCustom";

    public static final String COLUMN_RFM_SERVER = "rfmServer";
    public static final String COLUMN_APP_ID = "appId";
    public static final String COLUMN_PUB_ID = "pubId";

    private static final String DATABASE_NAME = "sampleadsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + SAMPLE_ADS_TABLE
            + " ("

            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TEST_CASE_NAME + " text not null, "
            + COLUMN_SITE_ID + " text not null, "
            + COLUMN_AD_TYPE + " text not null, "
            + COLUMN_REFRESH_COUNT + " integer not null, "
            + COLUMN_REFRESH_INTERVAL + " integer not null, "
            + COLUMN_LOCATION_TYPE + " text not null, "
            + COLUMN_LOCATION_PRECISION + " text not null, "
            + COLUMN_LAT + " text not null, "
            + COLUMN_LONG + " text not null, "
            + COLUMN_TARGETING_KEY_VALUE + " text not null, "
            + COLUMN_AD_WIDTH + " integer not null, "
            + COLUMN_AD_HEIGHT + " integer not null, "
            + COLUMN_TEST_MODE + " integer not null, "
            + COLUMN_FULL_SCREEN_MODE + " integer not null, "
            + COLUMN_CACHED_AD_MODE + " integer not null, "
            + COLUMN_VIDEO_AD_MODE + " integer not null, "
            + COLUMN_AD_ID + " text not null, "
            + COLUMN_IS_CUSTOM + " integer not null, "

            + COLUMN_RFM_SERVER + " text not null, "
            + COLUMN_APP_ID + " text not null, "
            + COLUMN_PUB_ID + " text not null "

            + ");";

    //private final Context mContext;

    public SQLiteHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final List<RFMAd> rfmAdList = new ArrayList<RFMAd>();

        rfmAdList.add(new RFMAd(-1, "Simple Banner", "",
                RFMAd.AdType.BANNER, 1, 0, RFMAd.LocationType.FIXED, "6","0.0", "0.0",
                "", 320, 50, true, false, false, false, "28401", false, "http://mrp.rubiconproject.com", "4A24CB908FB3013209C822000B2E019E", "111008"));

        rfmAdList.add(new RFMAd(-1, "Interstitial", "",
                RFMAd.AdType.INTERSTITIAL, 1, 0, RFMAd.LocationType.FIXED, "6", "0.0", "0.0",
                "", 320, 50, true, true, false, false, "28854", false, "http://mrp.rubiconproject.com", "FB4FB140C13601320C4722000B3510F7", "111008"));

        rfmAdList.add(new RFMAd(-1, "Banner in List View", "",
                RFMAd.AdType.BANNER_IN_LIST, 1, 0, RFMAd.LocationType.FIXED, "6", "0", "0",
                "", 320, 50, true, false, false, false, "0", false, "http://mrp.rubiconproject.com", "281844F0497A0130031D123139244773", "111008"));

        rfmAdList.add(new RFMAd(-1, "Vast Video Ad with Skip", "",
                RFMAd.AdType.INTERSTITIAL, 1, 0, RFMAd.LocationType.FIXED, "6", "0", "0",
                "", 320, 50, true, true, false, true, "0", false, "http://mrp.rubiconproject.com", "D0B80A70B6E30133107922000B3510F7", "111315"));

        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.beginTransaction();

        for (final RFMAd rfmAd : rfmAdList) {
            final ContentValues values = new ContentValues();
            values.put(COLUMN_TEST_CASE_NAME, rfmAd.getTestCaseName());
            values.put(COLUMN_SITE_ID, rfmAd.getSiteId());
            values.put(COLUMN_AD_TYPE, rfmAd.getActivityClassName());
            values.put(COLUMN_REFRESH_COUNT, rfmAd.getRefreshCount());
            values.put(COLUMN_REFRESH_INTERVAL, rfmAd.getRefreshInterval());
            values.put(COLUMN_LOCATION_TYPE, rfmAd.getLocationType().getLocType());
            values.put(COLUMN_LOCATION_PRECISION, rfmAd.getLocationPrecision());
            values.put(COLUMN_LAT, rfmAd.getLat());
            values.put(COLUMN_LONG, rfmAd.getLong());
            values.put(COLUMN_TARGETING_KEY_VALUE, rfmAd.getTargetingKeyValue());
            values.put(COLUMN_AD_WIDTH, rfmAd.getAdWidth());
            values.put(COLUMN_AD_HEIGHT, rfmAd.getAdHeight());
            values.put(COLUMN_TEST_MODE, rfmAd.getTestMode());
            values.put(COLUMN_AD_ID, rfmAd.getAdId());
            values.put(COLUMN_IS_CUSTOM, 0);
            values.put(COLUMN_FULL_SCREEN_MODE, rfmAd.getFullscreenMode());
            values.put(COLUMN_CACHED_AD_MODE, rfmAd.getCachedAdMode());
            values.put(COLUMN_VIDEO_AD_MODE, rfmAd.getVideoAdMode());
            values.put(COLUMN_RFM_SERVER, rfmAd.getRfmServer());
            values.put(COLUMN_APP_ID, rfmAd.getAppId());
            values.put(COLUMN_PUB_ID, rfmAd.getPubId());

            sqLiteDatabase.insert(SAMPLE_ADS_TABLE, null, values);
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        recreateDb(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        recreateDb(database);
    }

    private void recreateDb(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + SAMPLE_ADS_TABLE);
        onCreate(database);
    }
}
