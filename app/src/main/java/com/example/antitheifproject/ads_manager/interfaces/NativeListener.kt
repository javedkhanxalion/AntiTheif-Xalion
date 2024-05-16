package com.example.antitheifproject.ads_manager.interfaces

import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

/**
 * Created by
 * @Author: Javed Khan ,
 */
interface NativeListener {

    fun nativeAdLoaded(currentNativeAd: NativeAd?) {

    }

    fun nativeAdFailed(loadAdError: LoadAdError) {

    }

    fun nativeAdValidate(string: String) {

    }

    fun nativeAdLoaded2nd(currentNativeAd: NativeAd?) {

    }

    fun nativeAdFailed2nd(loadAdError: LoadAdError) {

    }

    fun nativeAdValidate2nd(string: String) {

    }
fun nativeAdClicked(){}

}