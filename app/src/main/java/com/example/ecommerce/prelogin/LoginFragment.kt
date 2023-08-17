package com.example.ecommerce.prelogin

import android.R.attr.button
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
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spannable()
        checkField()
        binding.btnLogin.setEnabled(false)
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_addProf_to_mainFragment)
            }
            btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    @SuppressLint("ResourceAsColor")
    private fun spannable() {
        val text =
            "Dengan masuk disini, kamu menyetujui Syarat dan Ketentuan serta Kebijakan Privasi TokoPhincon."
        val spannableString = SpannableString(text)
        val color1 = ForegroundColorSpan(R.color.primary)
        spannableString.setSpan(
            color1,
            37, 58, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val color2 = ForegroundColorSpan(R.color.primary)
        spannableString.setSpan(
            color2,
            64, 81, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
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
            binding.btnLogin.setEnabled(email.isNotEmpty() && isValidEmail(email) && password.length >= 8)
        }

        override fun afterTextChanged(editable: Editable) {}
    }

}