package com.example.ecommerce.ui.main.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemTransactionBinding
import com.example.ecommerce.model.TransactionDataResponse
import com.example.ecommerce.ui.main.CurrencyUtils

class TransactionAdapter(private val dataList: List<TransactionDataResponse>) :
    RecyclerView.Adapter<TransactionAdapter.MainViewHolder>() {

    private var itemClickListener: TransactionDataClickListener? = null

    interface TransactionDataClickListener {
        fun onItemClick(label: TransactionDataResponse)
    }

    fun setItemClickListener(listener: TransactionDataClickListener) {
        itemClickListener = listener
    }

    inner class MainViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionDataResponse) {
            binding.apply {
                tvTanggalTransaksi.text = item.date
                tvNamaBarang.text = item.name
                tvTotalBelanja.text = CurrencyUtils.formatRupiah(item.total)
                val countItem = item.items?.size
                tvJmlBarang.text = countItem.toString()

                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(ivBarang)
                if (item.rating == 0 || item.review.isNullOrEmpty()) {
                    btnReviewToResponse.visibility = View.VISIBLE
                } else {
                    btnReviewToResponse.visibility = View.GONE
                }
                btnReviewToResponse.setOnClickListener {
                    itemClickListener?.onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount() = dataList.size
}