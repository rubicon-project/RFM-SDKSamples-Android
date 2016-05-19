/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rubicon.rfmsample.RFMAd.AdType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_AD_HEIGHT;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_AD_ID;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_AD_TYPE;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_AD_WIDTH;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_APP_ID;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_CACHED_AD_MODE;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_VIDEO_AD_MODE;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_FULL_SCREEN_MODE;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_ID;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_IS_CUSTOM;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_LAT;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_LOCATION_PRECISION;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_LOCATION_TYPE;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_LONG;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_PUB_ID;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_REFRESH_COUNT;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_REFRESH_INTERVAL;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_RFM_SERVER;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_SITE_ID;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_TARGETING_KEY_VALUE;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_TEST_CASE_NAME;
import static com.rubicon.rfmsample.SQLiteHelper.COLUMN_TEST_MODE;
import static com.rubicon.rfmsample.SQLiteHelper.SAMPLE_ADS_TABLE;

public class RFMAdDataSource {

    String LOG_TAG = "RFMAdDataSource";
    private static RFMAdDataSource rfmAdDataSourceInstance = null;
    private SQLiteHelper mDatabaseHelper;
    private String[] mAllColumns = {
            COLUMN_ID,
            COLUMN_TEST_CASE_NAME,
            COLUMN_SITE_ID,
            COLUMN_AD_TYPE,
            COLUMN_REFRESH_COUNT,
            COLUMN_REFRESH_INTERVAL,
            COLUMN_LOCATION_TYPE,
            COLUMN_LOCATION_PRECISION,
            COLUMN_LAT,
            COLUMN_LONG,
            COLUMN_TARGETING_KEY_VALUE,
            COLUMN_AD_WIDTH,
            COLUMN_AD_HEIGHT,
            COLUMN_TEST_MODE,
            COLUMN_FULL_SCREEN_MODE,
            COLUMN_CACHED_AD_MODE,
            COLUMN_VIDEO_AD_MODE,
            COLUMN_AD_ID,
            COLUMN_IS_CUSTOM,
            COLUMN_RFM_SERVER,
            COLUMN_APP_ID,
            COLUMN_PUB_ID,
    };

    private RFMAdDataSource(final Context context) {
        mDatabaseHelper = new SQLiteHelper(context);
    }

    public static RFMAdDataSource getInstance(Context context) {
        if (rfmAdDataSourceInstance == null) {
            synchronized (RFMAdDataSource.class) {
                if (rfmAdDataSourceInstance == null) {
                    rfmAdDataSourceInstance = new RFMAdDataSource(context);
                }
            }
        }

        return rfmAdDataSourceInstance;
    }


    RFMAd createAdUnit(final RFMAd rfmAd) {
        try {
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
            values.put(COLUMN_FULL_SCREEN_MODE, rfmAd.getFullscreenMode());
            values.put(COLUMN_CACHED_AD_MODE, rfmAd.getCachedAdMode());
            values.put(COLUMN_VIDEO_AD_MODE, rfmAd.getVideoAdMode());
            values.put(COLUMN_AD_ID, rfmAd.getAdId());
            values.put(COLUMN_IS_CUSTOM, rfmAd.getAdIsCustom());

            values.put(COLUMN_RFM_SERVER, rfmAd.getRfmServer());
            values.put(COLUMN_APP_ID, rfmAd.getAppId());
            values.put(COLUMN_PUB_ID, rfmAd.getPubId());


            final SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
            final long insertId = database.insert(SAMPLE_ADS_TABLE, null, values);
            final Cursor cursor = database.query(SAMPLE_ADS_TABLE, mAllColumns,
                    COLUMN_ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            final RFMAd newAdConfiguration = cursorToAdConfiguration(cursor);
            cursor.close();
            database.close();
            return newAdConfiguration;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    RFMAd getRowById(long id) {
        RFMAd selectedAdConfiguration = null;
        String sqlQuery = "select * from " + SAMPLE_ADS_TABLE + " where _id=" + id;
        final SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = database.rawQuery(sqlQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                selectedAdConfiguration = cursorToAdConfiguration(cursor);
            cursor.close();
        }
        database.close();
        return selectedAdConfiguration;
    }

    void deleteSampleAdUnit(final RFMAd rfmAd) {
        final long id = rfmAd.getId();
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        database.delete(SAMPLE_ADS_TABLE, COLUMN_ID + " = " + id, null);
        Log.d(LOG_TAG, "Ad Configuration deleted with id: " + id);
        database.close();
    }

    void updateSampleAdUnit(final long adUnitId, HashMap<String, String> newValuesHashMap) {
        ContentValues newValues = new ContentValues();

        for (Map.Entry<String, String> entry : newValuesHashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.equals(COLUMN_REFRESH_COUNT) || key.equals(COLUMN_REFRESH_INTERVAL) ||
                    key.equals(COLUMN_AD_WIDTH) || key.equals(COLUMN_AD_HEIGHT))
                newValues.put(key, Integer.parseInt(value));
            else
                newValues.put(key, value);
        }

        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        if (adUnitId != -1) {
            database.update(SAMPLE_ADS_TABLE, newValues, COLUMN_ID + "=" + adUnitId, null);
        } else {
            // update all rows with same value
            database.update(SAMPLE_ADS_TABLE, newValues, null, null);
        }

    }

    List<RFMAd> getAllAdUnits() {
        final List<RFMAd> allRFMAds = new ArrayList<RFMAd>();
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = database.query(SAMPLE_ADS_TABLE,
                mAllColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            final RFMAd rfmAd = cursorToAdConfiguration(cursor);
            allRFMAds.add(rfmAd);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();
        return allRFMAds;
    }

    private RFMAd cursorToAdConfiguration(final Cursor cursor) {
        final long id = cursor.getLong(0);
        final String testCaseName = cursor.getString(1);
        final String siteId = cursor.getString(2);
        final AdType adType = AdType.fromActivityClassName(cursor.getString(3));
        final int refreshCount = cursor.getInt(4);
        final int refreshInterval = cursor.getInt(5);
        final String locationType = cursor.getString(6);
        final String locationPrecision = cursor.getString(7);
        final String lat = cursor.getString(8);
        final String lng = cursor.getString(9);
        final String targetingKeyValue = cursor.getString(10);
        final int adWidth = cursor.getInt(11);
        final int adHeight = cursor.getInt(12);
        final int testMode = cursor.getInt(13);
        final int fullscreenMode = cursor.getInt(14);
        final int cachedAdMode = cursor.getInt(15);
        final int videoAdMode = cursor.getInt(16);
        final String adId = cursor.getString(17);
        final int isCustom = cursor.getInt(18);

        final String rfmServer = cursor.getString(19);
        final String appId = cursor.getString(20);
        final String pubId = cursor.getString(21);

        if (adType == null) {
            return null;
        }

        return new RFMAd(id, testCaseName, siteId, adType, refreshCount, refreshInterval,
                RFMAd.LocationType.fromLocationName(locationType), locationPrecision, lat, lng, targetingKeyValue, adWidth, adHeight,
                testMode == 1, fullscreenMode == 1, cachedAdMode == 1, videoAdMode == 1, adId, isCustom == 1, rfmServer, appId, pubId, 0);
    }

    public void cleanUp() {
        if(mDatabaseHelper != null) {
            mDatabaseHelper.close();
            mDatabaseHelper = null;
            rfmAdDataSourceInstance = null;
        }
    }
}
