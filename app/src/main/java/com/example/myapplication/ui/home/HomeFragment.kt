package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        observePollutants()
        observeWeatherData()

        lineChart = binding.lineChart
        setupLineChart()

        return binding.root
    }

    private fun setupLineChart() {
        val entries = ArrayList<Entry>()

        // Data dummy untuk chart
        entries.apply {
            add(Entry(0f, 10f))
            add(Entry(1f, 15f))
            add(Entry(2f, 7f))
            add(Entry(3f, 20f))
            add(Entry(4f, 16f))
            add(Entry(5f, 25f))
            add(Entry(6f, 10f))
        }

        val dataSet = LineDataSet(entries, "AQI Forecast").apply {
            color = ContextCompat.getColor(requireContext(), R.color.teal_200)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
            lineWidth = 3f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.teal_200)
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Konfigurasi X-Axis
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawLabels(true)
            valueFormatter = IndexAxisValueFormatter(listOf("Today", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"))
            labelRotationAngle = 0f
        }

        // Nonaktifkan sumbu kanan
        lineChart.axisRight.isEnabled = false
        lineChart.axisLeft.isEnabled = true

        // Aktifkan fitur chart
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.text = "Weekly AQI Forecast"

        // Tambahkan animasi
        lineChart.animateX(1500, Easing.EaseInOutQuad)

        // Tambahkan MarkerView
        val markerView = CustomMarkerView(requireContext(), R.layout.marker_view)
        lineChart.marker = markerView

        // Refresh chart
        lineChart.invalidate()
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