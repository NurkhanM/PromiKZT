package product.promikz.data.category

import product.promikz.models.category.index.CategoryIndexModels
import product.promikz.models.category.show.CategoryShowModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface CategoryApi {

    @GET("/api/category/{idCategory}")
    @Headers("Accept: application/json")
    suspend fun getCategoryID(
        @Header("Authorization") auth: String,
        @Path("idCategory") number: Int
    ): Response<CategoryShowModels>

    @GET("/api/category")
    @Headers("Accept: application/json")
    suspend fun getCategoryIndex(
        @Header("Authorization") auth: String
    ): Response<CategoryIndexModels>


}