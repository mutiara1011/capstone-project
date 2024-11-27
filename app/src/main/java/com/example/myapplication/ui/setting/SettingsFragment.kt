package com.example.myapplication.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
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
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

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

        // Handle language card click
        binding.language.setOnClickListener {
            showLanguageDialog()
        }

        // Handle theme card click
        binding.theme.setOnClickListener {
            showThemeDialog()
        }

        return binding.root
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Bahasa Indonesia", "Español", "Français")
        val sharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentLanguage = sharedPreferences.getString("language", "English")

        // Inflate custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_language, null)
        val listView = dialogView.findViewById<ListView>(R.id.language_list)

        // Set up adapter for ListView
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_single_choice,
            languages
        )
        listView.adapter = adapter

        // Pre-select the current language
        val currentIndex = languages.indexOf(currentLanguage)
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.setItemChecked(currentIndex, true)

        // Build AlertDialog with custom layout
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true) // Ensure the dialog can be dismissed by clicking outside
            .create()

        // Handle item clicks
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage = languages[position]
            Toast.makeText(
                requireContext(),
                "Bahasa dipilih: $selectedLanguage",
                Toast.LENGTH_SHORT
            ).show()

            // Save selected language
            sharedPreferences.edit().putString("language", selectedLanguage).apply()

            dialog.dismiss()
        }

        // Handle close button (X)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()  // Dismiss the dialog when the close button (X) is clicked
        }

        dialog.show()
    }



    private fun showThemeDialog() {
        val sharedPreferences =
            requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentTheme = sharedPreferences.getString("theme", "Light Mode")

        // Inflate custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_theme, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.theme_group)

        // Pre-select the current theme
        if (currentTheme == "Dark Mode") {
            radioGroup.check(R.id.dark_mode)
        } else {
            radioGroup.check(R.id.light_mode)
        }

        // Build AlertDialog with custom layout
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                val selectedTheme = if (radioGroup.checkedRadioButtonId == R.id.dark_mode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "Dark Mode"
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "Light Mode"
                }

                // Save selected theme
                sharedPreferences.edit().putString("theme", selectedTheme).apply()

                Toast.makeText(requireContext(), "Tema dipilih: $selectedTheme", Toast.LENGTH_SHORT).show()
            }
            .create()

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
