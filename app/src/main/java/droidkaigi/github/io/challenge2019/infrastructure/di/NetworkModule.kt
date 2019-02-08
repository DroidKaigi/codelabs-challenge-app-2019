package droidkaigi.github.io.challenge2019.infrastructure.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import droidkaigi.github.io.challenge2019.infrastructure.network.HackerNewsApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
open class NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(@NetworkLogger loggingInterceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient =
        OkHttpClient.Builder().apply {
            loggingInterceptors.forEach {
                addNetworkInterceptor(it)
            }
        }.build()

    @Singleton
    @Provides
    @IntoSet
    @NetworkLogger
    fun provideNetworkLogger(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    @Singleton
    @Provides
    fun provideRegliApi(retrofit: Retrofit): HackerNewsApi = retrofit.create(HackerNewsApi::class.java)

    companion object {
        private const val BASE_URL = "https://hacker-news.firebaseio.com/v0/"
    }
}
