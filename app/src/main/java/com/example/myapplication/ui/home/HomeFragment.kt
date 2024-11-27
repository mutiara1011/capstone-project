package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
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
            homeViewModel.updateHighestPollutant(highestPollutant?.value)
        }

// Observasi nilai tertinggi untuk UI
        homeViewModel.highestPollutant.observe(viewLifecycleOwner) { highest ->
            if (highest != null) {
                binding.textAqi.text = highest.index
                binding.textDescription.text = highest.getAQIDescription()
            } else {
                binding.textAqi.text = getString(R.string.aqi_not_available)
                binding.textDescription.text = getString(R.string.not_availabe)
            }
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

        homeViewModel.aqi.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                Log.d("HomeFragment", "Received AQI data: $data")
                binding.tvDegree.text = "${data.degree}°" // Menampilkan degree
                binding.tvWindSpeed.text = "${data.wind} km/h" // Menampilkan kecepatan angin
                binding.tvHumidity.text = "${data.humidity}%" // Menampilkan kelembapan

                // Tentukan ikon berdasarkan kondisi cuaca
                val weatherCondition = data.degreeImg?: "Sunny"  // default "Sunny" jika null

                // Menentukan resource ikon berdasarkan kondisi cuaca
                val iconResource = when (weatherCondition) {
                    "Rainy" -> R.drawable.rainy
                    "Stormy" -> R.drawable.stormy
                    "Sunny" -> R.drawable.sunny
                    "Foggy" -> R.drawable.foggy
                    "Cloudy" -> R.drawable.cloudy
                    "Partly Cloudy" -> R.drawable.partly_cloudy
                    "Mostly Cloudy" -> R.drawable.mostly_cloudy
                    "Full Moon" -> R.drawable.full_moon
                    "Partly Cloudy Moon" -> R.drawable.partly_cloudy_moon
                    "Mostly Cloudy Moon" -> R.drawable.mostly_cloudy_moon


                    else -> R.drawable.ic_default_weather // Default icon jika kondisi cuaca tidak dikenal
                }

                // Mengubah ikon cuaca di UI
                binding.ivWeatherIcon.setImageResource(iconResource)
            } else {
                Log.d("HomeFragment", "No AQI data available.")
                binding.tvDegree.text = "-"
                binding.tvWindSpeed.text = "-"
                binding.tvHumidity.text = "-"

                // Menampilkan ikon default jika tidak ada data
                binding.ivWeatherIcon.setImageResource(R.drawable.ic_default_weather)
            }
        }
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
