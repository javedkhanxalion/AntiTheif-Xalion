package com.example.antitheifproject.ads_manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.antitheftalarm.dont.touch.phone.finder.BuildConfig
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

private const val LOG_TAG = "MyApplication"
//private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"

class AppOpenForSplash {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    fun loadAd(context: Context, adId: String) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(context, if (BuildConfig.DEBUG) "" else adId, request, object : AppOpenAd.AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isLoadingAd = false
                loadTime = Date().time
                Log.d(LOG_TAG, "onAdLoaded.")
                firebaseAnalytics("open_ad_splash_loaded", "openAdSplash")
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                iS_SPLASH_AD_DISMISS =true
                Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                firebaseAnalytics("open_ad_splash_failed_loaded", "openAdSplash")

            }
        })
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(activity, object : OnShowAdCompleteListener {
            override fun onShowAdComplete() {
                // Empty because the user will go back to the activity that shows the ad.
            }
        })
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(LOG_TAG, "The app open ad is already showing.")
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
//            loadAd(activity)
            return
        }

        Log.d(LOG_TAG, "Will show ad.")

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            override fun onAdDismissedFullScreenContent() {
//                IS_SPLASH_OPEN_AD = false
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false
                iS_SPLASH_AD_DISMISS =true

                IS_OPEN_SHOW = false
                Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                firebaseAnalytics("open_ad_splash_dismiss", "openAdSplash")

                onShowAdCompleteListener.onShowAdComplete()
//                loadAd(activity)
            }

            /** Called when fullscreen content failed to show. */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                firebaseAnalytics("open_ad_splash_failed_to_show", "openAdSplash")
//                IS_SPLASH_OPEN_AD = false
                IS_OPEN_SHOW = false
                appOpenAd = null
                isShowingAd = false
                iS_SPLASH_AD_DISMISS =true
                Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)

                onShowAdCompleteListener.onShowAdComplete()
                //                    loadAd(activity)
            }

            /** Called when fullscreen content is shown. */
            override fun onAdShowedFullScreenContent() {
//                IS_SPLASH_OPEN_AD = true
                IS_OPEN_SHOW = true
                firebaseAnalytics("open_ad_splash_showed", "openAdSplash")

                Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }
}