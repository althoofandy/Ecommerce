package com.example.ecommerce

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.example.ecommerce.databinding.ActivityMainBinding
import com.example.ecommerce.pref.SharedPref

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPref


    private val navHost by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }
    private val navController by lazy {
        navHost.navController
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref(this)
        AppCompatDelegate.setDefaultNightMode(
            if (sharedPref.getDarkTheme()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        checkSession()
    }


    fun logOut() {
            sharedPref.logout()
            navController.navigate(R.id.action_main_to_prelog)
    }

    fun goToDetailProduct(bundle: Bundle) {
        navController.navigate(R.id.action_main_to_detailproductFragment, bundle)
    }
    fun goToDetailProductFromCart(bundle: Bundle) {
        navController.navigate(R.id.action_cartFragment_to_detailtFragment, bundle)
    }

    fun goToSuccess(bundle: Bundle) {
        navController.navigate(R.id.action_mainTransac_to_successFragment, bundle)
    }

    fun goToDetailReview(bundle: Bundle) {
        navController.navigate(R.id.action_detailProductFragment_to_reviewFragment, bundle)
    }

    fun checkUsernameExist() {
        navController.navigate(R.id.action_main_to_profileFragment)
    }

    private fun checkSession() {
            val token = sharedPref.getAccessToken()
            if (token == null) {
                navController.navigate(R.id.action_main_to_prelog)
            }
    }

    fun profileToPrelog() {
            navController.navigate(R.id.action_addProfileFragment_to_prelogin_navigation)
    }


}