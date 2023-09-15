package com.example.ecommerce.ui.prelogin.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.ecommerce.api.Result
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.model.Auth
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository

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
        ViewModelFactory(repository,sharedPref)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkFirstInstall()
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun doLogin() {
        binding.apply {
            btnLogin.setOnClickListener {
                progressCircular.visibility = View.VISIBLE
                val email = binding.tieEmail.text.toString()
                val password = binding.tiePassword.text.toString()
                viewModel.doLogin("6f8856ed-9189-488f-9011-0ff4b6c08edc", Auth(email, password, ""))
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

                    }
            }
        }
    }

    private fun doRegister() {
        binding.apply {
            btnRegister.setOnClickListener {
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
                    after: Int
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
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            val email: String = binding.tieEmail.text.toString()
            val password: String = binding.tiePassword.text.toString()
            binding.btnLogin.isEnabled = email.isNotEmpty() && isValidEmail(email) && password.length >= 8

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