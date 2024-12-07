package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.remote.response.DataItem
import com.example.myapplication.data.remote.response.MainPolutant
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        observePollutants()

        observeWeatherData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setLogo(R.drawable.logo)
            setDisplayUseLogoEnabled(true)
        }
        setupRecyclerView()
        observePredict()
        homeViewModel.getPredict()
    }

    private fun observePollutants() {
        homeViewModel.aqiIndeks.observe(viewLifecycleOwner) { aqiIndex ->
            if (aqiIndex != null) {
                binding.textAqi.text = "$aqiIndex"
            } else {
                binding.textAqi.text = getString(R.string.aqi_not_available)
            }
        }
        homeViewModel.aqiDescription.observe(viewLifecycleOwner) { description ->
            if (!description.isNullOrEmpty()) {
                binding.textDescription.text = description
            } else {
                binding.textDescription.text = "-"
            }
        }
    }

    private fun observeWeatherData() {
        homeViewModel.location.observe(viewLifecycleOwner) { binding.textLocation.text = it }
        homeViewModel.date.observe(viewLifecycleOwner) { binding.textDate.text = it }
        homeViewModel.time.observe(viewLifecycleOwner) { binding.textTime.text = it }
        homeViewModel.aqi.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.tvDegree.text = "${data.degree}°"
                binding.tvWindSpeed.text = "${data.wind} km/h"
                binding.tvHumidity.text = "${data.humidity}%"
                val iconResource = getWeatherIcon(data.degreeImg ?: "Sunny")
                binding.ivWeatherIcon.setImageResource(iconResource)
            } else {
                resetWeatherUI()
            }
        }
    }

    private fun getWeatherIcon(condition: String): Int {
        return when (condition) {
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
            else -> R.drawable.ic_default_weather
        }
    }

    private fun resetWeatherUI() {
        binding.tvDegree.text = "-"
        binding.tvWindSpeed.text = "-"
        binding.tvHumidity.text = "-"
        binding.ivWeatherIcon.setImageResource(R.drawable.ic_default_weather)
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter()
        binding.recyclerViewHour.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapter
        }
        Log.d("HomeFragment", "RecyclerView is set up with HomeAdapter")

        binding.recyclerViewHour.adapter = adapter
    }



    private fun observePredict() {
        homeViewModel.aqiPredict.observe(viewLifecycleOwner) { aqiPredict ->
            if (aqiPredict != null) {
                adapter.submitList(aqiPredict) // Kirimkan daftar event ke adapter
            }
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