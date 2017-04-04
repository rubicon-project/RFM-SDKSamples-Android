package com.rubicon.rfmsample;

import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.web.assertion.WebAssertion;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;

import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.AssertionFailedError;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdActivity;
import com.millennialmedia.android.MMActivity;
import com.mopub.common.MoPubBrowser;
import com.mopub.mobileads.MoPubActivity;
import com.mopub.mobileads.MraidActivity;
import com.rfm.sdk.MraidVideoPlayerActivity;
import com.rfm.sdk.RFMActivity;
import com.rfm.sdk.ui.mediator.MRDCreativeView;
import com.rfm.util.RFMCacheManager;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.core.deps.guava.collect.Iterables.getOnlyElement;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.google.android.exoplayer.util.Assertions.checkNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import android.support.test.espresso.IdlingResource;
import android.view.View;
import android.app.Activity;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import android.support.test.internal.runner.lifecycle.ApplicationLifecycleMonitorImpl;
import android.test.InstrumentationTestCase;

import java.util.Collection;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class SimpleMainActivityTest {

    private static boolean clickHiddenButton = true;

    @Rule
    public ActivityTestRule<SampleMainActivity> mActivityRule = new ActivityTestRule<SampleMainActivity>(
            SampleMainActivity.class, false, false) {
        @Override
        protected void afterActivityLaunched() {
            MRDCreativeView.setTesting(true);
        }
    };


    private void launchMainActivityAndSelectRowFetchAd(final String testCaseName, final String adId) {
        System.out.println("##### start " + testCaseName + " #####");

        mActivityRule.launchActivity(new Intent());
        SystemClock.sleep(2000);

        if (clickHiddenButton) {
            for (int i = 0; i < 11; i++) {
                SystemClock.sleep(500);
                onView(withId(R.id.upload_file_to_db)).perform(click());
            }
            SystemClock.sleep(5000);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                UiDevice device = UiDevice.getInstance(getInstrumentation());
                UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
                if (allowPermissions.exists()) {
                    try {
                        allowPermissions.click();
                    } catch (UiObjectNotFoundException e) {
                        System.out.println("There is no permissions dialog to interact with ");
                    }
                }
            }
            clickHiddenButton = false;
            System.out.println("uploaded input.json");
        }

        selectRow(testCaseName, adId);
        IdlingResource ir = waitForView(R.id.fetch_ad);
        onView(withId(R.id.fetch_ad)).perform(click());
        unregisterIdleResource(ir);
    }

    @Test
    public void simple_banner() {
        launchMainActivityAndSelectRowFetchAd("Simple Banner", "28401");

        //SystemClock.sleep(5000);


        IdlingResource ir = waitForView(R.id.bannerviewone);

        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        onWebView()
                .withElement(findElement(Locator.ID, "expandbtn"))
                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.ID, "forceland"))
                .perform(webClick());

        unregisterIdleResource(ir);

        //SystemClock.sleep(1000);

        onWebView()
                .withElement(findElement(Locator.ID, "forceport"))
                .perform(webClick());

        SystemClock.sleep(1000);

        logRequestUrl("Simple Banner");

        System.out.println("##### end " + "Simple Banner" + " #####");
        pressBack();
    }

    @Test
    public void interstitial() {
        launchMainActivityAndSelectRowFetchAd("Interstitial", "28854");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void banner_in_listview() {
        mActivityRule.launchActivity(new Intent());
        SystemClock.sleep(1000);
        selectRow("Banner in List View", "0");
        //SystemClock.sleep(9000);
        //get View object and register with Idle resource polling

        IdlingResource ir = waitForView(R.id.adview);

        onView(withId(R.id.adview)).check(matches(isDisplayed()));
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("Banner in List View");

        //Unregister idle resource
        unregisterIdleResource(ir);

        System.out.println("##### end " + "Banner in List View" + " #####");
        pressBack();
    }

    @Test
    public void vast_ad() {
        launchMainActivityAndSelectRowFetchAd("Vast Ad", "30468");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("Vast Ad");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "Vast Ad" + " #####");
        pressBack();
    }

    @Test
    public void cached_banner_ad() {
        launchMainActivityAndSelectRowFetchAd("Cached Banner Ad", "0");

        //IdlingResource ir = waitForView(R.id.bannerviewone);
        SystemClock.sleep(5000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        //unregisterIdleResource(ir);
        logRequestUrl("Cached Banner Ad");

        System.out.println("##### end " + "Cached Banner Ad" + " #####");
        pressBack();
    }

    @Test
    public void cached_interstitial_ad() {
        launchMainActivityAndSelectRowFetchAd("Cached Interstitial Ad", "28407");

        SystemClock.sleep(5000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));
        // Creative is cached
        String jsonContent = RFMCacheManager.instance(mActivityRule.getActivity()).getString(UITestCaseData.PLACEMENT_CACHEDAD);
        assertThat((jsonContent != null), is(true));

        logRequestUrl("Cached Interstitial Ad");

        onView(withId(R.id.displayad)).perform(click());
        // Cache gets cleared once Ad is displayed
        jsonContent = RFMCacheManager.instance(mActivityRule.getActivity()).getString(UITestCaseData.PLACEMENT_CACHEDAD);
        SystemClock.sleep(5000);
        assertThat((jsonContent == null), is(true));

        System.out.println("##### end " + "Cached Interstitial Ad" + " #####");
        pressBack();
    }

    @Test
    public void mediation_banner() {
        launchMainActivityAndSelectRowFetchAd("Mediation Banner", "0");

        SystemClock.sleep(5000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));
        assertThatLogTextContainsString("RFM Ad: Ad displayed");
        logRequestUrl("Mediation Banner");

        System.out.println("##### end " + "Mediation Banner" + " #####");
        pressBack();
    }

    @Test
    public void mraid_v2_single_expand() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-SingleExpand", "28844");

        SystemClock.sleep(5000);

        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        SystemClock.sleep(1000);

        onWebView()
                .withElement(findElement(Locator.ID, "expandbtn"))
                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.ID, "forceland"))
                .perform(webClick());

        SystemClock.sleep(2000);

        onWebView()
                .withElement(findElement(Locator.ID, "forceport"))
                .perform(webClick());

        SystemClock.sleep(2000);

        logRequestUrl("MRAIDV2-ResizeAd");

        System.out.println("##### end " + "MRAIDV2-SingleExpand" + " #####");
        pressBack();
    }

    @Test
    public void mraid_v2_two_part_expand() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-TwoPartExpand", "28845");

        SystemClock.sleep(5000);

        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        SystemClock.sleep(1000);

