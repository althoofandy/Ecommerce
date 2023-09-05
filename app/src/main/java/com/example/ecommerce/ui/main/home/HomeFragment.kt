package com.example.ecommerce.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerce.MainActivity
import com.example.ecommerce.databinding.FragmentHomeBinding
import com.example.ecommerce.ui.main.db.AppExecutor
import com.example.ecommerce.ui.main.db.ProductDatabase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private val productDatabase by lazy {
        ProductDatabase.getDatabase(requireContext())
    }
    private lateinit var appExecutor: AppExecutor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logOut()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun logOut() {
        binding.apply {
            btnLogout.setOnClickListener {
                (requireActivity() as MainActivity).logOut()
                clearDb()

            }
        }
    }
    private fun clearDb(){
        appExecutor = AppExecutor()
        appExecutor.diskIO.execute {
            viewModel = HomeViewModel(productDatabase)
            viewModel.clearDb()
        }
    }

}