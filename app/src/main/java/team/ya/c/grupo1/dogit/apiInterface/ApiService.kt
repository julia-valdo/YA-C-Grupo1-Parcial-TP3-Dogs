package team.ya.c.grupo1.dogit.apiInterface
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url
import team.ya.c.grupo1.dogit.models.DogBreedResponse

interface ApiService {

    @GET()
    suspend fun getDogsAllBreed(@Url url:String):Response<DogBreedResponse>
}