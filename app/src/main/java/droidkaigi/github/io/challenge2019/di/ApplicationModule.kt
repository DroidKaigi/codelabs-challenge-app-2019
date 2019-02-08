package droidkaigi.github.io.challenge2019.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
open class ApplicationModule(application: Application) {

    private val context: Context = application.applicationContext

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideCoroutineContext(): CoroutineContext = Dispatchers.Default
}
