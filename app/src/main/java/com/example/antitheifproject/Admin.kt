package com.example.antitheifproject

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.antitheifproject.helper_class.Constants
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.utilities.CAMERA_PERMISSION


class Admin : DeviceAdminReceiver() {
    private var dbHelper: DbHelper? = null
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "This is an optional message to warn the user about disabling."
    }

    override fun onDisabled(context: Context, intent: Intent) {}
    override fun onEnabled(context: Context, intent: Intent) {}

    @SuppressLint("SuspiciousIndentation")
    override fun onPasswordFailed(context: Context, intent: Intent) {
//        Log.d("passwod", "onPasswordFailed: ")
        dbHelper = DbHelper(context)
        if (Constants.count < (dbHelper?.attemptNo ?: return)) {
            Log.d("passwod", "onPasswordFailed: plus ${Constants.count++}")
        } else if (dbHelper?.chkBroadCast(Constants.Intruder_Selfie) == true) {
            Log.d("passwod", "onPasswordFailed:  Camera")
            Constants.count = 0
            if (ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) == 0) {
                val intent = Intent("camera_intent")
                intent.putExtra("message", "This is my message!")
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        } else if (dbHelper?.chkBroadCast(Constants.Intruder_Alarm) == true) {
            Log.d("passwod", "onPasswordFailed:  Alaram")
            Constants.count = 0
            val intent = Intent("camera_intent")
            intent.putExtra("message", "This is my message!")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        Constants.pauseMediaPlayer()
        Constants.count = 1
//        if (Constants.isMyServiceRunning(context, CameraService::class.java)) {
        Constants.setFlash(context, false)
        Constants.mp?.stop()
//            context.stopService(Intent(context, CameraService::class.java))
//        }
    }
}