package droidkaigi.github.io.challenge2019

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import droidkaigi.github.io.challenge2019.presenter.StoriesViewModel
import droidkaigi.github.io.challenge2019.repository.EntryRepositoryImpl
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val repository = EntryRepositoryImpl.default()
            @Suppress("UNCHECKED_CAST")
            return StoriesViewModel(repository) as T
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProviders.of(this, Factory())[StoriesViewModel::class.java]
        viewModel.stories
            .observe(this, Observer { stories ->
                Timber.d("load stories $stories")
            })
        viewModel.isLoading
            .observe(this, Observer { isLoading ->
                Timber.d("is loading: $isLoading")
            })
        viewModel.loadTopStories()
    }

}