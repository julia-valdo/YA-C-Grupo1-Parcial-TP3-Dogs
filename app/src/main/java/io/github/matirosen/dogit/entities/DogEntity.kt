package io.github.matirosen.dogit.entities

import android.os.Parcel
import android.os.Parcelable

data class DogEntity (var race: String, var subrace: String, var age: Int, var gender: String,
                      var description: String, var weight: Double, var location: String,
                      var images: MutableList<String>, var adopterName: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(race)
        parcel.writeString(subrace)
        parcel.writeInt(age)
        parcel.writeString(description)
        parcel.writeString(gender)
        parcel.writeString(description)
        parcel.writeDouble(weight)
        parcel.writeString(location)
        parcel.writeList(images)
        parcel.writeString(adopterName)
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