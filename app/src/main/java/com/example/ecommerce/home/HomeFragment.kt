package com.example.ecommerce.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogout.setOnClickListener {
                (requireActivity() as MainActivity).logOut()
            }

            topAppBar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.notification -> {
                        Toast.makeText(requireContext(),"Notif",Toast.LENGTH_SHORT).show()
                    }
                    R.id.chart -> {
                        Toast.makeText(requireContext(),"chart",Toast.LENGTH_SHORT).show()
                    }
                    R.id.filter -> {
                        Toast.makeText(requireContext(),"filter",Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}