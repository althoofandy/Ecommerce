package com.example.ecommerce.ui.main.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.core.model.WishlistProduct
import com.example.ecommerce.databinding.FragmentWishlistBinding
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var adapter: WishlistAdapter

    private var isList: Boolean = true
    private lateinit var gridLayoutManager: GridLayoutManager
    private var counter = 0
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
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        binding.ivLayout.setOnClickListener {
            changeToggle()
        }
    }

    private fun changeToggle() {
        isList = !isList
        val imageRes =
            if (isList) {
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_format_list_bulleted_24
                )
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.baseline_grid_view_24)
            }
        binding.ivLayout.setImageDrawable(imageRes)
        if (isList) {
            gridLayoutManager.spanCount = 1
        } else {
            gridLayoutManager.spanCount = 2
        }
        adapter.toggleLayoutViewType()
    }

    private fun getData() {
        binding.apply {
            wishlistViewModel = WishlistViewModel(requireContext())
            adapter = WishlistAdapter(requireContext(), wishlistViewModel, firebaseAnalytics)
            cartViewModel = CartViewModel(requireContext())

            wishlistViewModel.getWishlistProduct()?.observe(viewLifecycleOwner) {
                adapter.submitList(it)

                if (it.isEmpty()) {
                    binding.linearErrorLayout.visibility = View.VISIBLE
                    binding.rvWishlist.visibility = View.GONE
                } else {
                    binding.linearErrorLayout.visibility = View.GONE
                    binding.rvWishlist.visibility = View.VISIBLE
                }

                adapter.setOnItemClickCallback(object :
                    WishlistAdapter.OnItemClickCallback {
                    override fun onItemClick(wishListProduct: WishlistProduct) {
                        firebaseAnalytics.logEvent("btn_addCart_from_wishlist_clicked", null)
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            val checkCartProduct =
                                cartViewModel.getCartById(wishListProduct.productId)
                            if (checkCartProduct?.productId != null) {
                                if (checkCartProduct?.quantity!! < checkCartProduct.stock!!) {
                                    counter = checkCartProduct.quantity
                                    counter++
                                    cartViewModel.updateCartItemQuantity(
                                        checkCartProduct.productId,
                                        counter
                                    )
                                } else {
                                    val contextView = binding.root
                                    Snackbar.make(
                                        contextView,
                                        getString(R.string.emptyStock),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                cartViewModel.addToCart(
                                    wishListProduct.productId,
                                    wishListProduct.productName,
                                    wishListProduct.productPrice,
                                    wishListProduct.image,
                                    wishListProduct.store,
                                    wishListProduct.sale,
                                    wishListProduct.stock,
                                    wishListProduct.totalRating,
                                    wishListProduct.productRating,
                                    wishListProduct.variantName,
                                    wishListProduct.variantPrice
                                )
                                val contextView = binding.root
                                Snackbar.make(
                                    contextView,
                                    R.string.snackbar_text,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onItemClickCard(data: String) {
                        val bundle = bundleOf("id_product" to data)
                        (requireActivity() as MainActivity).goToDetailProduct(bundle)
                    }
                })
                tvTotalBarang.text = "${it.size} "
            }
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
            rvWishlist.layoutManager = gridLayoutManager
            rvWishlist.adapter = adapter
        }
    }
}
