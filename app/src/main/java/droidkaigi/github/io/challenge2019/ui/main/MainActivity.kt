package droidkaigi.github.io.challenge2019.ui.main

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.squareup.moshi.Types
import droidkaigi.github.io.challenge2019.BaseActivity
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.StoryActivity
import droidkaigi.github.io.challenge2019.StoryAdapter
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences.Companion.saveArticleIds
import droidkaigi.github.io.challenge2019.databinding.ActivityMainBinding
import droidkaigi.github.io.challenge2019.di.component
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private const val STATE_STORIES = "stories"
    }

    private lateinit var storyAdapter: StoryAdapter

    private var getStoriesTask: AsyncTask<Long, Unit, List<Item?>>? = null
    private val itemJsonAdapter = moshi.adapter(Item::class.java)
    private val itemsJsonAdapter =
        moshi.adapter<List<Item?>>(Types.newParameterizedType(List::class.java, Item::class.java))

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component().inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.handler = this
        binding.viewModel = viewModel

        val itemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            this,
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )
        binding.itemRecycler.addItemDecoration(itemDecoration)
        storyAdapter = StoryAdapter(
            stories = mutableListOf(),
            onClickItem = { item ->
                val itemJson = itemJsonAdapter.toJson(item)
                val intent =
                    Intent(this@MainActivity, StoryActivity::class.java).apply {
                        putExtra(StoryActivity.EXTRA_ITEM_JSON, itemJson)
                    }
                startActivityForResult(intent)
            },
            onClickMenuItem = { item, menuItemId ->
                when (menuItemId) {
                    R.id.copy_url -> {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip = ClipData.newPlainText("url", item.url)
                    }
                    R.id.refresh -> {
                        // TODO: これ邪魔なので、とりあえずコメント
//                        hackerNewsApi.getItem(item.id).enqueue(object Callback<Item> {
//                            override fun onResponse(call: Call<Item>, response: Response<Item>) {
//                                response.body()?.let { newItem ->
//                                    val index = storyAdapter.stories.indexOf(item)
//                                    if (index == -1) return
//
//                                    storyAdapter.stories[index] = newItem
//                                    runOnUiThread {
//                                        storyAdapter.alreadyReadStories =
//                                            ArticlePreferences.getArticleIds(this@MainActivity)
//                                        storyAdapter.notifyItemChanged(index)
//                                    }
//                                }
//                            }
//
//                            override fun onFailure(call: Call<Item>, t: Throwable) {
//                                showError(t)
//                            }
//                        })
                    }
                }
            },
            alreadyReadStories = ArticlePreferences.getArticleIds(this)
        )
        binding.itemRecycler.adapter = storyAdapter

        val savedStories = savedInstanceState?.let { bundle ->
            bundle.getString(STATE_STORIES)?.let { itemsJson ->
                itemsJsonAdapter.fromJson(itemsJson)
            }
        }

        if (savedStories != null) {
            storyAdapter.stories = savedStories.toMutableList()
            storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
            storyAdapter.notifyDataSetChanged()
            return
        }


        viewModel.items.observe(this) { items ->
            storyAdapter.stories = items.toMutableList()
            storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
            storyAdapter.notifyDataSetChanged()
        }

        viewModel.loadTopStories()
    }

//    private fun loadTopStories() {
//        hackerNewsApi.getTopStories().enqueue(object : Callback<List<Long>> {
//
//            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
//                if (!response.isSuccessful) return
//
//                response.body()?.let { itemIds ->
//                    getStoriesTask = @SuppressLint("StaticFieldLeak") object : AsyncTask<Long, Unit, List<Item?>>() {
//
//                        override fun doInBackground(vararg itemIds: Long?): List<Item?> {
//                            val ids = itemIds.mapNotNull { it }
//                            val itemMap = ConcurrentHashMap<Long, Item?>()
//                            val latch = CountDownLatch(ids.size)
//
//                            ids.forEach { id ->
//                                hackerNewsApi.getItem(id).enqueue(object : Callback<Item> {
//                                    override fun onResponse(call: Call<Item>, response: Response<Item>) {
//                                        response.body()?.let { item -> itemMap[id] = item }
//                                        latch.countDown()
//                                    }
//
//                                    override fun onFailure(call: Call<Item>, t: Throwable) {
//                                        showError(t)
//                                        latch.countDown()
//                                    }
//                                })
//                            }
//
//                            try {
//                                latch.await()
//                            } catch (e: InterruptedException) {
//                                showError(e)
//                                return emptyList()
//                            }
//
//                            return ids.map { itemMap[it] }
//                        }
//
//                        override fun onPostExecute(items: List<Item?>) {
//                            progressView.visibility = View.GONE
//                            swipeRefreshLayout.isRefreshing = false
//                            storyAdapter.stories = items.toMutableList()
//                            storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
//                            storyAdapter.notifyDataSetChanged()
//                        }
//                    }
//
//                    getStoriesTask?.execute(*itemIds.take(20).toTypedArray())
//                }
//            }
//
//            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
//                showError(t)
//            }
//        })
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.getLongExtra(StoryActivity.READ_ARTICLE_ID, 0L)?.let { id ->
                    if (id != 0L) {
                        saveArticleIds(this, id.toString())
                        storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this)
                        storyAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                viewModel.loadTopStories()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.apply {
            putString(STATE_STORIES, itemsJsonAdapter.toJson(storyAdapter.stories))
        }

        super.onSaveInstanceState(outState, outPersistentState)
    }

    fun onRefresh() {
        viewModel.loadTopStories(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        getStoriesTask?.run {
            if (!isCancelled) cancel(true)
        }
    }
}
