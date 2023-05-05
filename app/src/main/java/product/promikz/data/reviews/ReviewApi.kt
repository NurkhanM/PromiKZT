package product.promikz.data.reviews

import product.promikz.models.review.get.products.ReviewProductModels
import product.promikz.models.review.get.specialist.ReviewSpecialistModels
import product.promikz.models.review.post.ReviewPostModels
import retrofit2.Response
import retrofit2.http.*

interface ReviewApi {

    @GET("/api/reviews/product/{idReview}")
    @Headers("Accept: application/json")
    suspend fun getReviews(
        @Path("idReview") number: Int
    ): Response<ReviewProductModels>

    @GET("/api/reviews/specialist/{idReview}")
    @Headers("Accept: application/json")
    suspend fun getReviewsSpecialist(
        @Path("idReview") number: Int
    ): Response<ReviewSpecialistModels>


    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/reviews")
    suspend fun postReview(
        @Header("Authorization") auth: String,
        @Field("text") text: String,
        @Field("type") type: String,
        @Field("review_id") review_id: Int
    ): Response<ReviewPostModels>

}