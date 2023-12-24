@file:Suppress("DEPRECATION")

package com.example.chatapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.SharedPrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {
    companion object {
        private const val KEY_REPLY_TEXT = "KEY_REPLY_TEXT"

        var sharedPrefs: SharedPreferences? = null
        var token: String?
            get() {
                return sharedPrefs?.getString("token", "")
            }
            set(value) {
                sharedPrefs?.edit()?.putString("token", value)?.apply()
            }

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userid",message.data["id"])
        Log.e("vietdung2802", "intent.putExtra ${message.data["id"]}")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val sharedCustomPref = SharedPrefs(applicationContext)
        sharedCustomPref.setIntValue("value", notificationID)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(Html.fromHtml("<b>${message.data["name"]}</b>: ${message.data["message"]}"))
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}