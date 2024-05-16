package com.example.antitheifproject.ui

import MainMenuGridAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentMainMenuActivityBinding
import com.example.antitheifproject.adapter.MainMenuLinearAdapter
import com.example.antitheifproject.ads_manager.AdsBanners.loadCollapsibleBanner
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.FunctionClass
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.ads_manager.showTwoInterAd
import com.example.antitheifproject.ads_manager.showTwoInterAdFirst
import com.example.antitheifproject.helper_class.Constants.isServiceRunning
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.model.MainMenuModel
import com.example.antitheifproject.utilities.ANTI_TITLE
import com.example.antitheifproject.utilities.AUDIO_PERMISSION
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.IS_GRID
import com.example.antitheifproject.utilities.IS_NOTIFICATION
import com.example.antitheifproject.utilities.LANG_SCREEN
import com.example.antitheifproject.utilities.PHONE_PERMISSION
import com.example.antitheifproject.utilities.autoServiceFunction
import com.example.antitheifproject.utilities.checkNotificationPermission
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.getMenuListGrid
import com.example.antitheifproject.utilities.id_banner_1
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_main_menu_screen
import com.example.antitheifproject.utilities.isShowInApp
import com.example.antitheifproject.utilities.moreApp
import com.example.antitheifproject.utilities.privacyPolicy
import com.example.antitheifproject.utilities.rateUs
import com.example.antitheifproject.utilities.requestCameraPermissionAudio
import com.example.antitheifproject.utilities.setImage
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.shareApp
import com.example.antitheifproject.utilities.showRatingDialog
import com.example.antitheifproject.utilities.showServiceDialog
import com.example.antitheifproject.utilities.showToast
import com.example.antitheifproject.utilities.val_banner_1
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal


