package comalpha4.chatappwithlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

/**
 * Created by Adnan Bashir manak on 06,August,2023
 * AIS company,
 * Krachi, Pakistan.
 */
interface LocationChangeListener {
    fun onLocationChanged(location: String)
}

class LocationHelper(private val context: Context) {

    private var locationManager: LocationManager? = null
    private var locationChangeListener: LocationChangeListener? = null

    fun setLocationChangeListener(listener: LocationChangeListener) {
        locationChangeListener = listener
    }

    // Listener to receive location updates
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latLng = "${location.latitude}, ${location.longitude}"
            locationChangeListener?.onLocationChanged(latLng)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): String? {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            return null
        }

        // Get last known location
        val lastKnownLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        return if (lastKnownLocation != null) {
            "${lastKnownLocation.latitude}, ${lastKnownLocation.longitude}"
        } else {
            null
        }
    }
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L, // Minimum time interval between updates (in milliseconds)
            1.0f,  // Minimum distance between updates (in meters)
            locationListener
        )
    }
    fun stopLocationUpdates() {
        locationManager?.removeUpdates(locationListener)
    }
}