//        onWebView()
//                .withElement(findElement(Locator.ID, "twopart"))
//                .perform(webClick());
//
//        SystemClock.sleep(1000);
//
//        onWebView()
//                .withElement(findElement(Locator.ID, "openiab"))
//                .perform(webClick());
//
//        SystemClock.sleep(4000);
//
//        pressBack();
//
//        onWebView()
//                .withElement(findElement(Locator.ID, "expandtest"))
//                .perform(webClick());
//
//        SystemClock.sleep(1000);
//
//        onWebView()
//                .withElement(findElement(Locator.ID, "openvideo"))
//                .perform(webClick());
//
//        SystemClock.sleep(40000);
//
//        onWebView()
//                .withElement(findElement(Locator.ID, "close"))
//                .perform(webClick());

        logRequestUrl("MRAIDV2-TwoPartExpand");

        System.out.println("##### end " + "MRAIDV2-TwoPartExpand" + " #####");
        pressBack();
    }

    @Test
    public void mraid_v2_resize_ad() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-ResizeAd", "28846");

        SystemClock.sleep(5000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));
        logRequestUrl("MRAIDV2-ResizeAd");

        System.out.println("##### end " + "MRAIDV2-ResizeAd" + " #####");
        pressBack();
    }

    @Test
    public void mraid_v2_resize_ad_error() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-ResizeAdError", "28847");

        SystemClock.sleep(5000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MRAIDV2-ResizeAdError");

        System.out.println("##### end " + "MRAIDV2-ResizeAdError" + " #####");
        pressBack();
    }

    @Test
    public void mraid_v1_single_expand() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV1-SingleExpand", "28851");

        SystemClock.sleep(5000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        logRequestUrl("MRAIDV1-SingleExpand");

        System.out.println("##### end " + "MRAIDV2-SingleExpand" + " #####");
        pressBack();
    }

    @Test
    public void html_banner() {
        launchMainActivityAndSelectRowFetchAd("HTML-Banner", "28853");

        //IdlingResource ir =waitForView(R.id.bannerviewone);

        SystemClock.sleep(3000);

        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        //unregisterIdleResource(ir);

        logRequestUrl("HTML-Banner");

        System.out.println("##### end " + "HTML-Banner" + " #####");
        pressBack();
    }

    @Test
    public void image_banner() {
        launchMainActivityAndSelectRowFetchAd("Image-Banner", "28855");

        //IdlingResource ir= waitForView(R.id.bannerviewone);
        SystemClock.sleep(3000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));
        //unregisterIdleResource(ir);
        logRequestUrl("Image-Banner");

        System.out.println("##### end " + "Image-Banner" + " #####");
        pressBack();
    }

    @Test
    public void image_banner_with_url() {
        launchMainActivityAndSelectRowFetchAd("Image-BannerWithURL", "29771");

        SystemClock.sleep(5000);
        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        logRequestUrl("Image-BannerWithURL");

        System.out.println("##### end " + "Image-BannerWithURL" + " #####");
        pressBack();
    }

    @Test
    public void vastv2_external_tag_banner() {
        launchMainActivityAndSelectRowFetchAd("VASTV2-ExternalTagBanner", "29345");

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("VASTV2-ExternalTagBanner");

        System.out.println("##### end " + "VASTV2-ExternalTagBanner" + " #####");
        pressBack();
    }

    @Test
    public void mraidv2_backfill_s2s_banner() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-BackFill-S2S-Banner", "29342");

        //IdlingResource ir = waitForView(R.id.bannerviewone);

        SystemClock.sleep(4000);

        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));


        onWebView()
                .withElement(findElement(Locator.ID, "twopart"))
                .perform(webClick());
        //unregisterIdleResource(ir);

        SystemClock.sleep(8000);

        onWebView()
                .withElement(findElement(Locator.ID, "openiab"))
                .perform(webClick());

        SystemClock.sleep(8000);

        pressBack();

        onWebView()
                .withElement(findElement(Locator.ID, "expandtest"))
                .perform(webClick());

        SystemClock.sleep(8000);

        onWebView()
                .withElement(findElement(Locator.ID, "openvideo"))
                .perform(webClick());

        SystemClock.sleep(30000);

