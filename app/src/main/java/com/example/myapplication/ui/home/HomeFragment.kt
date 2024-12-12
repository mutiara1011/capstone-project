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
    private lateinit var lineChart: LineChart
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
        homeViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
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

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setLogo(R.drawable.logo)
            setDisplayUseLogoEnabled(true)
        }

        observeLoadingState()

        homeViewModel.time.observe(viewLifecycleOwner) { currentTime ->
            binding.textTime.text = currentTime
        }

        setupRecyclerView()
        observePredict()
        homeViewModel.getPredict()

        lineChart = view.findViewById(R.id.lineChart)
        observeDaily()
        homeViewModel.getDaily()

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

    private fun observePredict() {
        homeViewModel.aqiPredict.observe(viewLifecycleOwner) { aqiPredict ->
            if (aqiPredict != null) {
                adapter.submitList(aqiPredict)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter()
        binding.recyclerViewHour.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapter
        }
        binding.progressRecyclerViewHour.visibility = View.VISIBLE

        homeViewModel.aqiPredict.observe(viewLifecycleOwner) { aqiPredict ->
            if (aqiPredict != null) {
                binding.recyclerViewHour.adapter = adapter
                binding.progressRecyclerViewHour.visibility = View.GONE
            } else {
                Toast.makeText(context, "Data not available", Toast.LENGTH_SHORT).show()
                binding.progressRecyclerViewHour.visibility = View.GONE
            }
        }
    }

    private fun observeDaily() {
        homeViewModel.aqiDaily.observe(viewLifecycleOwner) { aqiDaily ->
            if (aqiDaily != null) {
                Log.d("AQI Data", "Data received: $aqiDaily")
                setupLineChart(aqiDaily)
            } else {
                Log.d("AQI Data", "No data available")
            }
        }
    }

    private fun setupLineChart(dailyData: List<DailyDataItem>) {
        val entries = ArrayList<Entry>()

        // Ambil hanya 7 data terakhir atau sesuaikan sesuai kebutuhan
        val dataToDisplay = dailyData.takeLast(7)

        for (i in dataToDisplay.indices) {
            val date = dataToDisplay[i].date
            val aqiValue = dataToDisplay[i].detail.firstOrNull()?.aqiIndex ?: 0

            if (aqiValue > 0) {
                entries.add(Entry(i.toFloat(), aqiValue.toFloat()))
            }
        }

        val dataSet = LineDataSet(entries, "AQI Daily")
        dataSet.apply {
            color = resources.getColor(R.color.teal_200, null)
            setCircleColor(resources.getColor(R.color.teal_200, null))
            lineWidth = 3f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = resources.getColor(R.color.teal_200, null)
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Konfigurasi X-Axis
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawLabels(true)
            // Menampilkan label hanya untuk 7 data terakhir
            valueFormatter = IndexAxisValueFormatter(dataToDisplay.map { it.date })
            labelRotationAngle = 0f
            // Membatasi jumlah label X-axis hanya 7
            setLabelCount(7, true)
        }

        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.isEnabled = true

        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.text = "Weekly AQI Forecast"

        lineChart.animateX(1500, Easing.EaseInOutQuad)

        val markerView = CustomMarkerView(requireContext(), R.layout.marker_view)
        lineChart.marker = markerView

        lineChart.invalidate()
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