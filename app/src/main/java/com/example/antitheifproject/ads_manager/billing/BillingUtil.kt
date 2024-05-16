package com.example.antitheifproject.ads_manager.billing

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.util.Log
import com.example.antitheifproject.ads_manager.FullScreenAds
import com.example.antitheifproject.ads_manager.NativeAds
import com.antitheftalarm.dont.touch.phone.finder.BuildConfig
import com.example.antitheifproject.ads_manager.FullScreenAdsTwo


class BillingUtil(
    val activity: Activity,
    private val billingCallback: ((Boolean) -> Unit)?=null
) {
    var isBillingReady = false
    private var TAG: String = "billingApp"
    private var lifeTime: String = "demo_1"
//    private var arrayListInApp = ArrayList<SkuDetails>()
    private var billingActivity: Activity? = null

//    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
//        Log.d(TAG, "getOldPurchases: in Listener")
//        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
//            for (purchase in purchases) {
//                handlePurchase(activity, purchase)
//            }
//        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//            Log.d(TAG, "getOldPurchases: User Cancelled")
//        } else {
//            Log.d(TAG, "getOldPurchases: Other Error")
//        }
//    }
    companion object{
        var billingPrice = ""
    }

//    private var billingClient = BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener).enablePendingPurchases().build()

    init {
        lifeTime = if (isDebug()) "android.test.purchased" else "demo_1"
    }

    private fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && processInfo.processName == packageName) {
                return true
            }
        }
        return false
    }

/*    fun setupConnection(isLaunch: Boolean) {
//        billingActivity = PremiumScreenActivity()

//        if (billingActivity?.javaClass?.toString()?.equals(activity.javaClass.toString(), true) == true) {
            Log.d("screenShow", "setupconnection if")
            if (activity.isFinishing && activity.isDestroyed) {
                return
            }
            if (!isBillingReady) {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                            val skuList = ArrayList<String>()
                            skuList.add(lifeTime)
                            val params = SkuDetailsParams.newBuilder()
                            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                            billingClient.querySkuDetailsAsync(params.build()) { _, skuDetailsList ->
                                if (skuDetailsList != null) {
                                    if (skuDetailsList.isNotEmpty()) {
                                        for (itemDetail in skuDetailsList) {
                                            arrayListInApp.add(itemDetail)
                                            val sku = itemDetail.sku
                                            val price = itemDetail.price
                                            val freeTrail = itemDetail.freeTrialPeriod
                                            val currencyCode = itemDetail.priceCurrencyCode
                                            val description = itemDetail.description
                                            val subscriptionPeriod = itemDetail.subscriptionPeriod
                                            Log.d("itemDetail", "itemDetail sku->  $sku")
                                            Log.d("itemDetail", "itemDetail price->  $price")
                                            Log.d(
                                                "itemDetail", "itemDetail freeTrail->  $freeTrail"
                                            )
                                            Log.d(
                                                "itemDetail", "itemDetail description->  $description"
                                            )
                                            Log.d(
                                                "itemDetail", "itemDetail currencyCode->  $currencyCode"
                                            )
                                            Log.d(
                                                "itemDetail", "subscriptionPeriod->  $subscriptionPeriod"
                                            )
                                        }
                                        getOldPurchases(activity)
                                        if (isLaunch) {
                                            if (billingActivity?.javaClass?.toString()?.equals(
                                                    activity.javaClass.toString(), true
                                                ) == true
                                            ) {
                                                Log.d("screenShow", "insideflow if")
                                                if (activity.isFinishing && activity.isDestroyed) {
                                                    return@querySkuDetailsAsync
                                                }
                                                if (!isBillingReady) {
                                                    isBillingReady = true
                                                    val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList[0]).build()
                                                    val responseCode = billingClient.launchBillingFlow(
                                                        activity, billingFlowParams
                                                    ).responseCode
                                                    if (responseCode != BillingClient.BillingResponseCode.OK) {
                                                        Log.d(TAG, "Please try Again Later1")
                                                    }
                                                }
                                            } else {
                                                Log.d("screenShow", "insideflow else")
                                            }
                                        }
                                    }
                                }
                            }
                            Log.d(TAG, "onBillingServiceDisconnected: Setup Connection")
                            Log.d(
                                TAG, "onBillingServiceDisconnected:responseCode-> ${billingResult.responseCode}"
                            )
                            Log.d(
                                TAG, "onBillingServiceDisconnected:debugMessage-> ${billingResult.debugMessage}"
                            )
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Log.d(TAG, "onBillingServiceDisconnected: Setup Connection Failed")
                    }
                })
            }
//        } else {
//            Log.d("screenShow", "setupconnection else")
//
//        }
    }*/

/*    fun purchase(isLaunch: Boolean) {
//        billingActivity = PremiumScreenActivity()
//        if (billingActivity?.javaClass?.toString()?.equals(activity.javaClass.toString(), true) == true) {
            Log.d("screenShow", "purchase: screen")
            if (activity.isFinishing && activity.isDestroyed) {
                return
            }
            isBillingReady = false
            setupConnection(isLaunch)
//        } else {
//            Log.d("screenShow", "purchase: other screen")
//
//        }
    }*/

/*
    private fun handlePurchase(context: Context, purchase: Purchase) {
        val acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener {
            Log.d(TAG, "getOldPurchases: debugMessage  ${it.debugMessage}")
            Log.d(TAG, "getOldPurchases: responseCode  ${it.responseCode}")
        }
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

            for (item in purchase.skus) {
                if (item == lifeTime) {
                    billingCallback?.invoke(true)

                    PurchasePrefs(context).putBoolean("inApp", true)
                    FullScreenAds.mInterstitialAd = null
                    FullScreenAdsTwo.mInterstitialAd = null
                    NativeAds.currentNativeAd = null
                    NativeAds.currentNativeAd2nd = null

                    Log.d(TAG, "handlePurchase: premium  $item")
                }
            }

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient.acknowledgePurchase(
                    acknowledgePurchaseParams, acknowledgePurchaseResponseListener
                )
            }
        }
    }
*/

    fun checkPurchased(context: Context): Boolean {
        return PurchasePrefs(context).getBoolean("inApp")
    }
   /* fun getOldPurchases(context: Context) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, list ->
            Log.e("atiqTest", "getOldPurchases: $billingResult")
            val purchases = list
            //  if (purchases != null) {
            for (purchase in purchases) {
                //   if (purchase.skus.toString() == lifeTime) {
                PurchasePrefs(
                    context
                ).putBoolean("inApp", true)
                Log.i("BillingTag", "getOldPurchases: premium")
                //   }
                //    }
            }
        }

    }*/
}

