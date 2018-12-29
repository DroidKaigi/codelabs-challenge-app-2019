package droidkaigi.github.io.challenge2019

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.squareup.moshi.Types
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import droidkaigi.github.io.challenge2019.data.repository.Resource
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story
import java.util.concurrent.CountDownLatch

class StoryActivity : BaseActivity() {

    companion object {
        const val EXTRA_ITEM_JSON = "droidkaigi.github.io.challenge2019.EXTRA_ITEM_JSON"
        const val READ_ARTICLE_ID = "read_article_id"
        private const val STATE_COMMENTS = "comments"
    }

    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar

    private lateinit var commentAdapter: CommentAdapter

    private val storyJsonAdapter = moshi.adapter(Story::class.java)
    private val commentsJsonAdapter =
        moshi.adapter<List<Comment?>>(Types.newParameterizedType(List::class.java, Comment::class.java))

    private var story: Story? = null

    private val viewModel: StoryViewModel by lazy {
        ViewModelProviders.of(this).get(StoryViewModel::class.java)
    }

    override fun getContentView(): Int {
        return R.layout.activity_story
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = findViewById(R.id.web_view)
        recyclerView = findViewById(R.id.comment_recycler)
        progressView = findViewById(R.id.progress)

        story = intent.getStringExtra(EXTRA_ITEM_JSON)?.let {
            storyJsonAdapter.fromJson(it)
        }

        setupRecyclerView()

        val savedComments = savedInstanceState?.let { bundle ->
            bundle.getString(STATE_COMMENTS)?.let { itemsJson ->
                commentsJsonAdapter.fromJson(itemsJson)
            }
        }

        if (savedComments != null) {
            commentAdapter.comments = savedComments
            commentAdapter.notifyDataSetChanged()
            webView.loadUrl(story!!.url)
            return
        }

        viewModel.isLoading.observe(this, Observer { isLoading ->
            progressView.visibility = Util.setVisibility(isLoading == true)
        })

        viewModel.comments.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    commentAdapter.comments = resource.data
                    commentAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    showError(resource.t)
                }
            }
        })

        loadUrlAndComments()
    }

    private fun setupRecyclerView() {
        recyclerView.isNestedScrollingEnabled = false
        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)
        commentAdapter = CommentAdapter(emptyList())
        recyclerView.adapter = commentAdapter
    }

    private fun loadUrlAndComments() {
        val story = this.story ?: return

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                viewModel.onFinishWebPageLoading()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                viewModel.onErrorWebPageLoading()
            }
        }

        viewModel.onStartWebPageLoading()
        webView.loadUrl(story.url)

        viewModel.loadComments(story)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                loadUrlAndComments()
                return true
            }
            android.R.id.home -> {
                val intent = Intent().apply {
                    putExtra(READ_ARTICLE_ID, this@StoryActivity.story?.id)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.apply {
            putString(STATE_COMMENTS, commentsJsonAdapter.toJson(commentAdapter.comments))
        }

        super.onSaveInstanceState(outState)
    }
}
