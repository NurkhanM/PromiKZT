package product.promikz.data.pay

import okhttp3.MultipartBody
import product.promikz.models.pay.generete.PayGenereteModels
import product.promikz.models.pay.index.PayModels
import retrofit2.Response
import retrofit2.http.*

interface PayApi {

    @GET("/api/services")
    @Headers("Accept: application/json")
    suspend fun getServices(
    ): Response<PayModels>

    @POST("/api/pay")
    @Headers("Accept: application/json")
    suspend fun payGenerate(
        @Header("Authorization") auth: String,
        @QueryMap allPro: HashMap<String, String>
    ): Response<PayGenereteModels>


    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/pay")
    suspend fun payGenerateBanner(
        @Header("Authorization") auth: String,
        @QueryMap allPro: HashMap<String, String>,
        @Part images: MultipartBody.Part?
    ): Response<PayGenereteModels>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/pay")
    suspend fun payGenerateStory(
        @Header("Authorization") auth: String,
        @QueryMap allPro: HashMap<String, String>,
        @Part images: List<MultipartBody.Part?>,
        @Part img: MultipartBody.Part?
    ): Response<PayGenereteModels>

}