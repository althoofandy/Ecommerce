package com.example.ecommerce.ui.main.menu.cart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.databinding.FragmentCartBinding
import com.example.ecommerce.ui.main.CurrencyUtils

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter
    private lateinit var cartViewModel: CartViewModel

    private var selectedItemCount = 0
    private var totalItemCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackpressed()
        showData()
        checkBoxAllCheck()
    }

    private fun onBackpressed() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun checkBoxAllCheck() {
        binding.apply {
            cbAllCheck.setOnClickListener {
                selectAllItems(binding.cbAllCheck.isChecked)
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

            if(selectedItemCount>0){
                binding.btnDeleteAll.visibility = View.VISIBLE
                binding.btnBeliCart.visibility = View.VISIBLE
            }else{
                binding.btnDeleteAll.visibility = View.GONE
                binding.btnBeliCart.visibility = View.GONE
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

            val selectedIds = cartItems
                .filter { it.selected }
                .map { it.productId }

            binding.btnDeleteAll.setOnClickListener {
                cartViewModel.removeFromCartAll(selectedIds)
            }
            val totalSelectedPrice = cartItems.filter { it.selected }
                .sumBy { (it.productPrice + it.variantPrice!!) * it.quantity }

            val formattedPrice = CurrencyUtils.formatRupiah(totalSelectedPrice)
            binding.tvHargaCart.text = formattedPrice.toString()

        }
    }
}