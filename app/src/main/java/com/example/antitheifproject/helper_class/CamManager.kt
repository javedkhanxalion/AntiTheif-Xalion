package com.example.antitheifproject.helper_class

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.ErrorCallback
import android.hardware.Camera.PictureCallback
import android.hardware.Camera.PreviewCallback
import android.util.Log
import com.example.antitheifproject.helper_class.Constants.isMyServiceRunning
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class CamManager(private val mContext: Context) : PictureCallback, ErrorCallback, PreviewCallback,
    AutoFocusCallback {
    private var mCamera: Camera? = null
    private var mSurface: SurfaceTexture? = null
    fun takePhoto() {
        if (isFrontCameraAvailable) {
            initCamera()
        }
    }

    private val isFrontCameraAvailable: Boolean
        private get() {
            val context = mContext
            if (context == null || !context.packageManager.hasSystemFeature("android.hardware.camera")) {
                return false
            }
            val numberOfCameras = Camera.getNumberOfCameras()
            for (i in 0 until numberOfCameras) {
                val cameraInfo = CameraInfo()
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == 1) {
                    return true
                }
            }
            return false
        }

    private fun initCamera() {
        try {
            mCamera = Camera.open(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (mCamera != null) {
                val surfaceTexture = SurfaceTexture(123)
                mSurface = surfaceTexture
                mCamera!!.setPreviewTexture(surfaceTexture)
                val parameters = mCamera!!.parameters
                parameters.setRotation(270)
                if (autoFocusSupported(mCamera)) {
                    parameters.focusMode = "auto"
                } else {
                    Log.w("asdaxxx", "Autofocus is not supported")
                }
                mCamera!!.parameters = parameters
                mCamera!!.setPreviewCallback(this)
                mCamera!!.setErrorCallback(this)
                mCamera!!.startPreview()
            }
        } catch (e2: IOException) {
            e2.printStackTrace()
            releaseCamera()
        }
    }

    private fun autoFocusSupported(camera: Camera?): Boolean {
        return camera?.parameters?.supportedFocusModes?.contains("auto") ?: false
    }

    private fun releaseCamera() {
        val camera = mCamera
        if (camera != null) {
            camera.stopPreview()
            mCamera!!.release()
            mSurface!!.release()
            mCamera = null
            mSurface = null
        }
    }

    override fun onError(i: Int, camera: Camera) {
        if (i == 1) {
            Log.e("ContentValues", "Camera error: Unknown")
        } else if (i == 2) {
            Log.e(
                "ContentValues",
                "Camera error: Camera was disconnected due to use by higher priority user"
            )
        } else if (i != 100) {
            Log.e("ContentValues", "Camera error: no such error id ($i)")
        } else {
            Log.e("ContentValues", "Camera error: Media server died")
        }
    }

    override fun onPreviewFrame(bArr: ByteArray, camera: Camera) {
        try {
            if (autoFocusSupported(camera)) {
                camera.setPreviewCallback(null)
                camera.takePicture(null, null, this)
            } else {
                camera.setPreviewCallback(null)
                camera.takePicture(null, null, this)
            }
        } catch (e: Exception) {
            Log.e("ContentValues", "Camera error while taking picture")
            e.printStackTrace()
            releaseCamera()
        }
    }

    override fun onAutoFocus(z: Boolean, camera: Camera) {
        if (camera != null) {
            try {
                camera.takePicture(null, null, this)
                mCamera!!.autoFocus(null)
            } catch (e: Exception) {
                e.printStackTrace()
                releaseCamera()
            }
        }
    }

    override fun onPictureTaken(bArr: ByteArray, camera: Camera) {
        savePicture(bArr)
        releaseCamera()
    }

    private fun savePicture(bArr: ByteArray?) {
        try {
            val file = File(mContext.getExternalFilesDir(null).toString() + "/Anti Theft")
            if (bArr == null) {
//                Toast.makeText(mContext, "cant save image", Toast.LENGTH_LONG).show()
                Log.e("asdaxxx", "Can't save image - no data")
                return
            }
            if (!file.exists() && file.mkdirs()) {
                Log.d("msgdirector", "Directory is Created")
            }
            if (!file.exists()) {
//                Toast.makeText(mContext, "Can't create directory to save image", Toast.LENGTH_LONG)
//                    .show()
                Log.e("asdaxxx", "Can't create directory to save image.")
                return
            }
            val str = "intruderselfie_" + SimpleDateFormat("yyyyMMddhhmmss").format(Date()) + ".jpg"
            val str2 = file.path + File.separator + str
            Log.d("asdaxxx", str2)
            val fileOutputStream = FileOutputStream(File(str2))
            fileOutputStream.write(bArr)
            fileOutputStream.close()
            Log.d("asdaxxx", "New image was saved$str")
        } catch (e: Exception) {
            Log.e("asdaxxx", e.toString())
            e.printStackTrace()
        }
    }
}