package com.example.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce.databinding.FragmentMainBinding
import com.example.ecommerce.pref.SharedPref

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val navHost by lazy {
        childFragmentManager.findFragmentById(R.id.navview) as NavHostFragment
    }

    private val navController by lazy {
        navHost.findNavController()
    }
    private val sharedPref by lazy {
        SharedPref(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSession()
        checkUserNameExist()

        binding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.notification -> {
                    }

                    R.id.cart -> {
                        findNavController().navigate(R.id.action_main_to_cartFragment)
                    }

                    R.id.menu -> {}
                }
                true
            }

            binding.bottomNav.setupWithNavController(navController)
            binding.bottomNav.setOnItemReselectedListener { }
        }
    }

    private fun checkSession() {
        val session = sharedPref.getAccessToken()
        if (session.isNullOrEmpty()) {
            (requireActivity() as MainActivity).logOut()
        }
    }

    private fun checkUserNameExist() {
        val userName = sharedPref.getNameProfile()
        if (userName.isNullOrEmpty()) {
            (requireActivity() as MainActivity).checkUsernameExist()
        } else {
            binding.tvUserName.text = userName
        }
    }
}