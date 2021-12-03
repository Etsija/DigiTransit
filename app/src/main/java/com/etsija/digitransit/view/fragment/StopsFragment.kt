package com.etsija.digitransit.view.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.FragmentAlertsBinding
import com.etsija.digitransit.databinding.FragmentStopsBinding
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Constants
import com.etsija.digitransit.view.epoxy.StopEpoxyController
import com.etsija.digitransit.viewmodel.LocationViewModel
import com.etsija.digitransit.viewmodel.SharedViewModel
import java.util.jar.Manifest

class StopsFragment : BaseFragment() {

    private var _binding: FragmentStopsBinding? = null
    private val binding get() = _binding!!
    private val LOCATION_PERMISSION_REQUEST = 2000
    private val controller = StopEpoxyController()

    // ViewModel for this activity's lifecycle
    val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(this).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ervStops.setController(controller)
        prepRequestLocationUpdates()

        sharedViewModel.stops.observe(viewLifecycleOwner, { stops ->
            Log.d("Stops near this location", stops.toString())
            controller.stops = stops as ArrayList<Stop>
        })

    }

    // Request permission for the location updates
    private fun prepRequestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
        }
        else {
            val permissionRequest = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissionRequest, LOCATION_PERMISSION_REQUEST)
        }
    }

    // Get the current location
    private fun requestLocationUpdates() {
        locationViewModel.getLocationLiveData().observe(viewLifecycleOwner, Observer {
            binding.txtLat.text = it.latitude
            binding.txtLon.text = it.longitude
            Log.d("Location:", it.latitude.toString() + ":" + it.longitude.toString())

            // Get nearby stops
            sharedViewModel.getStops(it.latitude.toDouble(), it.longitude.toDouble(), Constants.Companion.RADIUS)

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("StopsFragment", "Enter requestLocationUpdates()")
                    requestLocationUpdates()
                } else {
                    Toast.makeText(context, "Unable to find location without permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}