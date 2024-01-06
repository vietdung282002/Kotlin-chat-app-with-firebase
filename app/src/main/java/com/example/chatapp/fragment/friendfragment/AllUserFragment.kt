package com.example.chatapp.fragment.friendfragment

import android.content.Intent
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
import com.example.chatapp.Utils.Companion.FRIEND
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.adapter.AllUserAdapter
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.databinding.FragmentAllUserBinding
import com.example.chatapp.model.OtherUser
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth

class AllUserFragment : Fragment(), OnUserClickListener {

    private lateinit var allUserBinding: FragmentAllUserBinding
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var rvUsers: RecyclerView
    private lateinit var allUserAdapter: AllUserAdapter
    private lateinit var fbauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        allUserBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_all_user, container, false)
        return allUserBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()
        allUserBinding.lifecycleOwner = viewLifecycleOwner
        allUserAdapter = AllUserAdapter()
        rvUsers = view.findViewById(R.id.rvUsers)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvUsers.layoutManager = layoutManager


        userViewModel.friendLists.observe(viewLifecycleOwner) {
            updateUsersData()
        }

        userViewModel.friendRequests.observe(viewLifecycleOwner) {
            updateUsersData()
        }

        userViewModel.friendRequested.observe(viewLifecycleOwner) {
            updateUsersData()
        }
        allUserAdapter.setOnUserClickListener(this@AllUserFragment)

    }

    private fun updateUsersData() {

        userViewModel.getUsers(
            userViewModel.friendLists.value ?: arrayListOf<String>(),
            userViewModel.friendRequested.value ?: arrayListOf<String>(),
            userViewModel.friendRequests.value ?: arrayListOf<String>()
        ).observe(viewLifecycleOwner) {

            allUserAdapter.setUserList(it)
            rvUsers.adapter = allUserAdapter
        }

    }

    override fun onUserSelected(position: Int, otherUser: OtherUser) {
        if (otherUser.relation == FRIEND) {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra("userid", otherUser.userid)
            startActivity(intent)
        }

    }

    override fun OnAddFriendClicked(position: Int, otherUser: OtherUser) {
        if (otherUser.userid != null) {
            userViewModel.addFriend(Utils.getUidLoggedIn(), otherUser.userid)
        }
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
