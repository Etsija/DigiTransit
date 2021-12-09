package com.etsija.digitransit.view.epoxy

import android.content.res.ColorStateList
import android.graphics.Color
import com.airbnb.epoxy.EpoxyController
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.ModelDepartureBinding
import com.etsija.digitransit.databinding.ModelDeparturesEmptyStateBinding
import com.etsija.digitransit.databinding.ModelLoadingStateBinding
import com.etsija.digitransit.model.Departure

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
            LoadingEpoxyModel().id("loading_state").addTo(this)
            return
        }

        if (departures.isEmpty()) {
            StopEpoxyController.EmptyStateEpoxyModel().id("departures_empty_state").addTo(this)
            return
        }

        // Show departures
        departures.forEach { departure ->
            DepartureEpoxyModel(departure)
                .id(departure.realtimeDeparture)
                .addTo(this)
        }

    }

    // This is the Epoxy model for one departure in the list
    data class DepartureEpoxyModel(
        val departure: Departure
    ): ViewBindingKotlinModel<ModelDepartureBinding>(R.layout.model_departure) {

        override fun ModelDepartureBinding.bind() {
            tvDate.text = departure.serviceDay
            tvHeadsign.text = departure.headsign
            tvRouteName.text = departure.route

            if (departure.realtime) {
                tvTimeUntilDeparture.text = departure.realtimeDeparture
                root.setStrokeColor(ColorStateList.valueOf(Color.GREEN))
            } else {
                tvTimeUntilDeparture.text = "~" + departure.scheduledDeparture
                root.setStrokeColor(ColorStateList.valueOf(Color.YELLOW))
            }
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