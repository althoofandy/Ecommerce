package com.example.ecommerce.ui.main.transaction.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.databinding.ItemListPaymentmethodBinding
import com.example.ecommerce.model.PaymentMethodCategoryResponse
import com.example.ecommerce.model.PaymentMethodItemResponse

class PaymentMethodAdapter(private val dataList: List<PaymentMethodCategoryResponse>) :
    RecyclerView.Adapter<PaymentMethodAdapter.MainViewHolder>() {
    private var itemClickListener: PaymentMethodItemClickListener? = null
    interface PaymentMethodItemClickListener {
        fun onItemClick(label: PaymentMethodItemResponse)
    }

    fun setItemClickListener(listener: PaymentMethodItemClickListener) {
        itemClickListener = listener
    }

    inner class MainViewHolder(private val binding: ItemListPaymentmethodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item:PaymentMethodCategoryResponse){
            binding.apply {
                tvPaymentMethods.text = item.title
                val adapter = PaymentAdapter(item.item, object : PaymentAdapter.PaymentItemClickListener {
                    override fun onItemClick(item: PaymentMethodItemResponse) {
                        if(item.status){
                            itemClickListener?.onItemClick(item)
                        }else{
                            binding.root.setOnClickListener(null)
                        }

                    }
                })
                val linearLayout = LinearLayoutManager(binding.root.context)
                binding.rvPaymentMethods.layoutManager = linearLayout
                binding.rvPaymentMethods.adapter = adapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemListPaymentmethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount() = dataList.size
}