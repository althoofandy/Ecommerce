package com.example.ecommerce

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref(this)
        checkSession()
        checkFirstInstall()


    }
     fun logOut(){
        navController.navigate(R.id.action_mainNav_to_preLogNav)
    }
    fun checkUsernameExist(){
            navController.navigate(R.id.action_main_to_profileFragment)
    }

    fun checkSession(){
        val token = sharedPref.getAccessToken()
        if(token!=null){
            navController.navigate(R.id.action_prelog_to_mainFragment)
        }
    }
    fun checkFirstInstall(){
        val isFirstInstall = sharedPref.getIsFirstInstall()
        if(isFirstInstall==true){
            navController.navigate(R.id.action_loginFragment_to_onboardingFragment)
        }
    }
}