/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.rubicon.rfmsample.RFMAd.AdType;

public class JsonToSQLLite {

    private static final String LINE_FEED = "\r\n";

    public static String readFileToString(String filename) {

        String everything = "";
        try {

            BufferedReader br = new BufferedReader(new FileReader(filename));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(LINE_FEED);
                    line = br.readLine();
                }
                everything = sb.toString();
            } finally {
                br.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return everything;

    }

    public static boolean saveToDB(Context context, String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("testcases");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);

                final String testCaseName = _jsonObject.getString(RFMAd.TEST_CASE_NAME);

                String serverName;
                if (_jsonObject.has(RFMAd.RFM_SERVER)) {
                    serverName = _jsonObject.getString(RFMAd.RFM_SERVER);
                } else {
                    serverName = "http://mrp.rubiconproject.com";
                }

                final String appId = _jsonObject.getString(RFMAd.APP_ID);
                final String adId = _jsonObject.getString(RFMAd.AD_ID);
                final String pubId = _jsonObject.getString(RFMAd.PUB_ID);

                final AdType adType = RFMAd.AdType.fromActivityClassName(_jsonObject.getString(RFMAd.AD_TYPE));

                RFMAd.LocationType locationType = RFMAd.LocationType.fromLocationName(_jsonObject.getString(RFMAd.LOCATION_TYPE));
                if (locationType == null)
                    locationType = RFMAd.LocationType.IP_BASED;

                String latitudeStr;
                if (_jsonObject.has(RFMAd.LAT)) {
                    latitudeStr = _jsonObject.getString(RFMAd.LAT);
                } else {
                    latitudeStr = "0.0";
                }

                String longitudeStr;
                if (_jsonObject.has(RFMAd.LONG)) {
                    longitudeStr = _jsonObject.getString(RFMAd.LONG);
                } else {
                    longitudeStr = "0.0";
                }

                String adWidthStr;
                if (_jsonObject.has(RFMAd.AD_WIDTH)) {
                    adWidthStr = _jsonObject.getString(RFMAd.AD_WIDTH);
                } else {
                    adWidthStr = "320";
                }
                int adWidth = 320;
                if (adWidthStr != null && !adWidthStr.isEmpty()) {
                    adWidth = Integer.parseInt(adWidthStr);
                }

                String adHeightStr;
                if (_jsonObject.has(RFMAd.AD_HEIGHT)) {
                    adHeightStr = _jsonObject.getString(RFMAd.AD_HEIGHT);
                } else {
                    adHeightStr = "50";
                }
                int adHeight = 50;
                if (adHeightStr != null && !adHeightStr.isEmpty()) {
                    adHeight = Integer.parseInt(adHeightStr);
                }

                boolean testMode;
                if (_jsonObject.has(RFMAd.TEST_MODE)) {
                    testMode = _jsonObject.getBoolean(RFMAd.TEST_MODE);
                } else {
                    testMode = true;
                }

                boolean fullscreenMode;
                if (_jsonObject.has(RFMAd.FULLSCREEN_MODE)) {
                    fullscreenMode = _jsonObject.getBoolean(RFMAd.FULLSCREEN_MODE);
                } else {
                    fullscreenMode = false;
                }

                boolean cachedAdMode;
                if (_jsonObject.has(RFMAd.CACHED_AD_MODE)) {
                    cachedAdMode = _jsonObject.getBoolean(RFMAd.CACHED_AD_MODE);
                } else {
                    cachedAdMode = false;
                }

                boolean videoAdMode;
                if (_jsonObject.has(RFMAd.VIDEO_AD_MODE)) {
                    videoAdMode = _jsonObject.getBoolean(RFMAd.VIDEO_AD_MODE);
                } else {
                    videoAdMode = false;
                }

                final RFMAd rfmAd = new RFMAd(-1, testCaseName, appId, adType,
                        1, 0, locationType, "0", latitudeStr, longitudeStr,
                        "", adWidth, adHeight, testMode, fullscreenMode, cachedAdMode, videoAdMode, adId, true, serverName, appId, pubId, 0);

                RFMAdDataSource rfmAdDataSource = RFMAdDataSource.getInstance(context);
                rfmAdDataSource.createAdUnit(rfmAd);

            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
