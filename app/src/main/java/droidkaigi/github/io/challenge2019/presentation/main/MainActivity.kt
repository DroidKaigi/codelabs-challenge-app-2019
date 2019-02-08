package droidkaigi.github.io.challenge2019.presentation.main

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.moshi.Types
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import droidkaigi.github.io.challenge2019.*
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences.Companion.saveArticleIds
import droidkaigi.github.io.challenge2019.ingest.IngestManager
import droidkaigi.github.io.challenge2019.presentation.di.ActivityModule
import droidkaigi.github.io.challenge2019.presentation.di.ActivityScope
import droidkaigi.github.io.challenge2019.presentation.story.StoryActivity
import kotlinx.coroutines.Runnable
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private const val STATE_STORIES = "stories"
    }

    @Inject
    lateinit var viewModelFactory: MainViewModel.Factory

    private lateinit var viewModel: MainViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var storyAdapter: StoryAdapter

    private val itemJsonAdapter = moshi.adapter(Item::class.java)
    private val itemsJsonAdapter =
        moshi.adapter<List<Item?>>(Types.newParameterizedType(List::class.java, Item::class.java))

    private val ingestManager = IngestManager()

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        recyclerView = findViewById(R.id.item_recycler)
        progressView = findViewById(R.id.progress)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        val itemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            recyclerView.context,
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(itemDecoration)
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
                        track()
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip = ClipData.newPlainText("url", item.url)
                    }
                    R.id.refresh -> {
                        viewModel.onClickItem(item.id)
                    }
                }
            },
            alreadyReadStories = ArticlePreferences.getArticleIds(this)
        )

        recyclerView.adapter = storyAdapter

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.onRefresh()
        }

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

        progressView.visibility = Util.setVisibility(true)

        viewModel.items.observe(this, Observer {
            progressView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
            storyAdapter.stories = it.toMutableList()
            storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
            storyAdapter.notifyDataSetChanged()
        })
        viewModel.item.observe(this, Observer {
            val index = storyAdapter.stories.indexOf(it)
            if (index == -1) {
                return@Observer
            }
            storyAdapter.stories[index] = it
            storyAdapter.alreadyReadStories =
                ArticlePreferences.getArticleIds(this@MainActivity)
            storyAdapter.notifyItemChanged(index)
        })
//            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
//                showError(t)
//            }

    }

    private fun track() {
        Thread(Runnable {
            val responseCode = ingestManager.track()
            Timber.d("IngestManager#track code:$responseCode")
        }).start()
    }

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
                progressView.visibility = Util.setVisibility(true)
                viewModel.onRefresh()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putString(STATE_STORIES, itemsJsonAdapter.toJson(storyAdapter.stories))
        }
        super.onSaveInstanceState(outState)
    }

    @ActivityScope
    @dagger.Subcomponent(
        modules = [
            ActivityModule::class
        ]
    )
    interface Subcomponent : AndroidInjector<MainActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<MainActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: MainActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = [Subcomponent::class])
    abstract class Module {
        @Binds
        @IntoMap
        @ActivityKey(MainActivity::class)
        abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
