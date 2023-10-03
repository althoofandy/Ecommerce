package com.example.ecommerce.ui.main.wishlist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemWishlistGridBinding
import com.example.ecommerce.databinding.ItemWishlistLinearBinding
import com.example.ecommerce.model.WishlistProduct
import com.example.ecommerce.ui.main.CurrencyUtils
import com.example.ecommerce.ui.main.store.mainStore.ViewType
import com.google.firebase.analytics.FirebaseAnalytics

class WishlistAdapter(
    private val context: Context,
    private val wishlistViewModel: WishlistViewModel,
    private val firebaseAnalytics: FirebaseAnalytics,
) : ListAdapter<WishlistProduct, RecyclerView.ViewHolder>(ProductComparator) {
    var item = true
    var currentViewType = ViewType.LINEAR

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (getItemViewType(viewType)) {
            LINEAR -> {
                val binding = ItemWishlistLinearBinding.inflate(layoutInflater, parent, false)
                LinearViewHolder(binding)
            }

            GRID -> {
                val binding = ItemWishlistGridBinding.inflate(layoutInflater, parent, false)
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

    inner class LinearViewHolder(private val binding: ItemWishlistLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: WishlistProduct) {
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
                binding.root.setOnClickListener {
                    onItemClickCallback?.onItemClickCard(product.productId)
                }
                btnAddToCart.setOnClickListener {
                    onItemClickCallback?.onItemClick(product)
                }
                btnDelete.setOnClickListener {
                    wishlistViewModel.removeWishlist(product.productId)
                }
            }
        }
    }

    inner class GridViewHolder(private val binding: ItemWishlistGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: WishlistProduct) {
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
                binding.root.setOnClickListener {
                    onItemClickCallback?.onItemClickCard(product.productId)
                }
                btnAddToCart.setOnClickListener {
                    onItemClickCallback?.onItemClick(product)
                }
                btnDelete.setOnClickListener {
                    firebaseAnalytics.logEvent("btn_delete_wishlist_clicked", null)
                    wishlistViewModel.removeWishlist(product.productId)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClick(position: WishlistProduct)
        fun onItemClickCard(data: String)
    }

    companion object {
        const val LINEAR = 1
        const val GRID = 0
    }
}

object ProductComparator : DiffUtil.ItemCallback<WishlistProduct>() {
    override fun areItemsTheSame(
        oldItem: WishlistProduct,
        newItem: WishlistProduct,
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: WishlistProduct,
        newItem: WishlistProduct,
    ): Boolean {
        return oldItem == newItem
    }
}
