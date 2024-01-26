@file:Suppress("DEPRECATION")

package com.example.chatapp.views.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        progressDialogSignIn = ProgressDialog(this)

        if(auth.currentUser != null){
            progressDialogSignIn.show()
            progressDialogSignIn.setMessage("Loading")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        signInBinding.signInTextToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        signInBinding.loginButton.setOnClickListener {
            email = signInBinding.loginetemail.text.toString()
            password = signInBinding.loginetpassword.text.toString()

            if(signInBinding.loginetemail.text.isEmpty()){
                Log.d("vietdung282002", "onCreate: ")
                Toast.makeText(this,"Please enter your email to login",Toast.LENGTH_LONG).show()
            }else if(signInBinding.loginetpassword.text.isEmpty()){
                Toast.makeText(this,"Please enter your password",Toast.LENGTH_LONG).show()
            }else{
                signIn(email,password)
            }
        }

        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                progressDialogSignIn.dismiss()
                finish()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()
    }

    private fun signIn(email: String, password: String) {
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Sign In")

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                progressDialogSignIn.dismiss()

                startActivity(Intent(this, MainActivity::class.java))

                finish()
            }else{
                progressDialogSignIn.dismiss()

                Toast.makeText(this,"Email or password Invalid",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {exception ->
            when(exception){
                is FirebaseAuthInvalidCredentialsException ->{
                    Toast.makeText(this,"Email or password Invalid",Toast.LENGTH_SHORT).show()
                }
                else ->{
                    Toast.makeText(this,"Login Failed, please check your internet",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}