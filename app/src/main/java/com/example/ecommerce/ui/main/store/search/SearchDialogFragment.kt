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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentSearchDialogBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.SEARCH
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
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
    private var searchString: String? = ""
    private var search: String? = ""
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        search = arguments?.getString(SEARCH)
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
        setSearchDialog()
        initEvent()
    }

    private fun setSearchDialog() {
        adapter = SearchAdapter()
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@SearchDialogFragment.adapter
        }
        if (search.isNullOrEmpty() || search == "null") {
            binding.tieSearch.setText("")
        } else {
            binding.tieSearch.setText(search)
        }

        binding.tieSearch.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event.action == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
                lifecycleScope.launch {
                    delay(1000)
                    setData(textView.text.toString())
                    dismiss()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        val accessToken = sharedPref.getAccessToken()
            ?: (requireActivity() as MainActivity).logOut()
        var job: Job? = null
        binding.tieSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.progressBar.visibility = View.VISIBLE
                searchString = s.toString()
                job?.cancel()
                job = lifecycleScope.launch {
                    delay(1000)
                    viewModel.doSearch(accessToken.toString(), s.toString())
                        .observe(viewLifecycleOwner) {
                            adapter.setSearchProducts(it.data)
                            binding.progressBar.visibility = View.GONE
                        }
                    adapter.setOnItemClickCallback(object : SearchAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: String) {
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS) {
                                param(FirebaseAnalytics.Param.SEARCH_TERM, data)
                            }
                            setData(data)
                            dismiss()
                        }
                    })
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            }
        )
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "SearchDialogFragment"

        @JvmStatic
        fun newInstance(
            search: String?,

        ): SearchDialogFragment {
            val myFragment = SearchDialogFragment()

            val args = Bundle().apply {
                putString(SEARCH, search)
            }
            myFragment.arguments = args

            return myFragment
        }
    }
}
