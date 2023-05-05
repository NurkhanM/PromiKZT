package product.promikz.data.settings

import product.promikz.models.version.VersionModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface SettingsApi {

    @GET("/api/settings")
    @Headers("Accept: application/json")
    suspend fun getVersion(
    ): Response<VersionModels>
}