package droidkaigi.github.io.challenge2019.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import droidkaigi.github.io.challenge2019.databinding.ActivityMainBinding
import droidkaigi.github.io.challenge2019.ext.copyToClipboard
import droidkaigi.github.io.challenge2019.ext.showError
import droidkaigi.github.io.challenge2019.ingest.IngestManager
import droidkaigi.github.io.challenge2019.presentation.di.ActivityModule
import droidkaigi.github.io.challenge2019.presentation.di.ActivityScope
import droidkaigi.github.io.challenge2019.presentation.story.StoryActivity
import kotlinx.coroutines.Runnable
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        const val ACTIVITY_REQUEST = 1
    }

    @Inject
    lateinit var viewModelFactory: MainViewModel.Factory

    @Inject
    lateinit var ingestManager: IngestManager

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.articles.observe(this, Observer {
            storyAdapter.stories = it.toMutableList()
            storyAdapter.notifyDataSetChanged()
        })
        viewModel.article.observe(this, Observer {
            val index = storyAdapter.stories.indexOfFirst { item -> item?.id == it.id }
            if (index == -1) {
                return@Observer
            }
            storyAdapter.stories[index] = it
            storyAdapter.notifyItemChanged(index)
        })
        viewModel.errorEvent.observe(this, Observer {
            showError(it)
        })

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
        }
        setUpRecyclerView()
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
                viewModel.onInit()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.getLongExtra(StoryActivity.READ_ARTICLE_ID, 0L)?.let { id ->
                    if (id != 0L) {
                        viewModel.onReadArticle(id)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setUpRecyclerView() {
        val itemDecoration = DividerItemDecoration(
            binding.itemRecycler.context,
            DividerItemDecoration.VERTICAL
        )
        binding.itemRecycler.addItemDecoration(itemDecoration)
        storyAdapter = StoryAdapter(
            stories = mutableListOf(),
            onClickItem = { item ->
                startActivityForResult(
                    StoryActivity.createIntent(this@MainActivity, item.content),
                    ACTIVITY_REQUEST
                )

            },
            onClickMenuItem = { item, menuItemId ->
                when (menuItemId) {
                    R.id.copy_url -> {
                        track()
                        copyToClipboard("url", item.content.url)
                    }
                    R.id.refresh -> {
                        viewModel.onClickItem(item.id)
                    }
                }
            }
        )

        binding.itemRecycler.adapter = storyAdapter
    }

    private fun track() {
        Thread(Runnable {
            val responseCode = ingestManager.track()
            Timber.d("IngestManager#track code:$responseCode")
        }).start()
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
