package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.chatapp.views.activities.ChatActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.viewmodel.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

const val TAG = "Find error"

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        viewModel.imageUrl.observe(this) {
            it?.let {
                Glide.with(this).load(it).into(binding.tlImage)
            }
        }

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFrag.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        generateToken()
        if (intent.getStringExtra("userid") != null) {
            val id = intent.getStringExtra("userid")
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userid", id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Online")

        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Online")
        }
    }

    override fun onPause() {
        super.onPause()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (auth.currentUser != null) {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")
            Log.d("vietdung282002", "onDestroy: ")
        }
    }

    private fun generateToken() {
        val firebaseInstance = FirebaseInstallations.getInstance()

        firebaseInstance.id.addOnSuccessListener { installationid ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener { gettoken ->

                token = gettoken

                val hashMap = hashMapOf<String, Any>("token" to token)

                firestore.collection("Tokens").document(Utils.getUidLoggedIn()).set(hashMap)
                    .addOnSuccessListener {

                    }
            }
        }.addOnFailureListener {}
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            // If we are on the Home fragment, exit the app
            if (navController.currentDestination?.id == R.id.homeFragment) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }

}