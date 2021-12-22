package com.etsija.digitransit.view.epoxy

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.*
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.utils.Helpers.Companion.colorToHex
import com.etsija.digitransit.utils.Helpers.Companion.findNextStops
import com.etsija.digitransit.utils.Helpers.Companion.getPatternNumbers
import com.etsija.digitransit.utils.Helpers.Companion.getPatternNumbersColored
import com.etsija.digitransit.utils.Helpers.Companion.setCardColor
import com.etsija.digitransit.utils.Helpers.Companion.setPatternColor
import java.lang.Integer.toHexString
import java.security.AccessController.getContext

class StopEpoxyController(
    private val stopInterface: StopInterface
): EpoxyController() {

    private var isLoading: Boolean = true
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

        // Sort alerts by descending start date and add the data to controller
        stops.sortedBy {
            it.distance
        }.forEach { stop ->
            // Do not show stops without patterns
            if (!stop.patterns.isNullOrEmpty()) {
                StopEpoxyModel(stop, stopInterface)
                    .id(stop.gtfsId)
                    .addTo(this)
            }
        }
    }

    // This is the Epoxy model for one stop in the list
    data class StopEpoxyModel(
        val stop: Stop,
        val stopInterface: StopInterface
    ): ViewBindingKotlinModel<ModelStopBinding>(R.layout.model_stop) {

        override fun ModelStopBinding.bind() {
            tvName.text = stop.stopName
            mStopCode.tvCode2.text = stop.stopCode ?: stop.gtfsId

            if (stop.parentName == null) {
                tvParentName.isGone = true
            } else {
                tvParentName.isVisible = true
                tvParentName.text = stop.parentName
            }
            tvDistance.text = stop.distance.toString()

            if (stop.zoneId == null) {
                lblZone.isGone = true
                tvZone.isGone = true
            } else {
                lblZone.isVisible = true
                tvZone.isVisible = true
                tvZone.text = stop.zoneId
            }

            if (stop.patterns.isNullOrEmpty()) {
                tvPatternNumbers.isGone = true
            } else {
                tvPatternNumbers.isVisible = true
                //tvPatternNumbers.text = getPatternNumbers(stop.patterns)
                tvPatternNumbers.text = Html
                    .fromHtml(getPatternNumbersColored(stop.patterns), FROM_HTML_MODE_LEGACY)
            }

            val nextStops = findNextStops(stop)
            if (nextStops == "") {
                tvNextStops.isGone = true
            } else {
                tvNextStops.isVisible = true
                tvNextStops.text = nextStops
            }

            // Set card symbol and colour based on type of stop
            stop.type?.let { type ->

                // Set card and stop code colors based on type of stop

                // Card color
                val color = setCardColor(type)
                tvType.setBackgroundColor(color)
                root.strokeColor = color

                //Log.d("setPatternColor()", colorToHex(setPatternColor("INBOUND")))
                //tvPatternNumbers.text = Html.fromHtml("xxx", FROM_HTML_MODE_LEGACY)


                // Stop code color
                mStopCode.mcCode.strokeColor = color
                mStopCode.root.setCardBackgroundColor(color)
                //mStopCode.root.strokeColor = color

                // Set card symbol
                when (type) {
                    "BUS" -> {
                        ivType.isVisible = true
                        ivType.setImageResource(R.drawable.ic_baseline_directions_bus_24)
                        tvType.text = ""
                    }
                    "TRAM" -> {
                        ivType.isVisible = true
                        ivType.setImageResource(R.drawable.ic_baseline_tram_24)
                        tvType.text = ""
                    }
                    "RAIL" -> {
                        ivType.isVisible = true
                        ivType.setImageResource(R.drawable.ic_baseline_train_24)
                        tvType.text = ""
                    }
                    "METRO" -> {
                        ivType.isGone = true
                        tvType.text = "M"
                    }
                    else -> {
                        ivType.isGone = true
                        tvType.text = ""
                    }
                }
            }

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