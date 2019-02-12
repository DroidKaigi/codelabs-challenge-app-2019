package droidkaigi.github.io.challenge2019.presenter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.databinding.RowStoryBinding
import droidkaigi.github.io.challenge2019.domain.hackernews.Story
import droidkaigi.github.io.challenge2019.ui.DataBoundListAdapter

class StoryAdapter : DataBoundListAdapter<Story, RowStoryBinding>(
    diffCallback = object : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(p0: Story, p1: Story): Boolean = p0.id == p1.id

        override fun areContentsTheSame(p0: Story, p1: Story): Boolean = p0.id == p1.id
    }
) {

    override fun createBinding(parent: ViewGroup): RowStoryBinding =
        DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_story,
                parent,
                false
            )

    override fun bind(binding: RowStoryBinding, item: Story) {
        binding.item = item
    }
}