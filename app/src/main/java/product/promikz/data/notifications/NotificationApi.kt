package product.promikz.data.notifications

import product.promikz.models.notification.index.NotificationModels
import product.promikz.models.notification.show.NotificationShowModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface NotificationApi {

    @GET("/api/notification")
    @Headers("Accept: application/json")
    suspend fun getNotification(
        @Header("Authorization") auth: String
    ): Response<NotificationModels>

    @GET("/api/notification/{idNotification}")
    @Headers("Accept: application/json")
    suspend fun showNotification(
        @Header("Authorization") auth: String,
        @Path("idNotification") idNotification: String
    ): Response<NotificationShowModels>

}