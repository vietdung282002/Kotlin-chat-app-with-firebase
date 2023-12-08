package com.example.chatapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.Utils
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.Messages
import com.example.chatapp.model.Users
import com.example.chatapp.mvvm.ChatAppViewModel


@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {

    private lateinit var chatBinding: ActivityChatBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)
        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        val bundle = intent.getBundleExtra("bundle")
        val users = bundle?.getParcelable<Users>("users")

        chatBinding.chatBackBtn.setOnClickListener{
            finish()
        }

        Glide.with(this).load(users?.imageUrl).into(chatBinding.chatImageViewUser)

        chatBinding.chatUserStatus.text =  users?.status
        chatBinding.chatUserName.text = users?.username

        chatBinding.viewModel = viewModel
        chatBinding.lifecycleOwner = this

        chatBinding.sendBtn.setOnClickListener {
            if(chatBinding.editTextMessage.text.isNotEmpty()){
                viewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    users?.userid!!,
                    users.username!!,
                    users.imageUrl!!
                )
            }
        }


        viewModel.getMessage(users?.userid!!).observe(this){
            initRecycleView(it,users.username,users.imageUrl)
        }


    }
    private fun initRecycleView(it: List<Messages>,userName:String?,imageUrl: String?) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.setUser(userName!!,imageUrl!!)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter
    }
}