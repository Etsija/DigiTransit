package com.etsija.digitransit.view.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.etsija.digitransit.databinding.FragmentStopsBinding
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Constants.Companion.ONE_SECOND
import com.etsija.digitransit.utils.Helpers.Companion.testLocations
import com.etsija.digitransit.utils.prefs
import com.etsija.digitransit.view.epoxy.StopEpoxyController
import com.etsija.digitransit.view.epoxy.StopInterface
import com.etsija.digitransit.viewmodel.LocationViewModel
import kotlinx.coroutines.*

class StopsFragment : BaseFragment(), StopInterface {

    private val LOG = "StopsFragment"
    private var _binding: FragmentStopsBinding? = null
    private val binding get() = _binding!!
    private val LOCATION_PERMISSION_REQUEST = 2000
    private val controller = StopEpoxyController(this)
    //private var latitude: String = "60.2068726"
    //private var longitude: String = "24.8939462"
    private var networkFailure: Boolean = false
    private lateinit var job: Job

    // ViewModel for this activity's lifecycle
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(this)[LocationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ervStops.setController(controller)
        prepRequestLocationUpdates()

        // Observe the address data and update UI
        locationViewModel.address.observe(viewLifecycleOwner, { address ->
            binding.tvAddress.text = address
        })

        // Observe stop data for changes
        sharedViewModel.stops.observe(viewLifecycleOwner, { stops ->
            // Notify user about problems with the network or data
            if (stops == null) {
                Toast.makeText(activity, "Network problems!", Toast.LENGTH_SHORT).show()
                networkFailure = true
                return@observe
            }
            networkFailure = false
            Log.d(LOG, stops.toString())
            controller.stops = stops as ArrayList<Stop>

            //stops.forEach { stop ->
            //    stop.type?.let { Log.d("Stop type", it) }
            //}

            // After data has changed, scroll to top of list to show nearest stops first
            binding.ervStops.scrollToPosition(0)
        })
    }

    // When the fragment becomes visible (eg. by returning from another fragment, or by bringing
    // the app from background to front), launch a coroutine to poll nearby stops
    override fun onStart() {
        super.onStart()
        Log.d(LOG, "onStart(): start polling the nearby stops")
        job = viewLifecycleOwner.lifecycleScope.launch {
            var value = 0
            withContext(Dispatchers.IO) {
                while (isActive) {
                    sharedViewModel.pollStops(
                        prefs.lastLat!!.toDouble(),
                        prefs.lastLon!!.toDouble(),
                        prefs.searchRadius
                    )
                    Log.d(LOG, "lifecycleScope: ${++value}")
                    delay(prefs.stopsSearchInterval * ONE_SECOND)
                }
            }
        }
    }

    // When the fragment is no longer visible (eg. by navigating to another fragment, or by
    // moving the app to background), cancel polling of stops
    override fun onStop() {
        super.onStop()
        Log.d(LOG, "onStop(): stop polling stops")
        if (job.isActive) {
            job.cancel()
        }
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

    // Observe the current location
    private fun requestLocationUpdates() {
        locationViewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            prefs.lastLat = it.latitude
            prefs.lastLon = it.longitude

            // This is for testing fixed locations (sanity check testing)
            //val (lat, lon) = testLocations(1)
            //prefs.lastLat = lat
            //prefs.lastLon = lon

            binding.tvLat.text = prefs.lastLat
            binding.tvLon.text = prefs.lastLon

            // Get the address of the new location
            context?.let { context ->
                locationViewModel.getAddress(context,
                    prefs.lastLat.toString().toDouble(),
                    prefs.lastLon.toString().toDouble())
            }
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
                    requestLocationUpdates()
                } else {
                    Toast.makeText(context, "Unable to find location without permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStopSelected(stop: Stop) {
        if (!networkFailure) {
            val navDirections = StopsFragmentDirections
                .actionStopsFragmentToDeparturesFragment(stop.gtfsId)
            navigateViaNavGraph(navDirections)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}