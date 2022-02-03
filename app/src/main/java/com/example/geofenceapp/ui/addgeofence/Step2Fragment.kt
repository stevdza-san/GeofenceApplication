package com.example.geofenceapp.ui.addgeofence

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geofenceapp.R
import com.example.geofenceapp.adapters.PredictionsAdapter
import com.example.geofenceapp.databinding.FragmentStep2Binding
import com.example.geofenceapp.util.ExtensionFunctions.hide
import com.example.geofenceapp.util.NetworkListener
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.example.geofenceapp.viewmodels.Step2ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

class Step2Fragment : Fragment() {

    private var _binding: FragmentStep2Binding? = null
    private val binding get() = _binding!!

    private val predictionsAdapter by lazy { PredictionsAdapter() }

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val step2ViewModel: Step2ViewModel by viewModels()

    private lateinit var placesClient: PlacesClient

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep2Binding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel
        binding.step2ViewModel = step2ViewModel
        binding.lifecycleOwner = this

        checkInternetConnection()

        binding.predictionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.predictionsRecyclerView.adapter = predictionsAdapter

        binding.geofenceLocationEt.doOnTextChanged { text, _, _, _ ->
            handleNextButton(text)
            getPlaces(text)
        }

        binding.step2Back.setOnClickListener {
            findNavController().navigate(R.id.action_step2Fragment_to_step1Fragment)
        }

        binding.step2Next.setOnClickListener {
            findNavController().navigate(R.id.action_step2Fragment_to_step3Fragment)
        }

        subscribeToObservers()

        return binding.root
    }

    private fun handleNextButton(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            step2ViewModel.enableNextButton(false)
        }
    }

    private fun subscribeToObservers() {
        lifecycleScope.launch {
            predictionsAdapter.placeId.collectLatest { placeId ->
                if (placeId.isNotEmpty()) {
                    onCitySelected(placeId)
                }
            }
        }
    }

    private fun onCitySelected(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        )
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                sharedViewModel.geoLatLng = response.place.latLng!!
                sharedViewModel.geoLocationName = response.place.name!!
                sharedViewModel.geoCitySelected = true
                binding.geofenceLocationEt.setText(sharedViewModel.geoLocationName)
                binding.geofenceLocationEt.setSelection(sharedViewModel.geoLocationName.length)
                binding.predictionsRecyclerView.hide()
                step2ViewModel.enableNextButton(true)
            }
            .addOnFailureListener { exception ->
                Log.e("Step2Fragment", exception.message.toString())
            }
    }

    private fun getPlaces(text: CharSequence?) {
        if (sharedViewModel.checkDeviceLocationSettings(requireContext())) {
            lifecycleScope.launch {
                if (text.isNullOrEmpty()) {
                    predictionsAdapter.setData(emptyList())
                } else {
                    val token = AutocompleteSessionToken.newInstance()

                    val request =
                        FindAutocompletePredictionsRequest.builder()
                            .setCountries(sharedViewModel.geoCountryCode)
                            .setTypeFilter(TypeFilter.CITIES)
                            .setSessionToken(token)
                            .setQuery(text.toString())
                            .build()
                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            predictionsAdapter.setData(response.autocompletePredictions)
                            binding.predictionsRecyclerView.scheduleLayoutAnimation()
                        }
                        .addOnFailureListener { exception: Exception? ->
                            if (exception is ApiException) {
                                Log.e("Step2Fragment", exception.statusCode.toString())
                            }
                        }
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please Enable Location Settings.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkInternetConnection() {
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { online ->
                    Log.d("Internet", online.toString())
                    step2ViewModel.setInternetAvailability(online)
                    if (online && sharedViewModel.geoCitySelected) {
                        step2ViewModel.enableNextButton(true)
                    } else {
                        step2ViewModel.enableNextButton(false)
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}














