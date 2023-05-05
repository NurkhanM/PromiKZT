package product.promikz.data.message


import product.promikz.models.message.index.MessageIndexModels
import product.promikz.models.message.show.MessageShowModels
import product.promikz.models.post.message.store.NewMessageModels

import retrofit2.Response
import retrofit2.http.*

interface MessageApi {

    @GET("/api/message")
    @Headers("Accept: application/json")
    suspend fun getMessageIndex(
        @Header("Authorization") auth: String
    ): Response<MessageIndexModels>

    @GET("/api/message/{idChat}")
    @Headers("Accept: application/json")
    suspend fun getMessageShow(
        @Header("Authorization") auth: String,
        @Path("idChat") number: Int
    ): Response<MessageShowModels>

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/message")
    suspend fun newMessage(
        @Header("Authorization") auth: String,
        @FieldMap params: HashMap<String, String>
    ): Response<NewMessageModels>

}