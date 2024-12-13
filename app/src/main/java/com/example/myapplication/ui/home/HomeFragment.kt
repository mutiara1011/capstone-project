package com.example.myapplication.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.R
import com.example.myapplication.data.remote.response.DailyDataItem
import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import com.example.myapplication.data.remote.response.acc.pref.dataStore
import com.example.myapplication.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        lifecycleScope.launch {
            val user = userPreference.getSession().first()
            if (!user.isLogin) {
                findNavController().navigate(R.id.action_homeFragment_to_welcomeFragment)
            }
        }

        scheduleAqiNotificationWorker()

        observePollutants()
        observeWeatherData()

        binding.textAqi.setOnClickListener { navigateToUserFragment() }
        binding.aqi.setOnClickListener { navigateToUserFragment() }
        binding.textDescription.setOnClickListener { navigateToUserFragment() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireContext().getSharedPreferences("AQINotificationPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isNotificationSent", false).apply()
    }

    private fun scheduleAqiNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<AQINotificationWorker>(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    private fun observeLoadingState() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun navigateToUserFragment() {
        findNavController().navigate(
            R.id.action_homeFragment_to_userFragment,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        observeLoadingState()

        homeViewModel.time.observe(viewLifecycleOwner) { currentTime ->
            binding.textTime.text = currentTime
        }

        homeViewModel.itemList.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        observeViewModel()

    }

    private fun observePollutants() {
        homeViewModel.aqiIndex.observe(viewLifecycleOwner) { aqiIndex ->
            if (aqiIndex != null) {
                binding.textAqi.text = "$aqiIndex"
                binding.aqi.text = getString(R.string.aqi_label)

                val aqiDescription = getAQIDescription(aqiIndex)
                binding.textDescription.text = aqiDescription

                val sharedPreferences = requireContext().getSharedPreferences("AQIData", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("aqi_index", aqiIndex)
                editor.apply()
            } else {
                binding.textAqi.text = getString(R.string.aqi_not_available)
                binding.textDescription.text = "-"
            }
        }
    }

    private fun getAQIDescription(aqiIndex: Int): String {
        return when (aqiIndex) {
            in 0..50 -> getString(R.string.aqi_good)
            in 51..100 -> getString(R.string.aqi_moderate)
            in 101..150 -> getString(R.string.aqi_sensitive_unhealthy)
            in 151..200 -> getString(R.string.aqi_unhealthy)
            in 201..300 -> getString(R.string.aqi_very_unhealthy)
            in 301..500 -> getString(R.string.aqi_hazardous)
            else -> getString(R.string.aqi_very_hazardous)
        }
    }

    private fun observeWeatherData() {
        homeViewModel.location.observe(viewLifecycleOwner) { binding.textLocation.text = it }
        homeViewModel.date.observe(viewLifecycleOwner) { binding.textDate.text = it }
        homeViewModel.time.observe(viewLifecycleOwner) { binding.textTime.text = it }

        homeViewModel.aqi.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.tvDegree.text = getString(R.string.degree_format, data.degree ?: "N/A")
                binding.tvWindSpeed.text = getString(R.string.wind_speed_format, data.wind ?: "N/A")
                binding.tvHumidity.text = getString(R.string.humidity_format, data.humidity ?: "N/A")

                val weatherCondition = data.degreeImg ?: ""
                val (iconResource, conditionName) = getWeatherIcon(weatherCondition)
                binding.ivWeatherIcon.setImageResource(iconResource)
                binding.tvWeather.text = getString(conditionName)
            } else {
                resetWeatherUI()
            }
        }
    }

    private fun getWeatherIcon(condition: String): Pair<Int, Int> {
        return when (condition) {
            "Rainy" -> Pair(R.drawable.rainy, R.string.rainy)
            "Stormy" -> Pair(R.drawable.stormy, R.string.stormy)
            "Sunny" -> Pair(R.drawable.sunny, R.string.sunny)
            "Foggy" -> Pair(R.drawable.foggy, R.string.foggy)
            "Cloudy" -> Pair(R.drawable.cloudy, R.string.cloudy)
            "Partly Cloudy" -> Pair(R.drawable.partly_cloudy, R.string.partly_cloudy)
            "Mostly Cloudy" -> Pair(R.drawable.mostly_cloudy, R.string.mostly_cloudy)
            "Full Moon" -> Pair(R.drawable.full_moon, R.string.full_moon)
            "Partly Cloudy Moon" -> Pair(R.drawable.partly_cloudy_moon, R.string.partly_cloudy_moon)
            "Mostly Cloudy Moon" -> Pair(R.drawable.mostly_cloudy_moon, R.string.mostly_cloudy_moon)
            else -> Pair(R.drawable.ic_default_weather, R.string.unknown)
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

        binding.progressRecyclerViewHour.visibility = View.VISIBLE

        binding.recyclerViewHour.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = HomeAdapter()
        }

        binding.progressRecycleViewDay.visibility = View.VISIBLE
        binding.recyclerViewDay.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = HomeAdapter()
        }
    }

    private fun observeViewModel() {
        homeViewModel.itemList.observe(viewLifecycleOwner) { items ->

            val hourlyPredict = items.filterIsInstance<ItemType.HourItem>()
            Log.d("HomeFragment", "Items: $items")
            val dailyPredict = items.filterIsInstance<ItemType.DayItem>()

            val adapterHour = binding.recyclerViewHour.adapter as HomeAdapter
            adapterHour.submitList(hourlyPredict)

            val adapterDay = binding.recyclerViewDay.adapter as HomeAdapter
            adapterDay.submitList(dailyPredict)

            binding.progressRecyclerViewHour.visibility = View.GONE
            binding.progressRecycleViewDay.visibility = View.GONE

            binding.recyclerViewHour.visibility = View.VISIBLE
            binding.recyclerViewDay.visibility = View.VISIBLE
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