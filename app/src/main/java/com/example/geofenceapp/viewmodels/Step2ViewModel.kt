package com.example.geofenceapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Step2ViewModel: ViewModel() {

    private val _nextButtonEnabled = MutableLiveData(false)
    val nextButtonEnabled: LiveData<Boolean> get() = _nextButtonEnabled

    private val _internetAvailable = MutableLiveData(true)
    val internetAvailable: LiveData<Boolean> get() = _internetAvailable

    fun enableNextButton(enable: Boolean){
        _nextButtonEnabled.value = enable
    }

    fun setInternetAvailability(online: Boolean){
        _internetAvailable.value = online
    }

}