package team.ya.c.grupo1.dogit.entities

import android.os.Parcel
import android.os.Parcelable

data class DogBreedEntity (var breed: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
    )

    constructor() : this("")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(breed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DogBreedEntity> {
        override fun createFromParcel(parcel: Parcel): DogBreedEntity {
            return DogBreedEntity(parcel)
        }

        override fun newArray(size: Int): Array<DogBreedEntity?> {
            return arrayOfNulls(size)
        }
    }


}