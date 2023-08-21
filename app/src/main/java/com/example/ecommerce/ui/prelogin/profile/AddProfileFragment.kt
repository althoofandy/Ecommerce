package com.example.ecommerce.ui.prelogin.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.ViewModelFactory
import com.example.ecommerce.api.Retrofit
import com.example.ecommerce.databinding.FragmentAddProfileBinding
import com.example.ecommerce.pref.SharedPref
import com.example.ecommerce.repos.EcommerceRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class AddProfileFragment : Fragment() {
    private var _binding: FragmentAddProfileBinding? = null
    private val binding get() = _binding!!
    private var uri : Uri? = null
    private val sharedPref by lazy {
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
    private val viewModel: ProfileViewModel by viewModels { factory }
    private fun generateFilename() = "photos-${System.currentTimeMillis()}.jpg"
    private fun buildNewUri(): Uri {
        val photosDir = File(requireContext().cacheDir, "photos")
        photosDir.mkdirs()
        val photoFile = File(photosDir, generateFilename())
        val authority = "${requireContext().packageName}.fileprovider"
        return FileProvider.getUriForFile(requireContext(), authority, photoFile)
    }
    val startGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val file = uriToFile(uri, requireContext())
            val reducedFile = reduceFileImage(file)
            setProfile(reducedFile)
            binding.vectorImg.visibility = View.GONE
            Glide.with(binding.root.context)
                .load(uri)
                .into(binding.ivPhoto)
            Log.d("CEK URI",uri.toString())
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    val startCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean? ->
        if (isSuccess == true) {
            binding.vectorImg.visibility = View.GONE
            Glide.with(binding.root.context)
                .load(uri)
                .into(binding.ivPhoto)
            val file = uriToFile(uri!!, requireContext())
            val reducedFile = reduceFileImage(file)
            setProfile(reducedFile)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
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
        spannable()
        btnEnable()
        choosePhoto()

    }
    private fun setProfile(file: File){
        binding.apply {
            btnDone.setOnClickListener {
                val requestUser =
                    MultipartBody.Part.createFormData("userName", binding.tieNama.text.toString())
                val requestImage = MultipartBody.Part.createFormData("userImage", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                val token = sharedPref.getAccessToken()
                viewModel.doProfile(token!!,requestUser,requestImage)
                viewModel.profileResponse.observe(viewLifecycleOwner){
                    if(it!=null){
                        findNavController().navigate(R.id.action_addProfileFragment_to_main_navigation)
                    }else{
                        Toast.makeText(requireContext(), "Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun choosePhoto(){
        binding.apply {
            cvPhoto.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.choose_pict)
                    .setItems(R.array.choose_pict) { dialog, which ->
                        when (which) {
                            0 -> {
                                startCamera()
                            }
                            1 -> {
                                startGallery()
                            }
                        }
                    }
                    .show()
            }
        }
    }
    private fun btnEnable(){
        binding.btnDone.setEnabled(false)
        binding.tieNama.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(s.isEmpty()){
                    binding.btnDone.setEnabled(false)
                }else{
                    binding.btnDone.setEnabled(true)
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

    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        uri = buildNewUri()
        startCamera.launch(uri)
    }
    private fun startGallery(){
        startGallery.launch("image/*")
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("ResourceAsColor")
    private fun spannable(){
        val text = "Dengan daftar disini, kamu menyetujui Syarat dan Ketentuan serta Kebijakan Privasi TokoPhincon."
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
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    requireContext(),
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}