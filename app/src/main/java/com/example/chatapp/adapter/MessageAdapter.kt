package com.example.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.Utils
import com.example.chatapp.model.Messages

class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var listOfMessage = listOf<Messages>()
    private val left = 0
    private val right = 1
    private var userName: String? = null
    private var imageUrl: String? = null

    class LeftViewHolder(itemView: View): ViewHolder(itemView){
        val tvUserName : TextView = itemView.findViewById(R.id.user_name)
        val messageText: TextView = itemView.findViewById(R.id.show_message)
        val timeSent: TextView = itemView.findViewById(R.id.timeView)
        val imageUser: ImageView = itemView.findViewById(R.id.user_image)
    }

    class RightViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.show_message)
        val timeSent: TextView = itemView.findViewById(R.id.timeView)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == right) {
            val view = inflater.inflate(R.layout.chatitemright, parent, false)
            RightViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.chatitemleft, parent, false)
            LeftViewHolder(view)
        }
    }


    override fun getItemCount(): Int {
        return listOfMessage.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = listOfMessage[position]
        if(getItemViewType(position) == right){
            val rightHolder = holder as RightViewHolder
            rightHolder.messageText.visibility = View.VISIBLE
            rightHolder.timeSent.visibility = View.VISIBLE

            rightHolder.messageText.text = message.message
            rightHolder.timeSent.text = Utils.getTimeDifference(message.time!!)
        }else{
            val leftViewHolder = holder as LeftViewHolder
            leftViewHolder.messageText.visibility = View.VISIBLE
            leftViewHolder.timeSent.visibility = View.VISIBLE
            leftViewHolder.tvUserName.visibility = View.VISIBLE
            leftViewHolder.imageUser.visibility = View.VISIBLE

            leftViewHolder.messageText.text = message.message
            leftViewHolder.timeSent.text = Utils.getTimeDifference(message.time!!)
            if(this.userName == null){
                leftViewHolder.tvUserName.text = ""
            }else{
                leftViewHolder.tvUserName.text = this.userName
            }
            if(this.imageUrl != null){
                Glide.with(leftViewHolder.itemView.context).load(this.imageUrl).into(holder.imageUser)
            }
        }

    }

    override fun getItemViewType(position: Int): Int =
        if (listOfMessage[position].sender == Utils.getUidLoggedIn()) right else left

    fun setMessageList(newList: List<Messages>){
        this.listOfMessage = newList
    }

    fun setUser(user: String,imageUrl: String){
        this.imageUrl = imageUrl
        this.userName = user
    }
}

