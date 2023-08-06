package comalpha4.chatappwithlocation.services

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.media.RingtoneManager
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import comalpha4.chatappwithlocation.LoggerGenratter
import comalpha4.chatappwithlocation.MapViewActivity
import comalpha4.chatappwithlocation.R
import comalpha4.chatappwithlocation.SessionManagement
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.Hashtable


/**
 * Created by Adnan Bashir manak on 07,July,2023
 * AIS company,
 * Krachi, Pakistan.
 */


class LocationService : Service(), LocationListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var notification: Notification? = null


    private var latitude = 0.0
    private var longitude = 0.0

    private var countryName = ""
    private var userId = ""
    private val jsonObjLocationList = arrayListOf<String>()
    private var isTenSecondIntervalOn = false
    private var mLastClickTime: Long = 0L

    /*lateinit var provideRetrofitclient: Retrofit
    private val networkApi: NetworkApi by lazy {
        provideRetrofitclient.create(NetworkApi::class.java)
    }*/
    private var apiIsAlreadyCalling = false

    companion object {

        private const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val TAG: String = "TrackingService"
        private const val twoMinutes: Long = ((1000 * 60) * 2)
        private const val thirtySeconds: Long = ((1000 * 60) / 2)
        private const val tenSeconds: Long = ((1000 * 10))

    }

    private var cdt: CountDownTimer? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {

        LoggerGenratter.getInstance().printLog(TAG, "Oncreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val userId = ""
        this.userId = userId
        LoggerGenratter.getInstance().printLog("user", "onStartCommand 11s2233")

        createNotificationChannel()
        createLocationRequest()
        getLocationCallBack()
        startLocationUpdates()
        val notificationIntent = Intent(this, MapViewActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)

        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Tracking")
            .setSmallIcon(comalpha4.chatappwithlocation.R.drawable.logo)
//            .setContentText(data)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        notification!!.flags = notification!!.flags or Notification.FLAG_FOREGROUND_SERVICE

        startForeground(1, notification)

        return START_NOT_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel s",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


    override fun onLocationChanged(p0: Location) {
        startLocationUpdates()
        Toast.makeText(this, "update", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(2.0F)
//            .setMinUpdateIntervalMillis(1000)
//            .setMaxUpdateDelayMillis(1000)
            .build()


    }

    private fun getLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {

                if (location.locations.isNotEmpty()) {
                    latitude = location.locations[0].latitude
                    longitude = location.locations[0].longitude
                    LoggerGenratter.getInstance()
                        .printLog("onLocationResult1:", " $latitude $longitude")
                }
                for (location1 in location.locations) {
                    latitude = location1.latitude
                    longitude = location1.longitude
//                    Timber.tag(TAG).d("onLocationResult2: $latitude $longitude")
                    LoggerGenratter.getInstance()
                        .printLog("onLocationResult2:", " $latitude $longitude")

//                    addLastActivity(long,lat)
//                    shareLiveLocationToServer(long, lat)
                }
//                Toast.makeText(applicationContext, "location changed", Toast.LENGTH_SHORT).show()
                val json = JSONObject()


                json.put("Longitude", longitude)
                json.put("Latitude", latitude)
                json.put("City", countryName)
                json.put("Country", countryName)
//              json.put("IsLiveTrackLocation", isLiveTrackLocation)
//              json.put("UserID", userId)

                jsonObjLocationList.add("$json")
//                Timber.tag(TAG).d("${jsonObjLocationList.size} : JSONObject: $json")
                LoggerGenratter.getInstance().printLog(
                    "onLocationResult2:",
                    " ${jsonObjLocationList.size} : JSONObject: $json"
                )


                makeApiCallAddAttendance(("$latitude,$longitude"), SessionManagement(this@LocationService).getUserId())

                /*if (SystemClock.elapsedRealtime() - mLastClickTime < twoMinutes) {
                    return
                }*/
                mLastClickTime = SystemClock.elapsedRealtime()
//                getCompleteAddressString(latitude, longitude)
            }
        }
    }


    override fun onDestroy() {

        LoggerGenratter.getInstance()
            .printLog("serviceLogs:", "Service Destroyed")

        fusedLocationClient.removeLocationUpdates(locationCallback)
        try {
            cdt?.cancel()
        } catch (exp: Exception) {
            LoggerGenratter.getInstance()
                .printLog("onLocationResult2:", "Service Exp onDestroy: $exp")

        }
        super.onDestroy()
    }


    private fun makeApiCallAddAttendance(data: String, id: String) {


        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!apiIsAlreadyCalling) {
                    apiIsAlreadyCalling = true
                    // do work from here
                    updateLocationinDB(data,SessionManagement(this@LocationService).getUserId())

                }

            } catch (e: Exception) {
                // Handle any errors
                // ...
            }
        }
    }


    private fun updateLocationinDB(latlng: String,useridd:String
    ) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().reference
        val userReference = reference?.child("users")?.child(useridd)
        userReference?.child("latlng")?.setValue(latlng)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email updated successfully
                    LoggerGenratter.getInstance()
                        .printLog("UPDAYE:", "Location Updated")
                    apiIsAlreadyCalling = false
                } else {
                    // Handle error
                    LoggerGenratter.getInstance()
                        .printLog("UPDAYE:", "Error in Location Updated")
                    apiIsAlreadyCalling = false
                }
            }


    }


    private fun creatinoutNotification(messageBody: String) {
        val intent = Intent(this, MapViewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent: PendingIntent
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // only for gingerbread and newer versions
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                this,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT or FLAG_IMMUTABLE
            )
        }
        val channelId = "fcm_default_channel" //getString(R.string.default_notification_channel_id);
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("AIS HRMS")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /*  */, notificationBuilder.build())
    }
}



