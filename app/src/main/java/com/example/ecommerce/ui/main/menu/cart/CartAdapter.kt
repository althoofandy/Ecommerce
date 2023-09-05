package com.example.ecommerce.ui.main.menu.cart

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ItemCartBinding
import com.example.ecommerce.model.ProductLocalDb
import com.example.ecommerce.ui.main.CurrencyUtils
import com.google.android.material.snackbar.Snackbar

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
            binding.include.tvCounter.text = counterQuantity.toString()
        }

        private var counterQuantity = 0
        @SuppressLint("ResourceAsColor")
        fun bind(item: ProductLocalDb) {

            binding.apply {
                counterQuantity = item.quantity
                updateCounter()
                tvProductName.text = item.productName
                tvVarian.text = item.variantName
                tvSisaProduk.text = "Sisa ${item.stock}"
                if(item.stock!! <= 5){
                    tvSisaProduk.setTextColor(Color.RED)
                }else{
                    tvSisaProduk.setTextColor(R.color.stockColor)
                }
                val sum = item.productPrice.plus(item.variantPrice!!)
                adsPrice.text = CurrencyUtils.formatRupiah(sum)

                Glide.with(root.context)
                    .load(item.image)
                    .into(ivProductCart)

                deleteButton.setOnClickListener {
                    cartViewModel.removeFromCart(item.productId)
                }

                include.btnDecrease.setOnClickListener {
                    if(counterQuantity <= 1){
                        cartViewModel.removeFromCart(item.productId)
                    }else{
                        counterQuantity--
                        updateCounter()
                        cartViewModel.updateCartItemQuantity(item.productId,counterQuantity)
                        include.btnIncrease.isSelected = false
                    }
                }

                include.btnIncrease.setOnClickListener {
                    if(item.stock <= counterQuantity){
                        val contextView = binding.root
                        Snackbar.make(
                            contextView,
                            "Stok Habis!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        counterQuantity++
                        updateCounter()
                        cartViewModel.updateCartItemQuantity(item.productId,counterQuantity)
                        include.btnIncrease.isSelected = false
                    }
                }

                binding.checkBox2.isChecked = item.selected

                binding.checkBox2.setOnClickListener {
                    cartViewModel.updateCartItemCheckbox(listOf(item.productId), checkBox2.isChecked)
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