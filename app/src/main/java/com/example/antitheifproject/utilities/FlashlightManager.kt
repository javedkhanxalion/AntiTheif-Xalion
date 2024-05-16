package com.example.antitheifproject.utilities

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FlashlightManager {
    private var hasSystemFeature: Boolean? = null

     fun setFlash(context: Context, isOn: Boolean) {
        if (hasSystemFeature == null) {
            hasSystemFeature = context.applicationContext.packageManager.hasSystemFeature("android.hardware.camera.flash")
        }

        if (hasSystemFeature == true) {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]

                try {
                    cameraManager.setTorchMode(cameraId, isOn)
                    if (!isOn) {
                        cameraManager.setTorchMode(cameraId, false)
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
        }
    }
}
