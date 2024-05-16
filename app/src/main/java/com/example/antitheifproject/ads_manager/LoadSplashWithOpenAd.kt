package com.example.antitheifproject.ads_manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.ads_manager.interfaces.AdMobAdListener
import com.example.antitheifproject.ads_manager.interfaces.AdsListener
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.inter_frequency_count


var openAdForSplash: AppOpenForSplash? = null
var iS_SPLASH_AD_DISMISS = false
private const val TAGGED = "TwoInterAdsSplash"
var IS_OPEN_SHOW = false
fun loadTwoInterAdsSplash(
    ads: AdsManager,
    activity: Activity,
    remoteConfigNormal: Boolean,
    adIdNormal: String,
    tagClass: String
) {
    FullScreenAdsTwo.loadFullScreenAdTwo(activity = activity,
        addConfig = remoteConfigNormal,
        fullScreenAdId = adIdNormal,
        adsListener = object : AdsListener {
            override fun adFailed() {
                Log.d(TAGGED, "adFailed: normal inter failed")
                firebaseAnalytics("inter_normal_failed_$tagClass", "interLoaded")
//                loadOpenAdSplash(activity)
            }

            override fun adLoaded() {
                Log.d(TAGGED, "adLoaded: normal inter load")
                firebaseAnalytics("inter_normal_loaded_$tagClass", "interLoaded")
            }

            override fun adNotFound() {
                Log.d(TAGGED, "adNotFound: normal not found")
                firebaseAnalytics("inter_normal_not_found_$tagClass", "interLoaded")
//                loadOpenAdSplash(activity)
            }
        })
}


fun showNormalInterAdSingle(
    ads: AdsManager,
    activity: Activity,
    remoteConfigNormal: Boolean,
    tagClass: String,
    function: ()->Unit
) {
//    Log.d(TAGGED, "showNormalInterAd->adIdMedium: $adIdMedium")
//    Log.d(TAGGED, "showNormalInterAd->adIdNormal: $adIdNormal")
    ads.fullScreenAdsTwo().showAndLoadTwo(activity, remoteConfigNormal, object : AdMobAdListener {
        override fun fullScreenAdShow() {
            Log.d(TAGGED, "fullScreenAdShow: normal inter ad show")
            inter_frequency_count++
            firebaseAnalytics("inter_normal_show_$tagClass", "inter_Show")
        }

        override fun fullScreenAdDismissed() {
            Log.d(TAGGED, "fullScreenAdDismissed: normal inter dismiss")
            firebaseAnalytics("inter_normal_dismisss_$tagClass", "inter_Show")
            iS_SPLASH_AD_DISMISS = true
//            loadTwoInterAds(
//                ads = ads,
//                activity = activity,
//                remoteConfigMedium = val_inter_main_medium,
//                remoteConfigNormal = val_inter_main_medium,
//                adIdMedium = id_inter_main_medium,
//                adIdNormal = id_inter_main_medium,
//                tagClass = "main_app_fragment_pre_load"
//            )
            function.invoke()
        }

        override fun fullScreenAdFailedToShow() {
            Log.d(TAGGED, "fullScreenAdFailedToShow: normal inter failed to show")
            firebaseAnalytics("inter_normal_failed_show_$tagClass", "inter_Show")
//            showOpenAd(activity)
            function.invoke()

        }

        override fun fullScreenAdNotAvailable() {
            Log.d(TAGGED, "fullScreenAdNotAvailable: normal inter not available")
            firebaseAnalytics("inter_normal_not_Found_$tagClass", "inter_Show")
//            showOpenAd(activity)
            function.invoke()

        }

    }, id_inter_main_medium, object : AdsListener {

    })
}


fun loadOpenAdSplash(context: Context) {
    openAdForSplash = AppOpenForSplash()
    openAdForSplash?.loadAd(context, context.getString(R.string.app_open_splash))
    Log.d(TAG, "loadOpenAdSplash: Load $openAdForSplash")
}


fun showOpenAd(activity: Activity) {
    Log.d(TAG, "loadOpenAdSplash: SHow $openAdForSplash")
    openAdForSplash?.showAdIfAvailable(activity)
}
