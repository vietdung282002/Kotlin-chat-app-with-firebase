package com.example.chatapp.fragment.friendfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.adapter.AllUserAdapter
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.databinding.FragmentAllFriendBinding
import com.example.chatapp.model.OtherUser
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth

class AllFriendFragment : Fragment(), OnUserClickListener {
    private lateinit var allFriendBinding: FragmentAllFriendBinding
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var rvUsers: RecyclerView
    private lateinit var friendUserAdapter: AllUserAdapter
    private lateinit var fbauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        allFriendBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_friend, container, false)
        return allFriendBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()
        allFriendBinding.lifecycleOwner = viewLifecycleOwner
        friendUserAdapter = AllUserAdapter()
        rvUsers = allFriendBinding.rvUsers
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvUsers.layoutManager = layoutManager

        userViewModel.friendLists.observe(viewLifecycleOwner) {
            it?.let { friendList ->
                userViewModel.getFriendUsers(friendList).observe(viewLifecycleOwner) { list ->
                    list?.let {
                        friendUserAdapter.setUserList(it)
                        rvUsers.adapter = friendUserAdapter
                    }
                }
            }
        }
        friendUserAdapter.setOnUserClickListener(this)
    }

    override fun onUserSelected(position: Int, otherUser: OtherUser) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra("userid", otherUser.userid)
        startActivity(intent)
    }

    override fun OnAddFriendClicked(position: Int, otherUser: OtherUser) {

    }

    override fun OnAcceptFriendClicked(position: Int, otherUser: OtherUser) {

    }

    override fun OnCancelClicked(position: Int, otherUser: OtherUser) {

    }
}