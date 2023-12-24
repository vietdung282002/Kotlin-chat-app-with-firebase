package com.example.chatapp.notifications

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.chatapp.R
import com.example.chatapp.SharedPrefs
import com.example.chatapp.Utils
import com.google.firebase.firestore.FirebaseFirestore

private const val CHANNEL_ID = "my_channel"

class NotificationReply : BroadcastReceiver() {


    val firestore = FirebaseFirestore.getInstance()


    override fun onReceive(context: Context?, intent: Intent?) {


        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteInput = RemoteInput.getResultsFromIntent(intent)


        val id = intent!!.getStringExtra("id")
        val friendsImage = intent!!.getStringExtra("friendsImage")
        val friendsname = intent!!.getStringExtra("friendsname")
        if (remoteInput == null){
            Log.e("vietdung2802", "111111" )

        }
        if (remoteInput != null) {
            Log.e("vietdung2802", "111112" )

            val repliedText = remoteInput.getString("KEY_REPLY_TEXT")


            val mysharedPrefs = SharedPrefs(context)
            val name = mysharedPrefs.getValue("name")
            val image = mysharedPrefs.getValue("image")

            val hashMap = hashMapOf<String, Any>(
                "sender" to Utils.getUidLoggedIn(),
                "time" to Utils.getTime(),
                "receiver" to id!!,
                "message" to repliedText!!
            )
            val uniqueId = listOf(id, Utils.getUidLoggedIn()).sorted()
            uniqueId.joinToString(separator = "")
            Log.e("vietdung2802", "${uniqueId.toString()}" )

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener {


                    if (it.isSuccessful) {


                    }


                }


            val setHashap = hashMapOf<String, Any>(
                "friendid" to id!!,
                "time" to Utils.getTime(),
                "sender" to Utils.getUidLoggedIn(),
                "message" to repliedText,
                "friendsimage" to friendsImage!!,
                "name" to friendsImage!!,
                "person" to "you",
            )

            val hashMapForReceiverRecent = hashMapOf<String, Any>(
                "friendId" to Utils.getUidLoggedIn(),
                "time" to Utils.getTime(),
                "sender" to Utils.getUidLoggedIn(),
                "message" to repliedText,
                "friendsImage" to image!!,
                "name" to name!!,
                "person" to friendsname!!
            )



            firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(id)
                .set(setHashap)

            firestore.collection("Conversation${id}").document(Utils.getUidLoggedIn())
                .set(hashMapForReceiverRecent)


            val sharedCustomPref = SharedPrefs(context)
            val replyid: Int = sharedCustomPref.getIntValue("values", 0)


            val repliedNotification =
                NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.chatapp)
                    .setContentText("Reply Sent").build()


            notificationManager.notify(replyid, repliedNotification)


        }


    }
}