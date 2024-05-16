package com.example.antitheifproject.ui

import android.app.Activity.RESULT_OK
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentInturderDetectionDetailBinding
import com.example.antitheifproject.Admin
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.ads_manager.showTwoInterAd
import com.example.antitheifproject.helper_class.Constants.Intruder_Alarm
import com.example.antitheifproject.helper_class.Constants.Intruder_Selfie
import com.example.antitheifproject.helper_class.Constants.isServiceRunning
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.CAMERA_PERMISSION
import com.example.antitheifproject.utilities.IS_GRID
import com.example.antitheifproject.utilities.autoServiceFunctionIntruder
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_intruder_detection_screen
import com.example.antitheifproject.utilities.loadImage
import com.example.antitheifproject.utilities.requestCameraPermission
import com.example.antitheifproject.utilities.setImage
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.startLottieAnimation
import com.example.antitheifproject.utilities.val_ad_instertital_intruder_detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_native_intruder_detection_screen
import com.example.antitheifproject.utilities.val_ad_native_intruder_detection_screen_is_H
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class FragmentInturderDetectionDetail :
    BaseFragment<FragmentInturderDetectionDetailBinding>(FragmentInturderDetectionDetailBinding::inflate) {

    private var dbHelper: DbHelper? = null
    private var mComponentName: ComponentName? = null
    private var mDevicePolicyManager: DevicePolicyManager? = null
    private var isGridLayout: Boolean? = null
    private var adsManager: AdsManager? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adsManager = AdsManager.appAdsInit(activity ?: return)

        dbHelper = DbHelper(context ?: return)
        _binding?.textView3?.text = getString(R.string.title_intruder)
        mDevicePolicyManager =
            context?.getSystemService(AppCompatActivity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mComponentName = ComponentName(context ?: return, Admin::class.java)
        _binding?.topLay?.navMenu?.loadImage(context ?: return, R.drawable.back_btn)
        _binding?.run {
            _binding?.topLay?.navMenu?.clickWithThrottle {
                findNavController().navigateUp()
            }
            topLay.setLayoutBtn.clickWithThrottle {
                loadLayoutDirection(!(isGridLayout ?: return@clickWithThrottle))
            }
        }
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun loadLayoutDirection(isGrid: Boolean) {
        checkSwitch()
        if (isGrid) {
            isGridLayout = true
            dbHelper?.saveData(context ?: return, IS_GRID, true)
            _binding?.run {
                topLay.setLayoutBtn.setImage(R.drawable.icon_grid)
                gridLayout.topGrid.visibility = View.VISIBLE
                linearlayout.topLinear.visibility = View.GONE
                gridLayout.inturderAlertSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (!bool) {
                            startLottieAnimation(
                                activeAnimationView,
                                activeAnimationViewText,
                                false
                            )
                            dbHelper?.setBroadCast(Intruder_Selfie, false)
                        } else if (ContextCompat.checkSelfPermission(
                                context ?: return@setOnCheckedChangeListener,
                                CAMERA_PERMISSION
                            ) == 0
                        ) {
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
                                dbHelper?.setBroadCast(Intruder_Selfie, true)
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
                                cameraActivityResultLauncher.launch(intent)
                            }
                        } else {
                            requestCameraPermission(gridLayout.inturderAlertSwitch)
                        }
                    }
                }
                gridLayout.stopAlertSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        dbHelper?.setAlarmSetting(Intruder_Alarm, bool)
                        if (bool) {
                            if (!isServiceRunning()) {
                                autoServiceFunctionIntruder(true, dbHelper)
                            }
                        }
                    }
                }
                gridLayout.intruderImagesView.clickWithThrottle {
                    adsManager?.let {
                        showTwoInterAd(
                            ads = it,
                            activity = activity ?: return@let,
                            remoteConfigMedium = val_inter_main_medium,
                            remoteConfigNormal = val_inter_main_normal,
                            adIdMedium = id_inter_main_medium,
                            adIdNormal = id_inter_main_normal,
                            tagClass = "Intruder Alert",
                            isBackPress = true,
                            layout = binding?.adsLay!!
                        ) {

                        }
                    }
                    findNavController().navigate(R.id.FragmentShowIntruder)

                }
                gridLayout.run {
                    at1.clickWithThrottle {
                        at1.setBackgroundColor(Color.parseColor("#5F82E2"))
                        at2.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        at3.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        dbHelper?.setAttemptNo("1")
                    }

                    at2.clickWithThrottle {
                        dbHelper?.setAttemptNo(ExifInterface.GPS_MEASUREMENT_2D)
                        at2.setBackgroundColor(Color.parseColor("#5F82E2"))
                        at1.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        at3.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                    }

                    at3.clickWithThrottle {
                        dbHelper?.setAttemptNo(ExifInterface.GPS_MEASUREMENT_3D)
                        at3.setBackgroundColor(Color.parseColor("#5F82E2"))
                        at2.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        at1.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                    }
                }
                loadNativeGrid()
            }
        } else {
            isGridLayout = false
            dbHelper?.saveData(context ?: return, IS_GRID, false)
            _binding?.run {
                topLay.setLayoutBtn.setImage(R.drawable.icon_list)
                linearlayout.topLinear.visibility = View.VISIBLE
                gridLayout.topGrid.visibility = View.GONE
                linearlayout.inturderAlertSwitch.isChecked =
                    dbHelper?.chkBroadCast(Intruder_Selfie) ?: return
                linearlayout.inturderAlertSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        if (!bool) {
                            startLottieAnimation(
                                activeAnimationView,
                                activeAnimationViewText,
                                false
                            )
                            dbHelper?.setBroadCast(Intruder_Selfie, false)
                        } else if (ContextCompat.checkSelfPermission(
                                context ?: return@setOnCheckedChangeListener,
                                CAMERA_PERMISSION
                            ) == 0
                        ) {
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
                                dbHelper?.setBroadCast(Intruder_Selfie, true)
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
                                cameraActivityResultLauncher.launch(intent)
                            }
                        } else {
                            requestCameraPermission(
                                linearlayout.inturderAlertSwitch
                            )
                        }
                    }
                }
                linearlayout.stopAlertSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                    if (compoundButton.isPressed) {
                        dbHelper?.setAlarmSetting(Intruder_Alarm, bool)
                        if (bool) {
                            if (!isServiceRunning()) {
                                autoServiceFunctionIntruder(true, dbHelper)
                            }
                        }
                    }
                }
                linearlayout.intruderImagesView.clickWithThrottle {
                    adsManager?.let {
                        showTwoInterAd(
                            ads = it,
                            activity = activity ?: return@let,
                            remoteConfigMedium = val_inter_main_medium,
                            remoteConfigNormal = val_inter_main_normal,
                            adIdMedium = id_inter_main_medium,
                            adIdNormal = id_inter_main_normal,
                            tagClass = "Intruder Alert",
                            isBackPress = true,
                            layout = binding?.adsLay!!
                        ) {
                        }
                    }
                    findNavController().navigate(R.id.FragmentShowIntruder)
                }
                linearlayout.run {
                    at1.clickWithThrottle {
                        at1.setBackgroundColor(Color.parseColor("#5F82E2"))
                        at2.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        at3.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        dbHelper?.setAttemptNo("1")

                    }
                    at2.clickWithThrottle {
                        dbHelper?.setAttemptNo(ExifInterface.GPS_MEASUREMENT_2D)
                        at2.setBackgroundColor(Color.parseColor("#5F82E2"))
                        at1.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        at3.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                    }
                    at3.clickWithThrottle {
                        dbHelper?.setAttemptNo(ExifInterface.GPS_MEASUREMENT_3D)
                        at3.setBackgroundColor(Color.parseColor("#5F82E2"))
                        at2.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                        at1.setBackgroundColor(
                            ContextCompat.getColor(
                                context ?: return@clickWithThrottle, R.color.menuBgColor
                            )
                        )
                    }
                }
                loadNativeList()

            }
        }
    }

    private var cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result in this block
        if (result.resultCode == RESULT_OK) {
            // Extract data from the result Intent if needed
            if (ContextCompat.checkSelfPermission(
                    context ?: return@registerForActivityResult,
                    CAMERA_PERMISSION
                ) == 0
                && (mDevicePolicyManager == null || mDevicePolicyManager!!.isAdminActive(
                    (mComponentName) ?: return@registerForActivityResult
                )
                        )
            ) {
                dbHelper?.setBroadCast(Intruder_Selfie, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSwitch()
        checkPinAttempt()
        dbHelper?.getBooleanData(context ?: return, IS_GRID, true)?.let {
            isGridLayout = it
            loadLayoutDirection(it)
        }
    }

    private fun checkPinAttempt() {
        when (dbHelper?.attemptNo) {
            1 -> {
                _binding?.gridLayout?.at1?.setBackgroundColor(Color.parseColor("#5F82E2"))
                _binding?.linearlayout?.at1?.setBackgroundColor(Color.parseColor("#5F82E2"))
            }

            2 -> {
                _binding?.gridLayout?.at2?.setBackgroundColor(Color.parseColor("#5F82E2"))
                _binding?.linearlayout?.at2?.setBackgroundColor(Color.parseColor("#5F82E2"))
            }

            3 -> {
                _binding?.gridLayout?.at3?.setBackgroundColor(Color.parseColor("#5F82E2"))
                _binding?.linearlayout?.at3?.setBackgroundColor(Color.parseColor("#5F82E2"))
            }
        }
    }

    private fun checkSwitch() {

        _binding?.run {
            if (dbHelper?.chkBroadCast(Intruder_Selfie) ?: return) {
                startLottieAnimation(activeAnimationView, activeAnimationViewText, true)
            } else {
                startLottieAnimation(activeAnimationView, activeAnimationViewText, false)
            }

            gridLayout.inturderAlertSwitch.isChecked =
                dbHelper?.chkBroadCast(Intruder_Selfie) ?: return
            linearlayout.inturderAlertSwitch.isChecked =
                dbHelper?.chkBroadCast(Intruder_Selfie) ?: return
            gridLayout.stopAlertSwitch.isChecked =
                dbHelper?.chkBroadCast(Intruder_Alarm) ?: return
            linearlayout.stopAlertSwitch.isChecked =
                dbHelper?.chkBroadCast(Intruder_Alarm) ?: return

        }
    }

    private fun loadNativeGrid() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_intruder_detection_screen,
            id_native_intruder_detection_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    _binding?.gridLayout?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.gridLayout?.adView?.visibility = View.GONE
                    if (isAdded && isVisible && !isDetached) {
                        val adView = if (val_ad_native_intruder_detection_screen_is_H) {
                            layoutInflater.inflate(R.layout.ad_unified_hight, null) as NativeAdView
                        } else {
                            layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                        }
                        adsManager?.nativeAds()
                            ?.nativeViewPolicy(currentNativeAd ?: return, adView)
                        _binding?.gridLayout?.nativeExitAd?.removeAllViews()
                        _binding?.gridLayout?.nativeExitAd?.addView(adView)
                    } else {
                        _binding?.gridLayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.gridLayout?.adView?.visibility = View.GONE
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
            val_ad_native_intruder_detection_screen,
            id_native_intruder_detection_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    _binding?.linearlayout?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.linearlayout?.adView?.visibility = View.GONE
                    if (isAdded && isVisible && !isDetached) {
                        val adView = if (val_ad_native_intruder_detection_screen_is_H) {
                            layoutInflater.inflate(R.layout.ad_unified_hight, null) as NativeAdView
                        } else {
                            layoutInflater.inflate(R.layout.ad_unified_low, null) as NativeAdView
                        }
                        adsManager?.nativeAds()
                            ?.nativeViewPolicy(currentNativeAd ?: return, adView)
                        _binding?.linearlayout?.nativeExitAd?.removeAllViews()
                        _binding?.linearlayout?.nativeExitAd?.addView(adView)
                    } else {
                        _binding?.linearlayout?.nativeExitAd?.visibility = View.GONE
                        _binding?.linearlayout?.adView?.visibility = View.GONE
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