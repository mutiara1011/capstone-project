package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.userFragment, R.id.settingsFragment, R.id.profileFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.welcomeFragment, R.id.loginFragment, R.id.signupFragment -> {
                    supportActionBar?.hide()
                    binding.navView.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.show()
                    binding.navView.visibility = View.VISIBLE

                    if (destination.id == R.id.homeFragment) {
                        supportActionBar?.setDisplayShowTitleEnabled(false)
                        supportActionBar?.setLogo(R.drawable.logo)
                    } else {
                        supportActionBar?.setDisplayShowTitleEnabled(false)
                        supportActionBar?.setLogo(null)
                        supportActionBar?.title = destination.label
                    }
                }
            }
        }
    }
}