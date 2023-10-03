package com.example.ecommerce.ui.main.transaction.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ItemListPaymentBinding
import com.example.ecommerce.model.PaymentMethodItemResponse

class PaymentAdapter(
    private val nestedDataList: List<PaymentMethodItemResponse>,
    private var itemClickListener: PaymentItemClickListener,
) :
    RecyclerView.Adapter<PaymentAdapter.NestedViewHolder>() {
    interface PaymentItemClickListener {
        fun onItemClick(item: PaymentMethodItemResponse)
    }

    fun setItemClickListener(listener: PaymentItemClickListener) {
        itemClickListener = listener
    }

    inner class NestedViewHolder(private val binding: ItemListPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentMethodItemResponse) {
            if (item.status == false) {
                binding.root.alpha = 0.5f
                binding.root.setOnClickListener(null)
            }
            binding.apply {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(ivBank)
                tvBankName.text = item.label
                binding.root.setOnClickListener {
                    itemClickListener?.onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val binding =
            ItemListPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = NestedViewHolder(binding)

        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(nestedDataList[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val item = nestedDataList[position]
        holder.bind(item)
    }

    override fun getItemCount() = nestedDataList.size
}
