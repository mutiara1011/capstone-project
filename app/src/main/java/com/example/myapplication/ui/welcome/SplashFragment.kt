package com.example.myapplication.ui.welcome

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import com.example.myapplication.data.remote.response.acc.pref.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        val logoImageView = view.findViewById<View>(R.id.logoImageView)

        val rotate = ObjectAnimator.ofFloat(logoImageView, View.ROTATION, 0f, 360f).apply {
            duration = 1500
            repeatCount = 0
        }

        rotate.start()

        lifecycleScope.launch {
            delay(2000)
            val user = userPreference.getSession().first()
            if (user.isLogin) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
            }
        }
    }
}