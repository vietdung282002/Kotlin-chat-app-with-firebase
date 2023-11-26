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
import com.example.chatapp.model.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(): RecyclerView.Adapter<UserAdapter.UserHolder>() {

    private var listOfUsers = listOf<Users>()
    private var listener: OnUserClickListener? = null
    class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val proflieName: TextView = itemView.findViewById(R.id.userName)
        val imageProfile: CircleImageView = itemView.findViewById(R.id.imageViewUser)
        val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = listOfUsers[position]
        val name = user.username!!.split("\\s".toRegex())[0]
        holder.proflieName.setText(name)

        if(user.status.equals("Online")){
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)
        }else{
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)
        }

        Glide.with(holder.itemView.context).load(user.imageUrl).into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, user)
        }
    }

    fun setOnUserClickListener(listener: OnUserClickListener){
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUserList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }
}

interface OnUserClickListener {
    fun onUserSelected(position: Int,users: Users)

}


