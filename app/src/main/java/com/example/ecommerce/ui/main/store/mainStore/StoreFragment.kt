package com.example.ecommerce.ui.main.store.mainStore

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.Retrofit
import com.example.ecommerce.core.model.GetProductsItemResponse
import com.example.ecommerce.databinding.FragmentStoreBinding
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.search.SearchDialogFragment
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Locale

enum class ViewType {
    LINEAR, GRID
}

class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
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

    private val viewModel: StoreViewModel by viewModels { factory }
    private lateinit var adapter: AdapterProduct

    private var search: String? = null
    private var sort: String? = null
    private var category: String? = null
    private var lowest: String? = null
    private var highest: String? = null
    private var isList: Boolean = true
    private var chipData = mutableListOf<String>()
    private lateinit var gridLayoutManager: GridLayoutManager
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

        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            adapter = AdapterProduct(requireContext())
            if (isInternetAvailable(requireContext())) {
                setChiperandFilter()
                getAllProducts()
                setSearch()
                swiperefresh.setOnRefreshListener {
                    adapter.refresh()
                    binding.swiperefresh.isRefreshing = false
                }
                chipFilter.setOnClickListener {
                    firebaseAnalytics.logEvent("chipFilter_clicked", null)
                    val bottomSheet = BottomSheet.newInstance(
                        category.toString(),
                        sort.toString(),
                        highest.toString(),
                        lowest.toString()
                    )
                    bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                }
                tieSearch.setOnClickListener {
                    if (search == null) {
                        viewModel.param.observe(viewLifecycleOwner) {
                            search = it.search
                        }
                    }
                    val fragmentManager = requireActivity().supportFragmentManager
                    val newFragment = SearchDialogFragment.newInstance(
                        search.toString()
                    )
                    val transaction = fragmentManager.beginTransaction()
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction
                        .add(android.R.id.content, newFragment)
                        .addToBackStack(null)
                        .commit()
                    newFragment.show(parentFragmentManager, SearchDialogFragment.TAG)
                }
                ivLayout.setOnClickListener {
                    changeToggle()
                }
            } else {
                shimmerGrid.visibility = View.GONE
                shimmerLinear.visibility = View.GONE
                linearLayout.visibility = View.GONE
                shimmerFilter.visibility = View.GONE

                linearErrorLayout.visibility = View.VISIBLE
                errorTypeText.text = getString(R.string.empty)
                errorTypeInfo.text = getString(R.string.no_internet)
                restartButton.text = getString(R.string.refresh)
                restartButton.setOnClickListener {
                    adapter.refresh()
                }
                rvProduct.visibility = View.GONE
            }
        }
    }

    private fun cekParam() {
        viewModel.param.observe(viewLifecycleOwner) {
            search = it.search
            sort = it.sort
            category = it.brand
            lowest = it.lowest.toString()
            highest = it.highest.toString()
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

    private fun getAllProducts() {
        binding.apply {
            horizontalScrollView.overScrollMode
            adapter = AdapterProduct(requireContext())
            viewModel.products.observe(viewLifecycleOwner) { pagingData ->
                adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                val bundle = Bundle().apply {
                    adapter.snapshot().items.forEach {
                        putString(FirebaseAnalytics.Param.ITEM_ID, it.productId)
                        putString(FirebaseAnalytics.Param.ITEM_NAME, it.productName)
                        putInt(FirebaseAnalytics.Param.PRICE, it.productPrice)
                        putString(FirebaseAnalytics.Param.ITEM_BRAND, it.brand)
                    }
                }

                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
                    param(FirebaseAnalytics.Param.ITEMS, arrayOf(bundle))
                }
            }
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
            binding.rvProduct.layoutManager = gridLayoutManager
            rvProduct.adapter = adapter

            adapter.setOnItemClickCallback(object : AdapterProduct.OnItemClickCallback {
                override fun onItemClicked(data: GetProductsItemResponse) {
                    val bundle = bundleOf("id_product" to data.productId)
                    (requireActivity() as MainActivity).goToDetailProduct(bundle)

                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                        param(FirebaseAnalytics.Param.ITEM_LIST_ID, data.productId)
                        param(FirebaseAnalytics.Param.ITEM_NAME, data.productName)
                    }
                }
            })

            adapter.addLoadStateListener { loadStates: CombinedLoadStates ->
                val isRefreshing = loadStates.refresh is LoadState.Loading
                val isError = loadStates.refresh is LoadState.Error

                if (isError) {
                    linearErrorLayout.visibility = View.VISIBLE
                    val error = (loadStates.refresh as LoadState.Error).error
                    val errorMessage = error.message

                    if (errorMessage?.contains("404") == true) {
                        swiperefresh.visibility = View.GONE
                        errorTypeText.text = getString(R.string.empty)
                        errorTypeInfo.text = getString(R.string.your_requested_data_is_unavailable)
                        restartButton.text = getString(R.string.reset)
                        restartButton.setOnClickListener {
                            sharedPref.getAccessToken()
                                ?: (requireActivity() as MainActivity).logOut()
                            chipGroup.removeAllViews()
                            viewModel.resetParam()
                            cekParam()
                            tieSearch.text?.clear()
                        }
                        rvProduct.visibility = View.GONE
                    } else {
                        swiperefresh.visibility = View.GONE
                        errorTypeText.text = "500"
                        errorTypeInfo.text = getString(R.string.internal_error)
                        restartButton.text = getString(R.string.refresh)
                        restartButton.setOnClickListener {
                            sharedPref.getAccessToken()
                                ?: (requireActivity() as MainActivity).logOut()
                            adapter.refresh()
                        }
                    }
                } else {
                    swiperefresh.visibility = View.VISIBLE
                    rvProduct.visibility = View.VISIBLE
                }
                if (isRefreshing) {
                    linearErrorLayout.visibility = View.GONE
                    swiperefresh.visibility = View.GONE
                    if (isList) {
                        shimmerLinear.visibility = View.VISIBLE
                        shimmerGrid.visibility = View.GONE
                    }
                    if (!isList) {
                        shimmerGrid.visibility = View.VISIBLE
                        shimmerLinear.visibility = View.GONE
                    }
                    horizontalScrollView.visibility = View.GONE
                    chipFilter.visibility = View.GONE
                    linearLayoutToogle.visibility = View.GONE
                } else {
                    shimmerGrid.visibility = View.GONE
                    shimmerLinear.visibility = View.GONE
                    horizontalScrollView.visibility = View.VISIBLE
                    chipFilter.visibility = View.VISIBLE
                    linearLayoutToogle.visibility = View.VISIBLE
                }
                shimmerFilter.visibility =
                    if (loadStates.refresh is LoadState.Loading && adapter.item) View.VISIBLE else if (loadStates.refresh is LoadState.Loading && !adapter.item) View.VISIBLE else View.GONE
            }
            val footerAdapter = LoadingStateAdapter { adapter.retry() }
            rvProduct.adapter = adapter.withLoadStateFooter(footer = footerAdapter)

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position < adapter.itemCount) {
                        1
                    } else {
                        gridLayoutManager.spanCount
                    }
                }
            }
        }
    }

    private fun setChiperandFilter() {
        binding.apply {
            adapter = AdapterProduct(requireContext())
            setFragmentResultListener(FILTER) { _, bundle ->
                sort = bundle.getString(CHIP_SORT)
                category = bundle.getString(CHIP_CATEGORY)
                lowest = bundle.getString(CHIP_LOWEST)
                highest = bundle.getString(CHIP_HIGHEST)
                viewModel.setQuery(
                    category,
                    lowest?.toIntOrNull(),
                    highest?.toIntOrNull(),
                    sort
                )
            }
            viewModel.param.observe(viewLifecycleOwner) {
                if (it != null) {
                    sort = it.sort
                    category = it.brand
                    lowest = it.lowest.toString()
                    highest = it.highest.toString()
                    chipData.clear()
                    if (!category.isNullOrEmpty()) {
                        category.let { chipData.add(it!!) }
                    }
                    if (!sort.isNullOrEmpty()) {
                        sort.let { chipData.add(it!!) }
                    }
                    if (!lowest.isNullOrEmpty()) {
                        val lowestValue = lowest!!.toDoubleOrNull()
                        if (lowestValue != null) {
                            val lowestFormat =
                                NumberFormat.getNumberInstance(Locale("id", "ID"))
                                    .format(lowestValue)
                            val lowestRp =
                                StringBuilder().append("> ").append("Rp").append(lowestFormat)
                                    .toString()
                            lowestRp.let { chipData.add(it) }
                        }
                    }
                    if (!highest.isNullOrEmpty()) {
                        val highestValue = highest!!.toDoubleOrNull()
                        if (highestValue != null) {
                            val highestFormat =
                                NumberFormat.getNumberInstance(Locale("id", "ID"))
                                    .format(highestValue)
                            val highestRp =
                                StringBuilder().append("< ").append("Rp").append(highestFormat)
                                    .toString()
                            highestRp.let { chipData.add(it) }
                        }
                    }
                    intentChip(chipData)
                }
            }
        }
    }

    private fun setSearch() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            FILTER,
            viewLifecycleOwner
        ) { _, bundle ->
            search = bundle.getString(SEARCH)
            binding.tieSearch.setText(search)
            viewModel.setSearch(search)
        }
    }

    private fun intentChip(names: List<String>) {
        binding.apply {
            chipGroup.removeAllViewsInLayout()
            for (name in names) {
                val chip = Chip(requireContext())
                chip.apply {
                    text = name
                    isChipIconVisible = false
                    isCloseIconVisible = false
                    isCheckable = true
                    isChecked = true
                }
                chipGroup.addView(chip as View)
            }
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val FILTER = "filter"
        const val CHIP_SORT = "chipSort"
        const val CHIP_CATEGORY = "chipCategory"
        const val CHIP_LOWEST = "chipLowest"
        const val CHIP_HIGHEST = "chipHighest"
        const val SEARCH = "search"
    }
}
