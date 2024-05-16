package com.example.antitheifproject.ui.showIntruderScreen

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.antitheftalarm.dont.touch.phone.finder.R
import com.antitheftalarm.dont.touch.phone.finder.databinding.ShowIntruderFragmentBinding
import com.example.antitheifproject.adapter.IntruderAdapter
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.example.antitheifproject.ads_manager.showTwoInterAd
import com.example.antitheifproject.helper_class.Constants.getAntiTheftDirectory
import com.example.antitheifproject.model.IntruderModels
import com.example.antitheifproject.utilities.BaseFragment
import com.example.antitheifproject.utilities.firebaseAnalytics
import com.example.antitheifproject.utilities.id_inter_main_medium
import com.example.antitheifproject.utilities.id_inter_main_normal
import com.example.antitheifproject.utilities.id_native_intruder_list_screen
import com.example.antitheifproject.utilities.setupBackPressedCallback
import com.example.antitheifproject.utilities.val_ad_instertital_intruder_list_screen_is_B
import com.example.antitheifproject.utilities.val_ad_native_intruder_list_screen
import com.example.antitheifproject.utilities.val_ad_native_intruder_list_screen_is_H
import com.example.antitheifproject.utilities.val_inter_main_medium
import com.example.antitheifproject.utilities.val_inter_main_normal
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.io.File

class FragmentShowIntruder :
    BaseFragment<ShowIntruderFragmentBinding>(ShowIntruderFragmentBinding::inflate) {

    private var adapter: IntruderAdapter? = null
    private var allIntruderList: ArrayList<IntruderModels> = ArrayList()
    private var dir: File? = null
    private var adsManager: AdsManager? = null
    companion object{
         var uriPic: Uri? = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adsManager = AdsManager.appAdsInit(activity ?: return)
        loadNative()

        setupViews()
        setupRecyclerView()
        setupBackPressedCallback {
            findNavController().navigateUp() }
        firebaseAnalytics(
            "show_intruder_fragment_load",
            "show_intruder_fragment_load_oncreate -->  Click"
        )

    }

    private fun setupViews() {
        dir = getAntiTheftDirectory()
        _binding?.backicon?.setOnClickListener {
            findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        adapter = IntruderAdapter(allIntruderList, requireActivity()) { intruderModel ,uri_->
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity ?: return@let,
                    remoteConfigMedium = val_inter_main_medium,
                    remoteConfigNormal = val_inter_main_normal,
                    adIdMedium = id_inter_main_medium,
                    adIdNormal = id_inter_main_normal,
                    tagClass = "show_intruder_fragment_load",
                    isBackPress = true,
                    layout = binding?.adsLay!!
                ) {
                    uriPic=uri_
                    findNavController().navigate(
                        R.id.ShowFullImageFragment,
                        bundleOf("obj" to intruderModel)
                    )
                }
            }
        }
        _binding?.intruderList?.adapter = adapter
    }

    private fun getFile(file: File) {
        _binding?.run {
            allIntruderList.clear()
            try {
                val listFiles = file.listFiles()
                listFiles?.let {
                    for (file2 in it) {
                        if (file2.isDirectory) {
                            getFile(file2)
                        } else if (file2.isFile && file2.name.endsWith(".jpg", true)
                            || file2.name.endsWith(".png", true) || file2.name.endsWith(
                                ".jpeg",
                                true
                            )
                        ) {
                            allIntruderList.add(IntruderModels(file2, false))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (allIntruderList.size > 0) {
                intruderList.visibility = View.VISIBLE
                noData.visibility = View.GONE
                adapter?.notifyDataSetChanged()
                nativeExitAd.visibility = View.GONE
                adView.visibility = View.GONE

            } else {
                intruderList.visibility = View.GONE
                noData.visibility = View.VISIBLE
                nativeExitAd.visibility = View.GONE
                adView.visibility = View.GONE
            }
        }

    }

    override fun onResume() {
        super.onResume()
        dir?.let { getFile(it) }
    }

    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_intruder_list_screen,
            id_native_intruder_list_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    _binding?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.adView?.visibility = View.GONE
                    if (isAdded && isVisible && !isDetached) {
                        val adView = if(val_ad_native_intruder_list_screen_is_H){
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


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

}
