package com.example.antitheifproject.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat.recreate
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentLanguageBinding
import com.example.antitheifproject.adapter.LanguageAppAdapter
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.model.LanguageAppModel
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_FIRST
import com.example.antitheifproject.utilities.LANG_CODE
import com.example.antitheifproject.utilities.LANG_SCREEN
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.id_native_language_screen
import com.example.antitheifproject.utilities.languageData
import com.example.antitheifproject.utilities.setLocaleMain
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.val_ad_native_language_screen
import com.example.antitheifproject.utilities.val_ad_native_language_screen_is_H
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView


class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {

    private var positionSelected: String = "en"
    private var isLangScreen: Boolean = false
    private var adsManager: AdsManager? = null
    private var sharedPrefUtils: DbHelper? = null
    private var adapter: LanguageAppAdapter? = null
    var list: ArrayList<LanguageAppModel> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
        adsManager = AdsManager.appAdsInit(activity ?: return)
        firebaseAnalytics("language_fragment_open", "language_fragment_open -->  Click")
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        arguments?.let {
            isLangScreen = it.getBoolean(LANG_SCREEN)
        }

        if (isLangScreen) {
            _binding?.backBtn?.visibility = View.GONE
        }
        _binding?.forwardBtn?.clickWithThrottle {
            sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
            if (!isLangScreen) {
                firebaseAnalytics(
                    "language_fragment_forward_btn_from",
                    "language_fragment_forward_btn_from -->  Click"
                )
                sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                setLocaleMain(positionSelected)
                recreate(requireActivity())
            } else {
                firebaseAnalytics(
                    "language_fragment_forward_btn_from",
                    "language_fragment_forward_btn_from -->  Click"
                )
                sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                setLocaleMain(positionSelected)
                sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
                findNavController().navigate(R.id.FragmentInAppScreen,
                    bundleOf("Is_From_Splash" to true)
                )
            }
        }

        sharedPrefUtils = DbHelper(context ?: return)
        positionSelected = sharedPrefUtils?.getStringData(requireContext(), LANG_CODE, "en") ?: "en"
        adapter = LanguageAppAdapter(clickItem = {
            positionSelected = it.country_code
        }, languageData = languageData())
        adapter?.selectLanguage(positionSelected)

        _binding?.title?.text = getString(R.string.set_app_language)
        _binding?.conversationDetail?.adapter = adapter
        _binding?.backBtn?.clickWithThrottle {
            if (!isLangScreen) {
                firebaseAnalytics(
                    "language_fragment_back_press",
                    "language_fragment_back_press -->  Click"
                )
                findNavController().popBackStack()
            }
        }
        setupBackPressedCallback {
            if (!isLangScreen) {
                firebaseAnalytics(
                    "language_fragment_back_press",
                    "language_fragment_back_press -->  Click"
                )
                findNavController().popBackStack()
            }
        }
            loadNative()
        } catch (e: Exception) {
          e.printStackTrace()
        }
    }

    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_language_screen,
            id_native_language_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
                       if (val_ad_native_language_screen_is_H) {
                            val adView =layoutInflater.inflate(R.layout.ad_unified_media_high, null) as NativeAdView
                           adsManager?.nativeAds()?.nativeViewMedia(currentNativeAd ?: return, adView)
                            _binding?.nativeExitAd?.removeAllViews()
                            _binding?.nativeExitAd?.addView(adView)
                        }else{
                            val adView = layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                           adsManager?.nativeAds()?.nativeViewPolicy(currentNativeAd ?: return, adView)
                            _binding?.nativeExitAd?.removeAllViews()
                            _binding?.nativeExitAd?.addView(adView)
                        }
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                    }
                    super.nativeAdValidate(string)
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }
}