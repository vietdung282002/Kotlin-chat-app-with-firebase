@file:Suppress("DEPRECATION")

package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
        signInBinding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

//        if(auth.currentUser != null){
//            startActivity(Intent(this,MainActivity::class.java))
//        }

        progressDialogSignIn = ProgressDialog(this)

        signInBinding.signInTextToSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        signInBinding.loginButton.setOnClickListener {
            email = signInBinding.loginetemail.text.toString()
            password = signInBinding.loginetpassword.text.toString()

            if(signInBinding.loginetemail.text.isEmpty()){
                Toast.makeText(this,"Please enter your email to login",Toast.LENGTH_SHORT).show()
            }else if(signInBinding.loginetpassword.text.isEmpty()){
                Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show()
            }else{
                signIn(email,password)
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        progressDialogSignIn.dismiss()
        finish()
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

                startActivity(Intent(this,MainActivity::class.java))
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
                    Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}