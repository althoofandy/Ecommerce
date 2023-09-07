package com.example.ecommerce.ui.main.transaction.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentCheckoutBinding
import com.example.ecommerce.model.CheckoutProduct
import com.example.ecommerce.model.ProductLocalDb
import com.example.ecommerce.ui.main.CurrencyUtils
import com.example.ecommerce.ui.main.db.AppExecutor
import com.example.ecommerce.ui.main.menu.cart.CartViewModel


class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CheckoutAdapter
    private lateinit var cartViewModel: CartViewModel
    private lateinit var appExecutor: AppExecutor
    private var dataProduct = listOf<CheckoutProduct>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            dataProduct = it.getParcelableArrayList("data_product") ?: emptyList()
        }
        _binding = FragmentCheckoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        getData()
    }
    private fun getData(){
        appExecutor = AppExecutor()
        cartViewModel = CartViewModel(requireContext())
        adapter = CheckoutAdapter(cartViewModel)

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvCheckout.layoutManager = linearLayout
        binding.rvCheckout.adapter = adapter

        Log.d("cek cekot",dataProduct.toString())
        adapter.submitList(dataProduct)

        val totalSelectedPrice = dataProduct.sumBy { (it.productPrice + it.variantPrice!!) * it.quantity }

        val formattedPrice = CurrencyUtils.formatRupiah(totalSelectedPrice)
        binding.tvHargaCheckout.text = formattedPrice.toString()
    }
    private fun initEvent() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnBeliCheckout.setOnClickListener {
                findNavController().navigate(R.id.action_checkoutFragment_to_paymentMethodFragment)
            }
        }
    }
}