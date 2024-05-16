package com.example.antitheifproject.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.adapter.IntroScreenAdapter
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentMainIntroBinding
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_INTRO
import com.example.antitheifproject.utilities.LANG_SCREEN
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.id_native_intro_screen
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.val_ad_native_intro_screen
import com.example.antitheifproject.utilities.val_ad_native_intro_screen_is_H
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView


class IntoScreenFragment :
    BaseFragment<FragmentMainIntroBinding>(FragmentMainIntroBinding::inflate) {

    var currentpage = 0
    private var introScreenAdapter: IntroScreenAdapter? = null
    private var sharedPrefUtils: DbHelper? = null
    private var ads: AdsManager? = null

    private var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(i: Int, v: Float, i1: Int) {
            currentpage = i
            _binding?.wormDotsIndicator?.attachTo(_binding?.mainSlideViewPager?:return)
        }

        override fun onPageSelected(i: Int) {
            currentpage = i
        }

        override fun onPageScrollStateChanged(i: Int) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics("intro_fragment_open", "intro_fragment_open -->  Click")
        introScreenAdapter = IntroScreenAdapter(requireContext())
        sharedPrefUtils = DbHelper(context ?: return)
        ads = AdsManager.appAdsInit(activity ?: return)
        _binding?.run {
            skipApp.clickWithThrottle {
                firebaseAnalytics(
                    "intro_fragment_move_to_next",
                    "intro_fragment_move_to_next -->  Click"
                )
                sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to true))
            }
            nextApp.clickWithThrottle {
                if (currentpage == 2) {
                    firebaseAnalytics(
                        "intro_fragment_move_to_next",
                        "intro_fragment_move_to_next -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                    findNavController().navigate(
                        R.id.LanguageFragment,
                        bundleOf(LANG_SCREEN to true)
                    )
                } else {
                    mainSlideViewPager.setCurrentItem(getItem(+1), true)
                }
            }
            mainSlideViewPager.adapter = introScreenAdapter
            mainSlideViewPager.addOnPageChangeListener(viewListener)

        }
        loadNative()
        setupBackPressedCallback {
        }

    }

    private fun getItem(i: Int): Int {
        return _binding?.mainSlideViewPager?.currentItem!! + i
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun loadNative() {
        ads?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_intro_screen,
            id_native_intro_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if(isAdded && isVisible && !isDetached){
                    _binding?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.adView?.visibility = View.GONE
                     if(val_ad_native_intro_screen_is_H){
                         val adView =layoutInflater.inflate(R.layout.ad_unified_media_high, null) as NativeAdView
                        ads?.nativeAds()?.nativeViewMedia(currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                    }else{
                        val adView = layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                        ads?.nativeAds()?.nativeViewPolicy(currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                    }
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if(isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if(isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                    }
                    super.nativeAdValidate(string)
                }
            })
    }

}