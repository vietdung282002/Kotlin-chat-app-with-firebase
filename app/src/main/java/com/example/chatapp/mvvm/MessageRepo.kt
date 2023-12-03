package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.Utils
import com.example.chatapp.model.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getMessage(friendid: String): LiveData<List<Messages>> {

        val messages = MutableLiveData<List<Messages>>()

        val uniqueid = listOf(Utils.getUiLoggedIn(), friendid).sorted()
        uniqueid.joinToString("")

        firestore.collection("Messages").document(uniqueid.toString()).collection("chats")
            .orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
                if(error != null){
                    return@addSnapshotListener
                }

                val messageList = mutableListOf<Messages>()

                if(!value!!.isEmpty){
                    value.documents.forEach{
                        val messageModal = it.toObject(Messages::class.java)

                        if(messageModal!!.sender.equals(Utils.getUiLoggedIn()) && messageModal.receiver.equals(friendid) ||
                            messageModal!!.sender.equals(friendid) && messageModal.receiver.equals(Utils.getUiLoggedIn())){

                            messageModal.let {
                                messageList.add(it!!)
                            }
                        }
                    }
                    messages.value = messageList
                }
            }
        return messages
    }
}