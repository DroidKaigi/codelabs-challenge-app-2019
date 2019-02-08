package droidkaigi.github.io.challenge2019.infrastructure.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApi {
    @GET("item/{v}.json")
    fun getItem(@Path("v") id: Long): Deferred<ItemResponse>

    @GET("user/{v}.json")
    fun getUser(@Path("v") id: String): Deferred<UserResponse>

    @GET("topstories.json")
    fun getTopStories(): Deferred<List<Long>>

    @GET("newstories.json")
    fun getNewStories(): Deferred<List<Long>>

    @GET("jobstories.json")
    fun getJobStories(): Deferred<List<Long>>
}
