package com.example.geofenceapp.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.libraries.places.api.model.AutocompletePrediction

@BindingAdapter("setCity")
fun TextView.setCity(prediction: AutocompletePrediction){
    this.text = prediction.getPrimaryText(null).toString()
}

@BindingAdapter("setCountry")
fun TextView.setCountry(prediction: AutocompletePrediction){
    this.text = prediction.getSecondaryText(null).toString()
}