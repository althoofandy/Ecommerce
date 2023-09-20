package com.example.ecommerce.ui.main.transaction.successpayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.MainFragment
import com.example.ecommerce.R
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentSuccessPaymentBinding
import com.example.ecommerce.model.PaymentDataResponse
import com.example.ecommerce.model.Rating
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.main.CurrencyUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@ExperimentalBadgeUtils class SuccessPaymentFragment : Fragment() {
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
    private var originFragment: String? = null
    private var rating: Int? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        arguments.let {
            dataPayment = it?.getParcelable("invoice")
            originFragment = it?.getString("originFragment")
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
                firebaseAnalytics.logEvent("btn_checkout_clicked",null)
                progressCircular.visibility = View.VISIBLE
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
                            progressCircular.hide()
                            if(originFragment!=null){
                                findNavController().navigateUp()
                            }else{
                                findNavController().navigate(R.id.action_successPaymentFragment_to_main_navigation,null)
                            }

                        }

                        is Result.Error -> {
                            progressCircular.hide()
                        }
                        is Result.Loading -> {
                            progressCircular.show()
                        }
                    }
                }
            }
            if(dataPayment?.rating != null){
                rbReview.rating = dataPayment?.rating!!.toFloat()
            }
            tvIdTransaksi.text = dataPayment?.invoiceId
            tvTanggal.text = dataPayment?.date
            tvWaktu.text = dataPayment?.time
            tvMetodePembayaran.text = dataPayment?.payment
            tvTotalPembayaran.text = CurrencyUtils.formatRupiah(dataPayment?.total)
            if (dataPayment!!.status) {
                tvStatus.text = getString(R.string.success)
            } else {
                tvStatus.text = getString(R.string.failed)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}