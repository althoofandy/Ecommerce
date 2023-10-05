package com.example.ecommerce.ui.main.menu.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.core.model.CheckoutProduct
import com.example.ecommerce.core.model.ProductLocalDb
import com.example.ecommerce.core.model.asCheckoutProduct
import com.example.ecommerce.databinding.FragmentCartBinding
import com.example.ecommerce.ui.main.CurrencyUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter
    private lateinit var cartViewModel: CartViewModel

    private var selectedItemCount = 0
    private var totalItemCount = 0

    private var listCart = listOf<CheckoutProduct>()
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
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackpressed()
        showData()
        initEvent()
    }

    private fun onBackpressed() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initEvent() {
        binding.apply {
            cbAllCheck.setOnClickListener {
                selectAllItems(binding.cbAllCheck.isChecked)
            }
            btnBeliCart.setOnClickListener {
                val bundle = bundleOf("data_product" to listCart)
                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment, bundle)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectAllItems(isSelected: Boolean) {
        binding.apply {
            val cartItems = adapter.currentList
            val selectedIds = mutableListOf<String>()

            for (item in cartItems) {
                item.selected = isSelected
                selectedIds.add(item.productId)
                if (cartItems.size > 0 && item.selected) {
                    btnDeleteAll.visibility = View.VISIBLE
                } else {
                    btnDeleteAll.visibility = View.GONE
                }
            }
            cartViewModel.updateCartItemCheckbox(selectedIds, isSelected)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showData() {
        cartViewModel = CartViewModel(requireContext())
        adapter = CartAdapter(cartViewModel)

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvCart.layoutManager = linearLayout
        binding.rvCart.adapter = adapter
        cartViewModel.getCartItem()?.observe(viewLifecycleOwner) { cartItems ->
            adapter.submitList(cartItems)

            selectedItemCount = cartItems.count { it.selected }
            totalItemCount = cartItems.size

            if (selectedItemCount > 0) {
                binding.btnDeleteAll.visibility = View.VISIBLE
                binding.btnBeliCart.isEnabled = true
            } else {
                binding.btnDeleteAll.visibility = View.GONE
                binding.btnBeliCart.isEnabled = false
            }

            if (!(totalItemCount > 0 && selectedItemCount == totalItemCount)) {
                binding.cbAllCheck.isChecked = false
            } else {
                binding.cbAllCheck.isChecked = true
                binding.linearError.visibility = View.GONE
            }

            if (totalItemCount <= 0) {
                binding.linearError.visibility = View.VISIBLE
            } else {
                binding.linearError.visibility = View.GONE
            }

            val selectedProduct = cartItems.filter { it.selected }.map {
                it.asCheckoutProduct(it.variantName, it.variantPrice)
            }
            listCart = selectedProduct

            val selectedIds = cartItems
                .filter { it.selected }
                .map { it.productId }
            adapter.setOnItemClickCallback(object : CartAdapter.OnItemClickCallback {
                override fun onItemClick(data: ProductLocalDb) {
                    val bundle = bundleOf("data_product" to data)
                    (requireActivity() as MainActivity).goToDetailProductFromCart(bundle)
                }
            })

            adapter.setOnItemClickCallback(object : CartAdapter.OnItemDeleteClickCallback {
                override fun onItemDeleteClick(position: ProductLocalDb) {
                    cartViewModel.removeFromCart(position.productId)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART) {
                        param(FirebaseAnalytics.Param.ITEM_ID, position.productId)
                        param(FirebaseAnalytics.Param.ITEM_NAME, position.productName)
                    }
                }
            })

            cartItems.forEach {
                val viewCart = Bundle()
                viewCart.putString(FirebaseAnalytics.Param.ITEM_ID, it.productId)
                viewCart.putString(FirebaseAnalytics.Param.ITEM_NAME, it.productName)
                viewCart.putString(
                    FirebaseAnalytics.Param.VALUE,
                    (it.quantity * it.productPrice).toString()
                )
                viewCart.putString(FirebaseAnalytics.Param.CURRENCY, "IDR")
                val params = Bundle()
                params.putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(viewCart))

                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_CART, params)
            }

            binding.btnDeleteAll.setOnClickListener {
                cartViewModel.removeFromCartAll(selectedIds)
                selectedIds.map {
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART) {
                        param(FirebaseAnalytics.Param.ITEM_ID, it)
                    }
                }
            }
            val totalSelectedPrice = cartItems.filter { it.selected }
                .sumBy { (it.productPrice + it.variantPrice!!) * it.quantity }

            val formattedPrice = CurrencyUtils.formatRupiah(totalSelectedPrice)
            binding.tvHargaCart.text = formattedPrice.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
