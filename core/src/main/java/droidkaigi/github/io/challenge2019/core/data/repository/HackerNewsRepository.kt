package droidkaigi.github.io.challenge2019.core.data.repository

interface HackerNewsRepository {

    suspend fun getTopStories(): List<Long>
}