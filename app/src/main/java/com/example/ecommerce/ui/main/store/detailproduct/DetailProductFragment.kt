package com.example.ecommerce.ui.main.store.detailproduct

import androidx.fragment.app.Fragment

class DetailProductFragment : Fragment() {
//    private var _binding: FragmentDetailProductBinding? = null
//    private val binding get() = _binding!!
//    private val sharedPref by lazy {
//        SharedPref(requireContext())
//    }
//    private val repository by lazy {
//        val apiService = Retrofit(requireContext()).getApiService()
//        val sharedPref = SharedPref(requireContext())
//        EcommerceRepository(apiService, sharedPref)
//    }
//
//    private val factory by lazy {
//        ViewModelFactory(repository, sharedPref)
//    }
//    private val viewModel: DetailProductViewModel by viewModels { factory }
//    private lateinit var cartViewModel: CartViewModel
//    private lateinit var wishlistViewModel: WishlistViewModel
//    private lateinit var database: ProductDatabase
//    private lateinit var productDao: ProductDAO
//
//    private var id_product: String? = null
//    private var varianName: String? = null
//    private var productPrice: Int? = 0
//    private var varianPrice: Int? = 0
//    private var product: GetProductDetailItemResponse? = null
//    private var counter = 0
//    private lateinit var appExecutors: AppExecutor
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        id_product = arguments?.getString("id_product")
//        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        getChip()
//        getDataDetail()
//        initEvent()
//    }
//
//    private fun initEvent() {
//        binding.apply {
//            topAppBar.setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//            btnShare.setOnClickListener {
//                val deepLink = "Product : ${product?.productName}\n" +
//                        "Price : ${CurrencyUtils.formatRupiah(product?.productPrice)}\n" +
//                        "Link : http://ecommerce.com/products/$id_product"
//
//                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.type = "text/plain"
//                shareIntent.putExtra(Intent.EXTRA_TEXT, deepLink)
//
//                startActivity(Intent.createChooser(shareIntent,null))
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun getDataDetail() {
//        appExecutors = AppExecutor()
//        database = ProductDatabase.getDatabase(requireContext())
//        productDao = database.productDao()
//
//        binding.apply {
//            progressCircular.visibility = View.VISIBLE
//            val accessToken = sharedPref.getAccessToken() ?: (requireActivity() as MainActivity).logOut()
//            viewModel.getDetailProduct(accessToken.toString(), id_product).observe(viewLifecycleOwner) {
//                when (it) {
//
//                    is Result.Success -> {
//
//                        scrollView2.visibility = View.VISIBLE
//                        viewBottom.visibility = View.VISIBLE
//                        progressCircular.hide()
//                        val listVarian = ArrayList<ProductVariant>()
//                        product = it.data.data
//                        productPrice = product?.productPrice
//
//                        tvProductPrice.text = CurrencyUtils.formatRupiah(product?.productPrice)
//                        tvProductName.text = product?.productName
//                        tvSale.text = product?.sale.toString()
//                        tvRatingUp.text = product?.productRating.toString()
//                        tvRate.text = "(${product?.totalRating})"
//                        tvProductRating.text = product?.productRating.toString()
//                        tvDescription.text = product?.description
//                        tvPercent.text = "${product?.totalSatisfaction}%"
//                        tvTotalRate.text = "${product?.totalRating} Rating"
//                        tvTotalReview.text = "${product?.totalReview} Ulasan"
//                        product?.productVariant?.forEach {
//                            listVarian.add(it)
//                        }
//                        createChip(listVarian)
//                        val images = product?.image
//                        val viewpagerAdapter = ProductPagerAdapter(images!!)
//                        vpImageProduct.adapter = viewpagerAdapter
//
//                        if (images.size <= 1) {
//                            tabDots.visibility = View.GONE
//                        } else {
//                            tabDots.visibility = View.VISIBLE
//                            TabLayoutMediator(tabDots, vpImageProduct) { _, _ -> }.attach()
//                        }
//
//                        binding.buttonLihatSemua.setOnClickListener {
//                            val bundle = bundleOf("id_product" to product?.productId)
//                            (requireActivity() as MainActivity).goToDetailReview(bundle)
//                        }
//
//                        btnBeliLangsung.setOnClickListener {
//                            val productLocalDb = product?.asProductLocalDb(varianName, varianPrice)
//                            val productCheckout = arrayListOf(
//                                productLocalDb?.asCheckoutProduct(
//                                    varianName?: "RAM 16GB",
//                                    varianPrice
//                                )
//                            )
//                            val listCart = productCheckout
//                            val bundle = bundleOf("data_product" to listCart)
//                            findNavController().navigate(
//                                R.id.action_detailProductFragment_to_checkoutFragment,
//                                bundle
//                            )
//                        }
//
//
//                        btnKeKeranjang.setOnClickListener {
//                            appExecutors.diskIO.execute {
//                                cartViewModel = CartViewModel(requireContext())
//                                val productLocalDb =
//                                    product?.asProductLocalDb(varianName, varianPrice)
//                                val checkProductExist =
//                                    cartViewModel.getCartById(productLocalDb?.productId!!)
//
//                                if (checkProductExist?.productId != null) {
//                                    if (checkProductExist.quantity < checkProductExist.stock!!) {
//                                        counter = checkProductExist.quantity
//                                        counter++
//                                        cartViewModel.updateCartItemQuantity(
//                                            checkProductExist.productId,
//                                            counter
//                                        )
//                                    } else {
//                                        val contextView = binding.viewBottom
//                                        Snackbar.make(
//                                            contextView,
//                                            "Stok Habis!",
//                                            Snackbar.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                } else {
//                                    cartViewModel.addToCart(
//                                        productLocalDb.productId,
//                                        productLocalDb.productName,
//                                        productLocalDb.productPrice,
//                                        productLocalDb.image,
//                                        productLocalDb.store,
//                                        productLocalDb.sale,
//                                        productLocalDb.stock,
//                                        productLocalDb.totalRating,
//                                        productLocalDb.productRating,
//                                        varianName ?: "RAM 16GB",
//                                        varianPrice
//                                    )
//                                    val contextView = binding.viewBottom
//                                    Snackbar.make(
//                                        contextView,
//                                        R.string.snackbar_text,
//                                        Snackbar.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        }
//                        appExecutors.diskIO.execute {
//                            wishlistViewModel = WishlistViewModel(requireContext())
//                            val wishlistLocalDb =
//                                product?.asWishlistProduct(varianName, varianPrice)
//
//                            val checkWishlistExist =
//                                wishlistViewModel.getProductWishlistById(wishlistLocalDb?.productId!!)
//
//                            var isChecked = false
//                            if (checkWishlistExist?.productId != null) {
//                                binding.toggleFavorite.isChecked = true
//                                isChecked = true
//                            } else {
//                                binding.toggleFavorite.isChecked = false
//                                isChecked = false
//                            }
//                            binding.toggleFavorite.setOnClickListener {
//                                isChecked = !isChecked
//                                if (isChecked) {
//                                    wishlistViewModel.addToCart(
//                                        wishlistLocalDb.productId,
//                                        wishlistLocalDb.productName,
//                                        wishlistLocalDb.productPrice,
//                                        wishlistLocalDb.image,
//                                        wishlistLocalDb.store,
//                                        wishlistLocalDb.sale,
//                                        wishlistLocalDb.stock,
//                                        wishlistLocalDb.totalRating,
//                                        wishlistLocalDb.productRating,
//                                        varianName ?: "RAM 16GB",
//                                        varianPrice
//                                    )
//                                } else {
//                                    wishlistViewModel.removeWishlist(wishlistLocalDb.productId)
//                                }
//                                binding.toggleFavorite.isChecked = isChecked
//                            }
//                        }
//                    }
//
//                    is Result.Error -> {
//                        progressCircular.hide()
//                        cartViewModel = CartViewModel(requireContext())
//                        binding.apply {
//                            viewBottom.visibility = View.GONE
//                            scrollView2.visibility = View.GONE
//                            linearError.visibility = View.VISIBLE
//                            btnRefresh.setOnClickListener {
//                                val accessToken =
//                                    sharedPref.getAccessToken() ?: throw Exception("Token is null")
//                                viewModel.getDetailProduct(accessToken, id_product!!)
//                            }
//                        }
//                    }
//
//                    is Result.Loading -> {
//                        progressCircular.show()
//                        scrollView2.visibility = View.GONE
//                        viewBottom.visibility = View.GONE
//                    }
//                }
//            }
//        }
//    }
//
//    private fun createChip(varians: List<ProductVariant>) {
//        binding.apply {
//            chipGroupVarian.removeAllViews()
//            for ((index, varian) in varians.withIndex()) {
//                val chip = Chip(requireContext())
//                chip.apply {
//                    text = varian.variantName
//                    isChipIconVisible = false
//                    isCloseIconVisible = false
//                    isCheckable = true
//                    tag = varian
//                    isChecked = index == 0
//                }
//                chipGroupVarian.addView(chip as View)
//            }
//        }
//    }
//
//    private fun getChip() {
//        binding.apply {
//            chipGroupVarian.setOnCheckedChangeListener { group, checkedId ->
//                val checkedChip = group.findViewById<Chip>(checkedId)
//                if (checkedChip != null) {
//                    val selectedVarian = checkedChip.tag as ProductVariant
//                    varianName = selectedVarian.variantName
//                    varianPrice = selectedVarian.variantPrice
//                    val updateTvProductPrice = productPrice?.plus(varianPrice!!)
//                    tvProductPrice.text = CurrencyUtils.formatRupiah(updateTvProductPrice)
//
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
}