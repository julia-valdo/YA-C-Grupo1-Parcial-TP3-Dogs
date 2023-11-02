package team.ya.c.grupo1.dogit.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class UserEntity(var firstName: String, var surname: String, var email: String,
                 var birthDate: String, var gender: String, var telephoneNumber: String,
                 var address: String, var uuid: String, var dogs: ArrayList<String>) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        // TODO: acá se construiría la lista de DogEntity
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(surname)
        parcel.writeString(email)
        parcel.writeString(birthDate)
        parcel.writeString(gender)
        parcel.writeString(telephoneNumber)
        parcel.writeString(address)
        parcel.writeString(uuid)
        // TODO: parcel.writeArray() ?
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