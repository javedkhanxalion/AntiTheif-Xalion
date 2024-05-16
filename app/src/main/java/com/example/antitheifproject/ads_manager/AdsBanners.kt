package com.example.antitheifproject.ads_manager

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.antitheftalarm.dont.touch.phone.finder.BuildConfig
import com.example.antitheifproject.ads_manager.AdsManager.isNetworkAvailable
import com.example.antitheifproject.ads_manager.billing.BillingUtil
import com.example.antitheifproject.ads_manager.interfaces.BannerListener
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*

object AdsBanners {
    private var adView: AdView? = null

    private const val bannerLogs = "bannerLogs"
    private const val bannerTestId = "ca-app-pub-3940256099942544/2014213617"

    private var adsBanner: AdsBanners? = null

    fun adsBanner(): AdsBanners {
        if (adsBanner == null) {
            adsBanner = AdsBanners
            Log.d("adsInit", "   AdsBannersClass")
        }
        return adsBanner as AdsBanners
    }

    fun loadCollapsibleBanner(
        activity: Activity,
        view: FrameLayout,
        addConfig: Boolean,
        bannerId: String?
    ) {

        if (isNetworkAvailable(activity) && addConfig && !BillingUtil(activity).checkPurchased(activity)) {

            Log.d(bannerLogs, "loadAdaptiveBanner : bannerId : $bannerId")
            val extras = Bundle()
            extras.putString("collapsible", "bottom")

            view.removeAllViews()
            adView = AdView(activity)
            view.addView(adView)
            adView?.adUnitId = if (isDebug()) bannerTestId else bannerId ?: bannerTestId
            adView?.setAdSize(adSize(activity, view))
            val adRequest =
                AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
            adView?.loadAd(adRequest)

            adView?.adListener = object : AdListener() {
                override fun onAdClicked() {
                    Log.d(bannerLogs, "BannerWithSize : onAdClicked")
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    Log.d(bannerLogs, "BannerWithSize : onAdClosed")
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d(bannerLogs, "BannerWithSize : onAdFailedToLoad ${p0.message}")
                    super.onAdFailedToLoad(p0)
                }

                override fun onAdImpression() {
                    Log.d(bannerLogs, "BannerWithSize : onAdImpression")
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    Log.d(bannerLogs, "BannerWithSize : onAdLoaded")
                    super.onAdLoaded()
                }

                override fun onAdOpened() {
                    Log.d(bannerLogs, "BannerWithSize : onAdOpened")
                    super.onAdOpened()
                }
            }
        } else {
            view.visibility = View.GONE
        }
    }

    fun loadBanner(
        activity: Activity,
        view: FrameLayout,
        addConfig: Boolean,
        bannerId: String?,
        bannerListener: BannerListener
    ) {

        if (isNetworkAvailable(activity) && addConfig && !BillingUtil(activity).checkPurchased(activity)) {
            firebaseAnalytics("bannerAdsCalled", "bannerAdsCalled->click")

            view.removeAllViews()
            adView = AdView(activity)
            view.addView(adView)
            adView?.adUnitId = if (isDebug()) bannerTestId else bannerId ?: bannerTestId
            adView?.setAdSize(AdSize.MEDIUM_RECTANGLE)
            val adRequest =
                AdRequest.Builder().build()
            adView?.loadAd(adRequest)

            adView?.adListener = object : AdListener() {
                override fun onAdClicked() {
                    Log.d(bannerLogs, "BannerWithSize : onAdClicked")
                    firebaseAnalytics("bannerAdsClicked", "bannerAdsClicked->click")
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    firebaseAnalytics("bannerAdsClosed", "bannerAdsClosed->click")

                    Log.d(bannerLogs, "BannerWithSize : onAdClosed")
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    firebaseAnalytics("bannerAdsFailed", "bannerAdsFailed->click")
                    bannerListener.bannerAdFailed(p0)
                    Log.d(bannerLogs, "BannerWithSize : onAdFailedToLoad ${p0.message}")
                    super.onAdFailedToLoad(p0)
                }

                override fun onAdImpression() {
                    firebaseAnalytics("bannerAdsImpression", "bannerAdsImpression->click")
                    Log.d(bannerLogs, "BannerWithSize : onAdImpression")
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    firebaseAnalytics("bannerAdsLoaded", "bannerAdsLoaded->click")
                    bannerListener.bannerAdLoaded()
                    Log.d(bannerLogs, "BannerWithSize : onAdLoaded")
                    super.onAdLoaded()
                }

                override fun onAdOpened() {
                    firebaseAnalytics("bannerAdsOpened", "bannerAdsOpened->click")

                    Log.d(bannerLogs, "BannerWithSize : onAdOpened")
                    super.onAdOpened()
                }
            }
        } else {
            Log.d(bannerLogs, "some value is false")
            bannerListener.bannerAdNoInternet()
            view.visibility = View.GONE
        }
    }


    fun destroyAd() {
        adView?.destroy()
    }

    private fun adSize(context: Activity, view: ViewGroup): AdSize {
        val display = context.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = view.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}