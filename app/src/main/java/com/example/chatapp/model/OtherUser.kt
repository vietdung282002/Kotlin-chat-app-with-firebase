package com.example.chatapp.model

import android.os.Parcel
import android.os.Parcelable
import com.example.chatapp.Utils.Companion.NOT_RELATION

data class OtherUser(
    val userid: String? = "",
    val status: String? = "",
    val imageUrl: String? = "",
    val username: String? = "",
    val useremail: String? = "",
    val friendLists : ArrayList<String>? = arrayListOf<String>(),
    val friendRequested : ArrayList<String>? = arrayListOf<String>(),
    val friendRequests : ArrayList<String>? = arrayListOf<String>(),
    var relation: Int? = NOT_RELATION
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(username)
        parcel.writeString(useremail)
        parcel.writeValue(relation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OtherUser> {
        override fun createFromParcel(parcel: Parcel): OtherUser {
            return OtherUser(parcel)
        }

        override fun newArray(size: Int): Array<OtherUser?> {
            return arrayOfNulls(size)
        }
    }

}
