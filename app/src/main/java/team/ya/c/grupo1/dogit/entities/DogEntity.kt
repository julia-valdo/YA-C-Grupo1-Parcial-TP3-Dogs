package team.ya.c.grupo1.dogit.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class DogEntity (var name: String, var breed: String, var subBreed: String, var age: Int,
                      var gender: String, var description: String, var weight: Double, var location: String,
                      var images: MutableList<String>, var adopterName: String, var id: String,
                      var followers: MutableList<String>, var adopterEmail: String, var ownerEmail: String,
                      var publicationDate: Date
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong())
    )

    constructor() : this("", "", "", 0, "", "", 0.0, "", ArrayList(), "","",ArrayList(),"", "", Date())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(breed)
        parcel.writeString(subBreed)
        parcel.writeInt(age)
        parcel.writeString(description)
        parcel.writeString(gender)
        parcel.writeString(description)
        parcel.writeDouble(weight)
        parcel.writeString(location)
        parcel.writeList(images)
        parcel.writeString(adopterName)
        parcel.writeString(id)
        parcel.writeList(followers)
        parcel.writeString(adopterEmail)
        parcel.writeString(ownerEmail)
        parcel.writeLong(publicationDate.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DogEntity> {
        override fun createFromParcel(parcel: Parcel): DogEntity {
            return DogEntity(parcel)
        }

        override fun newArray(size: Int): Array<DogEntity?> {
            return arrayOfNulls(size)
        }
    }

    fun imagesIsFull() : Boolean {
        return images.size >= 5
    }

}