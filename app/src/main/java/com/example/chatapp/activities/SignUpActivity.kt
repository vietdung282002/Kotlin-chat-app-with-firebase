@file:Suppress("DEPRECATION")

package com.example.chatapp.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var signUpAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password:String
    private lateinit var signUpPd: ProgressDialog

    private val emailRegex = Regex(
        "^\\s*([a-zA-Z0-9\\.\\+\\_\\-]+)@([a-zA-Z0-9\\.\\-]+)\\.\\w{2,6}\\s*$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        firestore = FirebaseFirestore.getInstance()
        signUpAuth = FirebaseAuth.getInstance()
        signUpPd = ProgressDialog((this))

        signUpBinding.signUpTextToSignIn.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signUpBinding.signUpBtn.setOnClickListener {
            name = signUpBinding.signUpEtName.text.toString()
            email = signUpBinding.signUpEmail.text.toString()
            password = signUpBinding.signUpPassword.text.toString()



            if(signUpBinding.signUpEmail.text.isEmpty()){
                signUpBinding.signUpEmail.error = "Please Enter your Email"
            }else{
                if(!isValidEmail(signUpBinding.signUpEmail.text.toString())){
                    signUpBinding.signUpEmail.error = "Please Enter valid Email"
                }
            }
            if(signUpBinding.signUpPassword.text.isEmpty()){
                signUpBinding.signUpPassword.error = "Please Enter your Password"
            }

            if(signUpBinding.signUpPassword.text.length < 6){
                signUpBinding.signUpPassword.error = "Password should be at least 6 characters"
            }

            if(signUpBinding.signUpEtName.text.isEmpty()){
                signUpBinding.signUpEtName.error = "Please Enter your name"
            }

            if(signUpBinding.signUpEmail.text.isNotEmpty() &&
                signUpBinding.signUpPassword.text.isNotEmpty() &&
                signUpBinding.signUpEtName.text.isNotEmpty() &&
                isValidEmail(signUpBinding.signUpEmail.text.toString()) &&
                signUpBinding.signUpPassword.text.length > 6){
                signUpUser(name, email, password)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }

    private fun signUpUser(name: String, email: String, password: String) {
        signUpPd.show()
        signUpPd.setMessage("Signing Up")

        signUpAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            val emptyArray = arrayListOf<String>()
            if(it.isSuccessful){
                val user = signUpAuth.currentUser
                val hashMap = hashMapOf(
                    "userid" to user!!.uid,
                    "username" to name,
                    "useremail" to email,
                    "status" to "Offline",
                    "imageUrl" to "https://www.pngarts.com/files/3/Avatar-Free-PNG-Image.png",
                    "friendLists" to emptyArray,
                    "friendRequested" to emptyArray,
                    "friendRequests" to emptyArray
                    )


                firestore.collection("Users").document(user.uid).set(hashMap)
                signUpPd.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }
    }
}