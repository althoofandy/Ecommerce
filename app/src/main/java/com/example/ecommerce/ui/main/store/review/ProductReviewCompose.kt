package com.example.ecommerce.ui.main.store.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.model.GetProductReviewItemResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.ui.theme.EcommerceTheme
import java.lang.Math.ceil
import java.lang.Math.floor

class ProductReviewCompose : Fragment() {
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
    private val viewModel: ProductReviewViewModel by viewModels { factory }
    private var id_product: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        id_product = arguments?.getString("id_product")
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (!id_product.isNullOrBlank()) {
                    viewModel.setProductId(id_product)
                }
                EcommerceTheme {
                    val productDetailReviewState by viewModel.getReviewProduct.observeAsState()
                    productDetailReviewState.let {
                        when (it) {
                            is Result.Success -> {
                                val data = it.data.data
                                MaterialTheme {
                                    ReviewCompose(data)
                                }
                            }

                            is Result.Loading -> {

                            }

                            is Result.Error -> {

                            }

                            null -> {

                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ReviewCompose(listReview: List<GetProductReviewItemResponse>) {
        val poppins = FontFamily(
            Font(R.font.poppins_medium, FontWeight.Medium),
            Font(R.font.poppins_semibold, FontWeight.SemiBold),
            Font(R.font.poppins_bold, FontWeight.Bold)
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Ulasan Pembeli",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal,
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
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(Color.White),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Review(listReview)
                }
            }
        )

    }


    @Composable
    fun Review(listReview: List<GetProductReviewItemResponse>) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(listReview) { reviewItem ->
                ListItem(reviewItem)
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ListItem(data: GetProductReviewItemResponse) {
        val poppins = FontFamily(
            Font(R.font.poppins_regular, FontWeight.Normal),
            Font(R.font.poppins_medium, FontWeight.Medium),
            Font(R.font.poppins_semibold, FontWeight.SemiBold),
            Font(R.font.poppins_bold, FontWeight.Bold)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    model = data.userImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(
                            CircleShape
                        )
                ) {
                    it.load(data.userImage).error(R.drawable.default_product)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = data.userName,
                        fontFamily = poppins,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSystemInDarkTheme()) darkColorScheme().onBackground else lightColorScheme().onBackground,
                    )
                    Row {
                        repeat(times = 5) { iteration ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if ((data.userRating ?: 0) > iteration) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.size(12.dp)
                            )

                        }
                    }

                }
            }

            Text(
                text = data.userReview,
                fontFamily = poppins,
                fontWeight = FontWeight.Normal,
                color = if (isSystemInDarkTheme()) darkColorScheme().onBackground else lightColorScheme().onBackground,
            )
        }

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun RatingBar(
        rating: Double,
        stars: Int = 5,
        starsColor: Color = if (isSystemInDarkTheme()) darkColorScheme().onBackground else lightColorScheme().onBackground,
    ) {
        val filledStars = floor(rating).toInt()
        val unfilledStars = (stars - ceil(rating)).toInt()
        Row {
            repeat(filledStars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = starsColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            repeat(unfilledStars) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = starsColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
//        ListItem()
    }
}

