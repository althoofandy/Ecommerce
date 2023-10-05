package com.example.ecommerce.ui.main.transaction.checkout

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.core.model.CheckoutProduct
import com.example.ecommerce.core.model.PaymentItem
import com.example.ecommerce.databinding.ItemCheckoutBinding
import com.example.ecommerce.ui.main.CurrencyUtils
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import com.google.android.material.snackbar.Snackbar

class CheckoutAdapter(private val cartViewModel: CartViewModel) :
    ListAdapter<CheckoutProduct, CheckoutAdapter.ProductCheckoutViewHolder>(
        ProductCheckoutDiffCallback()
    ) {

    private var itemClickListener: CheckoutClickListener? = null

    interface CheckoutClickListener {
        fun onItemClick(label: List<PaymentItem>)
    }

    fun setItemClickListener(listener: CheckoutClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCheckoutViewHolder {
        val binding =
            ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductCheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductCheckoutViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ProductCheckoutViewHolder(private val binding: ItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updateCounter() {
            binding.include.tvCounter.text = counterQuantity.toString()
        }

        private var counterQuantity = 0

        @SuppressLint("ResourceAsColor")
        fun bind(item: CheckoutProduct) {
            binding.apply {
                counterQuantity = item.quantity
                updateCounter()
                tvProductName.text = item.productName
                tvVarian.text = item.variantName
                tvSisaProduk.text = "${item.stock}"
                if (item.stock!! <= 5) {
                    tvremaining.setTextColor(Color.RED)
                    tvSisaProduk.setTextColor(Color.RED)
                } else {
                    tvremaining.setTextColor(Color.RED)
                    tvSisaProduk.setTextColor(R.color.stockColor)
                }
                val sum = item.productPrice.plus(item.variantPrice!!)
                adsPrice.text = CurrencyUtils.formatRupiah(sum)

                Glide.with(root.context)
                    .load(item.image)
                    .into(ivProductCart)

                include.btnDecrease.setOnClickListener {
                    if (counterQuantity <= 1) {
                    } else {
                        counterQuantity--
                        updateCounter()
                        cartViewModel.updateCartItemQuantity(item.productId, counterQuantity)
                        itemClickListener?.onItemClick(
                            listOf(
                                PaymentItem(
                                    item.productId,
                                    item.variantName,
                                    counterQuantity
                                )
                            )
                        )
                        include.btnIncrease.isSelected = false
                    }
                }

                include.btnIncrease.setOnClickListener {
                    if (item.stock!! <= counterQuantity) {
                        val contextView = binding.root
                        Snackbar.make(
                            contextView,
                            "Stok Habis!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        counterQuantity++
                        updateCounter()
                        cartViewModel.updateCartItemQuantity(item.productId, counterQuantity)
                        itemClickListener?.onItemClick(
                            listOf(
                                PaymentItem(
                                    item.productId,
                                    item.variantName,
                                    counterQuantity
                                )
                            )
                        )
                        include.btnIncrease.isSelected = false
                    }
                }
            }
        }
    }
}

class ProductCheckoutDiffCallback : DiffUtil.ItemCallback<CheckoutProduct>() {
    override fun areItemsTheSame(
        oldItem: CheckoutProduct,
        newItem: CheckoutProduct,
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: CheckoutProduct,
        newItem: CheckoutProduct,
    ): Boolean {
        return oldItem == newItem
    }
}
