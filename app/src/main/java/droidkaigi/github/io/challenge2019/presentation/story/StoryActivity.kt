package droidkaigi.github.io.challenge2019.presentation.story

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Types
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import droidkaigi.github.io.challenge2019.BaseActivity
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.Util
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.presentation.di.ActivityModule
import droidkaigi.github.io.challenge2019.presentation.di.ActivityScope
import droidkaigi.github.io.challenge2019.presentation.di.StoryActivityModule
import javax.inject.Inject

class StoryActivity : BaseActivity() {

    companion object {
        const val EXTRA_ITEM_JSON = "droidkaigi.github.io.challenge2019.EXTRA_ITEM_JSON"
        const val READ_ARTICLE_ID = "read_article_id"
        private const val STATE_COMMENTS = "comments"
    }

    @Inject
    lateinit var viewModelFactory: StoryViewModel.Factory

    private lateinit var viewModel: StoryViewModel

    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar

    private lateinit var commentAdapter: CommentAdapter

    private val itemJsonAdapter = moshi.adapter(Item::class.java)
    private val itemsJsonAdapter =
        moshi.adapter<List<Item?>>(Types.newParameterizedType(List::class.java, Item::class.java))

    private val item: Item?
        get() = intent.getStringExtra(EXTRA_ITEM_JSON)?.let {
            itemJsonAdapter.fromJson(it)
        }

    override fun getContentView(): Int {
        return R.layout.activity_story
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoryViewModel::class.java)

        webView = findViewById(R.id.web_view)
        recyclerView = findViewById(R.id.comment_recycler)
        progressView = findViewById(R.id.progress)

        recyclerView.isNestedScrollingEnabled = false
        val itemDecoration = androidx.recyclerview.widget.DividerItemDecoration(
            recyclerView.context,
            androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(itemDecoration)
        commentAdapter = CommentAdapter(emptyList())
        recyclerView.adapter = commentAdapter

        if (item == null) return

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
        viewModel.comments.observe(this, Observer {
            commentAdapter.comments = it
            commentAdapter.notifyDataSetChanged()
        })
        viewModel.commentLoading.observe(this, Observer {
            setProgressVisibility()
        })
        viewModel.webViewLoading.observe(this, Observer {
            setProgressVisibility()
        })

        loadUrl()
    }

    private fun setProgressVisibility() {
        val loading = viewModel.commentLoading.value == true || viewModel.webViewLoading.value == true
        progressView.visibility = Util.setVisibility(loading)
    }

    private fun loadUrl() {
        if (item == null) return

        viewModel.webViewLoading.value = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                viewModel.webViewLoading.value = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                viewModel.webViewLoading.value = false
            }
        }
        webView.loadUrl(item!!.url)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                progressView.visibility = Util.setVisibility(true)
                loadUrl()
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putString(STATE_COMMENTS, itemsJsonAdapter.toJson(commentAdapter.comments))
        }
        super.onSaveInstanceState(outState)
    }

    @ActivityScope
    @dagger.Subcomponent(
        modules = [
            ActivityModule::class,
            StoryActivityModule::class
        ]
    )
    interface Subcomponent : AndroidInjector<StoryActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<StoryActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            abstract fun storyActivityModule(module: StoryActivityModule): Builder

            override fun seedInstance(instance: StoryActivity) {
                activityModule(ActivityModule(instance))
                storyActivityModule(StoryActivityModule(instance.item!!))
            }
        }
    }

    @dagger.Module(subcomponents = [Subcomponent::class])
    abstract class Module {
        @Binds
        @IntoMap
        @ActivityKey(StoryActivity::class)
        abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
