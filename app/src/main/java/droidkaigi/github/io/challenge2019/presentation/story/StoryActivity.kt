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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.data.model.Story
import droidkaigi.github.io.challenge2019.databinding.ActivityStoryBinding
import droidkaigi.github.io.challenge2019.ext.showError
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

    private lateinit var binding: ActivityStoryBinding

    private lateinit var commentAdapter: CommentAdapter

    private val item: Story?
        get() = intent.getSerializableExtra(EXTRA_ITEM_JSON) as Story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoryViewModel::class.java)
        viewModel.comments.observe(this, Observer {
            commentAdapter.comments = it
            commentAdapter.notifyDataSetChanged()
        })
        viewModel.errorEvent.observe(this, Observer {
            showError(it)
        })

        binding = DataBindingUtil.setContentView<ActivityStoryBinding>(this, R.layout.activity_story).apply {
            viewModel = this@StoryActivity.viewModel
            lifecycleOwner = this@StoryActivity
        }

        setUpRecyclerView()

        loadUrl()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.refresh -> {
                viewModel.onInit()
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
            R.id.exit -> {
                this.finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpRecyclerView() {
        binding.commentRecycler.isNestedScrollingEnabled = false
        val itemDecoration = DividerItemDecoration(
            binding.commentRecycler.context,
            DividerItemDecoration.VERTICAL
        )
        binding.commentRecycler.addItemDecoration(itemDecoration)
        commentAdapter = CommentAdapter(emptyList())
        binding.commentRecycler.adapter = commentAdapter
    }

    private fun loadUrl() {
        viewModel.webViewLoading.value = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                viewModel.webViewLoading.value = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                viewModel.webViewLoading.value = false
            }
        }
        binding.webView.loadUrl(item!!.url)
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
