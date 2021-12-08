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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.FragmentAlertsBinding
import com.etsija.digitransit.databinding.FragmentStopsBinding
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Constants
import com.etsija.digitransit.utils.Constants.Companion.ONE_SECOND
import com.etsija.digitransit.utils.prefs
import com.etsija.digitransit.view.epoxy.StopEpoxyController
import com.etsija.digitransit.view.epoxy.StopInterface
import com.etsija.digitransit.viewmodel.LocationViewModel
import com.etsija.digitransit.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import java.util.jar.Manifest

class StopsFragment : BaseFragment(), StopInterface {

    private val LOG = "StopsFragment"
    private var _binding: FragmentStopsBinding? = null
    private val binding get() = _binding!!
    private val LOCATION_PERMISSION_REQUEST = 2000
    private val controller = StopEpoxyController(this)
    private var latitude: String = "60.2068726"
    private var longitude: String = "24.8939462"

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

        // Launch a coroutine to poll nearby stops
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    sharedViewModel.pollStops(
                        prefs.lastLat!!.toDouble(),
                        prefs.lastLon!!.toDouble(),
                        prefs.searchRadius
                    )
                    delay(prefs.stopsSearchInterval * ONE_SECOND)
                }
            }
        }

        sharedViewModel.stops.observe(viewLifecycleOwner, { stops ->
            Log.d(LOG, stops.toString())
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
            prefs.lastLat = it.latitude
            prefs.lastLon = it.longitude

            binding.txtLat.text = prefs.lastLat
            binding.txtLon.text = prefs.lastLon
            Log.d(LOG, prefs.lastLat + ":" + prefs.lastLon)
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

    override fun onStopSelected(stop: Stop) {
        val navDirections = StopsFragmentDirections
            .actionStopsFragmentToDeparturesFragment(stop.gtfsId)
        navigateViaNavGraph(navDirections)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.cancel()
        _binding = null
    }
}