package droidkaigi.github.io.challenge2019.ui.story

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.core.data.model.Story
import droidkaigi.github.io.challenge2019.databinding.ActivityStoryBinding
import droidkaigi.github.io.challenge2019.di.component
import timber.log.Timber
import javax.inject.Inject

class StoryActivity : AppCompatActivity() {

    companion object {
        const val READ_ARTICLE_ID = "read_article_id"

        private const val EXTRA_STORY = "story"

        fun createIntent(context: Context, story: Story) = Intent(context, StoryActivity::class.java).apply {
            putExtra(EXTRA_STORY, story)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(StoryViewModel::class.java)
    }

    private lateinit var commentAdapter: CommentAdapter

    private val story by lazy {
        intent.getParcelableExtra(EXTRA_STORY) as Story
    }

    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_story)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.commentRecycler.isNestedScrollingEnabled = false
        binding.commentRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        commentAdapter = CommentAdapter(emptyList())
        binding.commentRecycler.adapter = commentAdapter

        viewModel.comments.observe(this) { comments ->
            Timber.w("comments.size: ${comments.size}")
            commentAdapter.comments = comments
            commentAdapter.notifyDataSetChanged()
        }

        viewModel.getComments(story)
        loadUrl()

        // TODO: 回転の処理はViewModel使ってるので一旦無視
    }

    private fun loadUrl() {
        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                viewModel.isWebLoading.value = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                viewModel.isWebLoading.value = false
            }
        }

        viewModel.isWebLoading.value = true
        binding.webView.loadUrl(story.url)
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
                loadUrl()
                viewModel.getComments(story)
                return true
            }
            android.R.id.home -> {
                // TODO: あとで
//                val intent = Intent().apply {
//                    putExtra(READ_ARTICLE_ID, this@StoryActivity.item?.id)
//                }
//                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
