package com.example.ecommerce.ui.main.store.mainStore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.setFragmentResult
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.Retrofit
import com.example.ecommerce.databinding.BottomSheetBinding
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_CATEGORY
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_HIGHEST
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_LOWEST
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_SORT
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.FILTER
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class BottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    private var sort: String? = null
    private var category: String? = null
    private var lowest: String? = null
    private var highest: String? = null

    private lateinit var viewModel: StoreViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val repository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }
    private val sharedPref by lazy {
        SharedPref(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        category = arguments?.getString(CHIP_CATEGORY)
        sort = arguments?.getString(CHIP_SORT)
        highest = arguments?.getString(CHIP_HIGHEST)
        lowest = arguments?.getString(CHIP_LOWEST)

        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFilter()
        btnReset()
        showSelectedChip()
        btnShowProduct()
    }

    private fun btnShowProduct() {
        binding.btnTampilkan.setOnClickListener {
            sendData()
            dismiss()
            checkFirebaseAnalytic()
        }
    }

    private fun showSelectedChip() {
        viewModel = StoreViewModel(repository, sharedPref)

        binding.apply {
            chipGroupUrutkan.children.forEach { chip ->
                if ((chip as Chip).text == sort) {
                    chip.isChecked = true
                    buttonReset.visibility = View.VISIBLE
                }
            }
            chipGroupKategori.children.forEach { chip ->
                if ((chip as Chip).text == category) {
                    chip.isChecked = true
                    buttonReset.visibility = View.VISIBLE
                }
            }

            if (highest.isNullOrEmpty() || highest == "null") {
                tieHighest.setText("")
            } else {
                tieHighest.setText(highest)
            }

            if (lowest.isNullOrEmpty() || lowest == "null") {
                tieLowest.setText("")
            } else {
                tieLowest.setText(lowest)
            }
        }
    }

    private fun btnReset() {
        binding.apply {
            buttonReset.setOnClickListener {
                chipGroupKategori.clearCheck()
                chipGroupUrutkan.clearCheck()
                tieLowest.text?.clear()
                tieHighest.text?.clear()
                buttonReset.visibility = View.GONE
            }
        }
    }

    private fun loadFilter() {
        binding.apply {
            chipGroupUrutkan.setOnCheckedChangeListener { _, _ ->
                buttonReset.visibility = View.VISIBLE
            }
            chipGroupKategori.setOnCheckedChangeListener { _, _ ->
                buttonReset.visibility = View.VISIBLE
            }
        }
    }

    private fun sendData() {
        binding.apply {
            val sort: Chip? = chipGroupUrutkan.findViewById(chipGroupUrutkan.checkedChipId)
            val category: Chip? = chipGroupKategori.findViewById(chipGroupKategori.checkedChipId)
            val lowest = tieLowest.text.toString()
            val highest = tieHighest.text.toString()
            val bundle = Bundle().apply {
                putString(CHIP_SORT, sort?.text?.toString())
                putString(CHIP_CATEGORY, category?.text?.toString())
                putString(CHIP_LOWEST, lowest)
                putString(CHIP_HIGHEST, highest)
            }
            setFragmentResult(FILTER, bundle)
        }
    }

    private fun checkFirebaseAnalytic() {
        binding.apply {
            val sort: Chip? = chipGroupUrutkan.findViewById(chipGroupUrutkan.checkedChipId)
            val category: Chip? = chipGroupKategori.findViewById(chipGroupKategori.checkedChipId)
            val lowest = tieLowest.text.toString() ?: null
            val highest = tieHighest.text.toString() ?: null

            val sorts = Bundle()
            sorts.putString(FirebaseAnalytics.Param.ITEM_NAME, "sort")
            sorts.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "${sort?.text}")
            val cat = Bundle()
            cat.putString(FirebaseAnalytics.Param.ITEM_NAME, "category")
            cat.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "${category?.text}")
            val lowestAnalytics = Bundle()
            lowestAnalytics.putString(FirebaseAnalytics.Param.ITEM_NAME, "lowest")
            lowestAnalytics.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, lowest)
            val highestAnalytics = Bundle()
            highestAnalytics.putString(FirebaseAnalytics.Param.ITEM_NAME, "highest")
            highestAnalytics.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, highest)
            val params = Bundle()
            params.putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(sorts, cat)
            )

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"

        @JvmStatic
        fun newInstance(
            category: String?,
            sort: String?,
            highest: String?,
            lowest: String?,
        ): BottomSheet {
            val myFragment = BottomSheet()

            val args = Bundle().apply {
                putString(CHIP_SORT, sort)
                putString(CHIP_CATEGORY, category)
                putString(CHIP_HIGHEST, highest)
                putString(CHIP_LOWEST, lowest)
            }
            myFragment.arguments = args

            return myFragment
        }
    }
}
