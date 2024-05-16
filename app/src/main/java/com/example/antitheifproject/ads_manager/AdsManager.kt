package com.example.antitheifproject.ads_manager

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration


/**
 * Created by
 * @Author: Javed Khan ,
 */

object AdsManager {
    /*Ads*/
    private var appAds: AdsManager? = null

    fun appAdsInit(activity: Activity): AdsManager {
        if (appAds == null) {
            appAds = AdsManager
            instance(activity)
            Log.d("adsInit", "   AdsClass")
        }
        return appAds as AdsManager
    }

    private fun instance(activity: Activity) {
        try {
            MobileAds.initialize(activity) {}
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(
                        listOf(
                            "6EEDFFFDB10991D90042D447C324A2C8",
                            "9C6BF7A67630224C2115219E0B427854",
                            "5A19AC906A2FC17764509E7E3EA7434B",
                        )
                    )
                    .build()
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun adsBanners(): AdsBanners {
        return AdsBanners.adsBanner()
    }

    fun fullScreenAds(): FullScreenAds {
        return FullScreenAds.fullScreenAds()
    }
    fun fullScreenAdsTwo(): FullScreenAdsTwo {
        return FullScreenAdsTwo.fullScreenAdsTwo()
    }

    fun nativeAds(): NativeAds {
        return NativeAds.nativeAds()
    }
    fun nativeAdsSplash(): NativeAdsSplash {
        return NativeAdsSplash.nativeAds()
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }


    /*Useful links*/

    /**
     * HELP
     *https://developers.google.com/admob/android/test-ads
     *
     *
     * implementation platform('com.google.firebase:firebase-bom:30.3.1')
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.android.gms:play-services-ads:21.1.0")
    implementation("com.android.billingclient:billing:4.1.0")

    OPEN APP SDK
    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0"
    annotationProcessor "androidx.lifecycle:lifecycle-compiler:2.2.0"


     * */


}