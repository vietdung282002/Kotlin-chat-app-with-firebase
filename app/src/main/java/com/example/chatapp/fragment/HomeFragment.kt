
package com.example.chatapp.fragment

import android.app.ActivityOptions
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
import com.example.chatapp.MyApplication
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.SharedPrefs
import com.example.chatapp.adapter.RecentChatAdapter
import com.example.chatapp.adapter.OnRecentChatClicked
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.model.RecentChats
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), OnRecentChatClicked {

    private lateinit var recentChatAdapter: RecentChatAdapter
    private lateinit var homeViewModel: ChatAppViewModel
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var fbauth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        fbauth = FirebaseAuth.getInstance()

        homeBinding.lifecycleOwner = viewLifecycleOwner

        recentChatAdapter = RecentChatAdapter()

        homeViewModel.getRecentChats().observe(viewLifecycleOwner){
            homeBinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)
            recentChatAdapter.setOnRecentList(it)
            homeBinding.rvRecentChats.adapter = recentChatAdapter
            recentChatAdapter.notifyDataSetChanged()
        }

        recentChatAdapter.setOnRecentChatListener(this)
    }


    override fun getOnRecentChatClicked(position: Int, recentChatList: RecentChats) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra("userid", recentChatList.friendId)
        startActivity(intent)
    }
}