package product.promikz.data.story

import product.promikz.models.story.index.StoryIndex
import product.promikz.models.story.show.StoryShowModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface StoryApi {

    @GET("/api/stories")
    @Headers("Accept: application/json")
    suspend fun getStory(): Response<StoryIndex>

    @GET("/api/stories/{idStory}")
    @Headers("Accept: application/json")
    suspend fun getStoryShow(
        @Path("idStory") idStory: String
    ): Response<StoryShowModels>

}