package com.example.ecommerce.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentOnboardingBinding
import com.example.ecommerce.pref.SharedPref
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toRegister()
        toLogin()
        viewPager()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewPager() {
        val items = listOf(R.drawable.img, R.drawable.img_1, R.drawable.img_2)
        val pagerAdapter = MyPagerAdapter(items)
        binding.apply {
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

            binding.btnNextOnBoarding.setOnClickListener {
                val currentItem = binding.viewPager.currentItem
                val nextItem = currentItem + 1

                if (nextItem < items.size) {
                    binding.viewPager.setCurrentItem(nextItem, true)
                }

                if (nextItem == items.size - 1) {
                    binding.btnNextOnBoarding.visibility = View.GONE
                }
            }

            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position < items.size - 1) {
                        binding.btnNextOnBoarding.visibility = View.VISIBLE
                    } else {
                        binding.btnNextOnBoarding.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun toRegister() {
        binding.btnJoinNow.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_registerFragment)
            sharedPref.saveFirstInstall(false)
        }
    }

    private fun toLogin() {
        binding.btnSkipOnBoarding.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            sharedPref.saveFirstInstall(false)
        }
    }
}