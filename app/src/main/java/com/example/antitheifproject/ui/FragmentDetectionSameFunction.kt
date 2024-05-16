package com.example.antitheifproject.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentDetailModuleBinding
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.ads_manager.showTwoInterAd
import com.example.antitheifproject.helper_class.Constants.isServiceRunning
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.model.MainMenuModel
import com.example.antitheifproject.utilities.ANTI_TITLE
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_GRID
import com.example.antitheifproject.utilities.autoServiceFunctionInternalModule
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.loadImage
import com.example.antitheifproject.utilities.setImage
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.startLottieAnimation
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FragmentDetectionSameFunction :
    BaseFragment<FragmentDetailModuleBinding>(FragmentDetailModuleBinding::inflate) {

    private var isGridLayout: Boolean? = null
    private var model: MainMenuModel? = null
    var sharedPrefUtils: DbHelper? = null
    private var adsManager: AdsManager? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adsManager = AdsManager.appAdsInit(activity ?: return)
        arguments?.let {
            model = it.getParcelable(ANTI_TITLE) ?: return@let
        }
        _binding?.topLay?.title?.text = model?.maniTextTitle
        _binding?.textView3?.text = model?.bottomText
        sharedPrefUtils = DbHelper(context ?: return)


        _binding?.topLay?.navMenu?.loadImage(context ?: return, R.drawable.back_btn)

        _binding?.run {

            topLay.navMenu.clickWithThrottle {
                findNavController().navigateUp()
            }


            gridLayout.soundIcon.clickWithThrottle {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@let,
                        remoteConfigMedium = val_inter_main_medium,
                        remoteConfigNormal = val_inter_main_normal,
                        adIdMedium = id_inter_main_medium,
                        adIdNormal = id_inter_main_normal,
                        tagClass = model?.maniTextTitle ?: return@let,
                        isBackPress = true,
                        layout = binding?.adsLay!!
                    ) {
                    }
                }
                findNavController().navigate(
                    R.id.FragmentSoundSelection,
                    bundleOf(ANTI_TITLE to model)
                )
            }

            linearlayout.soundIcon.clickWithThrottle {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@let,
                        remoteConfigMedium = val_inter_main_medium,
                        remoteConfigNormal = val_inter_main_normal,
                        adIdMedium = id_inter_main_medium,
                        adIdNormal = id_inter_main_normal,
                        tagClass = model?.maniTextTitle ?: return@let,
                        isBackPress = true,
                        layout = binding?.adsLay!!
                    ) {
                    }
                }
                findNavController().navigate(
                    R.id.FragmentSoundSelection,
                    bundleOf(ANTI_TITLE to model)
                )
            }

            topLay.setLayoutBtn.clickWithThrottle {
                loadLayoutDirection(!(isGridLayout ?: return@clickWithThrottle))
            }

        }

        setupBackPressedCallback {
            findNavController().navigateUp()
        }

    }

    private fun loadLayoutDirection(isGrid: Boolean) {
        _binding?.run {
            if (isGrid) {
                isGridLayout = true
                sharedPrefUtils?.saveData(context ?: return, IS_GRID, true)
                topLay.setLayoutBtn.setImage(R.drawable.icon_grid)
                gridLayout.topGrid.visibility = View.VISIBLE
                linearlayout.topLinear.visibility = View.GONE
                gridLayout.titleText.text = model?.maniTextTitle
                model?.subMenuIcon?.let { gridLayout.topImage.loadImage(context, it) }
                gridLayout.soundImage.loadImage(context, R.drawable.icon_sound)
                gridLayout.customSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (compoundButton.isPressed) {
                            if (bool) {
                                startLottieAnimation(
                                    activeAnimationView,
                                    activeAnimationViewText,
                                    true
                                )
                                if (!isServiceRunning()) {
                                    autoServiceFunctionInternalModule(true, model?.isActive)
                                } else {
                                    sharedPrefUtils?.setBroadCast(model?.isActive, true)
                                }
                            } else {
                                startLottieAnimation(
                                    activeAnimationView,
                                    activeAnimationViewText,
                                    false
                                )
                                if (isServiceRunning()) {
                                    autoServiceFunctionInternalModule(false, model?.isActive)
                                } else {
                                    sharedPrefUtils?.setBroadCast(model?.isActive, false)
                                }
                            }
                        }
                    }
                }
                gridLayout.flashSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            sharedPrefUtils?.setBroadCast(
                                model?.isFlash, true
                            )
                        } else {
                            sharedPrefUtils?.setBroadCast(
                                model?.isFlash,
                                false
                            )
                        }
                    }
                }
                gridLayout.vibrationSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            sharedPrefUtils?.setBroadCast(
                                model?.isVibration, true
                            )
                        } else {
                            sharedPrefUtils?.setBroadCast(
                                model?.isVibration,
                                false
                            )
                        }
                    }
                }
                checkSwitch()
                loadNativeGrid()
            } else {
                isGridLayout = false
                sharedPrefUtils?.saveData(context ?: return, IS_GRID, false)
                topLay.setLayoutBtn.setImage(R.drawable.icon_list)
                linearlayout.topLinear.visibility = View.VISIBLE
                gridLayout.topGrid.visibility = View.GONE
                linearlayout.titleText.text = model?.maniTextTitle ?: return
                model?.subMenuIcon?.let { linearlayout.topImage.loadImage(context, it) }
                linearlayout.soundImage.loadImage(context, R.drawable.icon_sound)
                linearlayout.customSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            startLottieAnimation(activeAnimationView, activeAnimationViewText, true)
                            if (!isServiceRunning()) {
                                autoServiceFunctionInternalModule(true, model?.isActive)
                            } else {
                                sharedPrefUtils?.setBroadCast(model?.isActive, true)
                            }
                        } else {
                            startLottieAnimation(
                                activeAnimationView,
                                activeAnimationViewText,
                                false
                            )
                            if (isServiceRunning()) {
                                autoServiceFunctionInternalModule(false, model?.isActive)
                            } else {
                                sharedPrefUtils?.setBroadCast(model?.isActive, false)
                            }
                        }
                    }
                }
                linearlayout.flashSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            sharedPrefUtils?.setBroadCast(
                                model?.isFlash, true
                            )
                        } else {
                            sharedPrefUtils?.setBroadCast(
                                model?.isFlash,
                                false
                            )
                        }
                    }
                }
                linearlayout.vibrationSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            sharedPrefUtils?.setBroadCast(
                                model?.isVibration, true
                            )
                        } else {
                            sharedPrefUtils?.setBroadCast(
                                model?.isVibration,
                                false
                            )
                        }
                    }
                }
                checkSwitch()
                loadNativeList()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onResume() {
        super.onResume()
        checkSwitch()
        sharedPrefUtils?.getBooleanData(context ?: return, IS_GRID, true)?.let {
            loadLayoutDirection(it)
            isGridLayout = it
        }
    }

    private fun checkSwitch() {
        _binding?.run {
            if (sharedPrefUtils?.chkBroadCast(model?.isActive) ?: return) {
                startLottieAnimation(activeAnimationView, activeAnimationViewText, true)
            } else {
                startLottieAnimation(activeAnimationView, activeAnimationViewText, false)
            }
            gridLayout.customSwitch.isChecked =
                sharedPrefUtils?.chkBroadCast(model?.isActive) ?: return
            linearlayout.customSwitch.isChecked =
                sharedPrefUtils?.chkBroadCast(model?.isActive) ?: return
            gridLayout.flashSwitch.isChecked =
                sharedPrefUtils?.chkBroadCast(model?.isFlash) ?: return
            linearlayout.flashSwitch.isChecked =
                sharedPrefUtils?.chkBroadCast(model?.isFlash) ?: return
            gridLayout.vibrationSwitch.isChecked =
                sharedPrefUtils?.chkBroadCast(model?.isVibration) ?: return
            linearlayout.vibrationSwitch.isChecked =
                sharedPrefUtils?.chkBroadCast(model?.isVibration) ?: return
        }
    }

    private fun loadNativeGrid() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            model?.remoteValue ?: return,
            model?.idAds ?: return,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (!isAdded && !isVisible && isDetached) {
                        return
                    }
                    _binding?.gridLayout?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.gridLayout?.adView?.visibility = View.GONE
                    if (isAdded && isVisible && !isDetached) {
                        val adView = if (model?.remoteValueHigh == true) {
                            layoutInflater.inflate(R.layout.ad_unified_hight, null) as NativeAdView
                        } else {
                            layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                        }
                        adsManager?.nativeAds()?.nativeViewPolicy(currentNativeAd ?: return, adView)
                        _binding?.gridLayout?.nativeExitAd?.removeAllViews()
                        _binding?.gridLayout?.nativeExitAd?.addView(adView)
                    }

                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    _binding?.gridLayout?.nativeExitAd?.visibility = View.GONE
                    _binding?.gridLayout?.adView?.visibility = View.GONE
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    _binding?.gridLayout?.nativeExitAd?.visibility = View.GONE
                    _binding?.gridLayout?.adView?.visibility = View.GONE
                    super.nativeAdValidate(string)
                }


            })
    }

    private fun loadNativeList() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            model?.remoteValue ?: return,
            model?.idAds ?: return,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (!isAdded && !isVisible && isDetached) {
                        return
                    }
                    _binding?.linearlayout?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.linearlayout?.adView?.visibility = View.GONE
                    if (isAdded && isVisible && !isDetached) {
                        val adView = if (model?.remoteValueHigh == true) {
                            layoutInflater.inflate(R.layout.ad_unified_hight, null) as NativeAdView
                        } else {
                            layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                        }
                        adsManager?.nativeAds()?.nativeViewPolicy(currentNativeAd ?: return, adView)
                        _binding?.linearlayout?.nativeExitAd?.removeAllViews()
                        _binding?.linearlayout?.nativeExitAd?.addView(adView)
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    _binding?.linearlayout?.nativeExitAd?.visibility = View.GONE
                    _binding?.linearlayout?.adView?.visibility = View.GONE
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    _binding?.linearlayout?.nativeExitAd?.visibility = View.GONE
                    _binding?.linearlayout?.adView?.visibility = View.GONE
                    super.nativeAdValidate(string)
                }


            })
    }

}
