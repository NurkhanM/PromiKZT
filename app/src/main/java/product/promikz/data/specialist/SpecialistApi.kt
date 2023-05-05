package product.promikz.data.specialist

import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.errors.ErrorModels
import product.promikz.models.specialist.index.SpecialistIndexModels
import product.promikz.models.specialist.show.SpecialistShowModels
import retrofit2.Response
import retrofit2.http.*

interface SpecialistApi {


    @GET("/api/specialist?notReports=1")
    @Headers("Accept: application/json")
    suspend fun getSpecialistPage(
        @Header("Authorization") auth: String
    ): Response<SpecialistIndexModels>

    @GET("/api/specialist/{idSpecialist}")
    @Headers("Accept: application/json")
    suspend fun getSpecialistShow(
        @Header("Authorization") auth: String,
        @Path("idSpecialist") number: Int
    ): Response<SpecialistShowModels>


    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/specialist")
    suspend fun pushSpecialistCreate(
        @Header("Authorization") auth: String,
        @PartMap params: HashMap<String, RequestBody>,
        @PartMap params2: HashMap<String, RequestBody>,
        @PartMap params3: HashMap<String, RequestBody>,
        @PartMap params4: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?
    ): Response<SpecialistShowModels>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/specialist/{idSpecialist}")
    suspend fun pushSpecialistPut(
        @Header("Authorization") auth: String,
        @Path("idSpecialist") number: Int,
        @PartMap params: HashMap<String, RequestBody>,
        @PartMap params2: HashMap<String, RequestBody>,
        @PartMap params3: HashMap<String, RequestBody>,
        @PartMap params4: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?
    ): Response<ErrorModels>


}