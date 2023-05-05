package product.promikz.data.story

import product.promikz.models.story.index.StoryIndex
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface StoryApi {

    @GET("/api/stories")
    @Headers("Accept: application/json")
    suspend fun getStory(): Response<StoryIndex>

}