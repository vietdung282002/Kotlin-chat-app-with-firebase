package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.util.Util
import com.example.chatapp.Utils
import com.example.chatapp.model.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList(): LiveData<List<RecentChats>> {
        val mainChatList = MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation${Utils.getUiLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }
                val chatList = mutableListOf<RecentChats>()
                value?.forEach{
                    val recentModal = it.toObject(RecentChats::class.java)

                    if(recentModal.sender.equals(Utils.getUiLoggedIn())){
                        recentModal.let {
                            chatList.add(it)
                        }
                    }
                }
                mainChatList.value = chatList
            }

        return mainChatList
    }
}