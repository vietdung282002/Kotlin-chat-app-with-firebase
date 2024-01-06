package com.example.chatapp.fragment.friendfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.Utils
import com.example.chatapp.adapter.AllUserAdapter
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.databinding.FragmentFriendRequestBinding
import com.example.chatapp.model.OtherUser
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth


class FriendRequestFragment : Fragment(), OnUserClickListener {
    private lateinit var friendRequestBinding: FragmentFriendRequestBinding
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var rvRequestFriend: RecyclerView
    private lateinit var rvFriendRequested: RecyclerView
    private lateinit var friendRequestedUserAdapter: AllUserAdapter
    private lateinit var friendRequestsUserAdapter: AllUserAdapter
    private lateinit var fbauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        friendRequestBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friend_request, container, false)
        return friendRequestBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()
        friendRequestBinding.lifecycleOwner = viewLifecycleOwner
        friendRequestedUserAdapter = AllUserAdapter()
        friendRequestsUserAdapter = AllUserAdapter()
        rvRequestFriend = friendRequestBinding.rvFriendRequest
        rvFriendRequested = friendRequestBinding.rvSentFriendRequest
        val friendRequestedLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val requestFriendLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvFriendRequested.layoutManager = friendRequestedLayoutManager
        rvRequestFriend.layoutManager = requestFriendLayoutManager

        userViewModel.friendRequests.observe(viewLifecycleOwner) {
            it?.let { friendRequestList ->
                userViewModel.getFriendRequestsUsers(friendRequestList)
                    .observe(viewLifecycleOwner) { list ->
                        list?.let {
                            friendRequestsUserAdapter.setUserList(it)
                            rvRequestFriend.adapter = friendRequestsUserAdapter
                        }
                    }
            }
        }

        userViewModel.friendRequested.observe(viewLifecycleOwner) {
            it?.let { requestedList ->
                userViewModel.getFriendRequestedUsers(requestedList)
                    .observe(viewLifecycleOwner) { list ->
                        list?.let {
                            friendRequestedUserAdapter.setUserList(it)
                            rvFriendRequested.adapter = friendRequestedUserAdapter
                        }
                    }

            }
        }
        friendRequestsUserAdapter.setOnUserClickListener(this)
        friendRequestedUserAdapter.setOnUserClickListener(this)
    }

    override fun onUserSelected(position: Int, otherUser: OtherUser) {
    }

    override fun OnAddFriendClicked(position: Int, otherUser: OtherUser) {
    }

    override fun OnAcceptFriendClicked(position: Int, otherUser: OtherUser) {
        if (otherUser.userid != null) {
            userViewModel.acceptFriendRequest(Utils.getUidLoggedIn(), otherUser.userid)
        }
    }

    override fun OnCancelClicked(position: Int, otherUser: OtherUser) {
        if (otherUser.userid != null) {
            userViewModel.removeRequestFriend(Utils.getUidLoggedIn(), otherUser.userid)
        }
    }


}