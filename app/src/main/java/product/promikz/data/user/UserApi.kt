package product.promikz.data.user

import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.errors.ErrorModels
import product.promikz.models.favorite.FavoriteModels
import product.promikz.models.user.UserModels
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @GET("/api/user/favorites")
    @Headers("Accept: application/json")
    suspend fun getFavorite(
        @Header("Authorization") auth: String
    ): Response<FavoriteModels>


    @GET("/api/user")
    @Headers("Accept: application/json")
    suspend fun getUser(
        @Header("Authorization") auth: String
    ): Response<UserModels>


    @POST("/api/user/tokenNotify")
    @FormUrlEncoded
    @Headers("Accept: application/json")
    suspend fun sendNotify(
        @Header("Authorization") auth: String,
        @Field("_method") method: String,
        @Field("tokenNotify") tokenNotify: String

    ): Response<UserModels>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/user/update")
    suspend fun updateUserPost(
        @Header("Authorization") auth: String,
        @PartMap params: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?

    ): Response<ErrorModels>
}