package com.example.ecommerce.ui.main.menu.notification

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.core.model.Notification
import com.example.ecommerce.databinding.ItemNotificationBinding

class NotificationAdapter() :
    ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(ProductReviewDiffCallback()) {
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClick(position: Notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class NotificationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val binding = ItemNotificationBinding.bind(itemView)

        @SuppressLint("ResourceType")
        fun bind(item: Notification) {
            binding.apply {
                tvType.text = item.type
                tvBody.text = item.body
                tvDate.text = item.date
                tvTitle.text = item.title
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(ivNotification)
                if (item.isRead) {
                    when (itemView.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES ->
                            binding.notificationListItem.setBackgroundColor(
                                itemView.resources.getColor(
                                    R.color.black,
                                    null
                                )
                            )

                        else -> {
                            notificationListItem.setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.getContext(),
                                    android.R.color.white
                                )
                            )
                        }
                    }
                } else {
                    when (itemView.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES ->
                            binding.notificationListItem.setBackgroundColor(
                                itemView.resources.getColor(
                                    R.color.purple,
                                    null
                                )
                            )
                    }
                }

                binding.root.setOnClickListener {
                    onItemClickCallback?.onItemClick(item)
                }
            }
        }
    }
}

class ProductReviewDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(
        oldItem: Notification,
        newItem: Notification,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Notification,
        newItem: Notification,
    ): Boolean {
        return oldItem == newItem
    }
}
