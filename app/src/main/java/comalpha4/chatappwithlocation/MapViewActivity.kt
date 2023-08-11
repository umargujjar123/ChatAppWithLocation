package comalpha4.chatappwithlocation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.permissionx.guolindev.PermissionX
import comalpha4.chatappwithlocation.Models.UserModel
import comalpha4.chatappwithlocation.services.LocationService

class MapViewActivity : FragmentActivity(),LocationChangeListener , OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userDetailCard: View
    private lateinit var userImage: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var btnChat: TextView
    private lateinit var btnCall: TextView
    private val apiCallInterval: Long = 5000 // 5 seconds
    private val handler = Handler()
    lateinit var userModel: UserModel
 lateinit var  googleMap: GoogleMap
    private var currentMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
        userDetailCard = layoutInflater.inflate(R.layout.user_detatil_card, null)
        tvUserName = userDetailCard.findViewById(R.id.tvUserName)
        userImage = userDetailCard.findViewById(R.id.image)
        btnChat = userDetailCard.findViewById(R.id.btnChat)
        btnCall = userDetailCard.findViewById(R.id.btnCall)
        startService()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)




    }
    private val apiCallRunnable = object : Runnable {
        override fun run() {
            // Perform your API call here
            // Replace this with your actual API call logic
            if (googleMap!=null)
            {
               LoginUser()

            }
            // Re-post the Runnable with the delay
            handler.postDelayed(this, apiCallInterval)
        }
    }

    private fun startPeriodicApiCall() {
        // Start the initial API call
        handler.post(apiCallRunnable)
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

                    startPeriodicApiCall()
                } else {
                    Toast.makeText(
                        this,
                        "Some permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    override fun onLowMemory() {
        super.onLowMemory()

    }
    override fun onMapReady(googleMap: GoogleMap) {
        val customDrawable = BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_24)

        mMap = googleMap
        this.googleMap=googleMap
       LoginUser()
//        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())

//         Set marker click listener to manage the user detail card visibility
        mMap.setOnMarkerClickListener { marker ->
            var intent=Intent(this,ChatActivity::class.java)
            intent.putExtra("userid", marker.tag.toString())
            intent.putExtra("title", marker.title.toString())
            startActivity(intent)
           /* if (marker.tag!!.equals(""))
            currentMarker?.let {
                it.hideInfoWindow()
            }*/
            currentMarker = marker
            marker.showInfoWindow()
            true
        }

    }

    inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {

            return null
        }

        override fun getInfoContents(marker: Marker): View {
            // Show user detail card when a marker is clicked
            userDetailCard.visibility = View.VISIBLE

            // Sample user details (replace with actual user data)
            val userName = marker.title
            val userDetails = "Age: 30\nLocation: New York"

            // Update the user detail card views
            tvUserName.text = userName
//            tvUserDetails.text = userDetails

            btnChat.setOnClickListener {
                Toast.makeText(this@MapViewActivity, "chat", Toast.LENGTH_LONG).show()

            }
            btnCall.setOnClickListener {
                Toast.makeText(this@MapViewActivity, "call", Toast.LENGTH_LONG).show()

            }
            return userDetailCard
        }
    }






    fun LoginUser() {
        var reference: DatabaseReference? = null
        reference = FirebaseDatabase.getInstance().getReference("users")

       /* val query: Query =
            reference!!.orderByChild("email").equalTo(phoneNumberET.getText().toString())*/
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val boundsBuilder = LatLngBounds.builder()

                    for (data in dataSnapshot.children) {
                        var ltln=data.child("latlng").getValue<String>(String::class.java)

                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(ltln!!.split(",")[0].toDouble(), ltln!!.split(",")[1].toDouble())).title(data.child("name").getValue<String>(String::class.java)))//.icon(customDrawable))
                        marker?.tag=data.child("userId").getValue<String>(String::class.java)
                        marker?.showInfoWindow()
                        boundsBuilder.include(marker!!.position)

                      /*  userModel = UserModel(
                            data.child("userId").getValue<String>(String::class.java),
                            data.child("email").getValue<String>(String::class.java),
                            data.child("fcm").getValue<String>(String::class.java),
                            data.child("latlng").getValue<String>(String::class.java),
                            data.child("name").getValue<String>(String::class.java),
                            data.child("password").getValue<String>(String::class.java),
                            data.child("phone").getValue<String>(String::class.java)
                        )*/
                    }


                    val location = LatLng(33.6186045, 73.1665149)
                    /*val location = LatLng(37.7749, -122.4194)
                    val marker1 = mMap.addMarker(MarkerOptions().position(LatLng(37.7749, -122.4194)).title("User 1"))//.icon(customDrawable))
                    val marker2 = mMap.addMarker(MarkerOptions().position(LatLng(34.0522, -118.2437)).title("User 2"))//.icon(customDrawable))
                    boundsBuilder.include(marker2!!.position)*/
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 3.0F))
                    var currentlocation=LocationHelper(this@MapViewActivity).getCurrentLocation()?.split(",")
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 3f));

                    val uiSettings: UiSettings = googleMap.uiSettings
                    uiSettings.isZoomControlsEnabled = true

                } else {
                    Toast.makeText(this@MapViewActivity, "error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MapViewActivity, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}