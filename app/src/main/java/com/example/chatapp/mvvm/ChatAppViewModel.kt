package com.example.chatapp.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.MyApplication
import com.example.chatapp.SharedPrefs
import com.example.chatapp.Utils
import com.example.chatapp.model.Messages
import com.example.chatapp.model.OtherUser
import com.example.chatapp.model.RecentChats
import com.example.chatapp.model.Users
import com.example.chatapp.notifications.entity.NotificationData
import com.example.chatapp.notifications.entity.PushNotification
import com.example.chatapp.notifications.entity.Token
import com.example.chatapp.notifications.network.RetrofitInstance
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    private val userId = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    private val status = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val friendLists = MutableLiveData<ArrayList<String>>()
    val friendRequested = MutableLiveData<ArrayList<String>>()
    val friendRequests = MutableLiveData<ArrayList<String>>()

    private val firestore = FirebaseFirestore.getInstance()

    private val usersRepo = UsersRepo()
    private val messageRepo = MessageRepo()
    private val chatListRepo = ChatListRepo()
    private val mySharedPrefs = SharedPrefs(MyApplication.instance.applicationContext)


    init {
        getCurrentUser()

    }

    //
    fun getUsers(
        friendList: ArrayList<String>?,
        friendRequested: ArrayList<String>?,
        friendRequests: ArrayList<String>?,
    ): LiveData<List<OtherUser>> {
        if(friendList != null &&
            friendRequested!= null &&
            friendRequests!= null){
            return usersRepo.getUsers(friendList, friendRequested, friendRequests)
        }else{
            val list = arrayListOf<String>()
            return usersRepo.getUsers(list, list, list)
        }
    }

    fun getFriendUsers(friendList: ArrayList<String>): LiveData<List<OtherUser>> {
        return usersRepo.getFriendUsers(friendList)
    }

    fun getFriendRequestedUsers(friendRequested: ArrayList<String>): LiveData<List<OtherUser>> {
        return usersRepo.getFriendRequestedUsers(friendRequested)
    }

    fun getFriendRequestsUsers(friendRequests: ArrayList<String>): LiveData<List<OtherUser>> {
        return usersRepo.getFriendRequestsUsers(friendRequests)
    }


    //Get current user information
    private fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, _ ->
                if (value!!.exists()) {
                    val users = value.toObject(Users::class.java)
                    userId.value = users?.userid!!
                    name.value = users.username!!
                    email.value = users.useremail!!
                    status.value = users.status!!
                    imageUrl.value = users.imageUrl!!
                    friendLists.value = users.friendLists!!
                    friendRequested.value = users.friendRequested!!
                    friendRequests.value = users.friendRequests!!
                    mySharedPrefs.setValue("name", users.username)
                    mySharedPrefs.setValue("username", users.imageUrl)
                }
            }
    }

    //Send message
    fun sendMessage(sender: String, receiver: String, friendName: String, friendImage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendNameSplit = friendName.split("\\s".toRegex())[0]
            mySharedPrefs.setValue("friendId", receiver)
            mySharedPrefs.setValue("chatroomId", uniqueId.toString())
            mySharedPrefs.setValue("friendname", friendNameSplit)
            mySharedPrefs.setValue("friendImage", friendImage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->

                    val hashMapForRecent = hashMapOf<String, Any>(
                        "friendId" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to hashMap.get("message")!!,
                        "friendsImage" to friendImage,
                        "name" to friendName,
                        "person" to "you"
                    )
                    val hashMapForReceiverRecent = hashMapOf<String, Any>(
                        "friendId" to sender,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to hashMap.get("message")!!,
                        "friendsImage" to friendImage,
                        "name" to friendName,
                        "person" to name.value!!
                    )


                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashMapForRecent)
                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .set(hashMapForReceiverRecent)

                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, _ ->
                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty() && tokenObject != null) {
                                    PushNotification(
                                        NotificationData(
                                            userId.value!!,
                                            name.value!!,
                                            message.value!!
                                        ), tokenObject.token!!
                                    ).also {
                                        sendNotification(it)
                                    }
                                }
                            }

                        }
                    if (taskmessage.isSuccessful) {

                        message.value = ""
                    }
                }


        }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
        }
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext

        val hashMapUser = hashMapOf<String, Any>(
            "username" to name.value!!,
            "imageUrl" to imageUrl.value!!,
            "useremail" to email.value!!
        )

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()


                }

            }
        val hashMapUpdate = hashMapOf<String, Any>(
            "friendsImage" to imageUrl.value!!, "name" to name.value!!, "person" to name.value!!
        )

        firestore.collection("Conversation${Utils.getUidLoggedIn()}")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                value?.forEach {
                    firestore.collection("Conversation${it.id}").document(Utils.getUidLoggedIn())
                        .update(hashMapUpdate)

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(it.id)
                        .update(
                            "person", "you"
                        )
                }
            }


    }

    fun addFriend(sender: String, receiver: String) = viewModelScope.launch(Dispatchers.IO) {
        firestore.collection("Users").document(sender).update(
            hashMapOf<String, Any>("friendRequested" to FieldValue.arrayUnion(receiver))
        ).addOnSuccessListener {

        }.addOnFailureListener { e ->

        }

        firestore.collection("Users").document(receiver).update(
            hashMapOf<String, Any>("friendRequests" to FieldValue.arrayUnion(sender))
        ).addOnSuccessListener {

        }.addOnFailureListener { e ->

        }
    }

    fun removeRequestFriend(sender: String, receiver: String) = viewModelScope.launch(Dispatchers.IO) {
        firestore.collection("Users").document(sender).update(
            hashMapOf<String, Any>("friendRequested" to FieldValue.arrayRemove(receiver))
        ).addOnSuccessListener {

        }.addOnFailureListener { e ->

        }

        firestore.collection("Users").document(receiver).update(
            hashMapOf<String, Any>("friendRequests" to FieldValue.arrayRemove(sender))
        ).addOnSuccessListener {

        }.addOnFailureListener { e ->

        }
    }

    fun acceptFriendRequest(sender: String, receiver: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("vietdung282002", "$sender, $receiver ")
        firestore.collection("Users").document(sender).update(
            hashMapOf<String, Any>("friendRequests" to FieldValue.arrayRemove(receiver))
        ).addOnSuccessListener {
            firestore.collection("Users").document(sender).update((
                    hashMapOf<String, Any>("friendLists" to FieldValue.arrayUnion(receiver))
                    )).addOnSuccessListener {

            }
        }.addOnFailureListener { e ->

        }

        firestore.collection("Users").document(receiver).update(
            hashMapOf<String, Any>("friendRequested" to FieldValue.arrayRemove(sender))
        ).addOnSuccessListener {
            // Thành công
            firestore.collection("Users").document(receiver).update((
                    hashMapOf<String, Any>("friendLists" to FieldValue.arrayUnion(sender))
                    )).addOnSuccessListener {

            }
        }.addOnFailureListener { e ->

        }
    }

    fun getMessage(friendId: String): LiveData<List<Messages>> {
        return messageRepo.getMessage(friendId)
    }

    fun getUser(id: String): LiveData<OtherUser> {
        return usersRepo.getUserById(id)
    }

    fun getRecentChats(): LiveData<List<RecentChats>> {
        return chatListRepo.getAllChatList()
    }
}