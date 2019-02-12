@file:Suppress("NonAsciiCharacters", "MainFunctionReturnUnit")

package droidkaigi.github.io.challenge2019.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import droidkaigi.github.io.challenge2019.data.api.HackerNewsApi
import droidkaigi.github.io.challenge2019.data.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.domain.hackernews.Story
import droidkaigi.github.io.challenge2019.util.toEntry
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EntryRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var server: MockWebServer
    lateinit var jsonAdapter: JsonAdapter<ItemResponse>

    val json = """
            {
  "by" : "dhouston",
  "descendants" : 71,
  "id" : 8863,
  "kids" : [ 8952, 9224, 8917, 8884, 8887, 8943, 8869, 8958, 9005, 9671, 8940, 9067, 8908, 9055, 8865, 8881, 8872, 8873, 8955, 10403, 8903, 8928, 9125, 8998, 8901, 8902, 8907, 8894, 8878, 8870, 8980, 8934, 8876 ],
  "score" : 111,
  "time" : 1175714200,
  "title" : "My YC app: Dropbox - Throw away your USB drive",
  "type" : "story",
  "url" : "http://www.getdropbox.com/u/2/screencast.html"
}
        """.trimIndent()

    @BeforeTest
    fun initMockServer() {
        server = MockWebServer()
        server.enqueue(MockResponse().setBody("""[1, 1, 1, 1]"""))
        (0..3).forEach { _ ->
            server.enqueue(
                MockResponse()
                    .setBody(json)
            )
        }
        server.start()
    }

    @BeforeTest
    fun createJsonAdapter() {
        val builder = Moshi.Builder().build()
        jsonAdapter = builder.adapter(ItemResponse::class.java)
    }


    @AfterTest
    fun stopServer() {
        server.shutdown()
    }

    @Test
    fun `Top Stories の読み込み`() = runBlocking {
        val url = server.url("hacker-news.firebaseio.com/v0/")
        val retrofit = Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val api = retrofit.create(HackerNewsApi::class.java)
        val repository = EntryRepositoryImpl(api)
        val response = repository.loadTopStories()
        val story = jsonAdapter.fromJson(json)!!.toEntry() as Story
        assertEquals(
            response, Success(
                listOf(
                    story,
                    story,
                    story,
                    story
                )
            )
        )
    }
}