package com.example.antitheifproject.ads_manager

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


/**
 * Created by Javed Khan
 */
class Analytics(private val context: Context) {
    private var firebaseAnalytics: FirebaseAnalytics? = null


    init {
        firebaseAnalytics = Firebase.analytics
    }

    fun logEvent(eventName: String, bKey: String, bValue: String) {
        val bundle = Bundle()
        bundle.putString(bKey, bValue)
        firebaseAnalytics?.logEvent(eventName, bundle)

    }

    fun logProperty(propertyName: String, propertyValue: String) {
        firebaseAnalytics?.setUserProperty(propertyName, propertyValue)
    }


}