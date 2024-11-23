package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import androidx.appcompat.app.AppCompatActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val locationTextView: TextView = binding.textLocation
        homeViewModel.location.observe(viewLifecycleOwner) {
            locationTextView.text = it
        }

        val dateTextView: TextView = binding.textDate
        homeViewModel.date.observe(viewLifecycleOwner) {
            dateTextView.text = it
        }

        val timeTextView: TextView = binding.textTime
        homeViewModel.time.observe(viewLifecycleOwner) {
            timeTextView.text = it
        }

        val suhuTextView: TextView = binding.textSuhu
        homeViewModel.suhu.observe(viewLifecycleOwner) {
            suhuTextView.text = it
        }

        val anginTextView: TextView = binding.textAngin
        homeViewModel.angin.observe(viewLifecycleOwner) {
            anginTextView.text = it
        }

        val anginValueTextView: TextView = binding.textAnginValue
        homeViewModel.anginValue.observe(viewLifecycleOwner) {
            anginValueTextView.text = it
        }

        val kelembapanTextView: TextView = binding.textLembap
        homeViewModel.kelembapan.observe(viewLifecycleOwner) {
            kelembapanTextView.text = it
        }

        val kelembapanValueTextView: TextView = binding.textLembapValue
        homeViewModel.kelembapanValue.observe(viewLifecycleOwner) {
            kelembapanValueTextView.text = it
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set logo saat berada di HomeFragment
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)   // Sembunyikan teks judul
            setLogo(R.drawable.logo)            // Gunakan logo Anda
            setDisplayUseLogoEnabled(true)      // Tampilkan logo
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        // Kembalikan pengaturan toolbar ketika keluar dari HomeFragment
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)  // Tampilkan kembali teks judul
            setLogo(null)                     // Hapus logo
        }
    }
}
