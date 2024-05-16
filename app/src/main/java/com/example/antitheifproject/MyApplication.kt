package com.example.antitheifproject

import android.app.Application
import android.util.Log
import com.antitheftalarm.dont.touch.phone.finder.BuildConfig
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.utilities.id_native_app_open_screen
import com.example.antitheifproject.utilities.val_ad_native_app_open_screen
import com.hypersoft.billing.BillingManager
import com.hypersoft.billing.dataClasses.PurchaseDetail
import com.hypersoft.billing.interfaces.BillingListener

class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        Log.d("application_class", "onCreate")
        if (val_ad_native_app_open_screen) {
//            AppOpenManager(this,id_native_app_open_screen)
//            AppOpenManager(this,getString(R.string.app_open_splash))
            AdOpenApp(this,getString(R.string.app_open_splash))
        }

    }


}