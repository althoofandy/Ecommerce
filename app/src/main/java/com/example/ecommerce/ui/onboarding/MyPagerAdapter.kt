package com.example.ecommerce.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.databinding.ItemOnboardingBinding

class MyPagerAdapter(private val items: List<Int>) :
    RecyclerView.Adapter<MyPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {
            binding.ivOnboardingPhotos.setImageResource(item)
        }
    }
}