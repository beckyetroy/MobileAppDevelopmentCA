package org.wit.movie.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_g_maps.*

import org.wit.movie.R
import org.wit.movie.models.Location

class GMapsActivity : AppCompatActivity(), OnMapReadyCallback,  GoogleMap.OnMarkerDragListener {

    private lateinit var Map: GoogleMap
    var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_g_maps)
        location = intent.extras?.getParcelable<Location>("location")!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Map = googleMap
        val loc = LatLng(location.lat, location.lng)
        val options = MarkerOptions()
            .title("Movie")
            .snippet("GPS : " + loc.toString())
            .draggable(true)
            .position(loc)
        Map.addMarker(options)
        Map.setOnMarkerDragListener(this)
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
    }

    override fun onMarkerDragStart(marker: Marker) {

    }

    override fun onMarkerDrag(marker: Marker) {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = Map.cameraPosition.zoom
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }
}