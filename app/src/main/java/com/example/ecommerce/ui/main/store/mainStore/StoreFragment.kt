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
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentStoreBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.search.SearchAdapter
import com.example.ecommerce.ui.main.store.search.SearchDialogFragment
import com.google.android.material.chip.Chip
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
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

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
                    val bottomSheet = BottomSheet.newInstance(
                        category.toString(),
                        sort.toString(),
                        highest.toString(),
                        lowest.toString()
                    )
                    bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                }
                tieSearch.setOnClickListener {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val newFragment = SearchDialogFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction
                        .add(android.R.id.content, newFragment)
                        .addToBackStack(null)
                        .commit()
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
                errorTypeText.text = "Connection"
                errorTypeInfo.text = "Your connection is unavailable"
                restartButton.text = "Refresh"
                restartButton.setOnClickListener {
                    adapter.refresh()
                }
                rvProduct.visibility = View.GONE
            }
        }
    }

    private fun changeToggle() {
        isList = !isList
        val imageRes =
            if (isList) ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_format_list_bulleted_24
            ) else ContextCompat.getDrawable(requireContext(), R.drawable.baseline_grid_view_24)
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
            }
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
            binding.rvProduct.layoutManager = gridLayoutManager
            rvProduct.adapter = adapter

            adapter.setOnItemClickCallback(object : SearchAdapter.OnItemClickCallback {
                override fun onItemClicked(data: String) {
                    val bundle = bundleOf("id_product" to data)
                    (requireActivity() as MainActivity).goToDetailProduct(bundle)
                }
            })

            adapter.addLoadStateListener { loadStates: CombinedLoadStates ->
                val isRefreshing = loadStates.refresh is LoadState.Loading
                val isError = loadStates.refresh is LoadState.Error

                if (isError) {
                    linearErrorLayout.visibility = View.VISIBLE
                    rvProduct.visibility = View.GONE
                    val error = (loadStates.refresh as LoadState.Error).error
                    val errorMessage = error.message

                    if (errorMessage?.contains("404") == true) {
                        errorTypeText.text = "Empty"
                        errorTypeInfo.text = "Your requested data is unavailable"
                        restartButton.text = "Reset"
                        restartButton.setOnClickListener {
                            viewModel.resetParam()
                            tieSearch.text?.clear()
                        }
                        rvProduct.visibility = View.GONE
                    } else {
                        errorTypeText.text = "500"
                        errorTypeInfo.text = "Internal Server Error"
                        restartButton.text = "Refresh"
                        restartButton.setOnClickListener {
                            adapter.refresh()
                        }
                        rvProduct.visibility = View.GONE
                    }
                }
                if (isRefreshing) {
                    linearErrorLayout.visibility = View.GONE
                    rvProduct.visibility = View.GONE
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
                    rvProduct.visibility = View.VISIBLE
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
                val chipData = mutableListOf<String>()
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
                            NumberFormat.getNumberInstance(Locale("id", "ID")).format(lowestValue)
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
                            NumberFormat.getNumberInstance(Locale("id", "ID")).format(highestValue)
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
            chipGroup.removeAllViews()
            for (name in names) {
                val chip = Chip(requireContext())
                chip.apply {
                    text = name
                    isChipIconVisible = false
                    isCloseIconVisible = false
                    isCheckable = true
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

