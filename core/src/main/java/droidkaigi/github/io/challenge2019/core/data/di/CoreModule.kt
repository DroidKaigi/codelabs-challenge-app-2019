package droidkaigi.github.io.challenge2019.core.data.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import droidkaigi.github.io.challenge2019.core.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsDataRepository
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import javax.inject.Singleton

@Module
class CoreModule(
    private val context: Context
) {
    @Singleton
    @Provides
    fun providePreference(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideHackerNewsRepository(
        hackerNewsApi: HackerNewsApi,
        preferences: SharedPreferences
    ): HackerNewsRepository {
        return HackerNewsDataRepository(hackerNewsApi, preferences)
    }
}