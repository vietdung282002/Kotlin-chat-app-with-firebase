package com.example.chatapp.views.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.views.activities.EditProfileActivity
import com.example.chatapp.R
import com.example.chatapp.Utils
import com.example.chatapp.views.activities.SignInActivity
import com.example.chatapp.databinding.FragmentSettingBinding
import com.example.chatapp.viewmodel.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SettingFragment : Fragment() {

    private lateinit var settingBinding: FragmentSettingBinding
    private lateinit var settingViewModel: ChatAppViewModel
    private lateinit var fbauth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

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
        settingViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        fbauth = FirebaseAuth.getInstance()
        settingBinding.lifecycleOwner = viewLifecycleOwner
        settingBinding.viewModel = settingViewModel
        firestore = FirebaseFirestore.getInstance()
        settingViewModel.imageUrl.observe(viewLifecycleOwner){
            it?.let{
                Glide.with(this).load(it).placeholder(R.drawable.person).dontAnimate().into(settingBinding.settingUpdateImage)
            }
        }

        settingBinding.logOut.setOnClickListener {
            firestore.collection("Users").document(Utils.getUidLoggedIn())
                .update("status", "Offline")
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
            activity?.finish()
            fbauth.signOut()
        }
        settingBinding.editBtn.setOnClickListener{
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

}