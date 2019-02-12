package droidkaigi.github.io.challenge2019.repository

sealed class Resource<T>
data class Success<T>(val response: T) : Resource<T>()
data class Failure<T>(val error: Throwable) : Resource<T>()
class Loading<T> : Resource<T>() {

    @Suppress("UNCHECKED_CAST")
    override fun equals(other: Any?): Boolean {
        return (other as? Loading<T>) != null
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}