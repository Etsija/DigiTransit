package com.etsija.digitransit.view.fragment

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
import com.etsija.digitransit.utils.Helpers.Companion.getAddress
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
    private var latitude: String = "60.2068726"
    private var longitude: String = "24.8939462"
    private var networkFailure: Boolean = false

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
        viewLifecycleOwner.lifecycleScope.launch {
            var value = 0
            withContext(Dispatchers.IO) {
                while (true) {
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

        // Observe stop data for changes
        sharedViewModel.stops.observe(viewLifecycleOwner, { stops ->
            // Notify used about problems with the network or data
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
        locationViewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            prefs.lastLat = it.latitude
            prefs.lastLon = it.longitude

            // Testing Helsinki, Aarnin talo
            //prefs.lastLat = "60.1872239"
            //prefs.lastLon = "24.9533152"

            // Testing Helsinki, päärautatieasema
            //prefs.lastLat = "60.171323"
            //prefs.lastLon = "24.940923"

            // Testing Helsinki, Huopalahden asema
            //prefs.lastLat = "60.218564"
            //prefs.lastLon = "24.892657"

            // Testing Helsinki, Tikkurilan rautatieasema
            //prefs.lastLat = "60.293350"
            //prefs.lastLon = "25.044936"


            // Testing Oulu
            //prefs.lastLat = "65.0156201"
            //prefs.lastLon = "25.4697043"

            // Testing Hyvinkää/Vehkoja
            //prefs.lastLat = "60.608580"
            //prefs.lastLon = "24.838765"

            // Testing Mikkeli/Harri Häkkinen
            //prefs.lastLat = "61.6888813"
            //prefs.lastLon = "27.2577625"

            binding.tvLat.text = prefs.lastLat
            binding.tvLon.text = prefs.lastLon

            binding.tvAddress.text = context?.let { it1 ->
                getAddress(
                    it1,
                    prefs.lastLat.toString().toDouble(),
                    prefs.lastLon.toString().toDouble()
                )
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