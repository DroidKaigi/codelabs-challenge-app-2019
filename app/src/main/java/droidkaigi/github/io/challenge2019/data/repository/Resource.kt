package droidkaigi.github.io.challenge2019.data.repository

sealed class Resource<T> {
    data class Cache<T>(val data: T): Resource<T>()
    class Success<T>: Resource<T>()
    data class Error<T>(val t: Throwable): Resource<T>()
    class Loading<T>: Resource<T>()
}
