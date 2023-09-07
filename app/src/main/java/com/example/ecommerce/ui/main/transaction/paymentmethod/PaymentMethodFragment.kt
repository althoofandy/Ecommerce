package com.example.ecommerce.ui.main.transaction.paymentmethod

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentPaymentMethodBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository

class PaymentMethodFragment : Fragment() {
    private var _binding: FragmentPaymentMethodBinding? = null
    private val binding get() = _binding!!
    private val sharedPref by lazy {
        SharedPref(requireContext())
    }
    private val repository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }
    private lateinit var viewModel: PaymentMethodViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = PaymentMethodViewModel(repository)
        val tokenAccess = sharedPref.getAccessToken() ?: throw Exception("token is null")
        viewModel.getPaymentMethod(tokenAccess).observe(viewLifecycleOwner){
            when(it){
                is Result.Success ->{
                    Log.d("Cek data", it.data.data.toString())
                }
                is Result.Loading ->{

                }
                is Result.Error -> {

                }
            }
        }

    }
}