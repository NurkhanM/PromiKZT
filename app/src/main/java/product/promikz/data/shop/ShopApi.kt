package product.promikz.data.shop

import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.post.shopCreate.ShopCreateM
import product.promikz.models.shop.ShopsModels
import retrofit2.Response
import retrofit2.http.*

interface ShopApi {

    @GET("/api/shop/{idShop}")
    @Headers("Accept: application/json")
    suspend fun getShops(
        @Path("idShop") number: Int
    ): Response<ShopsModels>

    @FormUrlEncoded
    @POST("/api/shop/{idShop}")
    @Headers("Accept: application/json")
    suspend fun postDeleteShop(
        @Header("Authorization") auth: String,
        @Field("_method") delete: String,
        @Path("idShop") idShop: Int
    ): Response<String>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/shop")
    suspend fun pushShopCreate(
        @Header("Authorization") auth: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part img: MultipartBody.Part?
    ): Response<ShopCreateM>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/shop/{idShop}")
    suspend fun updateShopCreate(
        @Header("Authorization") auth: String,
        @Part("_method") method: RequestBody,
        @Path("idShop") idShop: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part img: MultipartBody.Part?
    ): Response<ShopCreateM>
}