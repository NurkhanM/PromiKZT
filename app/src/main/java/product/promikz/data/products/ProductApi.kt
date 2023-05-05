package product.promikz.data.products

import okhttp3.MultipartBody
import okhttp3.RequestBody
import product.promikz.models.errors.ErrorModels
import product.promikz.models.errors.Errors
import product.promikz.models.products.index.ProductIndexModels
import product.promikz.models.products.ratingAVG.RatingAVGModels
import product.promikz.models.products.show.ProductShowModels
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {

    @GET("/api/products?notReports=1")
    @Headers("Accept: application/json")
    suspend fun getTovarPage(
        @Header("Authorization") auth: String
    ): Response<ProductIndexModels>


    @GET("/api/products?type=2&notReports=1")
    @Headers("Accept: application/json")
    suspend fun getKursy(
        @Header("Authorization") auth: String
    ): Response<ProductIndexModels>

    @GET("/api/products?notReports=1&type=1")
    @Headers("Accept: application/json")
    suspend fun getTovarTradeIn(
        @Header("Authorization") auth: String
    ): Response<ProductIndexModels>

    @GET("/api/products?notReports=1")
    @Headers("Accept: application/json")
    suspend fun getSortViews(
        @Header("Authorization") auth: String,
        @Query("sort") sort: String
    ): Response<RatingAVGModels>

    @GET("/api/products?notReports=1")
    @Headers("Accept: application/json")
    suspend fun getSortRating(
        @Header("Authorization") auth: String,
        @Query("sort") sort: String
    ): Response<RatingAVGModels>

    @GET("/api/products?notReports=1")
    @Headers("Accept: application/json")
    suspend fun getSortProducts(
        @Header("Authorization") auth: String,
        @QueryMap params: HashMap<String, String>,
        @QueryMap filters: HashMap<String, String>,
        @Query("cities[]") cities: List<Int>
    ): Response<ProductIndexModels>

    @GET("/api/products?notReports=1")
    @Headers("Accept: application/json")
    suspend fun getFilterProducts(
        @Header("Authorization") auth: String,
        @QueryMap allPro: HashMap<String, String>
    ): Response<ProductIndexModels>

    @GET("/api/products/{idProducts}")
    @Headers("Accept: application/json")
    suspend fun getUpdateDataArrayUpdate(
        @Header("Authorization") auth: String,
        @Path("idProducts") number: Int
    ): Response<ProductShowModels>

    @GET("/api/products?activity=1")
    @Headers("Accept: application/json")
    suspend fun getShopsON(
        @Header("Authorization") auth: String,
        @Query("shop") idShop: String
    ): Response<ProductIndexModels>

    @GET("/api/products?activity=0")
    @Headers("Accept: application/json")
    suspend fun getShopsOFF(
        @Header("Authorization") auth: String,
        @Query("shop") idShop: String,
    ): Response<ProductIndexModels>

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("/api/products/{idProduct}")
    suspend fun postDeleteProduct(
        @Header("Authorization") auth: String,
        @Field("_method") delete: String,
        @Path("idProduct") idProduct: Int
    ): Response<String>

    @Headers("Accept: application/json")
    @POST("/api/product/disabled/{idProduct}")
    suspend fun postDisabledProduct(
        @Header("Authorization") auth: String,
        @Path("idProduct") idProduct: Int
    ): Response<String>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/products")
    suspend fun pushProductCreate(
        @Header("Authorization") auth: String,
        @PartMap params: HashMap<String, RequestBody>,
        @PartMap fields: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?,
        @Part images: List<MultipartBody.Part>
    ): Response<ErrorModels>


    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/products/{idUpdate}")
    suspend fun pushUpdateCreate(
        @Header("Authorization") auth: String,
        @Path("idUpdate") number: Int,
        @Part("_method") _method: RequestBody,
        @Part("name") name: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("category_id") category_id: RequestBody,
        @Part("brand_id") brand_id: RequestBody,
        @Part("state") state: RequestBody,
        @Part("price") price: RequestBody,
        @Part img: MultipartBody.Part?,
        @Part("description") description: RequestBody,
        @PartMap params: HashMap<String, RequestBody>?,
        @Part images: List<MultipartBody.Part>?,
        @PartMap installment: HashMap<String, RequestBody>?
    ): Response<Errors>

    @Multipart
    @Headers("Accept: application/json")
    @POST("/api/products/{idUpdate}")
    suspend fun pushUpdateKursy(
        @Header("Authorization") auth: String,
        @Path("idUpdate") number: Int,
        @Part("_method") _method: RequestBody,
        @Part("type") type: RequestBody,
        @Part("name") name: RequestBody,
        @Part("country_id") country_id: RequestBody,
        @Part("price") price: RequestBody,
        @Part img: MultipartBody.Part?,
        @Part("description") description: RequestBody,
        @Part images: List<MultipartBody.Part>?,
        @PartMap installment: HashMap<String, RequestBody>?
    ): Response<Errors>

}