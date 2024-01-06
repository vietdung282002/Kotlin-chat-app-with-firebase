package com.example.chatapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.Utils.Companion.FRIEND_REQUEST
import com.example.chatapp.Utils.Companion.NOT_RELATION
import com.example.chatapp.Utils.Companion.REQUESTED_FRIEND
import com.example.chatapp.model.OtherUser
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class AllUserAdapter: RecyclerView.Adapter<AllUserAdapter.UserHolder>() {

    private var listOfUsers = listOf<OtherUser>()
    private var listener: OnUserClickListener? = null
    class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val profileName: TextView = itemView.findViewById(R.id.userName)
        val imageProfile: CircleImageView = itemView.findViewById(R.id.imageViewUser)
        val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)
        val addFriendButton: MaterialButton = itemView.findViewById(R.id.add_friend_btn)
        val cancelButton: MaterialButton = itemView.findViewById(R.id.cancel_button)
        val acceptButton: MaterialButton = itemView.findViewById(R.id.accept_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUserAdapter.UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_user_item, parent, false)
        return AllUserAdapter.UserHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = listOfUsers[position]
        val name = user.username
        holder.profileName.text = name

        if(user.status.equals("Online")){
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)
        }else{
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)
        }

        Glide.with(holder.itemView.context).load(user.imageUrl).into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position,user)
        }

        if(user.relation == NOT_RELATION){
            holder.addFriendButton.visibility = View.VISIBLE
            holder.addFriendButton.setOnClickListener {
                listener?.OnAddFriendClicked(position,user)
            }
            holder.cancelButton.visibility = View.GONE
            holder.acceptButton.visibility = View.GONE
        }
        if(user.relation == FRIEND_REQUEST){
            holder.addFriendButton.visibility = View.GONE
            holder.cancelButton.visibility = View.GONE
            holder.acceptButton.visibility = View.VISIBLE
            holder.acceptButton.setOnClickListener {
                listener?.OnAcceptFriendClicked(position,user)
            }
        }
        if(user.relation == REQUESTED_FRIEND){
            holder.addFriendButton.visibility = View.GONE
            holder.cancelButton.visibility = View.VISIBLE
            holder.cancelButton.setOnClickListener {
                listener?.OnCancelClicked(position,user)
            }
            holder.acceptButton.visibility = View.GONE
        }
    }

    fun setOnUserClickListener(listener: OnUserClickListener){
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUserList(list: List<OtherUser>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }
}

interface OnUserClickListener {
    fun onUserSelected(position: Int,otherUser: OtherUser)

    fun OnAddFriendClicked(position: Int,otherUser: OtherUser)

    fun OnAcceptFriendClicked(position: Int,otherUser: OtherUser)

    fun OnCancelClicked(position: Int,otherUser: OtherUser)

}