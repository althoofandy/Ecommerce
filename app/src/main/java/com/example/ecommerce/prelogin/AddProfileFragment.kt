package com.example.ecommerce.prelogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentAddProfileBinding
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class AddProfileFragment : Fragment() {
    private var _binding: FragmentAddProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnDone.setOnClickListener {
                findNavController().navigate(R.id.action_addProf_to_mainFragment)
            }
            cvPhoto.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.choose_pict)
                    .setItems(R.array.choose_pict) { dialog, which ->

                    }
//                    .setBackground(R.drawable.)
                    .show()


            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}