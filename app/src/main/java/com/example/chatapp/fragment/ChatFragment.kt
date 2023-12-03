package com.example.chatapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.Utils
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.model.Messages
import com.example.chatapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {

    private lateinit var args: ChatFragmentArgs
    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chatToolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        chatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return chatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ChatFragmentArgs.fromBundle(requireArguments())

        chatAppViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        circleImageView = chatBinding.chatImageViewUser
        chatToolbar = chatBinding.toolBarChat



        chatBinding.chatBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        Glide.with(requireContext()).load(args.users.imageUrl).into(circleImageView)

        chatBinding.chatUserName.text = args.users.username
        chatBinding.chatUserStatus.text = args.users.status

        chatBinding.viewModel = chatAppViewModel
        chatBinding.lifecycleOwner = viewLifecycleOwner

        chatBinding.sendBtn.setOnClickListener {
            if (chatBinding.editTextMessage.text.toString() != "") {
                chatAppViewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    args.users.userid!!,
                    args.users.username!!,
                    args.users.imageUrl!!
                )
            }
        }

        chatAppViewModel.getMessage(args.users.userid!!).observe(viewLifecycleOwner) {
            initRecycleView(it)
        }
    }

    private fun initRecycleView(it: List<Messages>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter
    }
}