//        onWebView()
//                .withElement(findElement(Locator.ID, "close"))
//                .perform(webClick());

        pressBack();

        logRequestUrl("MRAIDV2-BackFill-S2S-Banner");

        System.out.println("##### end " + "MRAIDV2-BackFill-S2S-Banner" + " #####");
        pressBack();
    }

    @Test
    public void mraidv2_backfill_non_s2s_banner() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-BackFill-NonS2S-Banner", "29347");

        SystemClock.sleep(8000);
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MRAIDV2-BackFill-NonS2S-Banner");

        System.out.println("##### end " + "MRAIDV2-BackFill-NonS2S-Banner" + " #####");
        pressBack();
    }

    @Test
    public void vastv2_backfill_s2s_interstitial() {
        launchMainActivityAndSelectRowFetchAd("VASTV2-Backfill-S2S-Interstitial", "30468");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("VASTV2-Backfill-S2S-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "VASTV2-Backfill-S2S-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void vastv2_external_tag_interstitial() {
        launchMainActivityAndSelectRowFetchAd("VASTV2-ExternalTag-Interstitial", "29346");

        SystemClock.sleep(4000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("VASTV2-ExternalTag-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "VASTV2-ExternalTag-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mraidv2_video_s2s_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-Video-S2S-Interstitial", "28848");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MRAIDV2-Video-S2S-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "MRAIDV2-Video-S2S-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mraidv2_externaltag_interstitial_swipe() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV2-ExternalTag-Interstitial-Swipe", "28849");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MRAIDV2-ExternalTag-Interstitial-Swipe");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "MRAIDV2-ExternalTag-Interstitial-Swipe" + " #####");
        pressBack();
    }

    @Test
    public void rfm_direct_interstitial() {
        launchMainActivityAndSelectRowFetchAd("RFMDirect-Interstitial", "28854");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("RFMDirect-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "RFMDirect-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mraidv1_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MRAIDV1-Interstitial", "28850");

        SystemClock.sleep(3000);
        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MRAIDV1-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(RFMActivity.class);

        System.out.println("##### end " + "MRAIDV1-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithAllPartners_banner() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithAllPartners-Banner", "0");

        SystemClock.sleep(15000);
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MediationTag-AsscWithAllPartners-Banner");

        System.out.println("##### end " + "MediationTag-AsscWithAllPartners-Banner" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithDFPOnly_banner() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithDFPOnly-Banner", "0");

        SystemClock.sleep(15000);
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MediationTag-AsscWithDFPOnly-Banner");

        System.out.println("##### end " + "MediationTag-AsscWithDFPOnly-Banner" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithInMobiOnly_banner() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithInMobiOnly-Banner", "0");

        SystemClock.sleep(5000);
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MediationTag-AsscWithInMobiOnly-Banner");

        System.out.println("##### end " + "MediationTag-AsscWithInMobiOnly-Banner" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithMIMOnly_banner() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithMIMOnly-Banner", "0");

        SystemClock.sleep(5000);
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MediationTag-AsscWithMIMOnly-Banner");

        System.out.println("##### end " + "MediationTag-AsscWithMIMOnly-Banner" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithMopubOnly_banner() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithMopubOnly-Banner", "0");

        SystemClock.sleep(8000);
        assertThatLogTextContainsString("RFM Ad: Ad displayed");

        logRequestUrl("MediationTag-AsscWithMopubOnly-Banner");

        System.out.println("##### end " + "MediationTag-AsscWithMopubOnly-Banner" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithAllPartners_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithAllPartners-Interstitial", "0");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MediationTag-AsscWithAllPartners-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(AdActivity.class);

        System.out.println("##### end " + "MediationTag-AsscWithAllPartners-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithDFPOnly_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithDFPOnly-Interstitial", "0");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MediationTag-AsscWithDFPOnly-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(AdActivity.class);

        System.out.println("##### end " + "MediationTag-AsscWithDFPOnly-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithMIMOnly_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithMIMOnly-Interstitial", "0");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MediationTag-AsscWithMIMOnly-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(MMActivity.class);

        System.out.println("##### end " + "MediationTag-AsscWithMIMOnly-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void mediationtag_asscWithMopubOnly_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithMopubOnly-Interstitial", "0");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MediationTag-AsscWithMopubOnly-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(MoPubActivity.class);

        System.out.println("##### end " + "MediationTag-AsscWithMopubOnly-Interstitial" + " #####");
        pressBack();
    }

    @Test
    public void vastv2_backfill_s2s_15seconds_duration() {
        launchMainActivityAndSelectRowFetchAd("VASTV2-Backfill-S2S-15SecondsDuration", "30468");

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("VASTV2-Backfill-S2S-15SecondsDuration");
        SystemClock.sleep(5000);

        System.out.println("##### end " + "VASTV2-Backfill-S2S-15SecondsDuration" + " #####");
        pressBack();
    }

    @Test
    public void vastv2_backfill_s2s_30secDuration_20secSkip() {
        launchMainActivityAndSelectRowFetchAd("VASTV2-Backfill-S2S-30SecondsDuration-20SecSkip", "30468");

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("VASTV2-Backfill-S2S-30SecondsDuration-20SecSkip");

        System.out.println("##### end " + "VASTV2-Backfill-S2S-30SecondsDuration-20SecSkip" + " #####");
        pressBack();
    }

    @Test
    public void vastv2_backfill_s2s_playWithoutSound_15secDuration_2secSkip() {
        launchMainActivityAndSelectRowFetchAd("VASTV2-Backfill-S2S-PlayWithoutSound-15SecondsDuration-2SecSkip", "30468");

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("VASTV2-Backfill-S2S-PlayWithoutSound-15SecondsDuration-2SecSkip");

        System.out.println("##### end " + "VASTV2-Backfill-S2S-PlayWithoutSound-15SecondsDuration-2SecSkip" + " #####");
        pressBack();
    }

    public void mediationtag_asscWithInMobiOnly_interstitial() {
        launchMainActivityAndSelectRowFetchAd("MediationTag-AsscWithInMobiOnly-Interstitial", "0");

        SystemClock.sleep(3000);

        onView(withId(R.id.displayad)).check(matches(isDisplayed()));

        assertThatLogTextContainsString("RFM Ad: Received");

        logRequestUrl("MediationTag-AsscWithInMobiOnly-Interstitial");

        onView(withId(R.id.displayad)).perform(click());

        SystemClock.sleep(3000);

        assertCurrentActivityIsInstanceOf(MoPubActivity.class);

        System.out.println("##### end " + "MediationTag-AsscWithInMobiOnly-Interstitial" + " #####");
        pressBack();
    }

    // ------------ Failed test setups: Following tests are commented out as the Ad Setup were resulting in no Ads -----------------------------------
    //    @Test
//    public void mraidv2_video_backfill_nons2s_interstitial() {
//        launchMainActivityAndSelectRowFetchAd("MRAIDV2-Video-Backfill-NonS2S-Interstitial", "28867");
//
//        SystemClock.sleep(25000);
//        assertThatLogTextContainsString("RFM Ad: Ad displayed");
//
//        logRequestUrl("MRAIDV2-Video-Backfill-NonS2S-Interstitial");
//
//        System.out.println("##### end " + "MRAIDV2-Video-Backfill-NonS2S-Interstitial" + " #####");
//        pressBack();
//    }
//
//    @Test
//    public void vastv2_backfill_s2s_banner() {
//        launchMainActivityAndSelectRowFetchAd("VASTV2-Backfill-S2S-Banner", "30468");
//
//        //SystemClock.sleep(8000);
//        assertThatLogTextContainsString("RFM Ad: Ad displayed");
//
//        logRequestUrl("VASTV2-Backfill-S2S-Banner");
//        //SystemClock.sleep(8000);
//        System.out.println("##### end " + "VASTV2-Backfill-S2S-Banner" + " #####");
//        pressBack();
//    }
//
//    @Test

    // ------------  Failed test setups -----------------------------------

    // ------------  Helper functions ----------------------------------
    private IdlingResource waitForView(int adviewId) {
        View viewUnderTest =  getActivityInstance().findViewById(adviewId);
        IdlingResource ir = idleResourceRegisterAndStartPolling(viewUnderTest);
        return ir;
    }

    public IdlingResource idleResourceRegisterAndStartPolling(View v){
        IRViewVisible iResource = new IRViewVisible(v);
        System.out.println("IR-register");
        Espresso.registerIdlingResources(iResource);
        return iResource;
    }

    public void unregisterIdleResource(IdlingResource iResource){
        Espresso.unregisterIdlingResources(iResource);
        System.out.println("IR-Unregister");
    }

    public Activity getActivityInstance(){
        final Activity[] currentActivity = new Activity[1];
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){

                    currentActivity[0] = (Activity) resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity[0];
    }

    private void logRequestUrl(final String testCaseName) {
        String s = getText(allOf(withId(R.id.log_text)));
        int startIndex = s.indexOf("RFM Ad: Requesting Url:");
        int endIndex = s.indexOf("ad request");
        if (startIndex > 0 && endIndex > 0) {
            String requestingUrl = s.substring(startIndex + 23, endIndex);
            System.out.println(testCaseName + " RequestingUrl : " + requestingUrl);
        }
    }

    private void assertThatLogTextContainsString(final String testString) {
        String s = getText(allOf(withId(R.id.log_text)));
        assertThat(s, containsString(testString));
    }

    private String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    private void selectRow(final String testCaseName, final String adId) {
        onData(withContent(testCaseName, adId))
                .inAdapterView(withId(R.id.dynamic_list))
                .perform(click());
    }

    public static Matcher<Object> withContent(final String testCaseName, final String adId) {
        return new BoundedMatcher<Object, RFMAd>(RFMAd.class) {
            @Override
            public boolean matchesSafely(RFMAd myObj) {
                //return myObj.getCount().equals(content);
                return myObj.getTestCaseName().equals(testCaseName) && myObj.getAdId().equals(adId);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content '" + testCaseName + "'" + " '" + adId +"'" );
            }
        };
    }

    public void mraid_v2_expandtasks() {

        SystemClock.sleep(5000);

        onView(withId(R.id.bannerviewone)).check(matches(isDisplayed()));

        SystemClock.sleep(2000);

        onWebView()
                .withElement(findElement(Locator.ID, "twopart"))
                .perform(webClick());

        SystemClock.sleep(2000);

        onWebView()
                .withElement(findElement(Locator.ID, "openiab"))
                .perform(webClick());

        SystemClock.sleep(4000);

        pressBack();

        onWebView()
                .withElement(findElement(Locator.ID, "expandtest"))
                .perform(webClick());

        SystemClock.sleep(2000);

        onWebView()
                .withElement(findElement(Locator.ID, "openvideo"))
                .perform(webClick());

        SystemClock.sleep(40000);

        onWebView()
                .withElement(findElement(Locator.ID, "close"))
                .perform(webClick());

        pressBack();

    }

    public void assertCurrentActivityIsInstanceOf(Class<? extends Activity> activityClass) {
        Activity currentActivity = getActivityInstance();
        checkNotNull(currentActivity);
        checkNotNull(activityClass);
        assertTrue(currentActivity.getClass().isAssignableFrom(activityClass));
    }

    // ------------  Helper functions ----------------------------------
   }
