package product.promikz.data.report

import product.promikz.models.post.report.ReportModels
import retrofit2.Response
import retrofit2.http.*

interface ReportApi {

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/reports")
    suspend fun postReport(
        @Header("Authorization") auth: String,
        @Field ("report_id") id: Int,
        @Field ("type") type: String,
        @Field ("description") desc: String,
        @Field ("view") view: String
    ): Response<ReportModels>



}