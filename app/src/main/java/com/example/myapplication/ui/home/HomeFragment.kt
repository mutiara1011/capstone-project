package com.example.myapplication.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

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

        scheduleAqiNotificationWorker()

        observePollutants()
        observeWeatherData()

        lineChart = binding.lineChart
        setupLineChart()

        binding.textAqi.setOnClickListener { navigateToUserFragment() }
        binding.aqi.setOnClickListener { navigateToUserFragment() }
        binding.textDescription.setOnClickListener { navigateToUserFragment() }

        return binding.root
    }

    private fun observeLoadingState() {
        homeViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeErrorState() {
        homeViewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        homeViewModel.fetchData() // Panggil ulang data
                    }
                    .show()
            }
        }
    }


    private fun navigateToUserFragment() {
        findNavController().navigate(
            R.id.action_homeFragment_to_userFragment,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true) // Hapus HomeFragment dari back stack
                .build()
        )
    }


    private fun scheduleAqiNotificationWorker() {
        // Menjadwalkan Worker untuk berjalan setiap jam
        val workRequest = PeriodicWorkRequestBuilder<AQINotificationWorker>(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
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

        observeLoadingState()
        observeErrorState()

        homeViewModel.fetchData()

        homeViewModel.time.observe(viewLifecycleOwner) { currentTime ->
            binding.textTime.text = currentTime
        }

        setupRecyclerView()
        observePredict()
        homeViewModel.getPredict()

    }

    private fun observePollutants() {
        homeViewModel.aqiIndeks.observe(viewLifecycleOwner) { aqiIndex ->
            if (aqiIndex != null) {
                binding.textAqi.text = "$aqiIndex"
                binding.aqi.text = "AQI"

                val sharedPreferences = requireContext().getSharedPreferences("AQIData", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("aqi_index", aqiIndex) // Simpan AQI
                editor.apply()
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
                binding.tvDegree.text = getString(R.string.degree_format, data.degree ?: "N/A")
                binding.tvWindSpeed.text = getString(R.string.wind_speed_format, data.wind ?: "N/A")
                binding.tvHumidity.text = getString(R.string.humidity_format, data.humidity ?: "N/A")
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


        homeViewModel.aqiPredict.observe(viewLifecycleOwner) { aqiPredict ->
            if (aqiPredict != null) {
                binding.recyclerViewHour.adapter = adapter
            } else {
                Toast.makeText(context, "Data not available", Toast.LENGTH_SHORT).show()
            }
        }
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