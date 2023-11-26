package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

const val TAG = "Find error"

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.d(TAG, supportFragmentManager.backStackEntryCount.toString())
                if(supportFragmentManager.backStackEntryCount > 0){
                    finish()
                }else {
                    if (navController.currentDestination?.id == R.id.homeFragment) {
                        moveTaskToBack(true)
                    } else {
                        finish()
                    }
                }
            }
        })

        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFrag.navController
    }


}