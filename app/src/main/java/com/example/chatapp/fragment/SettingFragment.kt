package com.example.chatapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.activities.SignInActivity
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.databinding.FragmentSettingBinding
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment() {

    private lateinit var settingBinding: FragmentSettingBinding
    private lateinit var userViewModel: ChatAppViewModel
    private lateinit var fbauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_setting,container,false)
        return settingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()
        settingBinding.lifecycleOwner = viewLifecycleOwner

        settingBinding.logOut.setOnClickListener {
            fbauth.signOut()
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
            activity?.finish()
        }
    }
}