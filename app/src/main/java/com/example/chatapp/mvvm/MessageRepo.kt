package com.example.chatapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.Utils
import com.example.chatapp.model.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {

    private val firestore = FirebaseFirestore.getInstance()

        fun getMessage(friendId: String): LiveData<List<Messages>> {

            val messages = MutableLiveData<List<Messages>>()

            val uniqueId = listOf(Utils.getUidLoggedIn(), friendId).sorted()
            uniqueId.joinToString("")

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
                    if(error != null){
                        return@addSnapshotListener
                    }

                    val messageList = mutableListOf<Messages>()

                    if(!value!!.isEmpty){
                        value.documents.forEach{
                            val messageModal = it.toObject(Messages::class.java)

                            if(messageModal!!.sender.equals(Utils.getUidLoggedIn()) && messageModal.receiver.equals(friendId) ||
                                messageModal.sender.equals(friendId) && messageModal.receiver.equals(Utils.getUidLoggedIn())){

                                messageModal.let { messages1 ->
                                    messageList.add(messages1)
                                }
                            }
                        }
                        messages.value = messageList
                    }
                }
            return messages
        }
}