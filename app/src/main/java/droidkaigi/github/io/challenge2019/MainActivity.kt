package droidkaigi.github.io.challenge2019

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import com.squareup.moshi.Moshi
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var hackerNewsApi: HackerNewsApi

    private var getItemsTask: AsyncTask<Long, Unit, List<Item?>>? = null
    private val moshi = Moshi.Builder().build()
    private val itemJsonAdapter = moshi.adapter(Item::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        hackerNewsApi = retrofit.create(HackerNewsApi::class.java)

        recyclerView = findViewById(R.id.item_recycler)
        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        hackerNewsApi.getTopStories().enqueue(object : Callback<List<Long>> {

            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                if (!response.isSuccessful) return

                response.body()?.let { itemIds ->
                    getItemsTask = @SuppressLint("StaticFieldLeak") object: AsyncTask<Long, Unit, List<Item?>>() {
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

                        override fun onPostExecute(items: List<Item?>) {
                            viewAdapter = StoryAdapter(items) { item ->
                                val itemJson = itemJsonAdapter.toJson(item)
                                val intent = Intent(this@MainActivity, StoryActivity::class.java).apply {
                                    putExtra(StoryActivity.EXTRA_ITEM_JSON, itemJson)
                                }
                                startActivity(intent)
                            }
                            recyclerView.adapter = viewAdapter
                        }
                    }

                    getItemsTask?.execute(*itemIds.take(20).toTypedArray())
                }
            }

            override fun onFailure(call: Call<List<Long>>, t: Throwable) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        getItemsTask?.run {
            if (!isCancelled) cancel(true)
        }
    }
}
