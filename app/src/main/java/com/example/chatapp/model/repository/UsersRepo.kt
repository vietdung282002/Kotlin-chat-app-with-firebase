package com.example.chatapp.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.Utils
import com.example.chatapp.Utils.Companion.FRIEND
import com.example.chatapp.Utils.Companion.FRIEND_REQUEST
import com.example.chatapp.Utils.Companion.REQUESTED_FRIEND
import com.example.chatapp.model.dataclass.OtherUser
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUsers(
        friendList: ArrayList<String>,
        friendRequested: ArrayList<String>,
        friendRequests: ArrayList<String>,
    ): LiveData<List<OtherUser>> {
        val users = MutableLiveData<List<OtherUser>>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            val usersList = mutableListOf<OtherUser>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(OtherUser::class.java)

                if (user!!.userid != Utils.getUidLoggedIn()) {

                    user.let {
                        if (it.userid in friendList) {
                            it.relation = FRIEND
                        }
                        if (it.userid in friendRequests) {
                            it.relation = FRIEND_REQUEST
                        }
                        if (it.userid in friendRequested) {
                            it.relation = REQUESTED_FRIEND
                        }
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }

    fun getUserById(userId: String): LiveData<OtherUser> {
        val users = MutableLiveData<OtherUser>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            snapshot?.documents?.forEach { document ->
                val user = document.toObject(OtherUser::class.java)

                if (user!!.userid != Utils.getUidLoggedIn() && user.userid == userId) {
                    user.let {
                        users.value = it
                    }
                }

            }
        }
        return users
    }

    fun getFriendUsers(friendList: ArrayList<String>): LiveData<List<OtherUser>> {
        val users = MutableLiveData<List<OtherUser>>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            val usersList = mutableListOf<OtherUser>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(OtherUser::class.java)

                if (user!!.userid != Utils.getUidLoggedIn() && user.userid in friendList) {

                    user.let {
                        it.relation = FRIEND
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }

    fun getFriendRequestedUsers(friendRequested: ArrayList<String>): LiveData<List<OtherUser>> {
        val users = MutableLiveData<List<OtherUser>>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            val usersList = mutableListOf<OtherUser>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(OtherUser::class.java)

                if (user!!.userid != Utils.getUidLoggedIn() && user.userid in friendRequested) {

                    user.let {
                        it.relation = REQUESTED_FRIEND
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }

    fun getFriendRequestsUsers(friendRequests: ArrayList<String>): LiveData<List<OtherUser>> {
        val users = MutableLiveData<List<OtherUser>>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            val usersList = mutableListOf<OtherUser>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(OtherUser::class.java)

                if (user!!.userid != Utils.getUidLoggedIn() && user.userid in friendRequests) {

                    user.let {
                        it.relation = FRIEND_REQUEST
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }
}