package com.example.myapplication.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.data.remote.response.acc.pref.UserModel
import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import com.example.myapplication.data.remote.response.acc.pref.dataStore
import com.example.myapplication.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

@Suppress("DEPRECATION", "SameParameterValue", "NAME_SHADOWING")
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var userPreference: UserPreference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility = View.GONE
        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                showToast(R.string.name_password_empty)
                return@setOnClickListener
            }

            if (!isNetworkAvailable()) {
                showToast(R.string.no_internet_connection)
                return@setOnClickListener
            }

            showLoading(true)

            viewModel.login(username, password) { isSuccess, token, username ->
                showLoading(false)
                if (isSuccess) {
                    saveSession(username, token)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), username ?: "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveSession(username: String?, token: String?) {
        lifecycleScope.launch {
            try {
                val userModel = UserModel(
                    username = username ?: "",
                    token = token ?: "",
                    isLogin = true
                )
                userPreference.saveSession(userModel)
            } catch (e: Exception) {
                showToast(R.string.session_save_failed)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleFadeIn = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).apply {
            duration = 1000
            startDelay = 100
        }

        val messageFadeIn = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 0f, 1f).apply {
            duration = 1000
            startDelay = 100
        }

        val emailTextView = ObjectAnimator.ofFloat(binding.usernameTextView, View.TRANSLATION_Y, 100f, 0f).apply {
            duration = 500
            startDelay = 150
        }
        val emailTextViewScale = ObjectAnimator.ofFloat(binding.usernameTextView, View.SCALE_X, 0.8f, 1f).apply {
            duration = 500
            startDelay = 150
        }

        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.TRANSLATION_Y, 100f, 0f).apply {
            duration = 500
            startDelay = 200
        }
        val emailEditTextLayoutScale = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.SCALE_X, 0.8f, 1f).apply {
            duration = 500
            startDelay = 200
        }

        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.TRANSLATION_Y, 100f, 0f).apply {
            duration = 500
            startDelay = 250
        }
        val passwordTextViewScale = ObjectAnimator.ofFloat(binding.passwordTextView, View.SCALE_X, 0.8f, 1f).apply {
            duration = 500
            startDelay = 250
        }

        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.TRANSLATION_Y, 100f, 0f).apply {
            duration = 500
            startDelay = 300
        }
        val passwordEditTextLayoutScale = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.SCALE_X, 0.8f, 1f).apply {
            duration = 500
            startDelay = 300
        }

        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.TRANSLATION_Y, 100f, 0f).apply {
            duration = 500
            startDelay = 350
        }
        val loginButtonScale = ObjectAnimator.ofFloat(binding.loginButton, View.SCALE_X, 0.8f, 1f).apply {
            duration = 500
            startDelay = 350
        }

        AnimatorSet().apply {
            playTogether(
                titleFadeIn,
                messageFadeIn,
                emailTextView, emailTextViewScale,
                emailEditTextLayout, emailEditTextLayoutScale,
                passwordTextView, passwordTextViewScale,
                passwordEditTextLayout, passwordEditTextLayoutScale,
                loginButton, loginButtonScale
            )
            start()
        }
    }
}