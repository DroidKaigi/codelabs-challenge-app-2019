package droidkaigi.github.io.challenge2019.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import droidkaigi.github.io.challenge2019.core.data.di.ViewModelKey
import droidkaigi.github.io.challenge2019.ui.main.MainViewModel

@Module
abstract class AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}