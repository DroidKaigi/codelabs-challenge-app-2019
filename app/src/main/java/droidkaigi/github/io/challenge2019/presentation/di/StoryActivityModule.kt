package droidkaigi.github.io.challenge2019.presentation.di

import dagger.Module
import dagger.Provides
import droidkaigi.github.io.challenge2019.data.model.Item
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import droidkaigi.github.io.challenge2019.presentation.story.StoryViewModel

@Module
class StoryActivityModule(private val story: Item) {
    @Provides
    @ActivityScope
    fun provideViewModelFactory(repository: HackerNewsRepository) = StoryViewModel.Factory(repository, story)
}
