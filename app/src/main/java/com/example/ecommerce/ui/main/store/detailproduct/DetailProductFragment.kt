package com.example.ecommerce.ui.main.store.detailproduct

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentDetailProductBinding
import com.example.ecommerce.model.ProductVariant
import com.example.ecommerce.model.asProductLocalDb
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import com.example.ecommerce.ui.main.menu.db.AppExecutor
import com.example.ecommerce.ui.main.menu.db.ProductDAO
import com.example.ecommerce.ui.main.menu.db.ProductDatabase
import com.example.ecommerce.ui.main.store.CurrencyUtils
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class DetailProductFragment : Fragment() {
    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private val sharedPref by lazy {
        SharedPref(requireContext())
    }
    private val repository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }

    private val factory by lazy {
        ViewModelFactory(repository, sharedPref)
    }
    private val viewModel: DetailProductViewModel by viewModels { factory }
    private lateinit var cartViewModel: CartViewModel
    private lateinit var database: ProductDatabase
    private lateinit var productDao: ProductDAO

    private var id_product: String? = null

    private var varianName: String? = null
    private var productPrice: Int? = 0
    private var varianPrice: Int? = 0
    private lateinit var appExecutors: AppExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        id_product = arguments?.getString("id_product")
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataDetail()
        getChip()
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getDataDetail() {
        appExecutors = AppExecutor()
        database = ProductDatabase.getDatabase(requireContext())
        productDao = database.productDao()

        val accessToken = sharedPref.getAccessToken() ?: throw Exception("token null")
        viewModel.getDetailProduct(accessToken, id_product).observe(viewLifecycleOwner) {
            binding.apply {
                when (it) {
                    is Result.Success -> {
                        val listVarian = ArrayList<ProductVariant>()
                        val product = it.data.data
                        productPrice = product.productPrice

                        tvProductPrice.text = CurrencyUtils.formatRupiah(product.productPrice)
                        tvProductName.text = product.productName
                        tvSale.text = product.sale.toString()
                        tvRatingUp.text = product.productRating.toString()
                        tvProductRating.text = product.productRating.toString()
                        tvDescription.text = product.description
                        tvPercent.text = "${product.totalSatisfaction}%"
                        tvTotalRate.text = "${product.totalRating} Rating"
                        tvTotalReview.text = "${product.totalReview} Ulasan"
                        product.productVariant.forEach {
                            listVarian.add(it)
                        }
                        createChip(listVarian)
                        val images = product.image
                        val viewpagerAdapter = ProductPagerAdapter(images)
                        vpImageProduct.adapter = viewpagerAdapter
                        TabLayoutMediator(tabDots, vpImageProduct) { _, _ -> }.attach()

                        binding.buttonLihatSemua.setOnClickListener {
                            val bundle = bundleOf("id_product" to product.productId)
                            (requireActivity() as MainActivity).goToDetailReview(bundle)
                        }
                        btnKeKeranjang.setOnClickListener {
                            val productLocalDb = product.asProductLocalDb(varianName, varianPrice)

                            appExecutors.diskIO.execute{

                                cartViewModel = CartViewModel(requireContext())
                                val checkProductExist = cartViewModel.getCartById(productLocalDb.productId)
                                if(checkProductExist?.productId != null){
                                    val sumQuantity = productLocalDb.quantity
                                    val counter = sumQuantity + 1
                                    cartViewModel.updateCartItemQuantity(productLocalDb.productId,counter)
                                }
                                else{
                                    cartViewModel.addToCart(
                                        productLocalDb.productId,
                                        productLocalDb.productName,
                                        productLocalDb.productPrice,
                                        productLocalDb.image,
                                        productLocalDb.stock,
                                        varianName ?: "RAM 16GB",
                                        varianPrice
                                    )
                                }

                                val contextView = binding.view
                                Snackbar.make(
                                    contextView,
                                    R.string.snackbar_text,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    is Result.Error -> {

                    }

                    is Result.Loading -> {

                    }
                }
            }
        }
    }

    private fun createChip(varians: List<ProductVariant>) {
        binding.apply {
            chipGroupVarian.removeAllViews()
            for ((index, varian) in varians.withIndex()) {
                val chip = Chip(requireContext())
                chip.apply {
                    text = varian.variantName
                    isChipIconVisible = false
                    isCloseIconVisible = false
                    isCheckable = true
                    tag = varian
                    isChecked = index == 0
                }
                chipGroupVarian.addView(chip as View)
            }
        }
    }

    private fun getChip() {
        binding.apply {
            chipGroupVarian.setOnCheckedChangeListener { group, checkedId ->
                val checkedChip = group.findViewById<Chip>(checkedId)
                if (checkedChip != null) {
                    val selectedVarian = checkedChip.tag as ProductVariant
                    varianName = selectedVarian.variantName
                    varianPrice = selectedVarian.variantPrice
                    val updateTvProductPrice = productPrice?.plus(varianPrice!!)
                    tvProductPrice.text = CurrencyUtils.formatRupiah(updateTvProductPrice)

                }
            }
        }
    }
}