package com.example.chatapp.views.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.views.fragment.friendfragment.AllFriendFragment
import com.example.chatapp.views.fragment.friendfragment.AllUserFragment
import com.example.chatapp.views.fragment.friendfragment.FriendRequestFragment

class ViewPagerAdapter(fragment: Fragment):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0->{
                AllFriendFragment()
            }
            1 ->{
                AllUserFragment()
            }
            else ->{
                FriendRequestFragment()
            }
        }
    }
}