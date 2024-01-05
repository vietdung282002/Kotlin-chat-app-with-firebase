package com.example.chatapp.fragment.friendfragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.FragmentAllUserBinding
import com.example.chatapp.model.Users
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth

class AllUserFragment : Fragment(), OnUserClickListener {

    private lateinit var allUserBinding: FragmentAllUserBinding
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var rvUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var fbauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        allUserBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_user,container,false)
        return allUserBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()
        allUserBinding.lifecycleOwner = viewLifecycleOwner
        userAdapter = UserAdapter()
        rvUsers = view.findViewById(R.id.rvUsers)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvUsers.layoutManager = layoutManager

        userViewModel.getUsers().observe(viewLifecycleOwner) {
            it?.let {
                userAdapter.setUserList(it)
                rvUsers.adapter = userAdapter
            }
        }
        userAdapter.setOnUserClickListener(this)
    }
    override fun onUserSelected(position: Int, users: Users) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra("userid", users.userid)
        startActivity(intent)
    }
}