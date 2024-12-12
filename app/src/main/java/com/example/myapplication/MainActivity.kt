package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefs: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        sharedPrefs = newBase.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val language = sharedPrefs.getString("language", Locale.getDefault().language) ?: "en"
        val locale = Locale(language)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applySavedTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.userFragment, R.id.settingsFragment, R.id.profileFragment)
        )
        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.welcomeFragment, R.id.loginFragment, R.id.signupFragment -> {
                    binding.navView.visibility = View.GONE
                }
                else -> {
                    binding.navView.visibility = View.VISIBLE

                }
            }
        }
    }
    private fun applySavedTheme() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("settings", Context.MODE_PRIVATE)
        val currentTheme = sharedPreferences.getString("theme", "Light Mode")

        when (currentTheme) {
            "Dark Mode" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}