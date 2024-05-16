package com.example.antitheifproject.ads_manager

import android.app.Activity
import android.util.Log
import com.antitheftalarm.dont.touch.phone.finder.BuildConfig
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.ads_manager.billing.BillingUtil
import com.example.antitheifproject.ads_manager.interfaces.AdMobAdListener
import com.example.antitheifproject.ads_manager.interfaces.AdsListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

/**
 * Created by
 * @Author: Javed Khan
 */

object FullScreenAds {
    var mInterstitialAd: InterstitialAd? = null
    private const val fullScreenAdLog = "full_screen"
    private var fullScreenAdId: String? = null
    private var isAdLoading: Boolean = false
    private var fullScreenAdTestIdVideo: String = ""

    private var fullScreenAd: FullScreenAds? = null

    fun fullScreenAds(): FullScreenAds {
        if (fullScreenAd == null) {
            fullScreenAd = FullScreenAds
            Log.d("adsInit", "   FullScreenAdsClass")
        }
        return fullScreenAd as FullScreenAds
    }

    fun loadFullScreenAd(
        activity: Activity?,
        addConfig: Boolean,
        fullScreenAdId: String,
        adsListener: AdsListener
    ) {
        if (AdsManager.isNetworkAvailable(activity) && !BillingUtil(
                activity ?: return
            ).checkPurchased(activity) && addConfig
        ) {
//            this.fullScreenAdId =  if (NativeAds.isDebug()) "ca-app-pub-3940256099942544/1033173712" else fullScreenAdId
            Log.d(fullScreenAdLog, "loadFullScreenAd: request with ${fullScreenAdId}")
            if (mInterstitialAd == null && !isAdLoading) {
                isAdLoading = true
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(activity,
                    if (isDebug())
                        fullScreenAdTestIdVideo
                    else
                        fullScreenAdId,
                    adRequest, object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d(fullScreenAdLog, "loadFullScreenAd : ${adError.message}")
                            if (isDebug()) {
                                Snackbar.make(
                                    activity.window.decorView.rootView,
                                    "AD Error : ${adError.message}", Snackbar.LENGTH_LONG
                                ).show()
                            }
                            isAdLoading = false
                            mInterstitialAd = null
                            adsListener.adFailed()
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Log.d(fullScreenAdLog, "loadFullScreenAd : Ad was loaded.")

                            mInterstitialAd = interstitialAd
                            isAdLoading = false
                            adsListener.adLoaded()
                        }
                    })
            } else {
                adsListener.adAlreadyLoaded()
                Log.d(fullScreenAdLog, "loadFullScreenAd : having a AD. or loading precious")
            }
        } else {
            adsListener.adNotFound()
            if (isDebug()) {
                Log.d(fullScreenAdLog, "config : $addConfig")
                Snackbar.make(
                    activity?.window?.decorView?.rootView ?: return,
                    activity.getString(R.string.no_internet), Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    fun showAndLoad(
        activity: Activity?,
        addConfig: Boolean,
        adMobAdListener: AdMobAdListener,
        loadNewAdWithId: String,
        newAdListener: AdsListener
    ) {
        if (activity == null) return

        if (mInterstitialAd != null && !BillingUtil(activity).checkPurchased(activity) && addConfig) {

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(fullScreenAdLog, "Callback : Ad was dismissed.")
                    adMobAdListener.fullScreenAdDismissed()
                    mInterstitialAd = null
//                    CheckForImage.IS_SHOW_OPEN_AD = true
                    if (loadNewAdWithId != "") {
                        loadFullScreenAd(activity, addConfig, loadNewAdWithId, newAdListener)
                        logEventForAds(fullScreenAdLog, "load_new_ad", loadNewAdWithId)
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//                    CheckForImage.IS_SHOW_OPEN_AD = true
                    Log.d(fullScreenAdLog, "Callback : Ad failed to show.")
                    mInterstitialAd = null
                    if (isDebug()) {
                        Snackbar.make(
                            activity.window.decorView.rootView,
                            "AD Error : ${adError.message}", Snackbar.LENGTH_LONG
                        ).show()
                    }
                    adMobAdListener.fullScreenAdFailedToShow()
                    logEventForAds(fullScreenAdLog, "fail", loadNewAdWithId)

                }

                override fun onAdShowedFullScreenContent() {
//                    CheckForImage.IS_SHOW_OPEN_AD = false
                    Log.d(fullScreenAdLog, "Callback : Ad showed fullscreen content.")
                    adMobAdListener.fullScreenAdShow()
                }

                override fun onAdClicked() {
                    logEventForAds(fullScreenAdLog, "clicked", loadNewAdWithId)
                    super.onAdClicked()
                }
            }
            mInterstitialAd?.show(activity)
            logEventForAds(fullScreenAdLog, "show", loadNewAdWithId)

        } else {
            adMobAdListener.fullScreenAdNotAvailable()
            logEventForAds(fullScreenAdLog, "not_available", loadNewAdWithId)
        }
    }

    fun logEventForAds(adFormatName: String, adEvent: String, adIdLastDigits: String) {
        if (adIdLastDigits == "") return
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent("log_${adFormatName}_id_${adIdLastDigits.takeLast(4)}") {
            param("ad_event", "${adEvent}_id_${adIdLastDigits.takeLast(4)}")
            param("last_digits", adIdLastDigits.takeLast(4))
        }
    }

    fun isDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }
}