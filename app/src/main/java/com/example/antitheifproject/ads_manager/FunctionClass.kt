package com.example.antitheifproject.ads_manager

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File


object FunctionClass {

    private var toast: Toast? = null
    fun toast(context: Activity, message: String) {
        try {
            if (context.isDestroyed || context.isFinishing) return
            if (toast != null) {
                toast?.cancel()
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            if (context.isDestroyed || context.isFinishing) return
            toast?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun Context.toast(context: Context, message: String) {
        try {
            if (toast != null) {
                toast?.cancel()
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareApp(context: Context, title: String, message: String) {
        try {
            val shareBody = "https://play.google.com/store/apps/details?id=${context.packageName}"
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, message)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            context.startActivity(Intent.createChooser(sharingIntent, title))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun feedBackWithEmail(context: Activity, title: String, message: String, emailId: String) {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailId))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            emailIntent.putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun privacyWithExternalIntent(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://smartscreenrecorder.blogspot.com/2021/12/privacy-policy.html")

                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun externalIntent(context: Context, stringUrl: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(stringUrl)

                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun rateApp(context: Context, applicationId: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$applicationId")
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getUriFromFile(file: File, context: Context): Uri? =
        try {
            FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        } catch (e: Exception) {
            throw if (e.message?.contains("ProviderInfo.loadXmlMetaData") == true) {
                Error("FileProvider is not set or doesn't have needed permissions")
            } else {
                e
            }
        }

    fun formatMilliseconds(duration: Long): String {
        val seconds = (duration / 1000).toInt() % 60
        val minutes = (duration / (1000 * 60) % 60).toInt()
        val hours = (duration / (1000 * 60 * 60) % 24).toInt()
        val hh: String = if (hours in 1..9) {
            "0$hours:"
        } else {
            if (hours >= 10) {
                "$hours:"
            } else {
                ""
            }
        }
        val mm: String = if (minutes in 1..9) {
            "0$minutes:"
        } else {
            if (minutes >= 10) {
                "$minutes:"
            } else {
                "00:"
            }
        }
        val ss: String = if (seconds in 1..9) {
            "0$seconds"
        } else {
            if (seconds >= 10) {
                "" + seconds
            } else {
                "00"
            }
        }
        return hh + mm + ss
    }

    fun toggleShowKeyBoard(context: Context?, editText: EditText, show: Boolean) {
        if (show) {
            editText.apply {
                requestFocus()
                val imm =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        } else {
            editText.apply {
                clearFocus()
                val imm =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }

    fun dip2px(context: Context, dpValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}