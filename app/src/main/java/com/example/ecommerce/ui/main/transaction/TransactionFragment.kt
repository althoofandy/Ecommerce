package com.example.ecommerce.ui.main.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.MainActivity
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentTransactionBinding
import com.example.ecommerce.model.TransactionDataResponse
import com.example.ecommerce.model.asPaymentDataResponse
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }
    private lateinit var viewModel: TransactionViewModel
    private lateinit var sharedPref: SharedPref
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    fun getData() {
        binding.progressCircular.visibility = View.VISIBLE
        viewModel = TransactionViewModel(repository)
        sharedPref = SharedPref(requireContext())
        val accessToken = sharedPref.getAccessToken() ?: (requireActivity() as MainActivity).logOut()
        viewModel.getTransactionHistory(accessToken.toString()).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    binding.progressCircular.hide()
                    if(it!=null){
                        binding.linearErrorLayout.visibility = View.GONE
                        adapter = TransactionAdapter(it.data.data)
                        val linearLayout = LinearLayoutManager(requireContext())
                        binding.rvTransaction.layoutManager = linearLayout
                        binding.rvTransaction.adapter = adapter

                        adapter.setItemClickListener(object : TransactionAdapter.TransactionDataClickListener{
                            override fun onItemClick(label: TransactionDataResponse) {
                                val convert = label.asPaymentDataResponse(label.review,label.rating)
                                val bundle = Bundle().apply {
                                    putParcelable("invoice",convert)
                                }
                                (requireActivity() as MainActivity).goToSuccess(bundle)
                            }
                        })
                    }else{
                        binding.linearErrorLayout.visibility = View.VISIBLE
                    }
                }
                is Result.Loading -> {
                    binding.progressCircular.show()
                }

                is Result.Error -> {
                    binding.progressCircular.hide()
                    binding.linearErrorLayout.visibility = View.VISIBLE
                }
            }
        }

    }

}