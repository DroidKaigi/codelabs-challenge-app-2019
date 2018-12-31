package droidkaigi.github.io.challenge2019

import android.app.Application

class App : Application() {

    companion object {
        lateinit var appContext: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}