package droidkaigi.github.io.challenge2019

import android.app.Application
import com.facebook.stetho.Stetho
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }
}