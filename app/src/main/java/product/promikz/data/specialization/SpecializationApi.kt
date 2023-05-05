package product.promikz.data.specialization

import product.promikz.models.specialization.SpecializationModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface SpecializationApi {

    @GET("/api/specializations")
    @Headers("Accept: application/json")
    suspend fun getSpecializationIndex(
        @Header("Authorization") auth: String
    ): Response<SpecializationModels>
}