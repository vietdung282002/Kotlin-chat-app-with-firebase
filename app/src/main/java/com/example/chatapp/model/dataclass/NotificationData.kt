package com.example.chatapp.model.dataclass

data class NotificationData(
    val id: String,
    val name: String,
    val message: String
)

data class PushNotification(
    val data: NotificationData,
    val to: String
)