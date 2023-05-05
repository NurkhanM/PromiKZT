package product.promikz.data.api

import product.promikz.models.banner.BannerIndexModels
import product.promikz.models.errors.ErrorModels
import product.promikz.models.like.product.LikeProductModels
import product.promikz.models.like.specialist.LikeSpecialistModels
import product.promikz.models.similar.SimilarModels
import product.promikz.models.stats.StateModels
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/api/stats")
    @Headers("Accept: application/json")
    suspend fun getState(
    ): Response<StateModels>

    @GET("/api/similar/{idSimilar}")
    @Headers("Accept: application/json")
    suspend fun getSimilar(
        @Header("Authorization") auth: String,
        @Path("idSimilar") number: Int
    ): Response<SimilarModels> // ProductIndexModels


    @GET("/api/banners")
    suspend fun getBanner(): Response<BannerIndexModels>

    //Post request
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/rating/{name}/{idProduct}")
    suspend fun postRating(
        @Header("Authorization") auth: String,
        @Path("name") name: String,
        @Path("idProduct") idProduct: Int,
        @Field("value") rating: String
    ): Response<ErrorModels>

    @Headers("Accept: application/json")
    @POST("/api/like/product/{idLike}")
    suspend fun postLike(
        @Header("Authorization") auth: String,
        @Path("idLike") idLike: Int
    ): Response<LikeProductModels>

    @Headers("Accept: application/json")
    @POST("/api/like/specialist/{idLike}")
    suspend fun postSpecialist(
        @Header("Authorization") auth: String,
        @Path("idLike") idLike: Int
    ): Response<LikeSpecialistModels>


}

