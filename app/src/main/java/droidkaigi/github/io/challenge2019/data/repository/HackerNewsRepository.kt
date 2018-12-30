package droidkaigi.github.io.challenge2019.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.Room
import droidkaigi.github.io.challenge2019.App
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.api.response.mapper.toCommentIdEntities
import droidkaigi.github.io.challenge2019.data.api.response.mapper.toStoryEntity
import droidkaigi.github.io.challenge2019.data.db.AppDatabase
import droidkaigi.github.io.challenge2019.data.db.entity.mapper.toStory
import droidkaigi.github.io.challenge2019.data.repository.mapper.toComment
import droidkaigi.github.io.challenge2019.data.repository.mapper.toStory
import droidkaigi.github.io.challenge2019.model.Comment
import droidkaigi.github.io.challenge2019.model.Story
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// Singleton
object HackerNewsRepository {

    private val hackerNewsApi: HackerNewsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(HackerNewsApi::class.java)
    }

    private val db: AppDatabase by lazy {
        Room.databaseBuilder(
            App.appContext,
            AppDatabase::class.java,
            "database"
        ).build()
    }

    private val executor: Executor by lazy {
        Executors.newCachedThreadPool()
    }

    fun getTopStories(): LiveData<Resource<List<Story>>> {
        return MediatorLiveData<Resource<List<Story>>>().apply {
            addSource(refreshTopStories()) { resource -> resource?.let { value = it } }
            addSource(db.storyDao().getAllStories()) { storyEntities ->
                storyEntities?.let {
                    val stories = it.map { storyEntity -> storyEntity.toStory() }
                    value = Resource.Success(stories)
                }
            }
        }
    }

    private fun refreshTopStories(): LiveData<Resource<List<Story>>> {
        val liveData = MutableLiveData<Resource<List<Story>>>().apply {
            postValue(Resource.Loading())
        }

        hackerNewsApi.getTopStories().enqueue(object : Callback<List<Long>> {

            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                if (!response.isSuccessful) return

                response.body()?.let { itemIds ->
                    GetItemsTask(executor, hackerNewsApi) { items ->
                        val stories = items.mapNotNull { it?.toStoryEntity() }
                        val commentIds = items.mapNotNull { it?.toCommentIdEntities() }.flatten()
                        db.runInTransaction {
                            db.storyDao().clearAndInsert(stories)
                            db.commentIdDao().insert(commentIds)
                        }
                    }.execute(itemIds.take(20))
                }
            }

            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                liveData.postValue(Resource.Error(t))
            }
        })

        return liveData
    }

    fun getStory(id: Long): LiveData<Resource<Story>> {
        // This is not an optimal implementation, we'll fix it after
        val liveData = MutableLiveData<Resource<Story>>().apply {
            postValue(Resource.Loading())
        }

        GetItemsTask(executor, hackerNewsApi) { item ->
            val resource: Resource<Story> = item.firstOrNull()?.let {
                Resource.Success(it.toStory())
            } ?: Resource.Error(Exception("failed to get story"))
            liveData.postValue(resource)
        }.execute(listOf(id))

        return liveData
    }

    fun getComments(story: Story): LiveData<Resource<List<Comment?>>> {
        // This is not an optimal implementation, we'll fix it after
        val liveData = MutableLiveData<Resource<List<Comment?>>>().apply {
            postValue(Resource.Loading())
        }

        GetItemsTask(executor, hackerNewsApi) { items ->
            val comments = items.map { it?.toComment(emptyList()) }
            val resource: Resource<List<Comment?>> = if (!story.commentIds.isEmpty() && comments.all { it == null }) {
                Resource.Error(Exception("failed to get all comments"))
            } else {
                Resource.Success(comments)
            }
            liveData.postValue(resource)
        }.execute(story.commentIds)

        return liveData
    }

    private class GetItemsTask(
        private val executor: Executor,
        private val hackerNewsApi: HackerNewsApi,
        private val onPostExecute: (List<Item?>) -> Unit
    ) {

        fun execute(itemIds: List<Long>) {
            executor.execute {
                val ids = itemIds.map { it }
                val itemMap = ConcurrentHashMap<Long, Item>()
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
                    // do nothing
                }

                onPostExecute.invoke(ids.map { itemMap[it] })
            }
        }
    }
}
