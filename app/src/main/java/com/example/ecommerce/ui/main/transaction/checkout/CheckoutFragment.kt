package com.example.ecommerce.ui.main.transaction.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentCheckoutBinding
import com.example.ecommerce.model.CheckoutProduct
import com.example.ecommerce.model.Payment
import com.example.ecommerce.model.PaymentItem
import com.example.ecommerce.model.PaymentMethodItemResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.CurrencyUtils
import com.example.ecommerce.ui.main.db.AppExecutor
import com.example.ecommerce.ui.main.menu.cart.CartViewModel

class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CheckoutAdapter
    private lateinit var cartViewModel: CartViewModel
    private val repository: EcommerceRepository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var appExecutor: AppExecutor
    private var dataProduct = listOf<CheckoutProduct>()
    private var dataPayment: PaymentMethodItemResponse? = null
    private var listPayment = listOf<PaymentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            dataProduct = it.getParcelableArrayList("data_product") ?: emptyList()
            val bundle =
                findNavController().currentBackStackEntry?.savedStateHandle?.get<Bundle>("payment")
            dataPayment = bundle?.getParcelable("payment")
        }
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        getData()
    }

    private fun getData() {
        appExecutor = AppExecutor()
        cartViewModel = CartViewModel(requireContext())
        adapter = CheckoutAdapter(cartViewModel)

        binding.apply {
            val linearLayout = LinearLayoutManager(requireContext())
            rvCheckout.layoutManager = linearLayout
            rvCheckout.adapter = adapter

            adapter.submitList(dataProduct)

            val totalSelectedPrice =
                dataProduct.sumBy { (it.productPrice + it.variantPrice!!) * it.quantity }

            val formattedPrice = CurrencyUtils.formatRupiah(totalSelectedPrice)
            tvHargaCheckout.text = formattedPrice.toString()

            if (dataPayment != null) {
                Glide.with(requireContext())
                    .load(dataPayment?.image)
                    .into(ivBankImage)
                tvBankName.text = dataPayment?.label
            }
        }

    }

    private fun initEvent() {
        viewModel = CheckoutViewModel(repository)
        sharedPref = SharedPref(requireContext())
        val accessToken = sharedPref.getAccessToken() ?: throw Exception("token is null")

        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            cvPaymentMethods.setOnClickListener {
                findNavController().navigate(R.id.action_checkoutFragment_to_paymentMethodFragment)
            }
            if (dataPayment != null) {
                btnBeliCheckout.isEnabled = true
                listPayment =  dataProduct.map { PaymentItem(it.productId, it.variantName, it.quantity) }

                btnBeliCheckout.setOnClickListener {
                    viewModel.doBuyProducts(accessToken, Payment(dataPayment?.label!!, listPayment))
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                is Result.Success -> {
                                    Log.d("cek item:",listPayment.toString())
                                    val bundle = Bundle().apply {
                                        putParcelable("invoice",it.data.data)
                                    }
                                    findNavController().navigate(R.id.action_checkoutFragment_to_successPaymentFragment,bundle)
                                }

                                is Result.Loading -> {

                                }

                                is Result.Error -> {

                                }
                            }

                        }
                }
            } else {
                btnBeliCheckout.isEnabled = false
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}