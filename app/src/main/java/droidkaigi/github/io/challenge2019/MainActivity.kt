package droidkaigi.github.io.challenge2019

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.squareup.moshi.Types
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.ingest.IngestManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class MainActivity : BaseActivity() {

    companion object {
        private const val STATE_STORIES = "stories"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var storyAdapter: StoryAdapter
    private lateinit var hackerNewsApi: HackerNewsApi

    private var getStoriesTask: AsyncTask<Long, Unit, List<Item?>>? = null
    private val itemJsonAdapter = moshi.adapter(Item::class.java)
    private val itemsJsonAdapter =
        moshi.adapter<List<Item?>>(Types.newParameterizedType(List::class.java, Item::class.java))


    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IngestManager().track()
        recyclerView = findViewById(R.id.item_recycler)
        progressView = findViewById(R.id.progress)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        val retrofit = createRetrofit("https://hacker-news.firebaseio.com/v0/")

        hackerNewsApi = retrofit.create(HackerNewsApi::class.java)

        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)
        storyAdapter = StoryAdapter(emptyList()) { item ->
            val itemJson = itemJsonAdapter.toJson(item)
            val intent = Intent(this@MainActivity, StoryActivity::class.java).apply {
                putExtra(StoryActivity.EXTRA_ITEM_JSON, itemJson)
            }
            startActivity(intent)
        }
        recyclerView.adapter = storyAdapter

        swipeRefreshLayout.setOnRefreshListener { loadTopStories() }

        val savedStories = savedInstanceState?.let { bundle ->
            bundle.getString(STATE_STORIES)?.let { itemsJson ->
                itemsJsonAdapter.fromJson(itemsJson)
            }
        }

        if (savedStories != null) {
            storyAdapter.stories = savedStories
            storyAdapter.notifyDataSetChanged()
            return
        }

        progressView.visibility = Util.setVisibility(true)
        loadTopStories()
    }

    private fun loadTopStories() {
        hackerNewsApi.getTopStories().enqueue(object : Callback<List<Long>> {

            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                if (!response.isSuccessful) return

                response.body()?.let { itemIds ->
                    getStoriesTask = @SuppressLint("StaticFieldLeak") object : AsyncTask<Long, Unit, List<Item?>>() {

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
                                        showError(t)
                                        latch.countDown()
                                    }
                                })
                            }

                            try {
                                latch.await()
                            } catch (e: InterruptedException) {
                                showError(e)
                                return emptyList()
                            }

                            return ids.map { itemMap[it] }
                        }

                        override fun onPostExecute(items: List<Item?>) {
                            progressView.visibility = View.GONE
                            swipeRefreshLayout.isRefreshing = false
                            storyAdapter.stories = items
                            storyAdapter.notifyDataSetChanged()
                        }
                    }

                    getStoriesTask?.execute(*itemIds.take(20).toTypedArray())
                }
            }

            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                showError(t)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.apply {
            putString(STATE_STORIES, itemsJsonAdapter.toJson(storyAdapter.stories))
        }

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        getStoriesTask?.run {
            if (!isCancelled) cancel(true)
        }
    }
}
