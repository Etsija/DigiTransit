package com.etsija.digitransit.view.epoxy

import android.content.res.ColorStateList
import android.graphics.Color
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.EpoxyController
import com.etsija.digitransit.R
import com.etsija.digitransit.databinding.ModelAlertBinding
import com.etsija.digitransit.databinding.ModelAlertEmptyStateBinding
import com.etsija.digitransit.databinding.ModelLoadingStateBinding
import com.etsija.digitransit.model.Alert

class AlertEpoxyController(): EpoxyController() {

    var isLoading: Boolean = true
        set(value) {
            field = value
            if (field) {
                requestModelBuild()
            }
        }

    var alerts = ArrayList<Alert>()
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

        if (alerts.isEmpty()) {
            EmptyStateEpoxyModel().id("alert_empty_state")
            return
        }

        // Sort alerts by descending start date
        alerts.sortedByDescending {
            it.effectiveStartDate
        }.forEach { alert ->
            AlertEpoxyModel(alert).id(alert.effectiveStartDate).addTo(this)
        }
    }

    // This is the Epoxy model for one alert in the list
    data class AlertEpoxyModel(
        val alert: Alert
    ): ViewBindingKotlinModel<ModelAlertBinding>(R.layout.model_alert) {

        override fun ModelAlertBinding.bind() {
            tvStart.text = alert.effectiveStartDate
            // todo enddate
            tvDescription.text = alert.description
            // todo stop id, name, zone

            // Set info card color based on alert severity
            val color = when (alert.severity) {
                "INFO" -> Color.GREEN
                "WARNING" -> Color.YELLOW
                else -> Color.RED
            }
            tvSeverity.setBackgroundColor(color)
            root.setStrokeColor(ColorStateList.valueOf(color))
        }

    }

    // Handle loading state
    class LoadingEpoxyModel:
        ViewBindingKotlinModel<ModelLoadingStateBinding>(R.layout.model_loading_state) {

        override fun ModelLoadingStateBinding.bind() {
            // todo
        }
    }

    // Handle empty state
    class EmptyStateEpoxyModel:
        ViewBindingKotlinModel<ModelAlertEmptyStateBinding>(R.layout.model_alert_empty_state) {

        override fun ModelAlertEmptyStateBinding.bind() {
            // nothing to do
        }
    }
}