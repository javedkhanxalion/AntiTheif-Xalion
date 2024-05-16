package com.example.antitheifproject

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.antitheifproject.ads_manager.NativeAds
import com.example.antitheifproject.utilities.isSplash
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

/**
 * Created by
 * @Author: Javed Khan ,
 * @Company: Xpoosoft ,
 * @Email: xpoosoft@gmail.com ,
 * on 12/2/2021 , Thu .
 */


class AdOpenApp(private val myApplication: MyApplication, private var openAppAdId: String) :
    Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null

    fun fetchAd() {
        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                this@AdOpenApp.appOpenAd = appOpenAd
                loadTime = Date().time
                super.onAdLoaded(appOpenAd)
            }

        }
        val request = adRequest
        if (openAppAdId == "" || NativeAds.isDebug()) {
            openAppAdId = ""
        }
        AppOpenAd.load(
            myApplication, openAppAdId, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback ?: return
        )
    }

    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    private fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(LOG_TAG, "Will show ad. ${adError.message}")
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            if (isSplash) {
                appOpenAd?.show(currentActivity ?: return)
                appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            }
        } else {
            Log.d(LOG_TAG, "Can not show ad.")
            fetchAd()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
    }

    companion object {
        private const val LOG_TAG = "AppOpenManager"
        private var isShowingAd = false
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Init this class with in your Application class eg
    private lateinit var appOpenManager: AdOpenApp
    override fun onCreate() {
    super.onCreate()
    appOpenManager = AdOpenApp(this,"")
    }
     * */

}