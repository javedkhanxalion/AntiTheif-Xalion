package com.example.antitheifproject.ui

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentDetailModuleBinding
import com.example.antitheifproject.Admin
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.ads_manager.showTwoInterAd
import com.example.antitheifproject.helper_class.Constants.Intruder_Alarm
import com.example.antitheifproject.helper_class.Constants.isServiceRunning
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.model.MainMenuModel
import com.example.antitheifproject.utilities.ANTI_TITLE
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_GRID
import com.example.antitheifproject.utilities.autoServiceFunctionIntruder
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_password_screen
import com.example.antitheifproject.utilities.loadImage
import com.example.antitheifproject.utilities.setImage
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.startLottieAnimation
import com.example.antitheifproject.utilities.val_ad_native_password_screen
import com.example.antitheifproject.utilities.val_ad_native_password_screen_is_H
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FragmentPasswordDetail :
    BaseFragment<FragmentDetailModuleBinding>(FragmentDetailModuleBinding::inflate) {

    private var isGridLayout: Boolean? = null
    private var model: MainMenuModel? = null
    private var dbHelper: DbHelper? = null
    private var mComponentName: ComponentName? = null
    private var mDevicePolicyManager: DevicePolicyManager? = null
    private var adsManager: AdsManager? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adsManager = AdsManager.appAdsInit(activity ?: return)
        arguments?.let {
            model = it.getParcelable(ANTI_TITLE) ?: return
        }
        _binding?.textView3?.text = model?.bottomText
        mDevicePolicyManager =
            context?.getSystemService(AppCompatActivity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mComponentName = ComponentName(context ?: return, Admin::class.java)
        dbHelper = DbHelper(context ?: return)
        _binding?.topLay?.title?.text = model?.maniTextTitle
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
                        findNavController().navigate(
                            R.id.FragmentSoundSelection,
                            bundleOf(ANTI_TITLE to model)
                        )
                    }
                }
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
                        findNavController().navigate(
                            R.id.FragmentSoundSelection,
                            bundleOf(ANTI_TITLE to model)
                        )
                    }
                }
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
                dbHelper?.saveData(context ?: return, IS_GRID, true)
                topLay.setLayoutBtn.setImage(R.drawable.icon_grid)
                gridLayout.topImage.loadImage(context ?: return, model?.subMenuIcon ?: return)
                gridLayout.soundImage.loadImage(context ?: return, R.drawable.icon_sound)
                gridLayout.topGrid.visibility = View.VISIBLE
                linearlayout.topLinear.visibility = View.GONE
                gridLayout.titleText.text = model?.maniTextTitle ?: return
                gridLayout.customSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (!bool) {
                            startLottieAnimation(
                                activeAnimationView,
                                activeAnimationViewText,
                                false
                            )
                            dbHelper?.setBroadCast(Intruder_Alarm, false)
                        } else {
                            val devicePolicyManager = mDevicePolicyManager
                            if (devicePolicyManager == null || devicePolicyManager.isAdminActive(
                                    (mComponentName) ?: return@setOnCheckedChangeListener
                                )
                            ) {
                                startLottieAnimation(
                                    activeAnimationView,
                                    activeAnimationViewText,
                                    true
                                )
                                dbHelper?.setBroadCast(Intruder_Alarm, true)
                                if (!isServiceRunning()) {
                                    autoServiceFunctionIntruder(true, dbHelper)
                                }
                                return@setOnCheckedChangeListener
                            } else {
                                val intent = Intent("android.app.action.ADD_DEVICE_ADMIN")
                                intent.putExtra("android.app.extra.DEVICE_ADMIN", mComponentName)
                                intent.putExtra(
                                    "android.app.extra.ADD_EXPLANATION",
                                    "Administrator description"
                                )
                                startActivityForResult(intent, 3)
                            }
                        }
                    }
                }
                gridLayout.flashSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            dbHelper?.setBroadCast(
                                model?.isFlash, true
                            )
                        } else {
                            dbHelper?.setBroadCast(
                                model?.isFlash,
                                false
                            )
                        }
                    }
                }
                gridLayout.vibrationSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            dbHelper?.setBroadCast(
                                model?.isVibration, true
                            )
                        } else {
                            dbHelper?.setBroadCast(
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
                dbHelper?.saveData(context ?: return, IS_GRID, false)
                topLay.setLayoutBtn.setImage(R.drawable.icon_list)
                linearlayout.topImage.loadImage(context ?: return, model?.subMenuIcon ?: return)
                linearlayout.soundImage.loadImage(context ?: return, R.drawable.icon_sound)
                linearlayout.topLinear.visibility = View.VISIBLE
                gridLayout.topGrid.visibility = View.GONE
                linearlayout.titleText.text = model?.maniTextTitle ?: return
                linearlayout.customSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (!bool) {
                            startLottieAnimation(
                                activeAnimationView,
                                activeAnimationViewText,
                                false
                            )
                            dbHelper?.setBroadCast(Intruder_Alarm, false)
                        } else {
                            val devicePolicyManager = mDevicePolicyManager
                            if (devicePolicyManager == null || devicePolicyManager.isAdminActive(
                                    (mComponentName) ?: return@setOnCheckedChangeListener
                                )
                            ) {
                                startLottieAnimation(
                                    activeAnimationView,
                                    activeAnimationViewText,
                                    true
                                )
                                dbHelper?.setBroadCast(Intruder_Alarm, true)
                                if (!isServiceRunning()) {
                                    autoServiceFunctionIntruder(true, dbHelper)
                                }
                                return@setOnCheckedChangeListener
                            } else {
                                val intent = Intent("android.app.action.ADD_DEVICE_ADMIN")
                                intent.putExtra("android.app.extra.DEVICE_ADMIN", mComponentName)
                                intent.putExtra(
                                    "android.app.extra.ADD_EXPLANATION",
                                    "Administrator description"
                                )
                                startActivityForResult(intent, 3)
                            }
                        }
                    }
                }
                linearlayout.flashSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            dbHelper?.setBroadCast(
                                model?.isFlash, true
                            )
                        } else {
                            dbHelper?.setBroadCast(
                                model?.isFlash,
                                false
                            )
                        }
                    }
                }
                linearlayout.vibrationSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (bool) {
                            dbHelper?.setBroadCast(
                                model?.isVibration, true
                            )
                        } else {
                            dbHelper?.setBroadCast(
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
        dbHelper?.getBooleanData(context ?: return, IS_GRID, true)?.let {
            loadLayoutDirection(it)
            isGridLayout = it
        }
    }

    private fun checkSwitch() {
        _binding?.run {
            if (dbHelper?.chkBroadCast(Intruder_Alarm) ?: return) {
                startLottieAnimation(activeAnimationView, activeAnimationViewText, true)
            } else {
                startLottieAnimation(activeAnimationView, activeAnimationViewText, false)
            }
            gridLayout.customSwitch.isChecked =
                dbHelper?.chkBroadCast(Intruder_Alarm) ?: return
            linearlayout.customSwitch.isChecked =
                dbHelper?.chkBroadCast(Intruder_Alarm) ?: return
            gridLayout.flashSwitch.isChecked =
                dbHelper?.chkBroadCast(model?.isFlash)
                    ?: return
            linearlayout.flashSwitch.isChecked =
                dbHelper?.chkBroadCast(model?.isFlash)
                    ?: return
            gridLayout.vibrationSwitch.isChecked =
                dbHelper?.chkBroadCast(model?.isVibration)
                    ?: return
            linearlayout.vibrationSwitch.isChecked =
                dbHelper?.chkBroadCast(model?.isVibration)
                    ?: return
        }
    }

    private fun loadNativeGrid() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_password_screen,
            id_native_password_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.gridLayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.gridLayout?.adView?.visibility = View.GONE
                        if (isAdded && isVisible && !isDetached) {
                            val adView = if (val_ad_native_password_screen_is_H) {
                                layoutInflater.inflate(
                                    R.layout.ad_unified_hight,
                                    null
                                ) as NativeAdView
                            } else {
                                layoutInflater.inflate(
                                    R.layout.ad_unified_low,
                                    null
                                ) as NativeAdView
                            }
                            adsManager?.nativeAds()
                                ?.nativeViewPolicy(currentNativeAd ?: return, adView)
                            _binding?.gridLayout?.nativeExitAd?.removeAllViews()
                            _binding?.gridLayout?.nativeExitAd?.addView(adView)
                        }
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.gridLayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.gridLayout?.adView?.visibility = View.GONE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.gridLayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.gridLayout?.adView?.visibility = View.GONE
                    }
                    super.nativeAdValidate(string)
                }

            })
    }

    private fun loadNativeList() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_password_screen,
            id_native_password_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.linearlayout?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.linearlayout?.adView?.visibility = View.GONE
                        val adView = if (val_ad_native_password_screen_is_H) {
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
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.linearlayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.linearlayout?.adView?.visibility = View.GONE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.linearlayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.linearlayout?.adView?.visibility = View.GONE
                    }
                    super.nativeAdValidate(string)
                }


            })
    }

}