/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public final class FeedData {

    private static FeedItem[] mSampleFeedItems = new FeedItem[20];
    private static FeedItem[] mSampleChatItems = new FeedItem[5];
    public static FeedItem[] mSampleFeedLoadMore1 = new FeedItem[10];
    public static FeedItem[] mSampleFeedLoadMore2 = new FeedItem[10];

    static {
        mSampleFeedItems[0] = new FeedItem("0 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedItems[1] = new FeedItem("1 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedItems[2] = new FeedItem("2 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedItems[3] = new FeedItem("3 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedItems[4] = new FeedItem("4 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
        mSampleFeedItems[5] = new FeedItem("5 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedItems[6] = new FeedItem("6 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedItems[7] = new FeedItem("7 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedItems[8] = new FeedItem("8 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedItems[9] = new FeedItem("9 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
        mSampleFeedItems[10] = new FeedItem("10 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedItems[11] = new FeedItem("11 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedItems[12] = new FeedItem("12 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedItems[13] = new FeedItem("13 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedItems[14] = new FeedItem("14 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
        mSampleFeedItems[15] = new FeedItem("15 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedItems[16] = new FeedItem("16 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedItems[17] = new FeedItem("17 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedItems[18] = new FeedItem("18 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedItems[19] = new FeedItem("19 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");

        // Load more 1
        mSampleFeedLoadMore1[0] = new FeedItem("20 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedLoadMore1[1] = new FeedItem("21 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedLoadMore1[2] = new FeedItem("22 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedLoadMore1[3] = new FeedItem("23 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedLoadMore1[4] = new FeedItem("24 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
        mSampleFeedLoadMore1[5] = new FeedItem("25 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedLoadMore1[6] = new FeedItem("26 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedLoadMore1[7] = new FeedItem("27 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedLoadMore1[8] = new FeedItem("28 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedLoadMore1[9] = new FeedItem("29 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");

        // Load more 2
        mSampleFeedLoadMore2[0] = new FeedItem("30 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedLoadMore2[1] = new FeedItem("31 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedLoadMore2[2] = new FeedItem("32 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedLoadMore2[3] = new FeedItem("33 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedLoadMore2[4] = new FeedItem("34 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
        mSampleFeedLoadMore2[5] = new FeedItem("35 Sample news", "Product Manager", "1:50 AM", "Looking out for a Manager with 5+ yrs exp for a Rubicon Buyer cloud with strong grasp of media planning principles & excellent understanding of target segment, market intelligence and media medium technicalities.", "user_icon", "user_icon");
        mSampleFeedLoadMore2[6] = new FeedItem("36 Sample news", "HR", "9:50 AM", "Please pray for these children in Syria after the death of their mother. The oldest sister has to take care of her younger siblings. -Ayad L Gorgees. ***Please don't scroll past without Typing Amen! because they need our prayers!!", "user_icon", "user_icon");
        mSampleFeedLoadMore2[7] = new FeedItem("37 Sample news", "Founder at Rubicon Project", "4:50 PM", "Why, dear God, haven't you started marketing yet?", "user_icon", "user_icon");
        mSampleFeedLoadMore2[8] = new FeedItem("38 Sample news", "CPO", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleFeedLoadMore2[9] = new FeedItem("39 Sample news", "CEO at Rubicon Project", "4:10 AM", "Honored to represent Rubicon team ", "user_icon", "user_icon");
    }

    static{
        mSampleChatItems[0] = new FeedItem("Sample news", "", "1:50 AM", "Check out their website", "user_icon", "user_icon");
        mSampleChatItems[1] = new FeedItem("Sample news", "", "9:50 AM", "Hi! How are you?", "user_icon", "user_icon");
        mSampleChatItems[2] = new FeedItem("Sample news", "", "4:50 PM", "Well, see you then.", "user_icon", "user_icon");
        mSampleChatItems[3] = new FeedItem("Sample news", "", "6:50 PM", "With mobile being accepted as the definitive medium to access consumers’ minds and wallets, Brands have begun a multi-million dollar spending race to allure and retain customers. ", "user_icon", "user_icon");
        mSampleChatItems[4] = new FeedItem("Sample news", "", "4:10 AM", "Let's watch the match tonight", "user_icon", "user_icon");
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
//        for (int i = 0; i < count; i++) {
//            feedItems.add(mSampleFeedItems[random.nextInt(mSampleFeedItems.length)]);
//        }
        for (int i = 0; i < count; i++) {
            feedItems.add(mSampleFeedItems[i]);
        }
        return feedItems;
    }

    public static ArrayList<FeedItem> loadMore(int refreshCount) {
        ArrayList<FeedItem> feedItems = null;
            if(refreshCount == 1) {
                feedItems = new ArrayList<>(mSampleFeedLoadMore1.length);
                for (int i = 0; i < mSampleFeedLoadMore1.length; i++) {
                    feedItems.add(mSampleFeedLoadMore1[i]);
                }
            } else if(refreshCount == 2) {
                feedItems = new ArrayList<>(mSampleFeedLoadMore2.length);
                for (int i = 0; i < mSampleFeedLoadMore2.length; i++) {
                    feedItems.add(mSampleFeedLoadMore2[i]);
                }
            }

        if(feedItems != null) {
            Log.v("FeedData", "## Feed Items count " + feedItems.size());
        }
        return feedItems;
    }
}

