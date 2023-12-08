
package com.example.chatapp.fragment

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
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.activities.ChatFromHomeActivity
import com.example.chatapp.R
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.adapter.RecentChatAdapter
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.adapter.OnRecentChatClicked
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.model.RecentChats
import com.example.chatapp.model.Users
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), OnUserClickListener, OnRecentChatClicked {

    private lateinit var rvUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var recentChatAdapter: RecentChatAdapter
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var fbauth: FirebaseAuth
//    private lateinit var toolbar: Toolbar
//    private lateinit var circleImageView: CircleImageView

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

        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        fbauth = FirebaseAuth.getInstance()

//        toolbar = view.findViewById(R.id.toolbarMain)
//        circleImageView = toolbar.findViewById(R.id.tlImage)

        homeBinding.lifecycleOwner = viewLifecycleOwner

        userAdapter = UserAdapter()
        rvUsers = view.findViewById(R.id.rvUsers)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        rvUsers.layoutManager = layoutManager

        userViewModel.getUsers().observe(viewLifecycleOwner) {
            it?.let {
                userAdapter.setUserList(it)
                rvUsers.adapter = userAdapter
            }
        }
        userAdapter.setOnUserClickListener(this)

        
//        userViewModel.imageUrl.observe(viewLifecycleOwner){
//            it?.let{
//                Glide.with(requireContext()).load(it).into(circleImageView)
//            }
//        }

        recentChatAdapter = RecentChatAdapter()

        userViewModel.getRecentChats().observe(viewLifecycleOwner){
            homeBinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)
            recentChatAdapter.setOnRecentList(it)
            homeBinding.rvRecentChats.adapter = recentChatAdapter
        }

        recentChatAdapter.setOnRecentChatListener(this)


    }


    override fun onUserSelected(position: Int, users: Users) {

//        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
//        view?.findNavController()?.navigate(action)
//        Toast.makeText(requireContext(), "ClickOn${users.username}", Toast.LENGTH_LONG).show()
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        val bundle = Bundle()
        val parcel = users
        bundle.putParcelable("users",parcel)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }

    override fun getOnRecentChatClicked(position: Int, chat: RecentChats) {
//        val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentChatList)
//        view?.findNavController()?.navigate(action)
        val intent = Intent(requireActivity(), ChatFromHomeActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("chat", chat)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }
}