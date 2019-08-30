package hse24.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageRetreivalApi {

    @GET("/412468/412468_9fe0128c-c28f-4a44-87aa-cb0d4cc4cc95_{imageName}")
    fun getImageByName(
        @Path("imageName") imageName: String
    ): Single<String>
}
