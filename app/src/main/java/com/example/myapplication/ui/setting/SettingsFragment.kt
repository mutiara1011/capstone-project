package com.example.myapplication.ui.setting

import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSettingsBinding
import java.util.Locale

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.language.setOnClickListener {
            showLanguageDialog()
        }

        binding.theme.setOnClickListener {
            showThemeDialog()
        }

        return binding.root
    }

    @SuppressLint("StringFormatInvalid")
    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_indonesian)
        )
        val languageCodes = arrayOf("en", "id")
        val sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguageCode = sharedPreferences.getString("language", "en") ?: "en"

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_language, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.language_radio_group)
        val okButton = dialogView.findViewById<Button>(R.id.ok_button)

        languages.forEachIndexed { index, language ->
            val radioButton = RadioButton(requireContext()).apply {
                text = language
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            radioGroup.addView(radioButton)

            if (languageCodes[index] == currentLanguageCode) {
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
                val selectedLanguageCode = languageCodes[languages.indexOf(selectedLanguage)]

                Toast.makeText(
                    requireContext(),
                    getString(R.string.language_selected, selectedLanguage),
                    Toast.LENGTH_SHORT
                ).show()

                sharedPreferences.edit().apply {
                    putString("language", selectedLanguageCode)
                    apply()
                }

                updateLocale(selectedLanguageCode)
                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.language_select_prompt),
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

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        requireActivity().recreate()
    }


    @SuppressLint("StringFormatInvalid")
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

            Toast.makeText(requireContext(), getString(R.string.theme_selected, selectedTheme), Toast.LENGTH_SHORT).show()

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
