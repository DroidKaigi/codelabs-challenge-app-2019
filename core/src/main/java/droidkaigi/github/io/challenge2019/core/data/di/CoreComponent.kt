package droidkaigi.github.io.challenge2019.core.data.di

import dagger.Component
import droidkaigi.github.io.challenge2019.core.data.repository.HackerNewsRepository
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        NetModule::class
    ]
)
interface CoreComponent {
    @Component.Builder
    interface Builder {
        fun build(): CoreComponent
    }

    fun hackerNewsRepository(): HackerNewsRepository
}