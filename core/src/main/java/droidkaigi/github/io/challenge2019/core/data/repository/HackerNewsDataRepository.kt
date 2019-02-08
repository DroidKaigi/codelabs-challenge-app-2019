package droidkaigi.github.io.challenge2019.core.data.repository

import droidkaigi.github.io.challenge2019.core.data.api.HackerNewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class HackerNewsDataRepository(
    private val hackerNewsApi: HackerNewsApi
) : HackerNewsRepository {
    override suspend fun getTopStories(): List<Long> = withContext(Dispatchers.IO) {
        val call = hackerNewsApi.getTopStories()
        val response = call.execute()
        if (!response.isSuccessful) {
            // TODO
            throw IOException("Error")
        }
        response.body() ?: emptyList()
    }
}