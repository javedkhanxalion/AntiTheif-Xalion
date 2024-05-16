package com.example.antitheifproject.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.BuildConfig
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentExitScreenBinding
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.id_exit_dialog_native
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.val_ad_instertital_exit_dialog_is_B
import com.example.antitheifproject.utilities.val_exit_dialog_native
import com.example.antitheifproject.utilities.val_exit_dialog_native_is_H
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FragmentExitScreen :
    BaseFragment<FragmentExitScreenBinding>(FragmentExitScreenBinding::inflate) {

    private var adsManager: AdsManager? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adsManager = AdsManager.appAdsInit(activity ?: return)
        mCLickListener()
        loadNative()
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
    }

    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_exit_dialog_native,
            id_exit_dialog_native,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    binding?.nativeExitAd?.visibility = View.VISIBLE
                    binding?.adView?.visibility = View.GONE
                    if (isAdded && isVisible && !isDetached) {
                        val adView = if (val_exit_dialog_native_is_H) {
                            layoutInflater.inflate(
                                R.layout.ad_unified_media_high,
                                null
                            ) as NativeAdView
                        } else {
                            layoutInflater.inflate(
                                R.layout.ad_unified_media_low,
                                null
                            ) as NativeAdView
                        }
                        adsManager?.nativeAds()?.nativeViewMedia(currentNativeAd ?: return, adView)
                        binding?.nativeExitAd?.removeAllViews()
                        binding?.nativeExitAd?.addView(adView)
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    binding?.nativeExitAd?.visibility = View.GONE
                    binding?.adView?.visibility = View.GONE
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    binding?.nativeExitAd?.visibility = View.GONE
                    binding?.adView?.visibility = View.GONE
                    super.nativeAdValidate(string)
                }
            })
    }


    private fun mCLickListener() {

        _binding?.yesBtn?.setOnClickListener {
            activity?.finish()
        }
        _binding?.cancelBtn?.setOnClickListener {
            findNavController().navigateUp()
        }
        _binding?.rateImg?.setOnRatingBarChangeListener { ratingBar, fl, b ->
            if (fl > 3.5) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            }

        }
    }

}