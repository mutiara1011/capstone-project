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

        userViewModel.fetchData()
        userViewModel.fetchPollutants()

        pollutantAdapter = PollutantAdapter(listOf())
        binding?.recyclerPollutants?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pollutantAdapter
        }

        observeViewModel()

        userViewModel.fetchPollutants()

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
                        // Retry fetch data jika ada error
                        userViewModel.fetchData()
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
                    val (activityRecommendations, recommendations, description) = userViewModel.getAQIRecommendation(aqi)
                    binding?.activityRecommendations?.text = activityRecommendations
                    binding?.recommendations?.text = recommendations
                    if (_binding != null && description.isNotEmpty()) {
                        setupViewPager(description.split("\n").filter { it.isNotBlank() })
                    }                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollHandler?.removeCallbacks(autoScrollRunnable!!)
        autoScrollHandler = null
        _binding = null
    }
}
