package com.example.ecommerce.ui.main.menu.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter
    private lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackpressed()
        showData()
    }
    private fun onBackpressed(){
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showData(){
        cartViewModel = CartViewModel(requireContext())
        adapter = CartAdapter(cartViewModel)

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvCart.layoutManager = linearLayout
        binding.rvCart.adapter = adapter
        cartViewModel.getCartItem()?.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }
}