package comalpha4.chatappwithlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapViewActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private var mapFragment: SupportMapFragment? = null
    private lateinit var userDetailCard: View
    private lateinit var userImage: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var btnChat: TextView
    private lateinit var btnCall: TextView
    private var currentMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
//        userDetailCard = layoutInflater.inflate(R.layout.user_detatil_card, null)
//        tvUserName = userDetailCard.findViewById(R.id.tvUserName)
//        userImage = userDetailCard.findViewById(R.id.image)
//        btnChat = userDetailCard.findViewById(R.id.btnChat)
//        btnCall = userDetailCard.findViewById(R.id.btnCall)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        val location = LatLng(37.7749, -122.4194)
        val marker1 = mMap.addMarker(MarkerOptions().position(LatLng(37.7749, -122.4194)).title("User 1"))
        val marker2 = mMap.addMarker(MarkerOptions().position(LatLng(34.0522, -118.2437)).title("User 2"))
//
//        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
//
        // Set marker click listener to manage the user detail card visibility
//        mMap.setOnMarkerClickListener { marker ->
//            currentMarker?.let {
//                it.hideInfoWindow()
//            }
//            currentMarker = marker
//            marker.showInfoWindow()
//            true
//        }
//
//        val boundsBuilder = LatLngBounds.builder()
//        boundsBuilder.include(marker1!!.position)
//        boundsBuilder.include(marker2!!.position)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 0.0F))
        val uiSettings: UiSettings = googleMap.uiSettings
        uiSettings.isZoomControlsEnabled = true
    }
//
//    inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
//        override fun getInfoWindow(marker: Marker): View? {
//            return null
//        }
//
//        override fun getInfoContents(marker: Marker): View {
//            // Show user detail card when a marker is clicked
//            userDetailCard.visibility = View.VISIBLE
//
//            // Sample user details (replace with actual user data)
//            val userName = marker.title
//            val userDetails = "Age: 30\nLocation: New York"
//
//            // Update the user detail card views
//            tvUserName.text = userName
////            tvUserDetails.text = userDetails
//
//            return userDetailCard
//        }
//    }
}