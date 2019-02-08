package droidkaigi.github.io.challenge2019.core.data.di

import dagger.Module
import dagger.Provides
import droidkaigi.github.io.challenge2019.core.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsDataRepository
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import javax.inject.Singleton

@Module
class CoreModule {

    @Singleton
    @Provides
    fun provideHackerNewsRepository(hackerNewsApi: HackerNewsApi): HackerNewsRepository {
        return HackerNewsDataRepository(hackerNewsApi)
    }
}