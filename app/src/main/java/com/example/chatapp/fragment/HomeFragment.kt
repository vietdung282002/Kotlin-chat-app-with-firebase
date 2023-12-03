
package com.example.chatapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.SignInActivity
import com.example.chatapp.adapter.OnUserClickListener
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.model.Users
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragment : Fragment(), OnUserClickListener {

    private lateinit var rvUsers: RecyclerView
    private lateinit var userAdaper: UserAdapter
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var fbauth: FirebaseAuth
    private lateinit var toolbar: Toolbar
    private lateinit var circleImageView: CircleImageView

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

        toolbar = view.findViewById(R.id.toolbarMain)
        circleImageView = toolbar.findViewById(R.id.tlImage)

        homeBinding.lifecycleOwner = viewLifecycleOwner

        userAdaper = UserAdapter()
        rvUsers = view.findViewById(R.id.rvUsers)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        rvUsers.layoutManager = layoutManager

        userViewModel.getUsers().observe(viewLifecycleOwner) {
            it?.let {
                userAdaper.setUserList(it)
                rvUsers.adapter = userAdaper
            }
        }
        userAdaper.setOnUserClickListener(this)

        homeBinding.logOut.setOnClickListener {
            fbauth.signOut()
            startActivity(Intent(requireContext(),SignInActivity::class.java))
            activity?.finish()
        }

        userViewModel.imageUrl.observe(viewLifecycleOwner){
            it?.let{
                Glide.with(requireContext()).load(it).into(circleImageView)
            }
        }

    }

    override fun onUserSelected(position: Int, users: Users) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)
//        Toast.makeText(requireContext(), "ClickOn${users.username}", Toast.LENGTH_LONG).show()
    }
}