package droidkaigi.github.io.challenge2019

import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import droidkaigi.github.io.challenge2019.data.di.DataModule
import droidkaigi.github.io.challenge2019.di.ApplicationModule
import droidkaigi.github.io.challenge2019.infrastructure.di.NetworkModule
import droidkaigi.github.io.challenge2019.presentation.main.MainActivity
import droidkaigi.github.io.challenge2019.presentation.story.StoryActivity
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Singleton

class MyApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerMyApplication_Component.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    @Singleton
    @dagger.Component(
        modules = [
            AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            NetworkModule::class,
            DataModule::class,
            MainActivity.Module::class,
            StoryActivity.Module::class
        ]
    )
    interface Component : AndroidInjector<MyApplication>
}