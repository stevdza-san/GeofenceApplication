package com.example.geofenceapp.ui.addgeofence

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.geofenceapp.R
import com.example.geofenceapp.databinding.FragmentStep3Binding
import com.example.geofenceapp.viewmodels.SharedViewModel

class Step3Fragment : Fragment() {

    private var _binding: FragmentStep3Binding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep3Binding.inflate(inflater, container, false)
        binding.sharedViewModel = sharedViewModel

        binding.step3Back.setOnClickListener {
            findNavController().navigate(R.id.action_step3Fragment_to_step2Fragment)
        }

        binding.step3Done.setOnClickListener {
            sharedViewModel.geoRadius = binding.slider.value
            sharedViewModel.geofenceReady = true
            findNavController().navigate(R.id.action_step3Fragment_to_mapsFragment)
            Log.d("Step3Fragment", sharedViewModel.geoRadius.toString())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}