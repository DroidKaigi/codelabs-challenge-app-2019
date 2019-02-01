package droidkaigi.github.io.challenge2019.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story
import droidkaigi.github.io.challenge2019.data.repository.mapper.toComment
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

    // TODO: 3.Implement HackerNewsRepository

    fun getTopStories(): LiveData<Resource<List<Story?>>> {
        // This is not an optimal implementation, we'll fix it after
        val liveData = MutableLiveData<Resource<List<Story?>>>().apply {
            postValue(Resource.Loading())
        }

        TODO("Implement this method")

        return liveData
    }

    fun getStory(id: Long): LiveData<Resource<Story>> {
        // This is not an optimal implementation, we'll fix it after
        val liveData = MutableLiveData<Resource<Story>>().apply {
            postValue(Resource.Loading())
        }

        TODO("Implement this method")

        return liveData
    }

    fun getComments(story: Story): LiveData<Resource<List<Comment?>>> {
        // This is not an optimal implementation, we'll fix it after
        val liveData = MutableLiveData<Resource<List<Comment?>>>().apply {
            postValue(Resource.Loading())
        }

        GetItemsTask(hackerNewsApi) { items ->
            val comments = items.map { it?.toComment(emptyList()) }
            liveData.value = if (!story.commentIds.isEmpty() && comments.all { it == null }) {
                Resource.Error(Exception("failed to get all comments"))
            } else {
                Resource.Success(comments)
            }
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