package com.etsija.digitransit.view.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.etsija.digitransit.databinding.FragmentDeparturesBinding
import com.etsija.digitransit.model.Departure
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Constants
import com.etsija.digitransit.utils.Helpers.Companion.setCardColor
import com.etsija.digitransit.utils.Helpers.Companion.setCardSymbol
import com.etsija.digitransit.utils.prefs
import com.etsija.digitransit.view.epoxy.DeparturesEpoxyController
import kotlinx.coroutines.*

class DeparturesFragment : BaseFragment() {

    private val LOG = "BaseFragment"
    private var _binding: FragmentDeparturesBinding? = null
    private val binding get() = _binding!!
    private val controller = DeparturesEpoxyController()

    private val safeArgs: DeparturesFragmentArgs by navArgs()
    private val selectedStop: Stop? by lazy {
        sharedViewModel.stops.value?.find {
            it.gtfsId == safeArgs.selectedStopId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDeparturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set info card label based on type of the stop
        val text = setCardSymbol(selectedStop?.type!!)
        binding.tvType.text = text

        // Set info card color based on type of the stop
        val color = setCardColor(selectedStop?.type!!)
        binding.tvType.setBackgroundColor(color)
        binding.mcStopInfo.setStrokeColor(ColorStateList.valueOf(color))
        
        binding.tvStopName.text = selectedStop?.stopName
        binding.tvCode.text = selectedStop?.stopCode ?: selectedStop?.gtfsId
        //binding.tvZone.text = selectedStop?.zoneId

        if (selectedStop?.zoneId == null) {
            binding.lblZone.isGone = true
        } else {
            binding.lblZone.isVisible = true
            binding.tvZone.text = selectedStop?.zoneId
        }

        // Get pattern names from Patterns and show the patterns in a scrollable TextView
        // so that the view does not take the whole screen for busy stops
        val justNames = selectedStop?.patterns
            ?.map { pattern ->
                pattern?.name
            }?.joinToString(separator = "\n")

        if (justNames.isNullOrEmpty()) {
            binding.lblPatterns.isGone = true
            binding.tvPatterns.isGone = true
        } else {
            binding.lblPatterns.isVisible = true
            binding.tvPatterns.text = justNames
        }

        binding.tvPatterns.movementMethod = ScrollingMovementMethod()
        //binding.tvPatterns.text = justNames

        binding.ervDeparturesFromStop.setController(controller)

        // Launch a coroutine to poll next departures
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    sharedViewModel.pollDepartures(
                        selectedStop!!.gtfsId
                    )
                    delay(prefs.arrivalsSearchInterval * Constants.ONE_SECOND)
                }
            }
        }

        sharedViewModel.departures.observe(viewLifecycleOwner, { departures ->
            if (departures == null) {
                Toast.makeText(activity, "Network problems!", Toast.LENGTH_SHORT).show()
                return@observe
            }
            Log.d(LOG, departures.toString())
            controller.departures = departures as ArrayList<Departure>
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.cancel()
        _binding = null
    }
}