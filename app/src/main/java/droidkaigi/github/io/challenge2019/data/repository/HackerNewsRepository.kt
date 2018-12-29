package droidkaigi.github.io.challenge2019.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story
import droidkaigi.github.io.challenge2019.data.repository.mapper.toComment
import droidkaigi.github.io.challenge2019.data.repository.mapper.toStory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

// Singleton
object HackerNewsRepository {

    private val hackerNewsApi: HackerNewsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(HackerNewsApi::class.java)
    }

    fun getTopStories(): LiveData<List<Story?>> {
        val liveData = MutableLiveData<List<Story?>>()

        hackerNewsApi.getTopStories().enqueue(object : Callback<List<Long>> {

            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                if (!response.isSuccessful) return

                response.body()?.let { itemIds ->
                    GetItemsTask(hackerNewsApi) { items ->
                        val stories = items.map { it?.toStory() }
                        liveData.value = stories
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *itemIds.take(20).toTypedArray())
                }
            }

            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                liveData.value = emptyList()
            }
        })

        return liveData
    }

    fun getStory(id: Long): LiveData<Story?> {
        val liveData = MutableLiveData<Story?>()

        GetItemsTask(hackerNewsApi) { item ->
            liveData.value = item.firstOrNull()?.toStory()
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id)

        return liveData
    }

    fun getComments(story: Story): LiveData<List<Comment?>> {
        val liveData = MutableLiveData<List<Comment?>>()

        GetItemsTask(hackerNewsApi) { items ->
            val comments = items.map { it?.toComment(emptyList()) }
            liveData.value = comments
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *story.commentIds.toTypedArray())

        return liveData
    }

    private class GetItemsTask(
        private val hackerNewsApi: HackerNewsApi,
        private val onPostExecute: (List<Item?>) -> Unit
    ) : AsyncTask<Long, Unit, List<Item?>>() {

        override fun doInBackground(vararg itemIds: Long?): List<Item?> {
            val ids = itemIds.mapNotNull { it }
            val itemMap = ConcurrentHashMap<Long, Item?>()
            val latch = CountDownLatch(ids.size)

            ids.forEach { id ->
                hackerNewsApi.getItem(id).enqueue(object : Callback<Item> {

                    override fun onResponse(call: Call<Item>, response: Response<Item>) {
                        response.body()?.let { item -> itemMap[id] = item }
                        latch.countDown()
                    }

                    override fun onFailure(call: Call<Item>, t: Throwable) {
                        latch.countDown()
                    }
                })
            }

            try {
                latch.await()
            } catch (e: InterruptedException) {
                return emptyList()
            }

            return ids.map { itemMap[it] }
        }

        override fun onPostExecute(result: List<Item?>) {
            onPostExecute.invoke(result)
        }
    }
}
