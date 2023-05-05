package product.promikz.data.compare

import product.promikz.models.compare.CompareModels
import retrofit2.Response
import retrofit2.http.*

interface CompareApi {

    @GET("/api/compare/{idCategory}")
    @Headers("Accept: application/json")
    suspend fun getCompareID(
        @Header("Authorization") auth: String,
        @Path("idCategory") number: Int,
        @Query("products") id: String
    ): Response<CompareModels>

}