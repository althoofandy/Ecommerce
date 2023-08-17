package com.example.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val navHost by lazy {
        childFragmentManager.findFragmentById(R.id.navview) as NavHostFragment
    }

    private val navController by lazy {
        navHost.findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            topAppBar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.notification -> {
                        Toast.makeText(requireContext(),"Notif", Toast.LENGTH_SHORT).show()
                    }
                    R.id.chart -> {
                        Toast.makeText(requireContext(),"chart", Toast.LENGTH_SHORT).show()
                    }
                    R.id.filter -> {
                        Toast.makeText(requireContext(),"filter", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            binding.bottomNav.setupWithNavController(navController)
            binding.bottomNav.setOnItemReselectedListener { }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


}