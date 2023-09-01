package com.example.ecommerce.ui.main.store.mainStore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.setFragmentResult
import com.example.ecommerce.databinding.BottomSheetBinding
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_CATEGORY
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_HIGHEST
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_LOWEST
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.CHIP_SORT
import com.example.ecommerce.ui.main.store.mainStore.StoreFragment.Companion.FILTER
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class BottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    private var sort: String? = null
    private var category: String? = null
    private var lowest: String? = null
    private var highest: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        btnShowProduct()
        showSelectedChip()
    }

    private fun btnShowProduct() {
        binding.btnTampilkan.setOnClickListener {
            sendData()
            dismiss()
        }
    }

    private fun showSelectedChip() {
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

    companion object {
        const val TAG = "ModalBottomSheet"

        @JvmStatic
        fun newInstance(
            category: String?,
            sort: String?,
            highest: String?,
            lowest: String?
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