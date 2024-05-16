package com.example.antitheifproject.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.adapter.SoundSelectGridAdapter
import com.example.antitheifproject.adapter.SoundSelectLinearAdapter
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentSoundSelectionBinding
import com.example.antitheifproject.ads_manager.showTwoInterAd
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.model.MainMenuModel
import com.example.antitheifproject.utilities.ANTI_TITLE
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_GRID
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_main_menu_screen
import com.example.antitheifproject.utilities.id_native_sound_screen
import com.example.antitheifproject.utilities.loadImage
import com.example.antitheifproject.utilities.setImage
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.soundData
import com.example.antitheifproject.utilities.val_ad_instertital_sound_screen_is_B
import com.example.antitheifproject.utilities.val_ad_native_sound_screen
import com.example.antitheifproject.utilities.val_ad_native_sound_screen_is_H
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FragmentSoundSelection :
    BaseFragment<FragmentSoundSelectionBinding>(FragmentSoundSelectionBinding::inflate) {

    private var sharedPrefUtils: DbHelper? = null
    private var isGridLayout: Boolean? = null
    private var adsManager: AdsManager? = null
    private var adapterGrid: SoundSelectGridAdapter? = null
    private var adapterLinear: SoundSelectLinearAdapter? = null
    private var position: MainMenuModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            position = it.getParcelable(ANTI_TITLE) ?: return@let
        }
        adsManager = AdsManager.appAdsInit(activity ?: return)
        sharedPrefUtils = DbHelper(context ?: return)
        sharedPrefUtils?.getBooleanData(context ?: return, IS_GRID, true)?.let {
            loadLayoutDirection(it)
            isGridLayout = it
        }
        _binding?.topLay?.title?.text = getString(R.string.select_sound)
        _binding?.topLay?.navMenu?.loadImage(context ?: return, R.drawable.back_btn)
//            adsManager?.let {
//                showTwoInterAd(
//                    ads = it,
//                    activity = activity ?: return@let,
//                    remoteConfigMedium = val_inter_main_medium,
//                    remoteConfigNormal = val_inter_main_normal,
//                    adIdMedium = id_inter_main_medium,
//                    adIdNormal = id_inter_main_normal,
//                    tagClass = getString(R.string.select_sound),
//                    isBackPress = true,
//                    layout = binding?.adsLay!!
//                ) {
//                }
//            }
        _binding?.run {
            topLay.navMenu.clickWithThrottle {
                findNavController().popBackStack()
            }
            topLay.setLayoutBtn.clickWithThrottle {
                loadLayoutDirection(!(isGridLayout ?: return@clickWithThrottle))
            }
        }
        setupBackPressedCallback {
            findNavController().popBackStack()
        }
        loadNative()
    }

    private fun loadLayoutDirection(isGrid: Boolean) {

        if (isGrid) {
            isGridLayout = true
            sharedPrefUtils?.saveData(context ?: return, IS_GRID, true)
            _binding?.topLay?.setLayoutBtn?.setImage(R.drawable.icon_grid)
            adapterGrid = SoundSelectGridAdapter(clickItem = {
                sharedPrefUtils?.setTone(position?.maniTextTitle, it)
            }, soundData = soundData())
            val managerLayout = GridLayoutManager(context, 3)
            _binding?.recycler?.layoutManager = managerLayout
            sharedPrefUtils?.getTone(context ?: return, position?.maniTextTitle)
                ?.let { adapterGrid?.selectSound(it) }
            _binding?.recycler?.adapter = adapterGrid

        } else {
            isGridLayout = false
            sharedPrefUtils?.saveData(context ?: return, IS_GRID, false)
            _binding?.topLay?.setLayoutBtn?.setImage( R.drawable.icon_list)
            adapterLinear = SoundSelectLinearAdapter(clickItem = {
                sharedPrefUtils?.setTone(position?.maniTextTitle, it)
            }, soundData = soundData())

            val managerLayout = LinearLayoutManager(context)
            managerLayout.orientation = LinearLayoutManager.VERTICAL
            _binding?.recycler?.layoutManager = managerLayout
            sharedPrefUtils?.getTone(context ?: return, position?.maniTextTitle)
                ?.let { adapterLinear?.selectLanguage(it) }
            _binding?.recycler?.adapter = adapterLinear
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_sound_screen,
            id_native_sound_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if(!isAdded && !isVisible && isDetached){
                        return
                    }
                    _binding?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.adView?.visibility = View.GONE
                    if(isAdded && isVisible && !isDetached) {
                        val adView = if(val_ad_native_sound_screen_is_H){
                            layoutInflater.inflate(R.layout.ad_unified_hight, null) as NativeAdView
                        }else{
                            layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                        }
                        adsManager?.nativeAds()?.nativeViewPolicy(currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    _binding?.nativeExitAd?.visibility = View.GONE
                    _binding?.adView?.visibility = View.GONE
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    _binding?.nativeExitAd?.visibility = View.GONE
                    _binding?.adView?.visibility = View.GONE
                    super.nativeAdValidate(string)
                }
            })

//        adsManager?.nativeAdsSplash()?.loadNativeAd(
//            activity ?: return,
//            true,
//            id_native_main_menu_screen,
//            object : NativeListener {
//
//            })
    }

}