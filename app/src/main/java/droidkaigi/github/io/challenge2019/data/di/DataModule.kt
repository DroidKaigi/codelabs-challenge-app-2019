package droidkaigi.github.io.challenge2019.data.di

import dagger.Module
import dagger.Provides
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepository
import droidkaigi.github.io.challenge2019.data.repository.HackerNewsRepositorySource
import droidkaigi.github.io.challenge2019.infrastructure.database.PreferencesProvider
import droidkaigi.github.io.challenge2019.infrastructure.network.HackerNewsApi
import javax.inject.Singleton

@Module
open class DataModule {
    @Singleton
    @Provides
    fun provideHackerNewsRepository(
        hackerNewsApi: HackerNewsApi,
        preferencesProvider: PreferencesProvider
    ): HackerNewsRepository =
        HackerNewsRepositorySource(hackerNewsApi, preferencesProvider)
}
