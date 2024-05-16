package com.example.antitheifproject.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.SystemClock
import android.os.Vibrator
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.MainActivity
import com.example.antitheifproject.helper_class.CamManager
import com.example.antitheifproject.helper_class.Constants
import com.example.antitheifproject.helper_class.Constants.All_Alarm_Sound
import com.example.antitheifproject.helper_class.Constants.Battery_Detection
import com.example.antitheifproject.helper_class.Constants.Battery_Detection_Check
import com.example.antitheifproject.helper_class.Constants.Battery_Detection_Flash
import com.example.antitheifproject.helper_class.Constants.Battery_Detection_Vibration
import com.example.antitheifproject.helper_class.Constants.Clap_Detection
import com.example.antitheifproject.helper_class.Constants.Clap_Detection_Check
import com.example.antitheifproject.helper_class.Constants.Clap_Detection_Flash
import com.example.antitheifproject.helper_class.Constants.Clap_Detection_Vibration
import com.example.antitheifproject.helper_class.Constants.HandFree_Detection
import com.example.antitheifproject.helper_class.Constants.HandFree_Detection_Check
import com.example.antitheifproject.helper_class.Constants.HandFree_Detection_Flash
import com.example.antitheifproject.helper_class.Constants.HandFree_Detection_Vibration
import com.example.antitheifproject.helper_class.Constants.Intruder_Alarm_Flash
import com.example.antitheifproject.helper_class.Constants.Intruder_Alarm_Vibration
import com.example.antitheifproject.helper_class.Constants.Motion_Detection
import com.example.antitheifproject.helper_class.Constants.Motion_Detection_Check
import com.example.antitheifproject.helper_class.Constants.Motion_Detection_Flash
import com.example.antitheifproject.helper_class.Constants.Motion_Detection_Vibration
import com.example.antitheifproject.helper_class.Constants.Pocket_Detection
import com.example.antitheifproject.helper_class.Constants.Pocket_Detection_Check
import com.example.antitheifproject.helper_class.Constants.Pocket_Detection_Flash
import com.example.antitheifproject.helper_class.Constants.Pocket_Detection_Vibration
import com.example.antitheifproject.helper_class.Constants.Remove_Charger
import com.example.antitheifproject.helper_class.Constants.Remove_Charger_Check
import com.example.antitheifproject.helper_class.Constants.Remove_Charger_Flash
import com.example.antitheifproject.helper_class.Constants.Remove_Charger_Vibration
import com.example.antitheifproject.helper_class.Constants.Whistle_Detection
import com.example.antitheifproject.helper_class.Constants.Whistle_Detection_Check
import com.example.antitheifproject.helper_class.Constants.Whistle_Detection_Flash
import com.example.antitheifproject.helper_class.Constants.Whistle_Detection_Vibration
import com.example.antitheifproject.helper_class.Constants.Wifi_Alarm
import com.example.antitheifproject.helper_class.Constants.Wifi_Alarm_Check
import com.example.antitheifproject.helper_class.Constants.Wifi_Alarm_Flash
import com.example.antitheifproject.helper_class.Constants.Wifi_Alarm_Vibration
import com.example.antitheifproject.helper_class.Constants.Wrong_Password_Detection
import com.example.antitheifproject.helper_class.Constants.mp
import com.example.antitheifproject.helper_class.Constants.setFlash
import com.example.antitheifproject.helper_class.Constants.startMediaPlayer
import com.example.antitheifproject.helper_class.DbHelper
import com.example.antitheifproject.utilities.CAMERA_PERMISSION
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.io.IOException