class MainMenuFragment :
    BaseFragment<FragmentMainMenuActivityBinding>(FragmentMainMenuActivityBinding::inflate) {

    private var adapterGrid: MainMenuGridAdapter? = null
    private var adapterLinear: MainMenuLinearAdapter? = null
    var sharedPrefUtils: DbHelper? = null
    private var isGridLayout: Boolean? = null
    private var adsManager: AdsManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adsManager = AdsManager.appAdsInit(activity ?: return)
        firebaseAnalytics("main_menu_fragment_open", "main_menu_fragment_open -->  Click")
        sharedPrefUtils = DbHelper(context ?: return)
        setupBackPressedCallback {
            if (_binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
                _binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            } else {
                adsManager?.let {
                    showTwoInterAdFirst(
                        ads = it,
                        activity = activity ?: return@let ,
                        remoteConfigMedium = val_inter_main_medium,
                        remoteConfigNormal = val_inter_main_normal,
                        adIdMedium = id_inter_main_medium,
                        adIdNormal = id_inter_main_normal,
                        tagClass = "exit",
                        isBackPress = false,
                        layout = binding?.mainLayout?.adsLay!!
                    ) {
                    }
                }
                findNavController().navigate(R.id.FragmentExitScreen)
            }
        }
        _binding?.run {
            mainLayout.topLay.setLayoutBtn.clickWithThrottle {
                loadLayoutDirection(!(isGridLayout ?: return@clickWithThrottle))
            }
            mainLayout.topLay.settingBtn.clickWithThrottle {
                if (AdsManager.isNetworkAvailable(context)) {
                    firebaseAnalytics("purchase_dialog_continue_btn", "makingPurchase")
                    findNavController().navigate(
                        R.id.FragmentInAppScreen,
                        bundleOf("Is_From_Splash" to true)
                    )
                } else {
                    FunctionClass.toast(requireActivity(), getString(R.string.no_internet))
                }
            }
            navView.rateUsView.clickWithThrottle {
                showRatingDialog(onPositiveButtonClick = { it, _dialog ->
                    if (it >= 1F && it < 3F) {
                        _dialog.dismiss()
                        showToast("Thanks For Your Time")
                    } else if (it in 3F..5F) {
                        _dialog.dismiss()
                        requireContext().rateUs()
                    }
                })
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            navView.shareAppView.clickWithThrottle {
                requireContext().shareApp()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            navView.privacyView.clickWithThrottle {
                requireContext().privacyPolicy()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            navView.viewTop.clickWithThrottle {
                findNavController().navigate(
                    R.id.FragmentInAppScreen,
                    bundleOf("Is_From_Splash" to false)
                )
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            navView.moreAppView.clickWithThrottle {
                requireContext().moreApp()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            navView.languageView.clickWithThrottle {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@clickWithThrottle,
                        remoteConfigMedium = val_inter_main_medium,
                        remoteConfigNormal = val_inter_main_normal,
                        adIdMedium = id_inter_main_medium,
                        adIdNormal = id_inter_main_normal,
                        tagClass = "language_screen",
                        isBackPress = false,
                        layout = binding?.mainLayout?.adsLay!!
                    ) {
                    }
                }
                firebaseAnalytics(
                    "main_menu_fragment_language_open",
                    "main_menu_fragment_language_open -->  Click"
                )
                findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to false))
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            navView.navigationMain.setOnClickListener { }
            navView.customSwitch.setOnCheckedChangeListener { compoundButton, bool ->
                if (compoundButton.isPressed) {
                    if (bool) {
                        if (!isServiceRunning()) {
                            autoServiceFunction(true)
                            drawerLayout.closeDrawer(GravityCompat.START)
                        }
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.START)
                        showServiceDialog(
                            onPositiveNoClick = {
                                navView.customSwitch.isChecked = true
                            },
                            onPositiveYesClick = {
                                if (isServiceRunning()) {
                                    autoServiceFunction(false)
                                }
                            })
                    }
                }
            }
            mainLayout.topLay.navMenu.clickWithThrottle {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        loadCollapsibleBanner(
            activity ?: return,
            binding?.mainLayout?.bannerAds!!,
            val_banner_1,
            id_banner_1
        )
        checkNotificationPermission()
        val drawerView: View = view.findViewById(R.id.drawerLayout)
        if (drawerView is DrawerLayout) {
            drawerView.setDrawerListener(object : DrawerListener {
                override fun onDrawerSlide(view: View, v: Float) {
                    Log.d("drawer_check", "onDrawerSlide: $v")
                }

                override fun onDrawerOpened(view: View) {
                    _binding?.mainLayout?.hideAd?.visibility = View.VISIBLE
                }

                override fun onDrawerClosed(view: View) {
                    _binding?.mainLayout?.hideAd?.visibility = View.INVISIBLE
                }

                override fun onDrawerStateChanged(i: Int) {
                    Log.d("drawer_check", "onDrawerStateChanged: $i")
                }
            })
        }
    }

    private fun loadLayoutDirection(isGrid: Boolean) {
        if (isGrid) {
            isGridLayout = true
            sharedPrefUtils?.saveData(context ?: return, IS_GRID, true)
            _binding?.mainLayout?.topLay?.setLayoutBtn?.setImage(
                R.drawable.icon_grid
            )
            adapterGrid =
                MainMenuGridAdapter(activity ?: return, getMenuListGrid(sharedPrefUtils ?: return))
            val managerLayout = GridLayoutManager(context, 3)
            val spanSizeLookup1 = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // Return the span size for each item
                    return if (position == 3) {
                        // Return a larger span size for items at positions divisible by 3
                        3
                    } else {
                        // Return the default span size for other items
                        1
                    }
                }
            }
            managerLayout.spanSizeLookup = spanSizeLookup1
            _binding?.mainLayout?.recycler?.layoutManager = managerLayout
            _binding?.mainLayout?.recycler?.adapter = adapterGrid
            adapterGrid?.onClick = { s: MainMenuModel, i: Int ->
                loadFunction(s, i)
            }
        } else {
            isGridLayout = false
            sharedPrefUtils?.saveData(context ?: return, IS_GRID, false)
            adapterLinear =
                MainMenuLinearAdapter(
                    activity ?: return,
                    getMenuListGrid(sharedPrefUtils ?: return)
                )
            _binding?.mainLayout?.topLay?.setLayoutBtn?.setImage(
                R.drawable.icon_list
            )
            val managerLayout = LinearLayoutManager(context)
            managerLayout.orientation = LinearLayoutManager.VERTICAL
            _binding?.mainLayout?.recycler?.layoutManager = managerLayout
            _binding?.mainLayout?.recycler?.adapter = adapterLinear
            adapterLinear?.onClick = { s: MainMenuModel, i: Int ->
                loadFunction(s, i)
            }
        }
    }

    private fun loadFunction(model: MainMenuModel, position: Int) {
        firebaseAnalytics(model.maniTextTitle, "${model.maniTextTitle}_open -->  Click")

        when (position) {
            0 -> {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return,
                        remoteConfigMedium = val_inter_main_medium,
                        remoteConfigNormal = val_inter_main_normal,
                        adIdMedium = id_inter_main_medium,
                        adIdNormal = id_inter_main_normal,
                        tagClass = model.maniTextTitle,
                        isBackPress = false,
                        layout = binding?.mainLayout?.adsLay!!
                    ) {
                    }
                }
                findNavController().navigate(R.id.FragmentInturderDetectionDetail)
            }

            2 -> {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return,
                        remoteConfigMedium = val_inter_main_medium,
                        remoteConfigNormal = val_inter_main_normal,
                        adIdMedium = id_inter_main_medium,
                        adIdNormal = id_inter_main_normal,
                        tagClass = model.maniTextTitle,
                        isBackPress = false,
                        layout = binding?.mainLayout?.adsLay!!
                    ) {
                    }
                }
                findNavController().navigate(
                    R.id.FragmentPasswordDetail,
                    bundleOf(ANTI_TITLE to model)
                )
            }

            else -> {
                if (ContextCompat.checkSelfPermission(context ?: return, AUDIO_PERMISSION) == 0 &&
                    ContextCompat.checkSelfPermission(context ?: return, PHONE_PERMISSION) == 0
                ) {
                    adsManager?.let {
                        showTwoInterAd(
                            ads = it,
                            activity = activity ?: return,
                            remoteConfigMedium = val_inter_main_medium,
                            remoteConfigNormal = val_inter_main_normal,
                            adIdMedium = id_inter_main_medium,
                            adIdNormal = id_inter_main_normal,
                            tagClass = model.maniTextTitle,
                            isBackPress = false,
                            layout = binding?.mainLayout?.adsLay!!
                        ) {
                        }
                    }
                    findNavController().navigate(
                        R.id.FragmentDetectionSameFunction,
                        bundleOf(ANTI_TITLE to model)
                    )
                } else {
                    requestCameraPermissionAudio()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onResume() {
        super.onResume()
        sharedPrefUtils?.getBooleanData(context ?: return, IS_GRID, true)?.let {
            loadLayoutDirection(it)
            isGridLayout = it
        }
        sharedPrefUtils?.getBooleanData(context ?: return, IS_NOTIFICATION, false)?.let {
            _binding?.navView?.customSwitch?.isChecked = it
        }
//        if(isShowInApp) {
//            findNavController().navigate(R.id.FragmentInAppScreen)
//        }
    }

}