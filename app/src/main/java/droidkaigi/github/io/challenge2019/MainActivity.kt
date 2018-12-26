package droidkaigi.github.io.challenge2019

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

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var hackerNewsApi: HackerNewsApi

    private val itemMap = ConcurrentHashMap<Long, Item>()

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
                    viewAdapter = ItemAdapter(itemIds.take(20), hackerNewsApi)
                    runOnUiThread {
                        recyclerView.adapter = viewAdapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Long>>, t: Throwable) {

            }
        })
    }
}
