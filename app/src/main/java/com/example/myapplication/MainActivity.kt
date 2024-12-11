package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.userFragment, R.id.settingsFragment, R.id.profileFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Mengatur bottom navigation dengan NavController
        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        // Sembunyikan toolbar dan bottom navigation saat di WelcomeFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.welcomeFragment) {
                // Sembunyikan toolbar dan bottom navigation di WelcomeFragment
                supportActionBar?.hide()
                binding.navView.visibility = View.GONE
            } else {
                // Tampilkan toolbar dan bottom navigation setelah login
                supportActionBar?.show()
                binding.navView.visibility = View.VISIBLE

                if (destination.id == R.id.homeFragment) {
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
}