package com.example.myapplication.ui.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentUserBinding
import com.google.android.material.snackbar.Snackbar

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding

    private lateinit var userViewModel: UserViewModel
    private lateinit var pollutantAdapter: PollutantAdapter

    private lateinit var recommendationAdapter: RecommendationAdapter
    private var autoScrollHandler: Handler? = null
    private var autoScrollRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding = FragmentUserBinding.inflate(inflater, container, false)

        observeLoadingState()
        observeErrorState()

        userViewModel.fetchPollutants()

        pollutantAdapter = PollutantAdapter(listOf())
        binding?.recyclerPollutants?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pollutantAdapter
        }

        observeViewModel()

        return binding?.root!!
    }

    private fun observeLoadingState() {
        userViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeErrorState() {
        userViewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar.make(binding?.root ?: return@observe, errorMessage, Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        userViewModel.fetchPollutants()
                    }
                    .show()
            }
        }
    }

    private fun observeViewModel() {
        userViewModel.apply {
            qualityIndex.observe(viewLifecycleOwner) { binding?.qualityIndex?.text = it }
            textAqi.observe(viewLifecycleOwner) { binding?.textAqi?.text = it }

            aqiIndex.observe(viewLifecycleOwner) { aqi ->
                aqi?.let {
                    val (activityRecommendations, recommendations, description) = getAQIRecommendation(aqi)
                    binding?.activityRecommendations?.text = activityRecommendations
                    binding?.recommendations?.text = recommendations
                    if (_binding != null && description.isNotEmpty()) {
                        setupViewPager(description.split("\n").filter { it.isNotBlank() })
                    }
                }
            }

            pollutants.observe(viewLifecycleOwner) { pollutants ->
                pollutantAdapter = PollutantAdapter(pollutants)
                binding?.recyclerPollutants?.adapter = pollutantAdapter
                pollutants.maxByOrNull { it.index.toIntOrNull() ?: 0 }?.let { highestPollutant ->
                    binding?.aqiIndex?.text = highestPollutant.index
                    binding?.description?.text = highestPollutant.description
                    binding?.pollutantType?.text = getString(
                        R.string.pollutant,
                        highestPollutant.title,
                        highestPollutant.details
                    )
                }
            }
        }
    }

    private fun setupViewPager(recommendations: List<String>) {

        if (_binding == null) return

        recommendationAdapter = RecommendationAdapter(recommendations)
        binding?.viewPagerRecommendations?.adapter = recommendationAdapter

        autoScrollHandler = Handler(Looper.getMainLooper())
        autoScrollRunnable = object : Runnable {
            override fun run() {
                val currentItem = binding?.viewPagerRecommendations?.currentItem
                val nextItem = (currentItem?.plus(1))?.rem(recommendationAdapter.itemCount)
                if (nextItem != null) {
                    binding?.viewPagerRecommendations?.setCurrentItem(nextItem, true)
                }
                autoScrollHandler?.postDelayed(this, 3000)
            }
        }
        autoScrollHandler?.postDelayed(autoScrollRunnable!!, 3000)

        binding?.viewPagerRecommendations?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                autoScrollHandler?.removeCallbacks(autoScrollRunnable!!)
                autoScrollHandler?.postDelayed(autoScrollRunnable!!, 3000)
            }
        })
    }

    private fun getAQIRecommendation(aqiValue: Int): Triple<String, String, String> {
        val activityRecommendations = when (aqiValue) {
            in 0..50 -> getString(R.string.aqi_0_50)
            in 51..100 -> getString(R.string.aqi_51_100)
            in 101..150 -> getString(R.string.aqi_101_150)
            in 151..200 -> getString(R.string.aqi_151_200)
            in 201..300 -> getString(R.string.aqi_201_300)
            in 301..500 -> getString(R.string.aqi_301_500)
            else -> getString(R.string.aqi_invalid)
        }

        val healthRecommendations = when (aqiValue) {
            in 0..50 -> getString(R.string.health_risk_low)
            in 51..100 -> getString(R.string.health_risk_moderate)
            in 101..150 -> getString(R.string.health_risk_high)
            in 151..200 -> getString(R.string.health_risk_very_high)
            in 201..300 -> getString(R.string.health_risk_hazardous)
            in 301..500 -> getString(R.string.health_risk_hazardous)
            else -> getString(R.string.health_risk_invalid)
        }

        val description = when (aqiValue) {
            in 0..50 -> getString(R.string.description_0_50)
            in 51..100 -> getString(R.string.description_51_100)
            in 101..150 -> getString(R.string.description_101_150)
            in 151..200 -> getString(R.string.description_151_200)
            in 201..300 -> getString(R.string.description_201_300)
            in 301..500 -> getString(R.string.description_301_500)
            else -> getString(R.string.description_invalid)
        }

        return Triple(activityRecommendations, healthRecommendations, description)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollHandler?.removeCallbacks(autoScrollRunnable!!)
        autoScrollHandler = null
        _binding = null
    }
}
