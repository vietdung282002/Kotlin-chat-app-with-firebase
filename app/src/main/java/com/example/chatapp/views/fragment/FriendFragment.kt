package com.example.chatapp.views.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.views.adapter.ViewPagerAdapter
import com.example.chatapp.databinding.FragmentFriendBinding
import com.google.android.material.tabs.TabLayoutMediator

class FriendFragment : Fragment(){

    private lateinit var friendBinding: FragmentFriendBinding

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
    }

}