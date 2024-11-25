package com.example.myapplication.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentUserBinding
import com.example.myapplication.ui.home.HomeViewModel

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inisialisasi ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        // Binding layout
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observasi LiveData dari ViewModel dan update UI
        userViewModel.aqiTitle.observe(viewLifecycleOwner) {
            binding.aqi1.text = it // Mengatur teks untuk judul AQI
        }

        userViewModel.recommendationTitle.observe(viewLifecycleOwner) {
            binding.rekomendasi.text = it // Mengatur teks judul rekomendasi
        }

        userViewModel.recommendationDescription.observe(viewLifecycleOwner) {
            binding.deskripsiRekomendasi.text = it // Mengatur teks deskripsi rekomendasi
        }

        userViewModel.recommendationTitle2.observe(viewLifecycleOwner) {
            binding.rekomendasi2.text = it // Mengatur teks judul rekomendasi
        }

        userViewModel.recommendationDescription2.observe(viewLifecycleOwner) {
            binding.deskripsiRekomendasi2.text = it // Mengatur teks deskripsi rekomendasi
        }

        userViewModel.aqiTitle2.observe(viewLifecycleOwner) {
            binding.aqi2.text = it // Mengatur teks untuk judul AQI
        }

        userViewModel.pollutants.observe(viewLifecycleOwner) { pollutants ->
            val highestPollutant = pollutants.maxByOrNull { it.value.index.toIntOrNull() ?: 0 }

            // Update untuk polutan dengan indeks AQI tertinggi
            highestPollutant?.let {
                binding.aqiIndex.text = it.value.index
                binding.description1.text = it.value.getAQIDescription()
                binding.description2.text = "Polutan Utama: ${it.value.title} (${it.value.details})"
                homeViewModel.updateHighestPollutant(it.value)
            }

            // Pembaruan UI untuk setiap polutan
            pollutants.forEach { (key, data) ->
                updatePollutantUI(key, data)
            }
        }

        return root
    }

    private fun updatePollutantUI(key: String, data: PollutantData?) {
        data?.let {
            val indexText = it.index
            val descriptionText = it.getAQIDescription()
            when (key) {
                "O3" -> {
                    binding.indexO3.text = indexText
                    binding.descriptionO3.text = descriptionText
                    binding.descriptionO32.text = it.title
                    binding.descriptionO33.text = it.details
                }
                "CO" -> {
                    binding.indexCo.text = indexText
                    binding.descriptionCo.text = descriptionText
                    binding.descriptionCo2.text = it.title
                    binding.descriptionCo3.text = it.details
                }
                "NO2" -> {
                    binding.indexNo2.text = indexText
                    binding.descriptionNo2.text = descriptionText
                    binding.descriptionNo22.text = it.title
                    binding.descriptionNo23.text = it.details
                }
                "PM10" -> {
                    binding.indexPm10.text = indexText
                    binding.descriptionPm10.text = descriptionText
                    binding.descriptionPm102.text = it.title
                    binding.descriptionPm103.text = it.details
                }
                "PM2.5" -> {
                    binding.indexPm25.text = indexText
                    binding.descriptionPm25.text = descriptionText
                    binding.descriptionPm252.text = it.title
                    binding.descriptionPm253.text = it.details
                }
                "SO2" -> {
                    binding.indexSo2.text = indexText
                    binding.descriptionSo2.text = descriptionText
                    binding.descriptionSo22.text = it.title
                    binding.descriptionSo23.text = it.details
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
