package com.example.chatapp.model.dataclass

import android.os.Parcel
import android.os.Parcelable

data class Users (
    val userid: String? = "",
    val status: String? = "",
    val imageUrl: String? = "",
    val username: String? = "",
    val useremail: String? = "",
    val friendLists : ArrayList<String>? = arrayListOf<String>(),
    val friendRequested : ArrayList<String>? = arrayListOf<String>(),
    val friendRequests : ArrayList<String>? = arrayListOf<String>(),
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(username)
        parcel.writeString(useremail)
        parcel.writeStringList(friendLists)
        parcel.writeStringList(friendRequested)
        parcel.writeStringList(friendRequests)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }


}