package com.example.ecommerce.ui.main.store.detailproduct

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.Retrofit
import com.example.ecommerce.core.model.GetProductDetailItemResponse
import com.example.ecommerce.core.model.ProductLocalDb
import com.example.ecommerce.core.model.asCheckoutProduct
import com.example.ecommerce.core.model.asProductLocalDb
import com.example.ecommerce.core.model.asWishlistProduct
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import com.example.ecommerce.ui.main.CurrencyUtils
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import com.example.ecommerce.ui.main.store.ui.theme.EcommerceTheme
import com.example.ecommerce.ui.main.wishlist.WishlistViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailProductFragmentCompose : Fragment() {
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
    private var data_product: ProductLocalDb? = null
    private var id_product: String? = null

    private lateinit var cartViewModel: CartViewModel
    private lateinit var wishlistViewModel: WishlistViewModel

    private var varianName: String? = null
    private var varianPrice: Int? = 0
    private var isChecked: Boolean = false

    private var counter = 0
    private var product: GetProductDetailItemResponse? = null
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
        data_product = arguments?.getParcelable("data_product")
        id_product = arguments?.getString("id_product")

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EcommerceTheme {
                    wishlistViewModel = WishlistViewModel(requireContext())
                    val productDetailState by viewModel.productDetail.observeAsState()
                    if (productDetailState == null) {
                        if (data_product != null) {
                            viewModel.setProductId(data_product?.productId)
                        } else {
                            viewModel.setProductId(id_product)
                        }
                    }
                    productDetailState?.let {
                        when (it) {
                            is Result.Success -> {
                                LoadingScreen(isLoading = false)
                                val data = it.data.data
                                ProductDetail(data)
                                product = data
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val wishlistLocalDb =
                                            data?.asWishlistProduct(varianName, varianPrice)

                                        val checkWishlistExist =
                                            wishlistViewModel.getProductWishlistById(wishlistLocalDb?.productId!!)
                                        if (!(checkWishlistExist?.productId.isNullOrEmpty())) {
                                            isChecked = true
                                        } else {
                                            isChecked
                                        }
                                    }
                                }
                            }

                            is Result.Loading -> {
                                LoadingScreen(isLoading = true)
                            }

                            is Result.Error -> {
                                LoadingScreen(isLoading = false)
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProductDetail(product: GetProductDetailItemResponse) {
        val poppins = FontFamily(
            Font(R.font.poppins_medium, FontWeight.Medium),
            Font(R.font.poppins_semibold, FontWeight.SemiBold),
            Font(R.font.poppins_bold, FontWeight.Bold)
        )
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.productDetail),
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                findNavController().navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 62.dp)
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val productLocalDb =
                                product?.asProductLocalDb(varianName, varianPrice)
                            val productCheckout = arrayListOf(
                                productLocalDb?.asCheckoutProduct(
                                    varianName ?: "RAM 16GB",
                                    varianPrice
                                )
                            )
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT) {
                                param(FirebaseAnalytics.Param.ITEM_ID, productLocalDb?.productId!!)
                                param(FirebaseAnalytics.Param.ITEM_NAME, productLocalDb.productName)
                            }
                            val listCart = productCheckout
                            val bundle = bundleOf("data_product" to listCart)
                            findNavController().navigate(
                                R.id.action_detailProductFragment_to_checkoutFragment,
                                bundle
                            )
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                            .weight(1F, true)

                    ) {
                        Text(
                            text = stringResource(id = R.string.buyASAP),
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Button(
                        onClick = {
                            cartViewModel = CartViewModel(requireContext())
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                val productLocalDb =
                                    product?.asProductLocalDb(varianName, varianPrice)
                                val checkProductExist =
                                    cartViewModel.getCartById(productLocalDb?.productId!!)
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                                    param(FirebaseAnalytics.Param.ITEM_ID, productLocalDb.productId)
                                    param(
                                        FirebaseAnalytics.Param.ITEM_NAME,
                                        productLocalDb.productName
                                    )
                                    param(FirebaseAnalytics.Param.CURRENCY, "IDR")
                                }

                                if (checkProductExist?.productId != null) {
                                    if (checkProductExist.quantity < checkProductExist.stock!!) {
                                        counter = checkProductExist.quantity
                                        counter++
                                        cartViewModel.updateCartItemQuantity(
                                            checkProductExist.productId,
                                            counter
                                        )
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = context?.getString(R.string.emptyStock)!!,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                } else {
                                    cartViewModel.addToCart(
                                        productLocalDb.productId,
                                        productLocalDb.productName,
                                        productLocalDb.productPrice,
                                        productLocalDb.image,
                                        productLocalDb.store,
                                        productLocalDb.sale,
                                        productLocalDb.stock,
                                        productLocalDb.totalRating,
                                        productLocalDb.productRating,
                                        varianName ?: "RAM 16GB",
                                        varianPrice
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context?.getString(R.string.snackbar_text)!!,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .padding(start = 8.dp)
                            .weight(1F, true)
                    ) {
                        Text(
                            text = stringResource(id = R.string.addCart),
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EcommerceTheme {
                        DetailContent(product)
                    }
                }
            }
        )
    }

    @OptIn(
        ExperimentalFoundationApi::class,
        ExperimentalGlideComposeApi::class,
        ExperimentalLayoutApi::class,
        ExperimentalMaterial3Api::class
    )
    @Composable
    fun DetailContent(product: GetProductDetailItemResponse) {
        val poppins = FontFamily(
            Font(R.font.poppins_medium, FontWeight.Medium),
            Font(R.font.poppins_semibold, FontWeight.SemiBold),
            Font(R.font.poppins_bold, FontWeight.Bold)
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_NAME, product.productName)
            param(FirebaseAnalytics.Param.CURRENCY, product.productPrice.toString())
            param(FirebaseAnalytics.Param.VALUE, product.productPrice.toString())
        }

        var selectedVariantName: String by remember {
            var data = ""
            if(data_product!=null){
                data = data_product?.variantName!!
            }else{
                data = product.productVariant.firstOrNull()?.variantName ?: "16GB"
            }
            mutableStateOf(
                data
            )
        }
        var selectedVariantPrice: Int by remember {
            var data = 0
            if(data_product!=null){
                data = data_product?.variantPrice!!
            }else{
                data = 0
            }
            mutableStateOf(data)
        }
        varianName = selectedVariantName
        varianPrice = selectedVariantPrice

        val isDarkMode = isSystemInDarkTheme()

        val pageCount = product.image.size
        val pagerState = rememberPagerState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            repeat(1) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    HorizontalPager(
                        pageCount = pageCount,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) { page ->
                        GlideImage(
                            model = product.image.getOrElse(page) { "" },
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 340.dp)
                            .fillMaxWidth()
                            .background(color = Color.Transparent),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pageCount) { iteration ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = if (pagerState.currentPage == iteration) primaryColor() else Color.LightGray,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val convert =
                        CurrencyUtils.formatRupiah(product.productPrice.plus(selectedVariantPrice))
                    Text(
                        text = "$convert",
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 20.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        firebaseAnalytics.logEvent("btn_share_clicked", null)
                        val deepLink = "Product : ${product?.productName}\n" +
                                "Price : ${CurrencyUtils.formatRupiah(product?.productPrice)}\n" +
                                "Link : http://ecommerce.com/products/${data_product?.productId}"

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, deepLink)

                        startActivity(Intent.createChooser(shareIntent, null))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = if (isDarkMode) {
                                Color.White
                            } else {
                                Color.Black
                            },
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }
                    var icon by remember {
                        mutableStateOf(
                            if (isChecked) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            isChecked = !isChecked
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                wishlistViewModel = WishlistViewModel(requireContext())
                                val wishlistLocalDb =
                                    product.asWishlistProduct(
                                        selectedVariantName,
                                        selectedVariantPrice
                                    )
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST) {
                                    param(
                                        FirebaseAnalytics.Param.ITEM_ID,
                                        wishlistLocalDb.productId
                                    )
                                    param(
                                        FirebaseAnalytics.Param.ITEM_NAME,
                                        wishlistLocalDb.productName
                                    )
                                    param(FirebaseAnalytics.Param.CURRENCY, "IDR")
                                }
                                if (isChecked) {
                                    wishlistViewModel.addToCart(
                                        wishlistLocalDb.productId,
                                        wishlistLocalDb.productName,
                                        wishlistLocalDb.productPrice,
                                        wishlistLocalDb.image,
                                        wishlistLocalDb.store,
                                        wishlistLocalDb.sale,
                                        wishlistLocalDb.stock,
                                        wishlistLocalDb.totalRating,
                                        wishlistLocalDb.productRating,
                                        varianName ?: "RAM 16GB",
                                        varianPrice
                                    )
                                    icon = Icons.Default.Favorite
                                } else {
                                    wishlistViewModel.removeWishlist(wishlistLocalDb?.productId!!)
                                    icon = Icons.Default.FavoriteBorder
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Favorite",
                            tint = if (isDarkMode) {
                                if (isChecked) {
                                    Color.Red
                                } else {
                                    Color.White
                                }
                            } else {
                                if (isChecked) {
                                    Color.Red
                                } else {
                                    Color.Black
                                }
                            },
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        text = "${product.productName}",
                        fontSize = 14.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.sold) + " " + product.sale,
                            fontSize = 12.sp
                        )
                        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                            SuggestionChip(
                                onClick = {
                                },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .defaultMinSize(minHeight = 0.dp, minWidth = 0.dp)
                                    .noRippleClickable {}
                                    .clickable(enabled = false) {},
                                label = { RatingChipText(text = "${product.productRating} (${product.sale})") },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_star_24),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(18.dp)
                                            .height(18.dp)
                                            .wrapContentHeight(Alignment.CenterVertically),
                                    )
                                },
                            )
                        }
                    }
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                        text = stringResource(id = R.string.chooseVarian),
                        fontFamily = poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )

                    FlowRow(Modifier.padding(start = 8.dp)) {
                        product.productVariant.forEach { variant ->
//                            if(data_product!=null){
//                                if(data_product?.variantName != "RAM 16GB"){
//                                    selectedVariantName = data_product?.variantName!!
//                                }else{
//                                    selectedVariantName = variant.variantName
//                                }
//                            }
                            val isSelected = selectedVariantName == variant.variantName

                            InputChip(
                                label = {
                                    Text(text = variant.variantName)
                                },
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        selectedVariantName = variant.variantName
                                        selectedVariantPrice = variant.variantPrice
                                    }
                                },
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                        text = stringResource(id = R.string.productDesc),
                        fontFamily = poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        text = "${product.description}",
                        fontSize = 14.sp
                    )
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.buyerReview),
                            fontFamily = poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            firebaseAnalytics.logEvent("btn_seeAll_review_clicked", null)
                            val bundle = bundleOf("id_product" to product.productId)
                            (requireActivity() as MainActivity).goToDetailReview(bundle)
                        }) {
                            Text(
                                text = stringResource(id = R.string.lookAll),
                                fontFamily = poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = if (isDarkMode) {
                            Color.White
                        } else {
                            Color.Black
                        },
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .width(24.dp)
                            .height(24.dp),
                    )
                    val customTextStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = poppins,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "${product.productRating}",
                        fontSize = 20.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        style = customTextStyle
                    )
                    Text(
                        text = "/5.0",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = 48.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Text(
                                text = "${product.totalSatisfaction}%",
                                style = customTextStyle,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                            )
                            Text(
                                text = stringResource(id = R.string.buyerSatisfy),
                                style = customTextStyle,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                        ) {
                            Text(
                                text = "${product.totalRating} " + stringResource(id = R.string.rate),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                            )
                            Text(
                                text = ".",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "${product.totalReview} " + stringResource(id = R.string.ulasan),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RatingChipText(text: String) {
        Text(
            text = text,
            fontSize = 12.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClick()
        }
    }

    private object NoRippleTheme : RippleTheme {
        @Composable
        override fun defaultColor() = Color.Unspecified

        @Composable
        override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
    }

    @Composable
    fun LoadingScreen(isLoading: Boolean) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp),
                    color = primaryColor(),
                    strokeWidth = 4.dp
                )
            }
        }
    }

    @Composable
    fun primaryColor(): Color {
        return colorResource(id = R.color.purple)
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMessageCard() {
        DetailContent(product = product!!)
    }
}
