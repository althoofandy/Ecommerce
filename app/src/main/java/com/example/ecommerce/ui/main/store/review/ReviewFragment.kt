package com.example.ecommerce.ui.main.store.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentReviewBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository


class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private val sharedPref by lazy {
        SharedPref(requireContext())
    }
    private val repository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }

    private val factory by lazy {
        ViewModelFactory(repository, sharedPref)
    }

    private val viewModel: ProductReviewViewModel by viewModels { factory }
    private lateinit var adapter: ProductReviewAdapter

    private var id_product:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        id_product = arguments?.getString("id_product")
        _binding = FragmentReviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getReview()
        onBackPressed()
    }
    private fun getReview(){
        adapter = ProductReviewAdapter()
        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvReview.layoutManager = linearLayout
        binding.rvReview.adapter = adapter
        val accessToken = sharedPref.getAccessToken() ?: throw Exception("Token not available")
        viewModel.getProductReview(accessToken,id_product).observe(viewLifecycleOwner){
            when(it){
                is Result.Success ->{
                    val data = it.data.data
                    adapter.submitList(data)
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
    }

    private fun onBackPressed(){
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}