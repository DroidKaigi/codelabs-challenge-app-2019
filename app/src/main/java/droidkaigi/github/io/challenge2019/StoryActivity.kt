package droidkaigi.github.io.challenge2019

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import droidkaigi.github.io.challenge2019.data.repository.Resource

class StoryActivity : BaseActivity() {

    companion object {
        const val EXTRA_STORY_ID = "droidkaigi.github.io.challenge2019.EXTRA_STORY_ID"
        const val READ_ARTICLE_ID = "read_article_id"
    }

    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar

    private lateinit var commentAdapter: CommentAdapter

    private val storyId: Long by lazy {
        intent.getLongExtra(EXTRA_STORY_ID, 0L)
    }

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

        setupRecyclerView()
        setupWebView()

        viewModel.isLoading.observe(this, Observer { isLoading ->
            progressView.visibility = Util.setVisibility(isLoading == true)
        })

        viewModel.storyWithComments.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Cache -> {
                    loadUrl(resource.data.story.url)
                    commentAdapter.comments = resource.data.comments
                    commentAdapter.notifyDataSetChanged()
                }

                is Resource.Error -> {
                    showError(resource.t)
                }
            }
        })

        viewModel.loadStoryWithComments(storyId)
    }

    private fun setupRecyclerView() {
        recyclerView.isNestedScrollingEnabled = false
        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)
        commentAdapter = CommentAdapter(emptyList())
        recyclerView.adapter = commentAdapter
    }

    private fun setupWebView() {
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                viewModel.onFinishWebPageLoading()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                viewModel.onErrorWebPageLoading()
            }
        }
    }

    private fun loadUrl(url: String) {
        if (url == webView.url) return

        viewModel.onStartWebPageLoading()
        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                viewModel.loadStoryWithComments(storyId)
                return true
            }
            android.R.id.home -> {
                val intent = Intent().apply {
                    putExtra(READ_ARTICLE_ID, storyId)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
