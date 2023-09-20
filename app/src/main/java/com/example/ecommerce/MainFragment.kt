package com.example.ecommerce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce.databinding.FragmentMainBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.ui.main.menu.cart.CartViewModel
import com.example.ecommerce.ui.main.menu.notification.NotificationViewModel
import com.example.ecommerce.ui.main.wishlist.WishlistViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils.attachBadgeDrawable
import com.google.android.material.badge.ExperimentalBadgeUtils

@ExperimentalBadgeUtils
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val navHost by lazy {
        childFragmentManager.findFragmentById(R.id.navview) as NavHostFragment
    }

    private val navController by lazy {
        navHost.findNavController()
    }
    private val sharedPref by lazy {
        SharedPref(requireContext())
    }
    private lateinit var cartViewModel: CartViewModel
    private lateinit var wishViewModel: WishlistViewModel
    private lateinit var notificationViewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSession()
        checkUserNameExist()
        setBadge()

        binding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.notification -> {
                        findNavController().navigate(R.id.action_main_to_notifFragment)
                    }

                    R.id.cart -> {
                        findNavController().navigate(R.id.action_main_to_cartFragment)
                    }

                    R.id.menu -> {}
                }
                true
            }
            binding.bottomNav.setupWithNavController(navController)
            binding.bottomNav.setOnItemReselectedListener { }
        }
    }


    private fun setBadge() {
        cartViewModel = CartViewModel(requireContext())
        wishViewModel = WishlistViewModel(requireContext())
        notificationViewModel = NotificationViewModel(requireContext())

        cartViewModel.getCartItem()?.observe(viewLifecycleOwner) {
            val badgeDrawable = BadgeDrawable.create(requireContext())
            val numberOfItemsInCart = it.size
            badgeDrawable.number = numberOfItemsInCart

            if (numberOfItemsInCart > 0) {
                attachBadgeDrawable(badgeDrawable, binding.topAppBar, R.id.cart)
            } else {
                badgeDrawable.clearNumber()
            }
        }

        wishViewModel.getWishlistProduct()?.observe(viewLifecycleOwner) {
            val numberOfItemsInCart = it.size
            val bottomBadge = binding.bottomNav.getOrCreateBadge(R.id.wishlistFragment)
            if (numberOfItemsInCart > 0) {
                bottomBadge.number = numberOfItemsInCart
                bottomBadge.isVisible = true
            } else {
                bottomBadge.isVisible = false
                bottomBadge.clearNumber()
            }
        }

        notificationViewModel.getAllNotification()?.observe(viewLifecycleOwner) {
            val unread = it.filter {  !it.isRead }
            val badgeDrawable = BadgeDrawable.create(requireContext())
            val numberOfItemsInCart = unread.size
            badgeDrawable.number = numberOfItemsInCart

            if (numberOfItemsInCart > 0) {
                attachBadgeDrawable(badgeDrawable, binding.topAppBar, R.id.notification)
            } else {
                badgeDrawable.clearNumber()
            }
        }

    }

    private fun checkSession() {
        val session = sharedPref.getAccessToken()
        if (session.isNullOrEmpty()) {
            (requireActivity() as MainActivity).logOut()
        }
    }

    private fun checkUserNameExist() {
        val userName = sharedPref.getNameProfile()
        if (userName.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_main_to_profileFragment)
        } else {
            binding.tvUserName.text = userName
        }
    }

}