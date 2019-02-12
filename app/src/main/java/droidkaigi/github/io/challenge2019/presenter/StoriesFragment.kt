package droidkaigi.github.io.challenge2019.presenter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.databinding.FragmentStoriesBinding
import droidkaigi.github.io.challenge2019.repository.EntryRepositoryImpl
import droidkaigi.github.io.challenge2019.repository.Success

class StoriesFragment : Fragment() {

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val repository = EntryRepositoryImpl.default()
            @Suppress("UNCHECKED_CAST")
            return StoriesViewModel(repository) as T
        }

    }

    private lateinit var binding: FragmentStoriesBinding
    private lateinit var viewModel: StoriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
            .inflate<FragmentStoriesBinding>(
                inflater,
                R.layout.fragment_stories,
                container,
                false
            )
        binding = dataBinding
        binding.lifecycleOwner = this
        binding.list.addItemDecoration(
            DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL)
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders
            .of(this, Factory())[StoriesViewModel::class.java]
        binding.viewModel = viewModel
        val adapter = StoryAdapter()
        binding.list.adapter = adapter
        viewModel.stories.observe(this, Observer {
            when (it) {
                is Success -> adapter.submitList(it.response)
            }
        })

        if (savedInstanceState == null) {
            viewModel.loadTopStories()
        }
    }

}