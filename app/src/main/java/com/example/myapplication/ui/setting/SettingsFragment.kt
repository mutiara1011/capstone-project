package com.example.myapplication.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        settingsViewModel.language.observe(viewLifecycleOwner) {
            binding.language.text = it
        }

        settingsViewModel.theme.observe(viewLifecycleOwner) {
            binding.theme.text = it
        }

        settingsViewModel.notifications.observe(viewLifecycleOwner) {
            binding.notifications.text = it
        }

        binding.language.setOnClickListener {
            showLanguageDialog()
        }

        binding.theme.setOnClickListener {
            showThemeDialog()
        }

        return binding.root
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Bahasa Indonesia")
        val sharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("language", "English")

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_language, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.language_radio_group)
        val okButton = dialogView.findViewById<Button>(R.id.ok_button)

        languages.forEach { language ->
            val radioButton = RadioButton(requireContext()).apply {
                text = language
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.white, null))
            }
            radioGroup.addView(radioButton)

            if (language == currentLanguage) {
                radioButton.isChecked = true
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        okButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = dialogView.findViewById<RadioButton>(selectedRadioButtonId)
                val selectedLanguage = selectedRadioButton.text.toString()

                Toast.makeText(
                    requireContext(),
                    "Bahasa dipilih: $selectedLanguage",
                    Toast.LENGTH_SHORT
                ).show()

                sharedPreferences.edit().putString("language", selectedLanguage).apply()
                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Silakan pilih bahasa terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val closeButton = dialogView.findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showThemeDialog() {
        val sharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentTheme = sharedPreferences.getString("theme", "Light Mode")

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_theme, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.theme_group)
        val okButton = dialogView.findViewById<Button>(R.id.ok_button)

        if (currentTheme == "Dark Mode") {
            radioGroup.check(R.id.dark_mode)
        } else {
            radioGroup.check(R.id.light_mode)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        okButton.setOnClickListener {
            val selectedTheme = if (radioGroup.checkedRadioButtonId == R.id.dark_mode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "Dark Mode"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Light Mode"
            }

            sharedPreferences.edit().putString("theme", selectedTheme).apply()

            Toast.makeText(requireContext(), "Tema dipilih: $selectedTheme", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        val closeButton = dialogView.findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
