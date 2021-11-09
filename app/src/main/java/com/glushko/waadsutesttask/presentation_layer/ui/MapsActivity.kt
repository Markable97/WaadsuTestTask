package com.glushko.waadsutesttask.presentation_layer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.glushko.waadsutesttask.presentation_layer.vm.MapViewModel
import com.glushko.waadsutesttask.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.glushko.waadsutesttask.databinding.ActivityMapsBinding
import com.glushko.waadsutesttask.databinding.ToolbarBinding
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.utils.geojson.geoJsonLayer
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var bindingToolbar: ToolbarBinding
    private lateinit var model: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        bindingToolbar = binding.toolbar
        setContentView(binding.root)
        setSupportActionBar(bindingToolbar.toolbar)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        model = ViewModelProvider(this).get(MapViewModel::class.java)

        model.liveDataAPI.observe(this, Observer {
            bindingToolbar.toolbarProgressBar.visibility = View.INVISIBLE
            if(it == false){
                Snackbar.make(binding.root, getString(R.string.snackbar_maps_tittle_text),Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.snackbar_maps_action)){
                        model.getCoordinates()
                        bindingToolbar.toolbarProgressBar.visibility= View.VISIBLE
                    }.show()
            }
        })
        bindingToolbar.toolbarProgressBar.visibility = View.VISIBLE
        model.getCoordinates()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        model.liveDataCoordinate.observe(this, Observer {
            //Получаем из LiveData GeoJSON с сервера
            val geoJsonData: JSONObject? = it
            geoJsonData?.let{data ->
                //и добавляем слой на карту
                val layer = geoJsonLayer(mMap, data)
                layer.addLayerToMap()
                model.getMarkers(it)
            }
        })
        model.liveDataMarker.observe(this, Observer {markers ->
            markers.forEach {
                println("Point = ${it}")
                googleMap.addMarker(MarkerOptions()
                    .position(it.point)
                    .title(it.textMarker))
            }

        })


    }
}