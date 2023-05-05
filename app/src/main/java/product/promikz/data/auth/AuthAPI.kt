package product.promikz.data.auth

import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.auth.email.one.ActivitedSuccessModels
import product.promikz.models.auth.login.LoginModels
import product.promikz.models.auth.register.RegisterModels
import retrofit2.Response
import retrofit2.http.*

interface AuthAPI {

    @POST("/api/auth/email/sendCodeEmail")
    @Headers("Accept: application/json")
    suspend fun sendEmail(
        @Header("Authorization") auth: String,
    ): Response<ActivitedSuccessModels>

    @POST("/api/auth/phone/sendCode")
    @Headers("Accept: application/json")
    suspend fun sendPhone(
        @Header("Authorization") auth: String,
    ): Response<ActivitedSuccessModels>

    @FormUrlEncoded
    @POST("/api/auth/email/codeCheck")
    @Headers("Accept: application/json")
    suspend fun sendCheckEmail(
        @Header("Authorization") auth: String,
        @Field("code") code: String
    ): Response<ActivitedSuccessModels>

    @FormUrlEncoded
    @POST("/api/auth/password/codeCheck")
    @Headers("Accept: application/json")
    suspend fun codeCheckEmail(
        @Header("Authorization") auth: String,
        @Field("code") code: String
    ): Response<ActivitedSuccessModels>

    @FormUrlEncoded
    @POST("/api/auth/password/resetPassword")
    @Headers("Accept: application/json")
    suspend fun resetPassword(
        @Field("code") code: String,
        @Field("password") password: String,
        @Field("password_confirmation") password2: String
    ): Response<LoginModels>

    @FormUrlEncoded
    @POST("/api/auth/phone/codeCheck")
    @Headers("Accept: application/json")
    suspend fun sendCheckPhone(
        @Header("Authorization") auth: String,
        @Field("code") code: String
    ): Response<ActivitedSuccessModels>

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/auth/password/resetPasswordSendCode")
    suspend fun fargotEmail(
        @Field("phone") email: String)
    : Response<ActivitedSuccessModels>



    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/auth/checkUser")
    suspend fun fastPhone(
        @Field("phone") phone: String)
    : Response<ActivitedSuccessModels>

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/auth/login")
    suspend fun pushPost(
        @FieldMap params: HashMap<String, String>,
    ): Response<LoginModels>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/auth/register")
    suspend fun fastRegister(

        @Part("phone") phone: RequestBody,
        @Part("typeRegister") typeRegister: RequestBody

    ): Response<RegisterModels>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/auth/register")
    suspend fun pushRegistPost(

        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") password_confirmation: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("type") type: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part img: MultipartBody.Part?

    ): Response<RegisterModels>


}