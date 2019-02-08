package droidkaigi.github.io.challenge2019.presentation.story

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.data.model.Story
import droidkaigi.github.io.challenge2019.presentation.di.ActivityModule
import droidkaigi.github.io.challenge2019.presentation.di.ActivityScope
import droidkaigi.github.io.challenge2019.presentation.di.StoryActivityModule
import javax.inject.Inject

class StoryActivity : DaggerAppCompatActivity() {

    companion object {
        const val EXTRA_ITEM_JSON = "droidkaigi.github.io.challenge2019.EXTRA_ITEM_JSON"
        const val READ_ARTICLE_ID = "read_article_id"

        fun createIntent(context: Context, story: Story) =
            Intent(context, StoryActivity::class.java).apply {
                putExtra(StoryActivity.EXTRA_ITEM_JSON, story)
            }
    }

    @Inject
    lateinit var viewModelFactory: StoryViewModel.Factory

    private lateinit var viewModel: StoryViewModel

    private lateinit var webView: WebView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressView: ProgressBar

    private lateinit var commentAdapter: CommentAdapter

    private val item: Story?
        get() = intent.getSerializableExtra(EXTRA_ITEM_JSON) as Story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

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

        viewModel.comments.observe(this, Observer {
            commentAdapter.comments = it
            commentAdapter.notifyDataSetChanged()
            webView.loadUrl(item!!.url)
        })
        viewModel.commentLoading.observe(this, Observer {
            setProgressVisibility()
        })
        viewModel.webViewLoading.observe(this, Observer {
            setProgressVisibility()
        })

        loadUrl()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
//                progressView.visibility = Util.setVisibility(true)
                loadUrl()
                return true
            }
            android.R.id.home -> {
                val intent = Intent().apply {
                    putExtra(READ_ARTICLE_ID, this@StoryActivity.item?.id)
                }
                // TODO: backでも同じことする
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            R.id.exit -> {
                this.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setProgressVisibility() {
        val loading = viewModel.commentLoading.value == true || viewModel.webViewLoading.value == true
//        progressView.visibility = Util.setVisibility(loading)
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
