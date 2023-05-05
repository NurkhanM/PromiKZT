package product.promikz.data.skills

import product.promikz.models.specialist.skills.SkillsModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface SkillApi {

    @GET("/api/skills")
    @Headers("Accept: application/json")
    suspend fun getSkillsIndex(
        @Header("Authorization") auth: String
    ): Response<SkillsModels>

}