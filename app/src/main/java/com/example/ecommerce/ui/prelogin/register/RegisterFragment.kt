package com.example.ecommerce.ui.prelogin.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.example.ecommerce.model.Auth
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val userPreference by lazy {
        SharedPref(requireContext())
    }
    private val repository by lazy {
        val apiService = Retrofit(requireContext()).getApiService()
        val sharedPref = SharedPref(requireContext())
        EcommerceRepository(apiService, sharedPref)
    }

    private val factory by lazy {
        ViewModelFactory(repository)
    }

    private val viewModel: RegisterViewModel by viewModels { factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setEnabled(false)
        doLogin()
        doRegister()
        spannable()
        checkField()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun doLogin() {
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun doRegister() {
        binding.apply {
            btnRegister.setOnClickListener {
                val email = binding.tieEmail.text.toString()
                val password = binding.tiePassword.text.toString()
                viewModel.doRegister(
                    "6f8856ed-9189-488f-9011-0ff4b6c08edc",
                    Auth(email, password, "")
                )
                viewModel.registerResponse.observe(viewLifecycleOwner) {
                    if (it != null) {
                        findNavController().navigate(R.id.action_prelog_to_profileFragment)
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    @SuppressLint("ResourceAsColor")
    private fun spannable() {
        val text =
            "Dengan daftar disini, kamu menyetujui Syarat dan Ketentuan serta Kebijakan Privasi TokoPhincon."
        val spannableString = SpannableString(text)
        val color1 = ForegroundColorSpan(R.color.primary)
        spannableString.setSpan(
            color1,
            37, 58, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val color2 = ForegroundColorSpan(R.color.primary)
        spannableString.setSpan(
            color2,
            64, 82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvPolicy.text = spannableString
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

            val email: String = binding.tieEmail.getText().toString()
            val password: String = binding.tiePassword.getText().toString()
            binding.btnRegister.setEnabled(email.isNotEmpty() && isValidEmail(email) && password.length >= 8)
        }

        override fun afterTextChanged(editable: Editable) {}
    }


}