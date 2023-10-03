package com.example.ecommerce.ui.main.store.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ItemReviewProductBinding
import com.example.ecommerce.model.GetProductReviewItemResponse

class ProductReviewAdapter :
    ListAdapter<GetProductReviewItemResponse, ProductReviewAdapter.ProductReviewViewHolder>(
        ProductReviewDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductReviewViewHolder {
        val binding =
            ItemReviewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductReviewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ProductReviewViewHolder(private val binding: ItemReviewProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetProductReviewItemResponse) {
            binding.tvBuyerName.text = item.userName
            binding.tvReviewBuyer.text = item.userReview
            binding.tvRatingBuyer.rating = item.userRating.toFloat()
            Glide.with(binding.root.context)
                .load(item.userImage)
                .into(binding.ivImageBuyer)

            if (item.userImage.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(R.drawable.default_product)
                    .into(binding.ivImageBuyer)
            } else {
                Glide.with(binding.root.context)
                    .load(item.userImage)
                    .into(binding.ivImageBuyer)
            }
        }
    }
}

class ProductReviewDiffCallback : DiffUtil.ItemCallback<GetProductReviewItemResponse>() {
    override fun areItemsTheSame(
        oldItem: GetProductReviewItemResponse,
        newItem: GetProductReviewItemResponse,
    ): Boolean {
        return oldItem.userName == newItem.userName
    }

    override fun areContentsTheSame(
        oldItem: GetProductReviewItemResponse,
        newItem: GetProductReviewItemResponse,
    ): Boolean {
        return oldItem == newItem
    }
}
