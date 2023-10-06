package com.example.ecommerce.ui.main.transaction.paymentmethod

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.core.model.PaymentMethodItemResponse
import com.example.ecommerce.core.model.PaymentMethodResponse
import com.example.ecommerce.databinding.FragmentPaymentMethodBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.GsonBuilder

class PaymentMethodFragment : Fragment() {
    private var _binding: FragmentPaymentMethodBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PaymentMethodAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        getData()
    }

    private fun initEvent() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun getData() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(requireActivity()) {
                displayListPayment()

            }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys)
                if (configUpdate.updatedKeys.contains("payment")) {
                    remoteConfig.activate().addOnCompleteListener {
                        displayListPayment()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })
    }

    private fun displayListPayment() {
        val remoteConfig = Firebase.remoteConfig
        val dataPayment = GsonBuilder().create().fromJson(
            remoteConfig[PAYMENT_PARAM].asString(),
           PaymentMethodResponse::class.java
        )
        binding.progressCircular.hide()

        adapter = PaymentMethodAdapter(dataPayment.data)
        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvPaymentMethods.layoutManager = linearLayout
        binding.rvPaymentMethods.adapter = adapter

        adapter.setItemClickListener(object :
            PaymentMethodAdapter.PaymentMethodItemClickListener {
            override fun onItemClick(item: PaymentMethodItemResponse) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO) {
                    param(FirebaseAnalytics.Param.ITEM_NAME, item.label)
                }
                val bundle = bundleOf("payment" to item)
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "payment",
                    bundle
                )
                findNavController().popBackStack()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PAYMENT_PARAM = "payment"
    }
}
