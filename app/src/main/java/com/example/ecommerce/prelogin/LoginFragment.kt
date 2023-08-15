package com.example.ecommerce.prelogin

import android.R.attr.text
import android.R.id.text2
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spannable()
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_addProf_to_mainFragment)
            }
            btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            tiePassword.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().length < 8){
                        etPassword.error = getString(R.string.error_password)
                    }else{
                        etPassword.error = null
                    }
                    if(s.isNullOrEmpty()){
                        etPassword.error = null
                    }
                }
            })
            tieEmail.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable) {
                    if (!isValidEmail(s)){
                        etEmail.error = getString(R.string.error_email)
                    }else{
                        etEmail.error = null
                    }
                    if(s.isNullOrEmpty()){
                        etEmail.error = null
                    }
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
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
    private fun spannable(){
        val text = "Dengan masuk disini, kamu menyetujui Syarat dan Ketentuan serta Kebijakan Privasi TokoPhincon."
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

}