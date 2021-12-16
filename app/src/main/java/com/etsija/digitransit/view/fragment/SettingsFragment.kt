package com.etsija.digitransit.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.etsija.digitransit.databinding.FragmentSettingsBinding
import com.etsija.digitransit.utils.App.Companion.prefs

class SettingsFragment : Fragment() {
    private val LOG = "SettingsFragment()"
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the shared preferences and put them on screen

        binding.etLocInterval.setText(prefs?.locInterval.toString())
        binding.etRadius.setText(prefs?.searchRadius.toString())
        binding.etStopsInterval.setText(prefs?.stopsSearchInterval.toString())
        binding.etArrivalInterval.setText(prefs?.arrivalsSearchInterval.toString())

        // Save the shared preferences
        binding.btnSavePreferences.setOnClickListener {

            if (areSettingsOK()) {
                prefs!!.locInterval =
                    Integer.parseInt(binding.etLocInterval.text.toString())
                prefs!!.searchRadius =
                    Integer.parseInt(binding.etRadius.text.toString())
                prefs!!.stopsSearchInterval =
                    Integer.parseInt(binding.etStopsInterval.text.toString())
                prefs!!.arrivalsSearchInterval =
                    Integer.parseInt(binding.etArrivalInterval.text.toString())
                Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun areSettingsOK(): Boolean {
        var retValue: Boolean = false
        val locInterval = binding.etLocInterval
        val radius = binding.etRadius
        val stopsInterval = binding.etStopsInterval
        val arrivalsInterval = binding.etArrivalInterval

        if (locInterval.text.toString().toInt() < 5) {
            locInterval.error = "* Minimum is 5"
            retValue = false
        } else if (radius.text.toString().toInt() < 1) {
            radius.error = "* Mininum is 1"
            retValue = false
        } else if (stopsInterval.text.toString().toInt() < 10) {
            stopsInterval.error = "* Minimum is 10"
            retValue = false
        } else if (arrivalsInterval.text.toString().toDouble() < 10) {
            arrivalsInterval.error = "* Minimum is 10"
            retValue = false
        } else {
            retValue = true
        }
        return retValue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}