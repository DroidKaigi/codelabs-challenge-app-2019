package droidkaigi.github.io.challenge2019.ui.story

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.squareup.moshi.Types
import droidkaigi.github.io.challenge2019.BaseActivity
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.Util
import droidkaigi.github.io.challenge2019.core.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.di.component
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class StoryActivity : BaseActivity() {

    companion object {
        const val EXTRA_ITEM_JSON = "droidkaigi.github.io.challenge2019.EXTRA_ITEM_JSON"
        const val READ_ARTICLE_ID = "read_article_id"
        private const val STATE_COMMENTS = "comments"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(StoryViewModel::class.java)
    }

    private lateinit var webView: WebView
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var progressView: ProgressBar

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var hackerNewsApi: HackerNewsApi

    private var getCommentsTask: AsyncTask<Long, Unit, List<Item?>>? = null
    private var hideProgressTask: AsyncTask<Unit, Unit, Unit>? = null
    private val itemJsonAdapter = moshi.adapter(Item::class.java)
    private val itemsJsonAdapter =
        moshi.adapter<List<Item?>>(Types.newParameterizedType(List::class.java, Item::class.java))

    private val item by lazy {
        intent.getStringExtra(EXTRA_ITEM_JSON)?.let {
            itemJsonAdapter.fromJson(it)
        } ?: throw IllegalArgumentException()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component().inject(this)
        setContentView(R.layout.activity_story)

        viewModel.getComments(item)
        webView = findViewById(R.id.web_view)
        recyclerView = findViewById(R.id.comment_recycler)
        progressView = findViewById(R.id.progress)

        val retrofit = createRetrofit("https://hacker-news.firebaseio.com/v0/")

        hackerNewsApi = retrofit.create(HackerNewsApi::class.java)

        recyclerView.isNestedScrollingEnabled = false
        val itemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            recyclerView.context,
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(itemDecoration)
        commentAdapter = CommentAdapter(emptyList())
        recyclerView.adapter = commentAdapter

        val savedComments = savedInstanceState?.let { bundle ->
            bundle.getString(STATE_COMMENTS)?.let { itemsJson ->
                itemsJsonAdapter.fromJson(itemsJson)
            }
        }

        if (savedComments != null) {
            commentAdapter.comments = savedComments
            commentAdapter.notifyDataSetChanged()
            webView.loadUrl(item!!.url)
            return
        }

        progressView.visibility = View.VISIBLE

        viewModel.comments.observe(this) { items ->
            commentAdapter.comments = items
            commentAdapter.notifyDataSetChanged()
        }

        viewModel.getComments(item)

        loadUrl()
    }

    private fun loadUrl() {
        val progressLatch = CountDownLatch(2)

        hideProgressTask = @SuppressLint("StaticFieldLeak") object : AsyncTask<Unit, Unit, Unit>() {

            override fun doInBackground(vararg unit: Unit?) {
                try {
                    progressLatch.await()
                } catch (e: InterruptedException) {
                    showError(e)
                }
            }

            override fun onPostExecute(result: Unit?) {
                progressView.visibility = Util.setVisibility(false)
            }
        }

        hideProgressTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                progressLatch.countDown()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                progressLatch.countDown()
            }
        }
        webView.loadUrl(item!!.url)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                progressView.visibility = Util.setVisibility(true)
                loadUrl()
                viewModel.getComments(this.item)
                return true
            }
            android.R.id.home -> {
                val intent = Intent().apply {
                    putExtra(READ_ARTICLE_ID, this@StoryActivity.item?.id)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.apply {
            putString(STATE_COMMENTS, itemsJsonAdapter.toJson(commentAdapter.comments))
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onDestroy() {
        super.onDestroy()
        getCommentsTask?.run {
            if (!isCancelled) cancel(true)
        }
        hideProgressTask?.run {
            if (!isCancelled) cancel(true)
        }
    }
}
