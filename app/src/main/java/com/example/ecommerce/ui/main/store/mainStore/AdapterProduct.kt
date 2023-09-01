package com.example.ecommerce.ui.main.store.mainStore

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemProductGridBinding
import com.example.ecommerce.databinding.ItemProductLinearBinding
import com.example.ecommerce.model.GetProductsItemResponse
import com.example.ecommerce.ui.main.store.CurrencyUtils
import com.example.ecommerce.ui.main.store.search.SearchAdapter

class AdapterProduct(
    private val context: Context
) : PagingDataAdapter<GetProductsItemResponse, RecyclerView.ViewHolder>(ProductComparator) {
    var item = true
    var currentViewType = ViewType.LINEAR
    private var onItemClickCallback: SearchAdapter.OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: SearchAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (getItemViewType(viewType)) {
            LINEAR -> {
                val binding = ItemProductLinearBinding.inflate(layoutInflater, parent, false)
                LinearViewHolder(binding)
            }

            GRID -> {
                val binding = ItemProductGridBinding.inflate(layoutInflater, parent, false)
                GridViewHolder(binding)
            }

            else -> {
                throw IllegalArgumentException("Invalid ViewType")
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (currentViewType) {
            ViewType.LINEAR -> LINEAR
            ViewType.GRID -> GRID
        }
    }

    fun toggleLayoutViewType() {
        currentViewType = if (currentViewType == ViewType.GRID) ViewType.LINEAR else ViewType.GRID
    }

    @SuppressLint("NotifyDataSetChanged")

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            when (holder) {
                is LinearViewHolder -> {
                    holder.bind(data)
                }

                is GridViewHolder -> {
                    holder.bind(data)
                }
            }
        }
    }

    inner class LinearViewHolder(private val binding: ItemProductLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: GetProductsItemResponse) {
            item = true
            binding.apply {
                Glide.with(context)
                    .load(product.image)
                    .into(ivProduct)
                tvProductName.text = product.productName
                tvProductPrice.text = CurrencyUtils.formatRupiah(product.productPrice)
                tvStore.text = product.store
                tvRating.text = product.productRating.toString()
                tvSell.text = product.sale.toString()
            }
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(product.productId)
            }
        }
    }

    inner class GridViewHolder(private val binding: ItemProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: GetProductsItemResponse) {

            item = false
            binding.apply {
                Glide.with(context)
                    .load(product.image)
                    .into(ivProduct)
                tvProductName.text = product.productName
                tvProductPrice.text = CurrencyUtils.formatRupiah(product.productPrice)
                tvStore.text = product.store
                tvRating.text = product.productRating.toString()
                tvSell.text = product.sale.toString()
            }
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(product.productId)
            }

        }

    }

    companion object {
        const val LINEAR = 1
        const val GRID = 0
    }
}

object ProductComparator : DiffUtil.ItemCallback<GetProductsItemResponse>() {
    override fun areItemsTheSame(
        oldItem: GetProductsItemResponse,
        newItem: GetProductsItemResponse
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: GetProductsItemResponse,
        newItem: GetProductsItemResponse
    ): Boolean {
        return oldItem == newItem
    }
}




