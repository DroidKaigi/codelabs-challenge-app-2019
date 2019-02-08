package droidkaigi.github.io.challenge2019.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import droidkaigi.github.io.challenge2019.core.data.di.ViewModelKey
import droidkaigi.github.io.challenge2019.ui.main.MainViewModel
import droidkaigi.github.io.challenge2019.ui.story.StoryViewModel

@Module
abstract class AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoryViewModel::class)
    abstract fun bindStoryViewModel(viewModel: StoryViewModel): ViewModel
}