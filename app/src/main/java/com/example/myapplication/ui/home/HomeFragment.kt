package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.user.UserViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Observasi polutan dengan nilai AQI tertinggi
        userViewModel.pollutants.observe(viewLifecycleOwner) { pollutants ->
            val highestPollutant = pollutants.maxByOrNull { it.value.index.toIntOrNull() ?: 0 }
            highestPollutant?.let {
                homeViewModel.updateHighestPollutant(it.value)
            } ?: homeViewModel.updateHighestPollutant(null)
        }

        // Observasi nilai tertinggi untuk UI
        homeViewModel.highestPollutant.observe(viewLifecycleOwner) {
            binding.textHome.text = it
        }

        // Observasi data cuaca dan lokasi
        observeWeatherData()

        return binding.root
    }

    private fun observeWeatherData() {
        // Observasi lokasi
        homeViewModel.location.observe(viewLifecycleOwner) { binding.textLocation.text = it }

        // Observasi tanggal
        homeViewModel.date.observe(viewLifecycleOwner) { binding.textDate.text = it }

        // Observasi waktu
        homeViewModel.time.observe(viewLifecycleOwner) { binding.textTime.text = it }

        // Observasi suhu
        homeViewModel.suhu.observe(viewLifecycleOwner) { binding.textSuhu.text = it }

        // Observasi angin
        homeViewModel.angin.observe(viewLifecycleOwner) { binding.textAngin.text = it }
        homeViewModel.anginValue.observe(viewLifecycleOwner) { binding.textAnginValue.text = it }

        // Observasi kelembapan
        homeViewModel.kelembapan.observe(viewLifecycleOwner) { binding.textLembap.text = it }
        homeViewModel.kelembapanValue.observe(viewLifecycleOwner) { binding.textLembapValue.text = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setLogo(R.drawable.logo)
            setDisplayUseLogoEnabled(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            setLogo(null)
        }
    }
}
