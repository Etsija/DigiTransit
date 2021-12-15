package com.etsija.digitransit.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.etsija.digitransit.databinding.FragmentAlertsBinding
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.view.epoxy.AlertEpoxyController

class AlertsFragment : BaseFragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = AlertEpoxyController()
        binding.ervAlerts.setController(controller)

        sharedViewModel.alerts.observe(viewLifecycleOwner, { alerts ->
            if (alerts == null) {
                Toast.makeText(activity, "Network problems!", Toast.LENGTH_SHORT).show()
                return@observe
            }
            Log.d("Response from AlertsFragment()", alerts.toString())
            controller.alerts = alerts as ArrayList<Alert>
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}