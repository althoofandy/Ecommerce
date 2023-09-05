package com.example.ecommerce.ui.main.store.search

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentSearchDialogBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchDialogFragment : DialogFragment() {
    private var _binding: FragmentSearchDialogBinding? = null
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

    private val viewModel: SearchViewModel by viewModels { factory }
    private lateinit var adapter: SearchAdapter
    private var searchString: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchAdapter()
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@SearchDialogFragment.adapter
        }

        val accessToken = sharedPref.getAccessToken()
            ?: throw NullPointerException("Access token is null")
        var job: Job? = null
        binding.tieSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.progressBar.visibility = View.VISIBLE
                searchString = s.toString()
                job?.cancel()
                job = lifecycleScope.launch {
                    delay(1000)
                    val searchResult = viewModel.doSearch(accessToken, s.toString())
                    searchResult.observe(viewLifecycleOwner) {
                        adapter.setSearchProducts(it)
                        binding.progressBar.visibility = View.GONE
                    }
                    adapter.setOnItemClickCallback(object : SearchAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: String) {
                            setData(data)
                            dismiss()
                        }
                    })
                    binding.tieSearch.setOnKeyListener { _, actionId, event ->
                        if (event.action == KeyEvent.ACTION_DOWN && actionId== KeyEvent.KEYCODE_ENTER) {
                            setData(s.toString())
                            dismiss()
                            return@setOnKeyListener true
                        }
                        return@setOnKeyListener false
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setData(data: String) {
        val bundle = Bundle().apply {
            putString(StoreFragment.SEARCH, data)
        }
        requireActivity().supportFragmentManager.setFragmentResult(
            StoreFragment.FILTER,
            bundle
        )
    }
}

