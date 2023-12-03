package com.example.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.model.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter: RecyclerView.Adapter<RecentChatHolder>() {
    private var listOfChats = listOf<RecentChats>()
    private var listener: onRecentChatClicked? = null
    private var recentModal = RecentChats()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist,parent,false)
        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {

        val recentChatList = listOfChats[position]
        recentModal = recentChatList
        holder.userName.text = recentModal.name

        val themessage = recentChatList.message!!.split("").joinToString("")

        val makeLastMessage = "${recentChatList.person}: ${themessage}"

        holder.lastMessage.text = makeLastMessage

        Glide.with(holder.itemView.context).load(recentChatList.friendsImage).into(holder.imageView)

        holder.timeView.text = recentChatList.time!!.substring(0,5)

        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatClicked(position,recentChatList)
        }
    }

    fun setOnRecentChatListener(listener: onRecentChatClicked){
        this.listener = listener
    }

    fun setOnRecentList(list: List<RecentChats>){
        this.listOfChats = list
    }
}

interface onRecentChatClicked {
    fun getOnRecentChatClicked(position: Int, recentChatList: RecentChats)
}

class RecentChatHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)

}
