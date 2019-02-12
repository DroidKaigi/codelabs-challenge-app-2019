package droidkaigi.github.io.challenge2019.data.api

import droidkaigi.github.io.challenge2019.data.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.data.api.response.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApi {
    @GET("item/{id}.json")
    fun getItem(@Path("id") id: Long): Call<ItemResponse>

    @GET("user/{id}.json")
    fun getUser(@Path("id") id: String): Call<UserResponse>

    @GET("topstories.json")
    fun getTopStories(): Call<List<Long>>

    @GET("newstories.json")
    fun getNewStories(): Call<List<Long>>

    @GET("jobstories.json")
    fun getJobStories(): Call<List<Long>>
}
