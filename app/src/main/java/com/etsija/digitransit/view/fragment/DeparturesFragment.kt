package com.etsija.digitransit.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.FragmentDeparturesBinding
import com.etsija.digitransit.databinding.FragmentStopsBinding
import com.etsija.digitransit.model.Stop

class DeparturesFragment : BaseFragment() {

    private val LOG = "BaseFragment"
    private var _binding: FragmentDeparturesBinding? = null
    private val binding get() = _binding!!

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

        binding.test.text = selectedStop?.stopCode

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}