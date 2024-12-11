package com.example.myapplication.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import com.example.myapplication.data.remote.response.acc.pref.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var userPreference: UserPreference
    private lateinit var userNameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_profile, container, false)
        userNameTextView = binding.findViewById(R.id.userNameTextView)

        userPreference = UserPreference.getInstance(requireContext().dataStore)

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userPreference.getSession().first()
            requireActivity().runOnUiThread {
                userNameTextView.text = user.username
            }
        }

        val logoutButton: Button = binding.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }

        return binding
    }

    private fun logout() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                userPreference.logout()

                requireActivity().runOnUiThread {
                    findNavController().navigate(R.id.action_profileFragment_to_welcomeFragment)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
