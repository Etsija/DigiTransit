package com.etsija.digitransit.view.epoxy

import com.airbnb.epoxy.EpoxyController
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.ModelDeparturesEmptyStateBinding
import com.etsija.digitransit.databinding.ModelLoadingStateBinding
import com.etsija.digitransit.databinding.ModelStopEmptyStateBinding
import com.etsija.digitransit.model.Departure
import com.etsija.digitransit.model.Stop

class DeparturesEpoxyController: EpoxyController() {

    var isLoading: Boolean = true
        set(value) {
            field = value
            if (field) {
                requestModelBuild()
            }
        }

    var departures = ArrayList<Departure>()
        set(value) {
            field = value
            isLoading = false
            requestModelBuild()
        }

    // Main function to build the different models for the controller
    override fun buildModels() {

        if (isLoading) {
            DeparturesEpoxyController.LoadingEpoxyModel().id("loading_state").addTo(this)
            return
        }

        if (departures.isEmpty()) {
            StopEpoxyController.EmptyStateEpoxyModel().id("stop_empty_state").addTo(this)
            return
        }
    }

    // Handle loading state
    class LoadingEpoxyModel:
        ViewBindingKotlinModel<ModelLoadingStateBinding>(R.layout.model_loading_state) {

        override fun ModelLoadingStateBinding.bind() {
            // nothing to do
        }
    }

    // Handle empty state
    class EmptyStateEpoxyModel:
        ViewBindingKotlinModel<ModelDeparturesEmptyStateBinding>(R.layout.model_departures_empty_state) {

        override fun ModelDeparturesEmptyStateBinding.bind() {
            // nothing to do
        }
    }
}