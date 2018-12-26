package droidkaigi.github.io.challenge2019

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.ItemAdapter
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

    private var getItemTask: GetItemsTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        hackerNewsApi = retrofit.create(HackerNewsApi::class.java)

        recyclerView = findViewById(R.id.item_recycler)

        hackerNewsApi.getTopStories().enqueue(object : Callback<List<Long>> {

            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                if (!response.isSuccessful) return

                response.body()?.let { itemIds ->
                    getItemTask = GetItemsTask(hackerNewsApi) { items ->
                        viewAdapter = ItemAdapter(items)
                        runOnUiThread {
                            recyclerView.adapter = viewAdapter
                        }
                    }

                    getItemTask?.execute(*itemIds.take(20).toTypedArray())
                }
            }

            override fun onFailure(call: Call<List<Long>>, t: Throwable) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        getItemTask?.run {
            if (!isCancelled) cancel(true)
        }
    }

    class GetItemsTask(
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
