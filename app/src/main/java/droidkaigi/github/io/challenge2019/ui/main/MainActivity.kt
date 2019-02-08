package droidkaigi.github.io.challenge2019.ui.main

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences
import droidkaigi.github.io.challenge2019.data.db.ArticlePreferences.Companion.saveArticleIds
import droidkaigi.github.io.challenge2019.databinding.ActivityMainBinding
import droidkaigi.github.io.challenge2019.di.component
import droidkaigi.github.io.challenge2019.ui.story.StoryActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACTIVITY_REQUEST = 1
    }

    private lateinit var storyAdapter: StoryAdapter

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

        binding.itemRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        storyAdapter = StoryAdapter(
            stories = mutableListOf(),
            onClickItem = { story ->
                val intent = StoryActivity.createIntent(this, story)
                startActivityForResult(intent, ACTIVITY_REQUEST)
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

        viewModel.stories.observe(this) { stories ->
            storyAdapter.stories = stories.toMutableList()
            storyAdapter.alreadyReadStories = ArticlePreferences.getArticleIds(this@MainActivity)
            storyAdapter.notifyDataSetChanged()
        }

        viewModel.loadTopStories()

        // TODO: 回転の処理はViewModel使ってるので一旦無視
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.exit -> {
                this.finish()
                return true
            }
            R.id.refresh -> {
                viewModel.loadTopStories()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onRefresh() {
        viewModel.loadTopStories(true)
    }
}
