package com.example.chatapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.adapter.ViewPagerAdapter
import com.example.chatapp.databinding.FragmentFriendBinding
import com.example.chatapp.model.Users
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class FriendFragment : Fragment(){

    private lateinit var friendBinding: FragmentFriendBinding
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var rvUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var fbauth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        friendBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_friend,container,false)
        return friendBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ViewPagerAdapter(this)
        friendBinding.viewPager.adapter = adapter
        TabLayoutMediator(friendBinding.tabLayout,friendBinding.viewPager){tab,position ->
            when(position){
                0 ->{
                    tab.text = "Friend"
                }
                1 ->{
                    tab.text = "All"
                }
                else ->{
                    tab.text = "Request"
                }
            }
        }.attach()
//        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
//        fbauth = FirebaseAuth.getInstance()
//        friendBinding.lifecycleOwner = viewLifecycleOwner
//        userAdapter = UserAdapter()
//        rvUsers = view.findViewById(R.id.rvUsers)
//        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//        rvUsers.layoutManager = layoutManager
////
//        userViewModel.getUsers().observe(viewLifecycleOwner) {
//            it?.let {
//                userAdapter.setUserList(it)
//                rvUsers.adapter = userAdapter
//            }
//        }
//        userAdapter.setOnUserClickListener(this)
    }
//    override fun onUserSelected(position: Int, users: Users) {
//        val intent = Intent(requireActivity(), ChatActivity::class.java)
//        intent.putExtra("userid", users.userid)
//        startActivity(intent)
//    }
}