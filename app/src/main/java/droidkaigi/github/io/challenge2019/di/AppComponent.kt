package droidkaigi.github.io.challenge2019.di

import android.app.Activity
import dagger.Component
import droidkaigi.github.io.challenge2019.MyApplication
import droidkaigi.github.io.challenge2019.core.data.di.CoreComponent
import droidkaigi.github.io.challenge2019.core.data.di.ViewModelModule
import droidkaigi.github.io.challenge2019.core.data.di.scope.ModuleScope
import droidkaigi.github.io.challenge2019.ui.main.MainActivity
import droidkaigi.github.io.challenge2019.ui.story.StoryActivity

@ModuleScope
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class
    ],
    dependencies = [CoreComponent::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        fun coreComponent(coreComponent: CoreComponent): Builder
    }

    fun inject(activity: MainActivity)
    fun inject(activity: StoryActivity)
}

internal fun Activity.component(): AppComponent {
    val coreComponent = MyApplication.coreComponent(this)
    return DaggerAppComponent.builder()
        .coreComponent(coreComponent)
        .build()
}