package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.MyApplication
import com.example.chatapp.SharedPrefs
import com.example.chatapp.Utils
import com.example.chatapp.model.Messages
import com.example.chatapp.model.RecentChats
import com.example.chatapp.model.Users
import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    private val usersRepo = UsersRepo()
    private val messageRepo = MessageRepo()
    private val chatListRepo = ChatListRepo()
//
//    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        throwable.printStackTrace()
//    }
    init {
        getCurrentUser()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }


    //Get current user information
    private fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext

        firestore.collection("Users").document(Utils.getUiLoggedIn())
            .addSnapshotListener { value, _ ->
                if (value!!.exists()) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!

                    val mySharedPrefs = SharedPrefs(context)
                    mySharedPrefs.setValue("username", users.username)

                }
            }
    }


    //Send message
    fun sendMessage(sender: String, receiver: String, friendName: String, friendImage: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = MyApplication.instance.applicationContext

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendNameSplit = friendName.split("\\s".toRegex())[0]
            val mySharedPrefs = SharedPrefs(context)
            mySharedPrefs.setValue("friendId", receiver)
            mySharedPrefs.setValue("chatroomId", uniqueId.toString())
            mySharedPrefs.setValue("friendname", friendNameSplit)
            mySharedPrefs.setValue("friendImage", friendImage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener {

                    val hashMapForRecent = hashMapOf<String, Any>(
                        "friendId" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUiLoggedIn(),
                        "message" to message.value!!,
                        "friendsImage" to friendImage,
                        "name" to friendName,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUiLoggedIn()}").document(receiver)
                        .set(hashMapForRecent)

                    firestore.collection("Conversation${receiver}").document(Utils.getUiLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )

                    if (it.isSuccessful) {
                        message.value = ""
                    }
                }

//            val mysharedPrefs = SharedPrefs(context)
//            val friendid = mysharedPrefs.getValue("friendid")
//
//            val hashMapUpdate = hashMapOf<String, Any>("friendsimage" to imageUrl.value!!, "name" to name.value!!, "person" to name.value!!)
//
//
//
//            // updating the chatlist and recent list message, image etc
//
//            firestore.collection("Conversation${friendid}").document(Utils.getUiLoggedIn()).update(hashMapUpdate)
//
//            firestore.collection("Conversation${Utils.getUiLoggedIn()}").document(friendid!!).update("person", "you")


        }

    fun getMessage(friendid: String): LiveData<List<Messages>> {
        return messageRepo.getMessage(friendid)
    }

    fun getRecentChats(): LiveData<List<RecentChats>> {
        return chatListRepo.getAllChatList()
    }
}