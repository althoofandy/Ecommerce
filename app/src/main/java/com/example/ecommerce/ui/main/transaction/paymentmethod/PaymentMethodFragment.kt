package com.example.ecommerce.ui.main.transaction.paymentmethod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentPaymentMethodBinding
import com.example.ecommerce.model.PaymentMethodItemResponse
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

    private lateinit var adapter: PaymentMethodAdapter
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
        getData()
        initEvent()
    }

    private fun initEvent() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun getData() {
        viewModel = PaymentMethodViewModel(repository)
        val tokenAccess = sharedPref.getAccessToken() ?: (requireActivity() as MainActivity).logOut()

        viewModel.getPaymentMethod(tokenAccess.toString()).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    adapter = PaymentMethodAdapter(it.data.data)
                    val linearLayout = LinearLayoutManager(requireContext())
                    binding.rvPaymentMethods.layoutManager = linearLayout
                    binding.rvPaymentMethods.adapter = adapter

                    adapter.setItemClickListener(object :
                        PaymentMethodAdapter.PaymentMethodItemClickListener {
                        override fun onItemClick(item: PaymentMethodItemResponse) {
                            val bundle = bundleOf("payment" to item)
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "payment",
                                bundle
                            )
                            findNavController().popBackStack()
                        }

                    })
                }

                is Result.Loading -> {

                }

                is Result.Error -> {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}