package product.promikz.data.subscriber

import product.promikz.models.subscriber.category.SubCategoryModels
import product.promikz.models.subscriber.index.IndexSubscriberModels
import product.promikz.models.subscriber.shop.SubShopModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscriberApi {




    @GET("/api/subscriber")
    @Headers("Accept: application/json")
    suspend fun subIndex(
        @Header("Authorization") auth: String
    ): Response<IndexSubscriberModels>

    @POST("/api/subscriber/category/{idCategory}")
    @Headers("Accept: application/json")
    suspend fun subCategory(
        @Header("Authorization") auth: String,
        @Path("idCategory") number: Int
    ): Response<SubCategoryModels>


    @POST("/api/subscriber/shop/{idShop}")
    @Headers("Accept: application/json")
    suspend fun subShop(
        @Header("Authorization") auth: String,
        @Path("idShop") number: Int
    ): Response<SubShopModels>

}