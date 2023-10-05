package com.example.ecommerce.ui.prelogin.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.core.SharedPref
import com.example.ecommerce.core.di.Retrofit
import com.example.ecommerce.core.model.Auth
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.repos.EcommerceRepository
import com.example.ecommerce.ui.Result
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
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
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var tokenFcm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        checkFirstInstall()
        getTokenFcm()
    }

    private val viewModel: LoginViewModel by viewModels { factory }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.isEnabled = false
        doLogin()
        doRegister()
        checkField()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getTokenFcm() {
        Firebase.messaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    tokenFcm = token
                    val msg = "Generate Token succes, $token"
                    Log.d("MainActivity", msg)

                    Firebase.messaging.subscribeToTopic("promo")
                        .addOnCompleteListener { task1 ->
                            var msg1 = "Subscribed"
                            if (!task1.isSuccessful) {
                                msg1 = "Subscribe failed"
                            }
                            Log.d("MainActivity Subs", msg1)
                        }
                } else {
                    Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
            },
        )
    }

    private fun doLogin() {
        binding.apply {
            btnLogin.setOnClickListener {
                progressCircular.visibility = View.VISIBLE
                val email = binding.tieEmail.text.toString()
                val password = binding.tiePassword.text.toString()
                viewModel.doLogin(
                    "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                    Auth(email, password, tokenFcm)
                )
                    .observe(viewLifecycleOwner) {
                        when (it) {
                            is Result.Success -> {
                                progressCircular.hide()
                                findNavController().navigate(R.id.action_prelog_to_mainFragment)
                            }

                            is Result.Error -> {
                                progressCircular.hide()
                                Toast.makeText(
                                    requireContext(),
                                    "Invalid email or password!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is Result.Loading -> {
                                progressCircular.show()
                            }
                        }
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                            param(FirebaseAnalytics.Param.METHOD, "email")
                        }
                    }
            }
        }
    }

    private fun doRegister() {
        binding.apply {
            btnRegister.setOnClickListener {
                firebaseAnalytics.logEvent("btn_login_toRegiste_clicked", null)
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkField() {
        binding.apply {
            tieEmail.addTextChangedListener(LoginWatcher)
            tiePassword.addTextChangedListener(LoginWatcher)
        }
    }

    val LoginWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            binding.tiePassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.length < 8) {
                        binding.etPassword.error = getString(R.string.error_password)
                    } else {
                        binding.etPassword.error = null
                    }
                    if (s.isEmpty()) {
                        binding.etPassword.error = null
                    }
                }
            })
            binding.tieEmail.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (!isValidEmail(s)) {
                        binding.etEmail.error = getString(R.string.error_email)
                    } else {
                        binding.etEmail.error = null
                    }
                    if (s.isEmpty()) {
                        binding.etEmail.error = null
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            val email: String = binding.tieEmail.text.toString()
            val password: String = binding.tiePassword.text.toString()
            binding.btnLogin.isEnabled =
                email.isNotEmpty() && isValidEmail(email) && password.length >= 8
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private fun checkFirstInstall() {
        val isFirstInstall = sharedPref.getIsFirstInstall()
        if (isFirstInstall) {
            findNavController().navigate(R.id.action_loginFragment_to_onboardingFragment)
        }
    }
}
