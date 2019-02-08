package droidkaigi.github.io.challenge2019.core.data.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import droidkaigi.github.io.challenge2019.core.util.ViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}