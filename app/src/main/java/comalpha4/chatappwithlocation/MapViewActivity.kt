package comalpha4.chatappwithlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.permissionx.guolindev.PermissionX
import comalpha4.chatappwithlocation.services.LocationService

class MapViewActivity : AppCompatActivity(),LocationChangeListener {
    lateinit var location:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
      checkPermissions()
    }
    private fun startService() {
        LoggerGenratter.getInstance()
            .printLog("serviceLogs:", "Service called")
        val serviceIntent = Intent(this, LocationService::class.java)

        ContextCompat.startForegroundService(this, serviceIntent)
        LoggerGenratter.getInstance()
            .printLog("serviceLogs:", "Service started")
    }
    override fun onLocationChanged(location: String) {
       Toast.makeText(this,location,Toast.LENGTH_SHORT).show()
    }
    private fun checkPermissions() {
        PermissionX.init(this)
            .permissions(
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.FOREGROUND_SERVICE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION"
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()

                  startService()
                } else {
                    Toast.makeText(
                        this,
                        "Some permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}