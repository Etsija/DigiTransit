package com.etsija.digitransit.view.epoxy

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.EpoxyController
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.*
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Helpers.Companion.getPatternNumbers
import com.etsija.digitransit.utils.Helpers.Companion.setCardColor
import com.etsija.digitransit.utils.Helpers.Companion.setCardSymbol

class StopEpoxyController(
    private val stopInterface: StopInterface
): EpoxyController() {

    var isLoading: Boolean = true
        set(value) {
            field = value
            if (field) {
                requestModelBuild()
            }
        }

    var stops = ArrayList<Stop>()
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

        if (stops.isEmpty()) {
            EmptyStateEpoxyModel().id("stop_empty_state").addTo(this)
            return
        }

        // Sort alerts by descending start date
        stops.sortedBy {
            it.distance
        }.forEach { stop ->
            StopEpoxyModel(stop, stopInterface)
                .id(stop.id)
                .addTo(this)
        }
    }

    // This is the Epoxy model for one stop in the list
    data class StopEpoxyModel(
        val stop: Stop,
        val stopInterface: StopInterface
    ): ViewBindingKotlinModel<ModelStopBinding>(R.layout.model_stop) {

        override fun ModelStopBinding.bind() {
            tvStopNameInStops.text = stop.stopName
            tvCode.text = stop.stopCode
            tvDistance.text = stop.distance.toString()
            tvPatternNumbers.text = getPatternNumbers(stop.patterns)

            // Set info card label based on type of the stop
            val text = setCardSymbol(stop.type!!)
            tvType.text = text

            // Set info card color based on type of the stop
            val color = setCardColor(stop.type)
            tvType.setBackgroundColor(color)
            root.setStrokeColor(ColorStateList.valueOf(color))

            root.setOnClickListener {
                stopInterface.onStopSelected(stop)
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
        ViewBindingKotlinModel<ModelStopEmptyStateBinding>(R.layout.model_stop_empty_state) {

        override fun ModelStopEmptyStateBinding.bind() {
            // nothing to do
        }
    }


}