package com.example.chatapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.Utils
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.Messages
import com.example.chatapp.model.OtherUser
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {

    private lateinit var chatBinding: ActivityChatBinding
    private lateinit var chatViewModel: ChatAppViewModel
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var users: OtherUser
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)
        chatViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val userId = intent.getStringExtra("userid")

        chatViewModel.getUser(userId!!).observe(this) { user ->
            users = user
            chatViewModel.getMessage(user.userid!!).observe(this) {
                initRecycleView(it, user.username, user.imageUrl)
            }
            chatBinding.chatUserStatus.text = user.status
            chatBinding.chatUserName.text = user.username
            Glide.with(this).load(user.imageUrl).into(chatBinding.chatImageViewUser)

        }

        chatBinding.viewModel = chatViewModel
        chatBinding.lifecycleOwner = this

        chatBinding.chatBackBtn.setOnClickListener {
            finish()
        }


        chatBinding.sendBtn.setOnClickListener {
            if (chatBinding.editTextMessage.text.isNotEmpty()) {
                chatViewModel.sendMessage(
                    Utils.getUidLoggedIn(),
                    users.userid!!,
                    users.username!!,
                    users.imageUrl!!
                )
            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycleView(it: List<Messages>, userName: String?, imageUrl: String?) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.setUser(userName!!, imageUrl!!)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter
    }

    override fun onResume() {
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