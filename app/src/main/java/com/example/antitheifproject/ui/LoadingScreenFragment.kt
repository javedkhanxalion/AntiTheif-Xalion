package com.example.antitheifproject.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentLoadingBinding
import com.example.antitheifproject.ads_manager.loadTwoInterAds
import com.example.antitheifproject.ads_manager.showNormalInterAdSingle
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.utilities.ANTI_TITLE
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_FIRST
import com.example.antitheifproject.utilities.IS_INTRO
import com.example.antitheifproject.utilities.LANG_SCREEN
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_loading_screen
import com.example.antitheifproject.utilities.id_native_main_menu_screen
import com.example.antitheifproject.utilities.isFlowOne
import com.example.antitheifproject.utilities.isIntroLanguageShow
import com.example.antitheifproject.utilities.isSplash
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.val_ad_native_loading_screen
import com.example.antitheifproject.utilities.val_ad_native_loading_screen_is_H
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.delay

class LoadingScreenFragment :
    BaseFragment<FragmentLoadingBinding>(FragmentLoadingBinding::inflate) {

    private var sharedPrefUtils: DbHelper? = null
    private var adsManager: AdsManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSplash=true
        sharedPrefUtils = DbHelper(context ?: return)
        adsManager = AdsManager.appAdsInit(activity ?: return)
        firebaseAnalytics("loading_fragment_open", "loading_fragment_open -->  Click")
        if(isFlowOne){
            lifecycleScope.launchWhenCreated {
                delay(3000)
                    loadNative()
                    if(isAdded && isVisible && !isDetached) {
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
            }
        }else{
            _binding?.next?.visibility = View.INVISIBLE
            _binding?.nativeExitAd?.visibility = View.INVISIBLE
            _binding?.shimmerLayout?.visibility = View.INVISIBLE
            _binding?.animationView?.visibility = View.VISIBLE
            lifecycleScope.launchWhenCreated {
                delay(3000)
                adsManager?.let {
                    showNormalInterAdSingle(
                        it,
                        activity ?: return@let,
                        remoteConfigNormal = true,
                        tagClass = "splash"
                    ) {
                    }
                }
                getIntentMove()
            }

        }

        _binding?.next?.setOnClickListener {
            adsManager?.let {
                showNormalInterAdSingle(
                    it,
                    activity ?: return@let,
                    remoteConfigNormal = true,
                    tagClass = "splash"
                ) {
                }
            }
            getIntentMove()
        }

        setupBackPressedCallback {
        }

        adsManager?.let {
            loadTwoInterAds(
                ads = it,
                activity = activity ?: return@let,
                remoteConfigMedium = true,
                remoteConfigNormal = true,
                adIdMedium = id_inter_main_medium,
                adIdNormal = id_inter_main_normal,
                tagClass = "home_pre_cache"
            )
        }

    }

    private fun getIntentMove() {
        if (sharedPrefUtils?.getBooleanData(
                requireContext() ,
                IS_INTRO,
                false
            ) == false && isIntroLanguageShow
        ) {
            firebaseAnalytics(
                "loading_fragment_load_next_btn_intro",
                "loading_fragment_load_next_btn_intro -->  Click"
            )
            return findNavController().navigate(R.id.IntoScreenFragment)
        } else if (sharedPrefUtils?.getBooleanData(
                requireContext(),
                IS_FIRST,
                false
            ) == false && isIntroLanguageShow
        ) {
            firebaseAnalytics(
                "loading_fragment_load_next_btn_language",
                "loading_fragment_load_next_btn_language -->  Click"
            )
            return findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to true))
        } else {
            firebaseAnalytics(
                "loading_fragment_load_next_btn_main",
                "loading_fragment_load_next_btn_main -->  Click"
            )
           return findNavController().navigate(R.id.FragmentInAppScreen,bundleOf("Is_From_Splash" to true))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun loadNative() {
        adsManager?.nativeAdsSplash()?.loadNativeAd(
            activity ?: return,
            true,
            getString(R.string.id_native_loading_screen),
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if(isAdded && isVisible && !isDetached){
                    val adView = if(val_ad_native_loading_screen_is_H){
                        layoutInflater.inflate(R.layout.ad_unified_media_high, null) as NativeAdView
                    }else{
                        layoutInflater.inflate(R.layout.ad_unified_media_low, null) as NativeAdView
                    }
                    adsManager?.nativeAdsSplash()?.nativeViewMedia(currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.shimmerLayout?.visibility = View.GONE
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if(isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.shimmerLayout?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if(isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.shimmerLayout?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdValidate(string)
                }
            })

        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            true,
            id_native_main_menu_screen,
            object : NativeListener {

            })

    }

}