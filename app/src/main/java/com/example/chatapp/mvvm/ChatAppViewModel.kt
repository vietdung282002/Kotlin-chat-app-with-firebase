package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.MyApplication
import com.example.chatapp.SharedPrefs
import com.example.chatapp.Utils
import com.example.chatapp.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel: ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()

    init {
        getCurrentUser()
    }

    fun getUsers():LiveData<List<Users>>{
        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO){
        val context = MyApplication.instance.applicationContext

        firestore.collection("Users").document(Utils.getUiLoggedIn()).addSnapshotListener { value, error ->
            if(value!!.exists()){
                val users = value.toObject(Users::class.java)
                name.value = users?.username!!
                imageUrl.value = users?.imageUrl!!

                val mySharedPrefs = SharedPrefs(context)
                mySharedPrefs.setValue("username",users.username!!)

            }
        }
    }

}