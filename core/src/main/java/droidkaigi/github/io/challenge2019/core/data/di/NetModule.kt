package droidkaigi.github.io.challenge2019.core.data.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import droidkaigi.github.io.challenge2019.core.BuildConfig
import droidkaigi.github.io.challenge2019.core.data.api.HackerNewsApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }
    }.build()

    @Singleton
    @Provides
    fun provideHackerNewsApi(
        okHttp: OkHttpClient,
        moshi: Moshi
    ): HackerNewsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/v0/") // TODO: BuildConfig使いたい
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        return retrofit.create(HackerNewsApi::class.java)
    }
}