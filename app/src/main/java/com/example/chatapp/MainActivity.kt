package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.fragment.HomeFragment
import com.example.chatapp.fragment.SettingFragment
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth

const val TAG = "Find error"

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var fbauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()

        viewModel.imageUrl.observe(this){
            it?.let{
                Glide.with(this).load(it).into(binding.tlImage)
            }
        }

        binding.logOut.setOnClickListener {
            fbauth.signOut()
            startActivity(Intent(this,SignInActivity::class.java))
        }


        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFrag.navController

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        }
        else {
            // If we are on the Home fragment, exit the app
            if (navController.currentDestination?.id == R.id.homeFragment) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }

    fun hideView(){
        binding.bottomNavigationView.visibility = View.GONE
        binding.toolbarMain.visibility = View.GONE
    }

    fun showView(){
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.toolbarMain.visibility = View.VISIBLE
    }
}