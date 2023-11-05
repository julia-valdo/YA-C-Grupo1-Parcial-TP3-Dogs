package team.ya.c.grupo1.dogit.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class UserEntity(var firstName: String, var surname: String, var email: String,
                    var profileImage: String, var telephoneNumber: String, var uuid: String,
                    var favoriteDogs: MutableList<String>, var adoptedDogs: MutableList<String>) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!
    )
    constructor() : this("", "", "",
                        "", "", "",
                        mutableListOf<String>(), mutableListOf<String>())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(surname)
        parcel.writeString(email)
        parcel.writeString(profileImage)
        parcel.writeString(telephoneNumber)
        parcel.writeString(uuid)
        parcel.writeStringList(favoriteDogs)
        parcel.writeStringList(adoptedDogs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserEntity> {
        override fun createFromParcel(parcel: Parcel): UserEntity {
            return UserEntity(parcel)
        }

        override fun newArray(size: Int): Array<UserEntity?> {
            return arrayOfNulls(size)
        }
    }


}