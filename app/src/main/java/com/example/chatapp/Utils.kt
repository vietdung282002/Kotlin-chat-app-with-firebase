package com.example.chatapp

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.*

class Utils {
    companion object {
        private var auth = FirebaseAuth.getInstance()
        private var userid: String = ""

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2

        fun getUidLoggedIn(): String {
            if (auth.currentUser != null) {
                userid = auth.currentUser!!.uid
            }

            return userid
        }

        fun getTime(): String {
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val date = Date(System.currentTimeMillis())

            return formatter.format(date)

        }

        fun getTimeDifference(postTime: String): String {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            val oneDayDateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            oneDayDateFormatter.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            val oneWeekdateFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
            oneWeekdateFormatter.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            val moreThanOneWeekDateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
            moreThanOneWeekDateFormatter.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            val moreThanOneYearDateFormatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
            moreThanOneYearDateFormatter.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            val inDayDateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            inDayDateFormatter.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

            val currentTime = System.currentTimeMillis()
            val postDate = dateFormatter.parse(postTime)
            val elapsedTime = currentTime - postDate.time

            val seconds = (elapsedTime / 1000).toInt()
            val minutes = (seconds / 60)
            val hours = (minutes / 60)
            val days = (hours / 24)

            return when {
                days > 0 -> {
                    when {
                        days == 1 -> {

                            "yesterday"
                        }

                        days < 7 -> {

                            "${oneWeekdateFormatter.format(postDate)}"
                        }

                        days < 365 -> {

                            "${moreThanOneWeekDateFormatter.format(postDate)}"
                        }

                        else -> {
                            "${moreThanOneYearDateFormatter.format(postDate)}"
                        }

                    }
                }

                days == 0 -> {

                    "${inDayDateFormatter.format(postDate)}"
                }

                else -> ""
            }
        }
    }
}