package com.example.myapplication.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Binding layout
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observasi LiveData dari ViewModel dan update UI
        userViewModel.aqiTitle.observe(viewLifecycleOwner) {
            binding.aqi1.text = it // Mengatur teks untuk judul AQI
        }

        userViewModel.aqiIndex.observe(viewLifecycleOwner) {
            binding.aqiIndex.text = it // Mengatur teks untuk nilai AQI
        }

        userViewModel.description1.observe(viewLifecycleOwner) {
            binding.description1.text = it // Mengatur teks deskripsi 1
        }

        userViewModel.description2.observe(viewLifecycleOwner) {
            binding.description2.text = it // Mengatur teks deskripsi 2
        }

        userViewModel.recommendationTitle.observe(viewLifecycleOwner) {
            binding.rekomendasi.text = it // Mengatur teks judul rekomendasi
        }

        userViewModel.recommendationDescription.observe(viewLifecycleOwner) {
            binding.deskripsiRekomendasi.text = it // Mengatur teks deskripsi rekomendasi
        }

        userViewModel.aqiTitle2.observe(viewLifecycleOwner) {
            binding.aqi2.text = it // Mengatur teks untuk judul AQI
        }

        // Data AQI O3
        userViewModel.indexO3.observe(viewLifecycleOwner) {
            binding.indexO3.text = it
        }

        userViewModel.descriptionO3.observe(viewLifecycleOwner) {
            binding.descriptionO3.text = it
        }

        userViewModel.titleIndeksO3.observe(viewLifecycleOwner) {
            binding.descriptionO32.text = it
        }

        userViewModel.detailsO3.observe(viewLifecycleOwner) {
            binding.descriptionO33.text = it
        }

        // Data AQI CO
        userViewModel.indexCO.observe(viewLifecycleOwner) {
            binding.indexCo.text = it
        }

        userViewModel.descriptionCO.observe(viewLifecycleOwner) {
            binding.descriptionCo.text = it
        }

        userViewModel.titleIndeksCO.observe(viewLifecycleOwner) {
            binding.descriptionCo2.text = it
        }

        userViewModel.detailsCO.observe(viewLifecycleOwner) {
            binding.descriptionCo3.text = it
        }

        // Data AQI NO2
        userViewModel.indexNO2.observe(viewLifecycleOwner) {
            binding.indexNo2.text = it
        }

        userViewModel.descriptionNO2.observe(viewLifecycleOwner) {
            binding.descriptionNo2.text = it
        }

        userViewModel.titleIndeksNO2.observe(viewLifecycleOwner) {
            binding.descriptionNo22.text = it
        }

        userViewModel.detailsNO2.observe(viewLifecycleOwner) {
            binding.descriptionNo23.text = it
        }

        // Data AQI PM10
        userViewModel.indexPM10.observe(viewLifecycleOwner) {
            binding.indexPm10.text = it
        }

        userViewModel.descriptionPM10.observe(viewLifecycleOwner) {
            binding.descriptionPm10.text = it
        }

        userViewModel.titleIndeksPM10.observe(viewLifecycleOwner) {
            binding.descriptionPm102.text = it
        }

        userViewModel.detailsPM10.observe(viewLifecycleOwner) {
            binding.descriptionPm103.text = it
        }

        // Data AQI PM2.5
        userViewModel.indexPM25.observe(viewLifecycleOwner) {
            binding.indexPm25.text = it
        }

        userViewModel.descriptionPM25.observe(viewLifecycleOwner) {
            binding.descriptionPm25.text = it
        }

        userViewModel.titleIndeksPM25.observe(viewLifecycleOwner) {
            binding.descriptionPm252.text = it
        }

        userViewModel.detailsPM25.observe(viewLifecycleOwner) {
            binding.descriptionPm253.text = it
        }

        // Data AQI SO2
        userViewModel.indexSO2.observe(viewLifecycleOwner) {
            binding.indexSo2.text = it
        }

        userViewModel.descriptionSO2.observe(viewLifecycleOwner) {
            binding.descriptionSo2.text = it
        }

        userViewModel.titleIndeksSO2.observe(viewLifecycleOwner) {
            binding.descriptionSo22.text = it
        }

        userViewModel.detailsSO2.observe(viewLifecycleOwner) {
            binding.descriptionSo23.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
