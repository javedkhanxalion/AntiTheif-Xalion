package com.example.antitheifproject.ui

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.FragmentSplashBinding
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.billing.BillingUtil
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.ads_manager.loadTwoInterAdsSplash
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.LANG_CODE
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.id_banner_1
import com.example.antitheifproject.utilities.id_exit_dialog_native
import com.example.antitheifproject.utilities.id_frequency_counter
import com.example.antitheifproject.utilities.id_inter_counter
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_Battery_Detection_screen
import com.example.antitheifproject.utilities.id_native_Motion_Detection_screen
import com.example.antitheifproject.utilities.id_native_Pocket_Detection_screen
import com.example.antitheifproject.utilities.id_native_Remove_Charger_screen
import com.example.antitheifproject.utilities.id_native_Whistle_Detection_screen
import com.example.antitheifproject.utilities.id_native_app_open_screen
import com.example.antitheifproject.utilities.id_native_clap_detection_screen
import com.example.antitheifproject.utilities.id_native_hand_free_screen
import com.example.antitheifproject.utilities.id_native_intro_screen
import com.example.antitheifproject.utilities.id_native_intruder_detection_screen
import com.example.antitheifproject.utilities.id_native_intruder_list_screen
import com.example.antitheifproject.utilities.id_native_language_screen
import com.example.antitheifproject.utilities.id_native_loading_screen
import com.example.antitheifproject.utilities.id_native_main_menu_screen
import com.example.antitheifproject.utilities.id_native_password_screen
import com.example.antitheifproject.utilities.id_native_sound_screen
import com.example.antitheifproject.utilities.inter_frequency_count
import com.example.antitheifproject.utilities.isFlowOne
import com.example.antitheifproject.utilities.isIntroLanguageShow
import com.example.antitheifproject.utilities.isShowInApp
import com.example.antitheifproject.utilities.isSplash
import com.example.antitheifproject.utilities.setLocaleMain
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.val_ad_instertital_Battery_Detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_Motion_Detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_Pocket_Detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_Remove_Charger_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_Whistle_Detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_clap_detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_exit_dialog_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_hand_free_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_intruder_detection_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_intruder_list_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_main_menu_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_password_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_show_image_screen_is_B
import com.example.antitheifproject.utilities.val_ad_instertital_sound_screen_is_B
import com.example.antitheifproject.utilities.val_ad_native_Battery_Detection_screen
import com.example.antitheifproject.utilities.val_ad_native_Battery_Detection_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_Motion_Detection_screen
import com.example.antitheifproject.utilities.val_ad_native_Motion_Detection_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_Pocket_Detection_screen
import com.example.antitheifproject.utilities.val_ad_native_Pocket_Detection_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_Remove_Charger_screen
import com.example.antitheifproject.utilities.val_ad_native_Remove_Charger_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_Whistle_Detection_screen
import com.example.antitheifproject.utilities.val_ad_native_Whistle_Detection_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_clap_detection_screen
import com.example.antitheifproject.utilities.val_ad_native_clap_detection_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_hand_free_screen
import com.example.antitheifproject.utilities.val_ad_native_hand_free_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_intro_screen
import com.example.antitheifproject.utilities.val_ad_native_intro_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_intruder_detection_screen
import com.example.antitheifproject.utilities.val_ad_native_intruder_detection_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_intruder_list_screen
import com.example.antitheifproject.utilities.val_ad_native_intruder_list_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_language_screen
import com.example.antitheifproject.utilities.val_ad_native_language_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_loading_screen
import com.example.antitheifproject.utilities.val_ad_native_loading_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_main_menu_screen
import com.example.antitheifproject.utilities.val_ad_native_main_menu_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_password_screen
import com.example.antitheifproject.utilities.val_ad_native_password_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_show_image_screen
import com.example.antitheifproject.utilities.val_ad_native_show_image_screen_is_H
import com.example.antitheifproject.utilities.val_ad_native_sound_screen
import com.example.antitheifproject.utilities.val_ad_native_sound_screen_is_H
import com.example.antitheifproject.utilities.val_banner_1
import com.example.antitheifproject.utilities.val_exit_dialog_native
import com.example.antitheifproject.utilities.val_exit_dialog_native_is_H
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_medium_Inapp_first
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale


