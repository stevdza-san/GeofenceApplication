package com.example.geofenceapp.bindingadapters

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.example.geofenceapp.viewmodels.Step1ViewModel
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("updateGeofenceName", "enableNextButton", requireAll = true)
fun TextInputEditText.onTextChanged(
    sharedViewModel: SharedViewModel,
    step1ViewModel: Step1ViewModel
) {
    this.setText(sharedViewModel.geoName)
    Log.d("Bindings", sharedViewModel.geoName)
    this.doOnTextChanged { text, _, _, _ ->
        if (text.isNullOrEmpty()) {
            step1ViewModel.enableNextButton(false)
        } else {
            step1ViewModel.enableNextButton(true)
        }
        sharedViewModel.geoName = text.toString()
        Log.d("Bindings", sharedViewModel.geoName)
    }
}

@BindingAdapter("nextButtonEnabled", "saveGeofenceId", requireAll = true)
fun TextView.step1NextClicked(nextButtonEnabled: Boolean, sharedViewModel: SharedViewModel) {
    this.setOnClickListener {
        if (nextButtonEnabled) {
            sharedViewModel.geoId = System.currentTimeMillis()
            this.findNavController().navigate(R.id.action_step1Fragment_to_step2Fragment)
        }
    }
}

@BindingAdapter("setProgressVisibility")
fun ProgressBar.setProgressVisibility(nextButtonEnabled: Boolean) {
    if (nextButtonEnabled) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}







