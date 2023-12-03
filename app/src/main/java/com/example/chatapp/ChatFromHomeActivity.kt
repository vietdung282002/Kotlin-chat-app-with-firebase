package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.databinding.ActivityChatFromHomeBinding
import com.example.chatapp.model.Messages
import com.example.chatapp.model.RecentChats
import com.example.chatapp.model.Users
import com.example.chatapp.mvvm.ChatAppViewModel

class ChatFromHomeActivity : AppCompatActivity() {
    private lateinit var chatBinding: ActivityChatFromHomeBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatFromHomeBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)
        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        val bundle = intent.getBundleExtra("bundle")
        val chat = bundle?.getParcelable<RecentChats>("chat")

        chatBinding.chatBackBtn.setOnClickListener{
            finish()
        }

        Glide.with(this).load(chat?.friendsImage).into(chatBinding.chatImageViewUser)

        chatBinding.chatUserStatus.text =  chat?.status
        chatBinding.chatUserName.text = chat?.name

        chatBinding.viewModel = viewModel
        chatBinding.lifecycleOwner = this

        chatBinding.sendBtn.setOnClickListener {
            if(chatBinding.editTextMessage.text.isNotEmpty()){
                viewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    chat?.friendId!!,
                    chat.name!!,
                    chat.friendsImage!!
                )
            }
        }


        viewModel.getMessage(chat?.friendId!!).observe(this){
            initRecycleView(it)
        }


    }
    private fun initRecycleView(it: List<Messages>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter
    }
}