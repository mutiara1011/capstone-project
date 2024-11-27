package com.example.myapplication.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var pollutantAdapter: PollutantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding = FragmentUserBinding.inflate(inflater, container, false)

        pollutantAdapter = PollutantAdapter(emptyList())
        binding.recyclerPollutants.apply {
            adapter = pollutantAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        userViewModel.aqiTitle.observe(viewLifecycleOwner) {
            binding.aqi1.text = it
        }

        userViewModel.recommendationTitle.observe(viewLifecycleOwner) {
            binding.rekomendasi.text = it
        }

        userViewModel.recommendationDescription.observe(viewLifecycleOwner) {
            binding.deskripsiRekomendasi.text = it
        }

        userViewModel.recommendationTitle2.observe(viewLifecycleOwner) {
            binding.rekomendasi2.text = it
        }

        userViewModel.recommendationDescription2.observe(viewLifecycleOwner) {
            binding.deskripsiRekomendasi2.text = it
        }

        userViewModel.aqiTitle2.observe(viewLifecycleOwner) {
            binding.aqi2.text = it
        }

        observeViewModel()

        userViewModel.fetchPollutants()

        return binding.root
    }

    private fun observeViewModel() {
        userViewModel.pollutants.observe(viewLifecycleOwner) { pollutants ->
            val highestPollutant = pollutants.maxByOrNull { it.index.toIntOrNull() ?: 0 }

            highestPollutant?.let {
                binding.aqiIndex.text = it.index
                binding.description1.text = it.description
                binding.description2.text = getString(R.string.pollutant, it.title, it.details)
            }

            pollutantAdapter = PollutantAdapter(pollutants)
            binding.recyclerPollutants.adapter = pollutantAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
