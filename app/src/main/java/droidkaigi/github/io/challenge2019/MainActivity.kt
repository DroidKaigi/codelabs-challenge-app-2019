package droidkaigi.github.io.challenge2019

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.ProgressBar
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences.Companion.saveArticleIds
import droidkaigi.github.io.challenge2019.data.repository.Resource

class MainActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var storyAdapter: StoryAdapter

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView = findViewById(R.id.item_recycler)
        progressView = findViewById(R.id.progress)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        setupRecyclerView()

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadTopStories()
        }

        viewModel.topStories.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    if (!swipeRefreshLayout.isRefreshing) {
                        progressView.visibility = Util.setVisibility(true)
                    }
                }
                is Resource.Cache -> {
                    if (storyAdapter.stories == resource.data) return@Observer
                    storyAdapter.stories = resource.data.toMutableList()
                    storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
                    storyAdapter.notifyDataSetChanged()
                }
                is Resource.Success -> {
                    progressView.visibility = Util.setVisibility(false)
                    swipeRefreshLayout.isRefreshing = false
                }
                is Resource.Error -> {
                    progressView.visibility = Util.setVisibility(false)
                    swipeRefreshLayout.isRefreshing = false
                    showError(resource.t)
                }
            }
        })

        viewModel.story.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Cache -> {
                    val story = resource.data
                    val index = storyAdapter.stories.indexOf(story)
                    if (index == -1) return@Observer

                    if (storyAdapter.stories[index] == story) return@Observer
                    storyAdapter.stories[index] = story
                    runOnUiThread {
                        storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
                        storyAdapter.notifyItemChanged(index)
                    }
                }
                is Resource.Error -> {
                    showError(resource.t)
                }
            }
        })

        viewModel.loadTopStories()
    }

    private fun setupRecyclerView() {
        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)
        storyAdapter = StoryAdapter(
            stories = mutableListOf(),
            onClickStory = { story ->
                val intent = Intent(this@MainActivity, StoryActivity::class.java).apply {
                    putExtra(StoryActivity.EXTRA_STORY_ID, story.id)
                }
                startActivityForResult(intent)
            },
            onClickMenuItem = { story, menuItemId ->
                when (menuItemId) {
                    R.id.copy_url -> {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip = ClipData.newPlainText("url", story.url)
                    }
                    R.id.refresh -> {
                        viewModel.loadStory(story.id)
                    }
                }
            },
            alreadyReadStories = ArticlePreferences.getArticleIds(this)
        )
        recyclerView.adapter = storyAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode) {
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
}
