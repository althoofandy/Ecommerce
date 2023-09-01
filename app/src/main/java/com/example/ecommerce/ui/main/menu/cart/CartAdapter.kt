package com.example.ecommerce.ui.main.menu.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemCartBinding
import com.example.ecommerce.model.ProductLocalDb
import com.example.ecommerce.ui.main.store.CurrencyUtils

class CartAdapter(private val cartViewModel: CartViewModel) :
    ListAdapter<ProductLocalDb, CartAdapter.ProductCartViewHolder>(ProductReviewDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductCartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ProductCartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updateCounter() {
            binding.include.tvCounter.text = counter.toString()
        }

        private var counter = 0
        fun bind(item: ProductLocalDb) {

            binding.apply {
                counter = item.quantity
                updateCounter()
                tvProductName.text = item.productName
                tvVarian.text = item.variantName
                tvSisaProduk.text = "Sisa ${item.stock}"
                val sum = item.productPrice.plus(item.variantPrice!!)
                adsPrice.text = CurrencyUtils.formatRupiah(sum)

                Glide.with(root.context)
                    .load(item.image)
                    .into(ivProductCart)

                deleteButton.setOnClickListener {
                    cartViewModel.removeFromCart(item.productId)
                }

                include.btnDecrease.setOnClickListener {
                    counter--
                    updateCounter()
                    cartViewModel.updateCartItemQuantity(item.productId,counter)
                    include.btnIncrease.isSelected = false
                }

                include.btnIncrease.setOnClickListener {
                    counter++
                    updateCounter()
                    cartViewModel.updateCartItemQuantity(item.productId,counter)
                    include.btnDecrease.isSelected = false
                }

            }
        }
    }
}

class ProductReviewDiffCallback : DiffUtil.ItemCallback<ProductLocalDb>() {
    override fun areItemsTheSame(
        oldItem: ProductLocalDb,
        newItem: ProductLocalDb
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: ProductLocalDb,
        newItem: ProductLocalDb
    ): Boolean {
        return oldItem == newItem
    }
}