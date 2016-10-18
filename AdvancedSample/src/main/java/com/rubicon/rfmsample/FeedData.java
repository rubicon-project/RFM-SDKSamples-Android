/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import java.util.ArrayList;
import java.util.Random;

public final class FeedData {

    private static FeedItem[] mSampleFeedItems = new FeedItem[5];
    private static FeedItem[] mSampleChatItems = new FeedItem[5];

    static {
        mSampleFeedItems[0] = new FeedItem("Rubicon Project", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedItems[1] = new FeedItem("Rubicon Project", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedItems[2] = new FeedItem("Rubicon Project", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedItems[3] = new FeedItem("Rubicon Project", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedItems[4] = new FeedItem("Rubicon Project", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
    }

    static{
        mSampleChatItems[0] = new FeedItem("Rubicon Project", "", "1:50 AM", "Check out their website", "user_icon", "user_icon");
        mSampleChatItems[1] = new FeedItem("Rubicon Project", "", "9:50 AM", "Hi! How are you?", "user_icon", "user_icon");
        mSampleChatItems[2] = new FeedItem("Rubicon Project", "", "4:50 PM", "Well, see you then.", "user_icon", "user_icon");
        mSampleChatItems[3] = new FeedItem("Rubicon Project", "", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleChatItems[4] = new FeedItem("Rubicon Project", "", "4:10 AM", "Let's watch the match tonight", "user_icon", "user_icon");
    }

    public static class FeedItem {
        private String title;
        private String subtitle;
        private String timestamp;
        private String description;
        private String thumbImage;
        private String bigImage;

        FeedItem(String title, String subtitle, String time_tt, String description_tt, String thumb_image, String big_image) {
            this.title = title;
            this.subtitle = subtitle;
            this.timestamp = time_tt;
            this.description = description_tt;
            this.thumbImage = thumb_image;
            this.bigImage = big_image;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getDescription() {
            return description;
        }

        public String getThumbImage() {
            return thumbImage;
        }

        public String getBigImage() {
            return bigImage;
        }

    }

    public static ArrayList<FeedItem> generateChatItems(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("generateFeedItems - Invalid count:" + count);
        }
        final ArrayList<FeedItem> feedItems = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            feedItems.add(mSampleChatItems[random.nextInt(mSampleChatItems.length)]);
        }
        return feedItems;
    }


    public static ArrayList<FeedItem> generateFeedItems(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("generateFeedItems - Invalid count:" + count);
        }
        final ArrayList<FeedItem> feedItems = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            feedItems.add(mSampleFeedItems[random.nextInt(mSampleFeedItems.length)]);
        }
        return feedItems;
    }
}