class SystemEventsService : Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var proximitySensor: Sensor? = null
    private var vibrator: Vibrator? = null
    private var dbHelper: DbHelper? = null
    private var accelerometer: Sensor? = null
    private var vibrationManager: VibrationManager? = null
    var audioClassifier: AudioClassifier? = null
    var handler: Handler? = null
    var audioRecord: AudioRecord? = null
    private var mLastClickTime: Long = 0
    var mgr: CamManager? = null

    companion object {
        var isAvailable: Boolean? = true
    }

    //System Event BrodCast
    var eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                val telephonyManager =
                    context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                when (telephonyManager.callState) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        stopRun()
                        isAvailable = false
                        // Phone is ringing (incoming call)
                        val incomingNumber =
                            intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        Log.d("CallReceiver", "Incoming call from: $incomingNumber")
                        // Add your logic here for incoming call
                    }

                    TelephonyManager.CALL_STATE_IDLE -> {
                        stopRun()
                        isAvailable = false
                        // Phone is idle (no calls)
                        Log.d("CallReceiver", "Call state: Idle")
                        // Add your logic here for idle state
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        isAvailable = true
                        // Phone is off-hook (in a call)
                        Log.d("CallReceiver", "Call state: Off-hook")
                        // Add your logic here for off-hook state
                    }
                }
            } else
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPercentage = (level.toFloat() / scale.toFloat() * 100).toInt()
                    Log.d("SystemEventsService", "Battery level: $batteryPercentage%")
                    if (batteryPercentage >= 100) {
                        if (dbHelper?.chkBroadCast(Battery_Detection_Check) == true) {
                            setRingtone(
                                Battery_Detection,
                                Battery_Detection_Flash,
                                Battery_Detection_Vibration
                            )
                        }
                    }
                    // Handle battery level change event
                } else if (intent?.action == Intent.ACTION_HEADSET_PLUG) {
                    val state = intent.getIntExtra("state", -1)
                    if (state == 0) {
                        Log.d("SystemEventsService", "Headset removed")
                        if (dbHelper?.chkBroadCast(HandFree_Detection_Check) == true) {
                            setRingtone(
                                HandFree_Detection,
                                HandFree_Detection_Flash,
                                HandFree_Detection_Vibration
                            )
                        }
                    } else {
                        if (dbHelper?.chkBroadCast(HandFree_Detection_Check) == true) {
                            setRingtone(
                                HandFree_Detection,
                                HandFree_Detection_Flash,
                                HandFree_Detection_Vibration
                            )
                            Log.d("SystemEventsService", "Headset attach")
                        }
                    }
                } else if (intent?.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    val wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN
                    )
                    when (wifiState) {
                        WifiManager.WIFI_STATE_ENABLED -> {
                            if (dbHelper?.chkBroadCast(Wifi_Alarm_Check) == true) {
                                setRingtone(Wifi_Alarm, Wifi_Alarm_Flash, Wifi_Alarm_Vibration)
                                Log.d("SystemEventsService", "WiFi turned ON")
                                // Handle WiFi turned ON event
                            }
                        }

                        WifiManager.WIFI_STATE_DISABLED -> {
                            if (dbHelper?.chkBroadCast(Wifi_Alarm_Check) == true) {
                                setRingtone(Wifi_Alarm, Wifi_Alarm_Flash, Wifi_Alarm_Vibration)
                                Log.d("SystemEventsService", "WiFi turned OFF")
                                // Handle WiFi turned OFF event
                            }
                        }
                    }
                } else {
                    if (intent?.action == Intent.ACTION_POWER_CONNECTED) {
                        if (dbHelper?.chkBroadCast(Remove_Charger_Check) == true) {
                            Log.d("SystemEventsService", "Charger connected")
                            // Handle charger connected event
                            setRingtone(
                                Remove_Charger,
                                Remove_Charger_Flash,
                                Remove_Charger_Vibration
                            )
                        }
                    } else if (intent?.action == Intent.ACTION_POWER_DISCONNECTED) {
                        if (dbHelper?.chkBroadCast(Remove_Charger_Check) == true) {
                            Log.d("SystemEventsService", "Charger disconnected")
                            setRingtone(
                                Remove_Charger,
                                Remove_Charger_Flash,
                                Remove_Charger_Vibration
                            )
                            // Handle charger disconnected event
                        }
                    }
                }
        }
    }

    override fun onCreate() {
        super.onCreate()

    }

    private fun registerIntent() {
        try {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_BATTERY_CHANGED)
            filter.addAction(Intent.ACTION_HEADSET_PLUG)
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            filter.addAction(Intent.ACTION_POWER_CONNECTED)
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            registerReceiver(eventReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startForeground() {
        createNotificationChannel()
        val remoteViews = RemoteViews(packageName, R.layout.notification_collapsed)
        var z = false
        val activity = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setTextViewText(R.id.text_view_collapsed_2, "Intruder Alert is Active")
        val i = resources.configuration.uiMode and 48
        if (!(i == 0 || i == 16 || i != 32)) {
            z = true
        }
        if (z) {
            remoteViews.setTextColor(R.id.text_view_collapsed_2, Color.parseColor("#FFFFFF"))
            remoteViews.setTextColor(R.id.text_view_collapsed_1, Color.parseColor("#FFFFFF"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(
                5,
                NotificationCompat.Builder(
                    this,
                    com.example.antitheifproject.utilities.NOTIFY_CHANNEL_ID
                ).setOngoing(true)
                    .setColor(-1)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setCustomContentView(remoteViews)
                    .setContentIntent(activity).build(),
                FOREGROUND_SERVICE_TYPE_CAMERA
            )
        } else {
            startForeground(
                5,
                NotificationCompat.Builder(
                    this,
                    com.example.antitheifproject.utilities.NOTIFY_CHANNEL_ID
                ).setOngoing(true)
                    .setColor(-1)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(activity).build()
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                com.example.antitheifproject.utilities.NOTIFY_CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            // Register battery events
            try {
                mgr = CamManager(this)
                LocalBroadcastManager.getInstance(this)
                    .registerReceiver(cameraReceiver, IntentFilter("camera_intent"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            startForeground()
            registerIntent()
            isAvailable = true
            dbHelper = DbHelper(this)
            vibrationManager = VibrationManager(this)
            // Register proximity sensor
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            if (proximitySensor != null) {
                sensorManager?.registerListener(
                    this,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else {
                Log.e("SystemEventsService", "Proximity sensor not available")
            }

            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer != null) {
                sensorManager?.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            clapFun()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister receivers and listeners
        try {
            sensorManager?.unregisterListener(this)
            unregisterEventReceiver()
            LocalBroadcastManager.getInstance(this).unregisterReceiver(cameraReceiver)
            stopRun()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun stopRun() {
        mp?.stop()
        setFlash(this, false)
        vibrator?.cancel()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (isAvailable == true) {
            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                val isPocketMode = event.values[0] < proximitySensor?.maximumRange ?: return
                if (dbHelper?.chkBroadCast(Pocket_Detection_Check) == true)
                    if (isPocketMode) {
                        setRingtone(
                            Pocket_Detection,
                            Pocket_Detection_Flash,
                            Pocket_Detection_Vibration
                        )
                    }
            } else if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                if (dbHelper?.chkBroadCast(Motion_Detection_Check) == true) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    // Detect motion based on accelerometer values (you can customize this logic)
                    if (x > 20 || y > 20 || z > 20) {
                        // Motion detected
                        setRingtone(
                            Motion_Detection,
                            Motion_Detection_Flash,
                            Motion_Detection_Vibration
                        )
                    }
                }
            }
        }
    }

    private fun unregisterEventReceiver() {
        unregisterReceiver(eventReceiver)
    }

    private fun setRingtone(checkDetectionString: String, isFlash: String, isVibration: String) {
        if (isAvailable == true) {
            isAvailable = false
            if (dbHelper?.getAlarmSetting(All_Alarm_Sound) == false) {
                if (mp?.isPlaying == true) {
                    mp?.stop()
                }
                mp = MediaPlayer.create(this, getRingTone(checkDetectionString))
                mp?.isLooping = false
                mp?.setVolume(100.0f, 100.0f)
                startMediaPlayer(this)
            }
            if (dbHelper?.getAlarmSetting(isVibration) == true) {
                vibrationManager?.startVibration()
            }
            if (dbHelper?.getAlarmSetting(isFlash) == true) {
                setFlash(this, true)
            }
        }
    }

    private fun getRingTone(checkDetectionString: String): Int {
        when (dbHelper?.getTone(this, checkDetectionString)) {
            0 -> {
                return R.raw.tone1
            }

            1 -> {
                return R.raw.tone2
            }

            2 -> {
                return R.raw.tone3
            }

            3 -> {
                return R.raw.tone4
            }

            4 -> {
                return R.raw.tone5
            }

            5 -> {
                return R.raw.tone6
            }

        }
        return R.raw.tone1
    }

    override fun stopService(name: Intent): Boolean {
        return super.stopService(name)
    }

    private fun clapFun() {
        val handlerThread = HandlerThread("backgroundThread")
        handlerThread.start()
        handler = HandlerCompat.createAsync(handlerThread.looper)
        try {
            audioClassifier = AudioClassifier.createFromFile(this, "yamnet.tflite")
        } catch (e: IOException) {
            Log.e("okkkk=-=-=-=-", "fun: $e")
            e.printStackTrace()
            audioClassifier = null
        }
        val createInputTensorAudio = audioClassifier?.createInputTensorAudio()
        audioRecord = audioClassifier?.createAudioRecord()
        handler?.post(object : Runnable {
            override fun run() {
                try {
                    audioRecord?.startRecording()
                    createInputTensorAudio?.load(audioRecord)
                    for (classifications in audioClassifier?.classify(createInputTensorAudio)
                        ?: return) {
                        for (category in classifications.categories) {
                            Log.d(
                                "checktag",
                                "run: ${category.label} ${category.displayName} ${category.index}"
                            )
                            if (category.score.toDouble() > 0.5 && (category.index == 56 || category.index == 58 || category.index == 57)
                            ) {
                                if (dbHelper?.chkBroadCast(Clap_Detection_Check) == true) {
                                    setRingtone(
                                        Clap_Detection,
                                        Clap_Detection_Flash,
                                        Clap_Detection_Vibration
                                    )
                                }
                            }
                            if (category.score.toDouble() > 0.5 && (category.index == 35 || category.index == 396 || category.index == 397)
                            ) {
                                if (dbHelper?.chkBroadCast(Whistle_Detection_Check) == true) {
                                    setRingtone(
                                        Whistle_Detection,
                                        Whistle_Detection_Flash,
                                        Whistle_Detection_Vibration
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("okkk", e.message!!)
                }
                handler?.postDelayed(this, 500)
            }
        })
    }

    private val cameraReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            // Get extra data included in the Intent
            Log.d("passwod", "onPasswordFailed:  system event service")
//            stopRun()
            loadCapture()
        }
    }

    private fun loadCapture() {
        try {

            if (dbHelper?.chkBroadCast(Constants.Intruder_Alarm) == true) {
                Log.d("passwod", "onPasswordFailed:  Intruder_Alarm")
                if (mp?.isPlaying == true) {
                    mp?.stop()
                }
                mp = MediaPlayer.create(this,  getRingTone(Wrong_Password_Detection))
                mp?.isLooping = false
                mp?.setVolume(100.0f, 100.0f)
                startMediaPlayer(this)

                if (dbHelper?.getAlarmSetting(Intruder_Alarm_Vibration) == true) {
                    Log.d("passwod", "onPasswordFailed:  Intruder_Alarm_Vibration")
                    vibrationManager?.startVibration()
                }
                if (dbHelper?.getAlarmSetting(Intruder_Alarm_Flash) == true) {
                    Log.d("passwod", "onPasswordFailed:  Intruder_Alarm_Flash")
                    setFlash(this, true)
                }
            }
            if (dbHelper?.chkBroadCast(Constants.Intruder_Selfie) == true) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) != 0) {
                    return
                }

                Log.d("passwod", "onPasswordFailed:  Intruder_Take_Picture")
                mgr?.takePhoto()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

