package com.example.ecommerce.ui.main.transaction.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.Retrofit
import com.example.ecommerce.core.model.CheckoutProduct
import com.example.ecommerce.core.model.Payment
import com.example.ecommerce.core.model.PaymentItem
import com.example.ecommerce.core.model.PaymentMethodItemResponse
import com.example.ecommerce.databinding.FragmentCheckoutBinding
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import com.example.ecommerce.ui.main.CurrencyUtils
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

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
    private var dataProduct = listOf<CheckoutProduct>()
    private var dataPayment: PaymentMethodItemResponse? = null
    private var listPayment = listOf<PaymentItem>()
    private var itemCount: Int? = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        getData()
        initEvent()
    }

    private fun getData() {
        cartViewModel = CartViewModel(requireContext())
        viewModel = CheckoutViewModel(repository)
        adapter = CheckoutAdapter(cartViewModel)

        binding.apply {
            val linearLayout = LinearLayoutManager(requireContext())
            rvCheckout.layoutManager = linearLayout
            rvCheckout.adapter = adapter

            adapter.submitList(dataProduct)

            adapter.setItemClickListener(object : CheckoutAdapter.CheckoutClickListener {
                override fun onItemClick(item: List<PaymentItem>) {
                    item.forEach { paymentItem ->
                        val productToUpdate =
                            dataProduct.find { it.productId == paymentItem.productId }
                        productToUpdate?.quantity = paymentItem.quantity
                    }

                    item.forEach { paymentItem ->
                        val productToUpdate =
                            dataProduct.find { it.productId == paymentItem.productId }
                        productToUpdate?.quantity = paymentItem.quantity
                    }

                    val totalSelectedPrice =
                        dataProduct.sumBy { (it.productPrice + it.variantPrice!!) * it.quantity }
                    listPayment =
                        dataProduct.map { PaymentItem(it.productId, it.variantName, it.quantity) }
                    updateTotalPrice(totalSelectedPrice)
                }
            })
            listPayment = dataProduct.map { PaymentItem(it.productId, it.variantName, it.quantity) }

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

    private fun updateTotalPrice(item: Int) {
        binding.tvHargaCheckout.text = CurrencyUtils.formatRupiah(item)
    }

    private fun initEvent() {
        viewModel = CheckoutViewModel(repository)
        sharedPref = SharedPref(requireContext())
        val accessToken =
            sharedPref.getAccessToken() ?: (requireActivity() as MainActivity).logOut()

        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            cvPaymentMethods.setOnClickListener {
                findNavController().navigate(R.id.action_checkoutFragment_to_paymentMethodFragment)
            }
            if (dataPayment != null) {
                btnBeliCheckout.isEnabled = true
                dataProduct.map { itemCount = it.quantity }

                btnBeliCheckout.setOnClickListener {
                    binding.progressCircular.visibility = View.VISIBLE
                    cartViewModel.removeFromCartAll(dataProduct.map { it.productId })
                    viewModel.doBuyProducts(
                        accessToken.toString(),
                        Payment(dataPayment?.label!!, listPayment)
                    )
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                is Result.Success -> {
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                                        param(
                                            FirebaseAnalytics.Param.TRANSACTION_ID,
                                            it.data.data?.invoiceId!!
                                        )
                                        param(
                                            FirebaseAnalytics.Param.SUCCESS,
                                            it.data.data!!.status.toString()
                                        )
                                        param(
                                            FirebaseAnalytics.Param.ITEM_NAME,
                                            it.data.data!!.payment
                                        )
                                    }
                                    binding.progressCircular.hide()
                                    val bundle = Bundle().apply {
                                        putParcelable("invoice", it.data.data)
                                    }
                                    findNavController().navigate(
                                        R.id.action_checkoutFragment_to_successPaymentFragment,
                                        bundle
                                    )
                                }

                                is Result.Loading -> {
                                    binding.progressCircular.show()
                                }

                                is Result.Error -> {
                                    binding.progressCircular.hide()
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
