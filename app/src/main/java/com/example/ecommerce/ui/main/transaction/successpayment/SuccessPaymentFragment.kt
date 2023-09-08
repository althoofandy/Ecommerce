package com.example.ecommerce.ui.main.transaction.successpayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentSuccessPaymentBinding
import com.example.ecommerce.model.PaymentDataResponse
import com.example.ecommerce.model.Rating
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.CurrencyUtils

class SuccessPaymentFragment : Fragment() {
    private var _binding: FragmentSuccessPaymentBinding? = null
    private val binding get() = _binding!!

    private var dataPayment: PaymentDataResponse? = null
    private val repository: EcommerceRepository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }
    private lateinit var sharedPref: SharedPref
    private lateinit var viewModel: SuccessPaymentViewModel
    private var getreview: String? = ""
    private var rating: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            dataPayment = it?.getParcelable("invoice")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        viewModel = SuccessPaymentViewModel(repository)
        sharedPref = SharedPref(requireContext())
        val accessToken = sharedPref.getAccessToken() ?: throw Exception("token is null")

        binding.apply {
            btnBeliCheckout.setOnClickListener {
                val ratings = rbReview.rating.toInt()
                if(ratings == 0){
                    rating = null
                }else{
                    rating = ratings
                }

                val review = tieReviewDescription.text.toString()
                if (review.isEmpty()) {
                    getreview = null
                } else {
                    getreview = review
                }

                viewModel.doGiveRate(
                    accessToken,
                    Rating(dataPayment!!.invoiceId, rating, getreview)
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is Result.Success -> {
                            findNavController().navigate(R.id.action_successPaymentFragment_to_main_navigation)
                        }

                        is Result.Error -> {}
                        is Result.Loading -> {}
                    }
                }
            }

            tvIdTransaksi.text = dataPayment?.invoiceId
            tvTanggal.text = dataPayment?.date
            tvWaktu.text = dataPayment?.time
            tvMetodePembayaran.text = dataPayment?.payment
            tvTotalPembayaran.text = CurrencyUtils.formatRupiah(dataPayment?.total)
            if (dataPayment!!.status) {
                tvStatus.text = "Berhasil"
            } else {
                tvStatus.text = "Gagal"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}