package com.example.ecommerce.ui.main.menu.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.databinding.FragmentNotificationBinding
import com.example.ecommerce.model.Notification

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val adapter:NotificationAdapter by lazy { NotificationAdapter() }
    private val viewModel: NotificationViewModel by lazy { NotificationViewModel(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvNotification.layoutManager = linearLayout
        binding.rvNotification.adapter = adapter
        viewModel.getAllNotification()?.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
        adapter.setOnItemClickCallback(object:NotificationAdapter.OnItemClickCallback{
            override fun onItemClick(item: Notification) {
                viewModel.updateNotification(Notification(item.id,item.type,item.date,item.title,item.body,item.image,true))
            }

        })

    }

}