class SplashFragment :
    BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private var dbHelper: DbHelper? = null
    private var adsManager: AdsManager? = null
    private var remoteConfig: FirebaseRemoteConfig? = null
//    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSplash = false
//      Log.d("device_id", "onViewCreated: ${getDeviceId()}")
        adsManager = AdsManager.appAdsInit(activity ?: return)
        dbHelper = DbHelper(context ?: return)
        dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }
        loadTwoInterAdsSplash(
            adsManager ?: return,
            activity ?: return,
            remoteConfigNormal = true,
            adIdNormal = getString(R.string.id_fullscreen_splash_2nd),
            "splash"
        )
        inter_frequency_count = 0
        CoroutineScope(Dispatchers.Main).launch {
            initRemoteIds()
            initRemoteConfig()
        }
        isShowInApp=true

        setupBackPressedCallback {
            //Do Nothing
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun observeSplashLiveData() {
        try {
            lifecycleScope.launchWhenStarted {
                try {
                    delay(1000)
                    findNavController().navigate(R.id.myLoadingFragment)
                    firebaseAnalytics("splash_fragment_load", "splash_fragment_load -->  Click")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initRemoteIds() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Set the minimum interval for fetching, in seconds
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
// Fetch the remote config values
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity ?: return) { task ->
                if (task.isSuccessful) {
                    // Apply the fetched values to your app
                    applyAdIdsFromRemoteConfig(remoteConfig)
                } else {
                    // Handle the error
                    // For example, use default values or log an error message
                }
            }
    }

    fun getDeviceId(): String? {
        val messageDigest = MessageDigest.getInstance("MD5")
        val androidId: String =
            Settings.Secure.getString(context?.contentResolver, "android_id")
        messageDigest.update(androidId.toByteArray())
        val arrby = messageDigest.digest()
        val sb = StringBuffer()
        val n = arrby.size
        for (i in 0 until n) {
            var oseamiya = Integer.toHexString((255 and arrby[i].toInt()))
            while (oseamiya.length < 2) {
                oseamiya = "0$oseamiya"
            }
            sb.append(oseamiya)
        }
        return try {
            sb.toString().uppercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ""
        }
    }

    private fun applyAdIdsFromRemoteConfig(remoteConfig: FirebaseRemoteConfig) {

        id_inter_counter = remoteConfig.getLong("id_inter_counter").toInt()
        id_frequency_counter = remoteConfig.getLong("id_frequency_counter").toInt()
        id_inter_main_medium = remoteConfig.getString("id_inter_main_medium")
        id_native_main_menu_screen = remoteConfig.getString("id_native_main_menu_screen")
        id_inter_main_normal = remoteConfig.getString("id_inter_main_normal")
        id_native_loading_screen = remoteConfig.getString("id_native_loading_screen")
        id_native_loading_screen = remoteConfig.getString("id_native_loading_screen")
        id_native_loading_screen = remoteConfig.getString("id_native_loading_screen")
        id_native_intro_screen = remoteConfig.getString("id_native_intro_screen")
        id_native_language_screen = remoteConfig.getString("id_native_language_screen")
        id_native_sound_screen = remoteConfig.getString("id_native_sound_screen")
        id_native_intruder_list_screen = remoteConfig.getString("id_native_intruder_list_screen")
        id_native_intruder_detection_screen = remoteConfig.getString("id_native_intruder_detection_screen")
        id_native_password_screen = remoteConfig.getString("id_native_password_screen")
        id_native_Pocket_Detection_screen = remoteConfig.getString("id_native_Pocket_Detection_screen")
        id_native_Motion_Detection_screen = remoteConfig.getString("id_native_Motion_Detection_screen")
        id_native_Whistle_Detection_screen = remoteConfig.getString("id_native_Whistle_Detection_screen")
        id_native_hand_free_screen = remoteConfig.getString("id_native_hand_free_screen")
        id_native_clap_detection_screen = remoteConfig.getString("id_native_clap_detection_screen")
        id_native_Remove_Charger_screen = remoteConfig.getString("id_native_Remove_Charger_screen")
        id_native_Battery_Detection_screen = remoteConfig.getString("id_native_Battery_Detection_screen")
        id_native_app_open_screen = remoteConfig.getString("id_native_app_open_screen")
        id_exit_dialog_native = remoteConfig.getString("id_exit_dialog_native")
        id_banner_1 = remoteConfig.getString("id_banner_1")

        Log.d("remote_ids", "$id_inter_counter")
        Log.d("remote_ids", "$id_frequency_counter")
        Log.d("remote_ids", id_inter_main_medium)
        Log.d("remote_ids", id_native_main_menu_screen)
        Log.d("remote_ids", id_inter_main_normal)
        Log.d("remote_ids", id_native_loading_screen)
        Log.d("remote_ids", id_native_loading_screen)
        Log.d("remote_ids", id_native_loading_screen)
        Log.d("remote_ids", id_native_intro_screen)
        Log.d("remote_ids", id_native_language_screen)
        Log.d("remote_ids", id_native_sound_screen)
        Log.d("remote_ids", id_native_intruder_list_screen)
        Log.d("remote_ids", id_native_intruder_detection_screen)
        Log.d("remote_ids", id_native_password_screen)
        Log.d("remote_ids", id_native_Pocket_Detection_screen)
        Log.d("remote_ids", id_native_Motion_Detection_screen)
        Log.d("remote_ids", id_native_Whistle_Detection_screen)
        Log.d("remote_ids", id_native_hand_free_screen)
        Log.d("remote_ids", id_native_clap_detection_screen)
        Log.d("remote_ids", id_native_Remove_Charger_screen)
        Log.d("remote_ids", id_native_Battery_Detection_screen)
        Log.d("remote_ids", id_exit_dialog_native)
        Log.d("remote_ids", id_banner_1)
    }

    private fun initRemoteConfig() {

        try {
            FirebaseApp.initializeApp(context ?: return)
            remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 6
            }
            remoteConfig?.setConfigSettingsAsync(configSettings)
            remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
            remoteConfig?.fetchAndActivate()?.addOnCompleteListener(activity ?: return) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("RemoteConfig", "Config params updated: $updated")
                } else {
                    Log.d("RemoteConfig", "Fetch failed")
                }

                val_ad_instertital_main_menu_screen_is_B =remoteConfig!!["val_ad_instertital_main_menu_screen_is_B"].asBoolean()
                val_ad_instertital_sound_screen_is_B =remoteConfig!!["val_ad_instertital_sound_screen_is_B"].asBoolean()
                val_ad_instertital_intruder_list_screen_is_B =remoteConfig!!["val_ad_instertital_intruder_list_screen_is_B"].asBoolean()
                val_ad_instertital_show_image_screen_is_B =remoteConfig!!["val_ad_instertital_show_image_screen_is_B"].asBoolean()
                val_ad_instertital_intruder_detection_screen_is_B =remoteConfig!!["val_ad_instertital_intruder_detection_screen_is_B"].asBoolean()
                val_ad_instertital_Pocket_Detection_screen_is_B =remoteConfig!!["val_ad_instertital_Pocket_Detection_screen_is_B"].asBoolean()
                val_ad_instertital_password_screen_is_B =remoteConfig!!["val_ad_instertital_password_screen_is_B"].asBoolean()
                val_ad_instertital_Motion_Detection_screen_is_B =remoteConfig!!["val_ad_instertital_Motion_Detection_screen_is_B"].asBoolean()
                val_ad_instertital_Whistle_Detection_screen_is_B =remoteConfig!!["val_ad_instertital_Whistle_Detection_screen_is_B"].asBoolean()
                val_ad_instertital_hand_free_screen_is_B =remoteConfig!!["val_ad_instertital_hand_free_screen_is_B"].asBoolean()
                val_ad_instertital_clap_detection_screen_is_B =remoteConfig!!["val_ad_instertital_clap_detection_screen_is_B"].asBoolean()
                val_ad_instertital_Remove_Charger_screen_is_B =remoteConfig!!["val_ad_instertital_Remove_Charger_screen_is_B"].asBoolean()
                val_ad_instertital_Battery_Detection_screen_is_B =remoteConfig!!["val_ad_instertital_Battery_Detection_screen_is_B"].asBoolean()
                val_ad_instertital_exit_dialog_is_B =remoteConfig!!["val_ad_instertital_exit_dialog_is_B"].asBoolean()
                val_banner_1 =remoteConfig!!["val_banner_1"].asBoolean()

                val_inter_main_medium = remoteConfig!!["val_inter_main_medium"].asBoolean()
                val_inter_main_normal = remoteConfig!!["val_inter_main_normal"].asBoolean()
                val_inter_main_medium_Inapp_first = remoteConfig!!["val_inter_main_medium_Inapp_first"].asBoolean()

                val_ad_native_main_menu_screen =remoteConfig!!["val_ad_native_main_menu_screen"].asBoolean()
                val_ad_native_loading_screen =remoteConfig!!["val_native_loading_screen"].asBoolean()
                val_ad_native_intro_screen =
                    remoteConfig!!["val_ad_native_intro_screen"].asBoolean()
                val_ad_native_language_screen =
                    remoteConfig!!["val_ad_native_language_screen"].asBoolean()
                val_ad_native_sound_screen =
                    remoteConfig!!["val_ad_native_sound_screen"].asBoolean()
                val_ad_native_intruder_list_screen =
                    remoteConfig!!["val_ad_native_intruder_list_screen"].asBoolean()
                val_ad_native_show_image_screen =
                    remoteConfig!!["val_ad_native_show_image_screen"].asBoolean()
                val_ad_native_intruder_detection_screen =
                    remoteConfig!!["val_ad_native_intruder_detection_screen"].asBoolean()
                val_ad_native_password_screen =
                    remoteConfig!!["val_ad_native_password_screen"].asBoolean()
                val_ad_native_Motion_Detection_screen =
                    remoteConfig!!["val_ad_native_Motion_Detection_screen"].asBoolean()
                val_ad_native_Whistle_Detection_screen =
                    remoteConfig!!["val_ad_native_Whistle_Detection_screen"].asBoolean()
                val_ad_native_hand_free_screen =
                    remoteConfig!!["val_ad_native_hand_free_screen"].asBoolean()
                val_ad_native_clap_detection_screen =
                    remoteConfig!!["val_ad_native_clap_detection_screen"].asBoolean()
                val_ad_native_Remove_Charger_screen =
                    remoteConfig!!["val_ad_native_Remove_Charger_screen"].asBoolean()
                val_ad_native_Battery_Detection_screen =
                    remoteConfig!!["val_ad_native_Battery_Detection_screen"].asBoolean()
                val_ad_native_Pocket_Detection_screen =
                    remoteConfig!!["val_ad_native_Pocket_Detection_screen"].asBoolean()
                val_exit_dialog_native = remoteConfig!!["val_exit_dialog_native"].asBoolean()

                val_ad_native_loading_screen_is_H =
                    remoteConfig!!["val_ad_native_loading_screen_is_H"].asBoolean()
                val_ad_native_intro_screen_is_H =
                    remoteConfig!!["val_ad_native_intro_screen_is_H"].asBoolean()
                val_ad_native_language_screen_is_H =
                    remoteConfig!!["val_ad_native_language_screen_is_H"].asBoolean()
                val_ad_native_sound_screen_is_H =
                    remoteConfig!!["val_ad_native_sound_screen_is_H"].asBoolean()
                val_ad_native_intruder_list_screen_is_H =
                    remoteConfig!!["val_ad_native_intruder_list_screen_is_H"].asBoolean()
                val_ad_native_show_image_screen_is_H =
                    remoteConfig!!["val_ad_native_show_image_screen_is_H"].asBoolean()
                val_ad_native_intruder_detection_screen_is_H =
                    remoteConfig!!["val_ad_native_intruder_detection_screen_is_H"].asBoolean()
                val_ad_native_password_screen_is_H =
                    remoteConfig!!["val_ad_native_password_screen_is_H"].asBoolean()
                val_ad_native_Motion_Detection_screen_is_H =
                    remoteConfig!!["val_ad_native_Motion_Detection_screen_is_H"].asBoolean()
                val_ad_native_Whistle_Detection_screen_is_H =
                    remoteConfig!!["val_ad_native_Whistle_Detection_screen_is_H"].asBoolean()
                val_ad_native_hand_free_screen_is_H =
                    remoteConfig!!["val_ad_native_hand_free_screen_is_H"].asBoolean()
                val_ad_native_clap_detection_screen_is_H =
                    remoteConfig!!["val_ad_native_clap_detection_screen_is_H"].asBoolean()
                val_ad_native_Remove_Charger_screen_is_H =
                    remoteConfig!!["val_ad_native_Remove_Charger_screen_is_H"].asBoolean()
                val_ad_native_Battery_Detection_screen_is_H =
                    remoteConfig!!["val_ad_native_Battery_Detection_screen_is_H"].asBoolean()
                val_ad_native_Pocket_Detection_screen_is_H =
                    remoteConfig!!["val_ad_native_Pocket_Detection_screen_is_H"].asBoolean()
                val_ad_native_main_menu_screen_is_H =
                    remoteConfig!!["val_ad_native_main_menu_screen_is_H"].asBoolean()
                val_exit_dialog_native_is_H =
                    remoteConfig!!["val_exit_dialog_native_is_H"].asBoolean()
                isIntroLanguageShow = remoteConfig!!["isIntroLanguageShow"].asBoolean()
                isFlowOne = remoteConfig!!["isFlowOne"].asBoolean()

                Log.d(
                    "RemoteConfig",
                    "Fetch val_inter_main_medium -> $val_inter_main_medium"
                )
                /*Log.d(
                    "RemoteConfig",
                    "Fetch val_inter_main_medium -> $val_banner_1"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_inter_main_normal -> $val_inter_main_normal"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_loading_screen -> $val_ad_native_loading_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_main_menu_screen -> $val_ad_native_main_menu_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_intro_screen -> $val_ad_native_intro_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_language_screen -> $val_ad_native_language_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_sound_screen -> $val_ad_native_sound_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_sound_screen -> $val_ad_native_intruder_list_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_intruder_detection_screen -> $val_ad_native_show_image_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_password_screen -> $val_ad_native_password_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_Motion_Detection_screen -> $val_ad_native_Motion_Detection_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_Whistle_Detection_screen -> $val_ad_native_Whistle_Detection_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_hand_free_screen -> $val_ad_native_hand_free_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_clap_detection_screen -> $val_ad_native_clap_detection_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_Remove_Charger_screen -> $val_ad_native_Remove_Charger_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_Battery_Detection_screen -> $val_ad_native_Battery_Detection_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_ad_native_Pocket_Detection_screen -> $val_ad_native_Pocket_Detection_screen"
                )
                Log.d(
                    "RemoteConfig",
                    "Fetch val_exit_dialog_native -> $val_exit_dialog_native"
                )
*/
                adsManager?.nativeAdsSplash()?.loadNativeAd(
                    activity ?: return@addOnCompleteListener,
                    true,
                    getString(R.string.id_native_loading_screen),
                    object : NativeListener {

                    })

                observeSplashLiveData()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
