package com.example.ecommerce.ui.main.store.detailproduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemProductDetailBinding

class ProductPagerAdapter(private val items: List<String>) :
    RecyclerView.Adapter<ProductPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProductDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemProductDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.root.context)
                .load(item)
                .into(binding.ivProductDetailPhotos)
        }
    }
}
