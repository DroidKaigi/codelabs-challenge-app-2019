package droidkaigi.github.io.challenge2019.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.Room
import droidkaigi.github.io.challenge2019.App
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.api.response.mapper.toCommentEntity
import droidkaigi.github.io.challenge2019.data.api.response.mapper.toCommentIdEntities
import droidkaigi.github.io.challenge2019.data.api.response.mapper.toStoryEntity
import droidkaigi.github.io.challenge2019.data.db.AppDatabase
import droidkaigi.github.io.challenge2019.data.db.entity.mapper.toStory
import droidkaigi.github.io.challenge2019.data.db.entity.mapper.toStoryWithComments
import droidkaigi.github.io.challenge2019.model.Story
import droidkaigi.github.io.challenge2019.model.StoryWithComments
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
        return MediatorLiveData<Resource<Story>>().apply {
            addSource(refreshStory(id)) { resource -> resource?.let { value = it } }
            addSource(db.storyDao().byId(id)) { storyEntity ->
                storyEntity?.let { value = Resource.Success(it.toStory()) }
            }
        }
    }

    private fun refreshStory(id: Long): LiveData<Resource<Story>> {
        val liveData = MutableLiveData<Resource<Story>>().apply {
            postValue(Resource.Loading())
        }

        GetItemsTask(executor, hackerNewsApi) { items ->
            val item = items.firstOrNull()
            if (item == null) {
                liveData.postValue(Resource.Error(Exception("failed to get story")))
                return@GetItemsTask
            }

            db.runInTransaction {
                val alreadyRead = db.storyDao().getAlreadyReadStories()
                    .find { it.id == id }?.alreadyRead ?: false
                db.storyDao().insert(item.toStoryEntity(alreadyRead))
            }
        }.execute(listOf(id))

        return liveData
    }

    fun getStoryWithComments(storyId: Long): LiveData<Resource<StoryWithComments>> {
        return MediatorLiveData<Resource<StoryWithComments>>().apply {
            addSource(refreshStoryWithComments(storyId)) { resource -> resource?.let { value = it } }
            addSource(db.storyCommentJoinDao().byStoryIdWithComments(storyId)) { storyWithCommentsEntity ->
                storyWithCommentsEntity?.let { value = Resource.Success(it.toStoryWithComments()) }
            }
        }
    }

    private fun refreshStoryWithComments(storyId: Long): LiveData<Resource<StoryWithComments>> {
        val liveData = MutableLiveData<Resource<StoryWithComments>>().apply {
            postValue(Resource.Loading())
        }

        executor.execute {
            val commentIds = db.commentIdDao().byStoryId(storyId).map { it.id }
            refreshComments(storyId, commentIds,
                onSuccess = {},
                onError = { error -> liveData.postValue(Resource.Error(error)) }
            )
        }

        return liveData
    }

    private fun refreshComments(
        storyId: Long,
        commentIds: List<Long>,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        GetItemsTask(executor, hackerNewsApi) { items ->
            if (!commentIds.isEmpty() && items.all { it == null }) {
                onError?.invoke(Exception("failed to get all comments"))
                return@GetItemsTask
            }

            val comments = items.mapNotNull { it?.toCommentEntity(storyId) }
            db.runInTransaction {
                db.commentDao().insert(comments)
            }
            onSuccess?.invoke()
        }.execute(commentIds)
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
