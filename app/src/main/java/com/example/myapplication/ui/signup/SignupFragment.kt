package com.example.myapplication.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSignupBinding

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupBinding.bind(view)

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
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (isInputInvalid(name, email, password)) return@setOnClickListener

            showLoading(true)

            viewModel.register(name, email, password) { isSuccess, message ->
                showLoading(false)
                if (isSuccess) {
                    showRegistrationSuccessDialog(email)
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isInputInvalid(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                showToastMessage(R.string.fill_fields)
                true
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToastMessage(R.string.invalid_email_format)
                true
            }
            else -> false
        }
    }

    private fun showToastMessage(messageResId: Int) {
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    private fun showRegistrationSuccessDialog(email: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.register_success))
            setMessage(getString(R.string.success_message, email))
            setPositiveButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 150
        }

        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 200
        }

        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 250
        }

        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 300
        }

        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 350
        }

        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 400
        }

        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.TRANSLATION_X, -100f, 0f).apply {
            duration = 500
            startDelay = 450
        }

        AnimatorSet().apply {
            playTogether(
                titleFadeIn,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup,
            )
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}