package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan view binding untuk mengatur tampilan
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)

        // Setup NavController untuk navigasi antar fragment
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_user, R.id.navigation_settings, R.id.navigation_profile)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Mengatur bottom navigation dengan NavController
        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        // Mengganti toolbar dengan logo saat berada di HomeFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_home) {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                supportActionBar?.setLogo(R.drawable.logo)  // Ganti dengan logo Anda
            } else {
                supportActionBar?.setDisplayShowTitleEnabled(false)
                supportActionBar?.setLogo(null)  // Menghilangkan logo
                supportActionBar?.title = destination.label  // Atur judul sesuai label
            }
        }
    }
}
