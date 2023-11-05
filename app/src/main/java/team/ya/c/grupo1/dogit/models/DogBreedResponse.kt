package team.ya.c.grupo1.dogit.models

import com.google.gson.annotations.SerializedName

data class DogBreedResponse(
    @SerializedName("status") var status:String,
    @SerializedName("message") var dogBreeds:List<String>)

