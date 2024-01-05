@file:Suppress("DEPRECATION")

package com.example.chatapp.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.TAG
import com.example.chatapp.Utils
import com.example.chatapp.databinding.ActivityEditProfileBinding
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {
    private lateinit var editProfileBinding: ActivityEditProfileBinding
    private lateinit var editProfileViewModel: ChatAppViewModel
    private lateinit var storageRef: StorageReference
    lateinit var store: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private var uri: Uri? = null

    private lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        editProfileViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        editProfileBinding.viewModel = editProfileViewModel
        editProfileBinding.lifecycleOwner = this

        store = FirebaseStorage.getInstance()
        storageRef = store.reference

        editProfileViewModel.imageUrl.observe(this) {
            loadImage()
        }

        editProfileBinding.save.setOnClickListener {
            editProfileViewModel.updateProfile()
            finish()
        }
        editProfileBinding.cancel.setOnClickListener {
            finish()
        }

        editProfileBinding.changeAvatarButton.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {

                        takePhotoWithCamera()


                    }

                    options[item] == "Choose from Gallery" -> {
                        pickImageFromGallery()
                    }

                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }



    }


@SuppressLint("QueryPermissionsNeeded")
private fun pickImageFromGallery() {

    val pickPictureIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
        startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
    }
}

    // To take a photo with the camera, you can use this code
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)


    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    Log.d(TAG, ": ${imageUri.toString()}")
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }


    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {

        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        bitmap = imageBitmap!!

        editProfileBinding.settingUpdateImage.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->


            val task = taskSnapshot.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it
                editProfileViewModel.imageUrl.value = uri.toString()


            }






            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage() {
        editProfileViewModel.imageUrl.observe(this) {
            Glide.with(this).load(it).placeholder(R.drawable.person).dontAnimate()
                .into(editProfileBinding.settingUpdateImage)
            Log.d(TAG, "loadImage: $it")
        }
    }

    override fun onResume() {
        loadImage()

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


}