package com.example.chatapp.model

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

