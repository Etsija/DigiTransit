package com.etsija.digitransit.view.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.navigation.fragment.navArgs
import com.etsija.digitransit.databinding.FragmentDeparturesBinding
import com.etsija.digitransit.model.Pattern

import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Helpers
import com.etsija.digitransit.utils.Helpers.Companion.getPatternNumbers
import com.etsija.digitransit.utils.Helpers.Companion.setCardColor
import com.etsija.digitransit.utils.Helpers.Companion.setCardSymbol
import com.etsija.digitransit.utils.Helpers.Companion.tidyPatternName

class DeparturesFragment : BaseFragment() {

    private val LOG = "BaseFragment"
    private var _binding: FragmentDeparturesBinding? = null
    private val binding get() = _binding!!
    private lateinit var lvPatterns: ListView

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
        binding.root.setStrokeColor(ColorStateList.valueOf(color))

        //binding.test.text = selectedStop?.patterns.toString()
        binding.tvName.text = selectedStop?.stopName
        binding.tvCode.text = selectedStop?.stopCode
        binding.tvZone.text = selectedStop?.zoneId

        // Only a temp solution!
        val justNames = selectedStop?.patterns
            ?.map { pattern ->
                pattern?.name
            }?.joinToString(separator = "\n")
        binding.tvPatterns.text = justNames
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}