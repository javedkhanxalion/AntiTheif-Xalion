package com.example.antitheifproject.helper_class

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class DbHelper(val context: Context) {
    fun userExists(): Boolean {
        return File("/data/data/" + context.packageName + "/shared_prefs/" + Constants.SHARED_PREFS + ".xml").exists()
    }

    fun createUser(str: String?) {
        val edit = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putString(Constants.User_Value, str)
        edit.apply()
    }

    fun chkPass(str: String): Boolean {
        return str == context.getSharedPreferences(Constants.SHARED_PREFS, 0)
            .getString(Constants.User_Value, "null")
    }

    fun chkBroadCast(str: String?): Boolean {
        return context.getSharedPreferences(Constants.SHARED_PREFS, 0).getBoolean(str, false)
    }

    fun setBroadCast(str: String?, z: Boolean) {
        val edit = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putBoolean(str, z)
        edit.apply()
    }

    val attemptNo: Int
        get() = context.getSharedPreferences(
            Constants.SHARED_PREFS,
            0
        ).getString(Constants.No_of_attempts, "1")!!.toInt()

    fun setAttemptNo(str: String?) {
        val edit = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putString(Constants.No_of_attempts, str)
        edit.apply()
    }

    fun setAlarmSetting(str: String?, bool: Boolean?) {
        val edit = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putBoolean(str, bool!!)
        edit.apply()
    }

    fun getAlarmSetting(str: String?): Boolean {
        return context.getSharedPreferences(Constants.SHARED_PREFS, 0).getBoolean(str, false)
    }

    val tone: Int
        get() = context.getSharedPreferences(
            Constants.SHARED_PREFS,
            0
        ).getString(Constants.Tone_Selected, "0")!!.toInt()


    fun getTone(context : Context, str: String?) : Int{
       return context.getSharedPreferences(Constants.SHARED_PREFS,0 ).getInt(str, 0)
    }

    fun setTone(str: String?,value: Int?) {
        val edit = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putInt(str, value?:return)
        edit.apply()
    }
    fun setTone(str: String?) {
        val edit = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putString(Constants.Tone_Selected, str)
        edit.apply()
    }

    fun setAppPurchase(z: Boolean, appCompatActivity: AppCompatActivity) {
        val edit = appCompatActivity.getSharedPreferences(Constants.SHARED_PREFS, 0).edit()
        edit.putBoolean(Constants.Apppurchase, z)
        edit.apply()
    }

    fun getAppPurchase(context: Context): Boolean {
        return context.getSharedPreferences(Constants.SHARED_PREFS, 0)
            .getBoolean(Constants.Apppurchase, false)
    }

    fun getBooleanData(context: Context, key: String?, value: Boolean): Boolean {
        return context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
            .getBoolean(key, value)
    }


    fun getIntData(context: Context, key: String?, value: Int): Int {
        return context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
            .getInt(key, value)
    }


    // Get Data
    fun getStringData(context: Context, key: String?, value: String): String? {
        return context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
            .getString(key, value)
    }

    // Save Data
    fun saveData(context: Context, key: String?, `val`: String?) {
        context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE).edit()
            .putString(key, `val`).apply()
    }

    fun saveData(context: Context, key: String?, `val`: Int) {
        context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE).edit()
            .putInt(key, `val`)
            .apply()
    }

    fun saveData(context: Context, key: String?, `val`: Boolean) {
        context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, `val`)
            .apply()
    }

    fun getSharedPrefEditor(context: Context, pref: String?): SharedPreferences.Editor {
        return context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit()
    }

    fun saveData(editor: SharedPreferences.Editor) {
        editor.apply()
    }

    companion object {
        const val AD_PREFS = "adspref"
        fun setRemoteValue(context: Context, str: String?, z: Boolean) {
            val edit = context.getSharedPreferences(AD_PREFS, 0).edit()
            edit.putBoolean(str, z)
            edit.apply()
        }

        fun getRemoteValue(context: Context, str: String?): Boolean {
            return context.getSharedPreferences(AD_PREFS, 0).getBoolean(str, true)
        }

        fun setRemoteCounter(context: Context, str: String?, j: Long) {
            val edit = context.getSharedPreferences(AD_PREFS, 0).edit()
            edit.putLong(str, j)
            edit.apply()
        }

        fun getRemoteCounter(context: Context, str: String?): Long {
            return context.getSharedPreferences(AD_PREFS, 0).getLong(str, 5L)
        }


    }
}