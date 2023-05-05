package product.promikz.data.country

import product.promikz.models.country.index.CountryIndexModels
import product.promikz.models.country.show.CountryShowModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface CountryApi {

    @GET("/api/country")
    @Headers("Accept: application/json")
    suspend fun getCountry(
    ): Response<CountryIndexModels>

    @GET("/api/country/{idCountry}")
    @Headers("Accept: application/json")
    suspend fun getCity(
        @Path("idCountry") number: Int
    ): Response<CountryShowModels>

}