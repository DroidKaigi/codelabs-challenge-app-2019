package droidkaigi.github.io.challenge2019

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import droidkaigi.github.io.challenge2019.core.data.di.DaggerCoreComponent
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApplication : Application() {

    private val coreComponent by lazy {
        DaggerCoreComponent.builder().build()
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }

    companion object {
        fun coreComponent(context: Context) = (context.applicationContext as MyApplication).coreComponent
    }
}