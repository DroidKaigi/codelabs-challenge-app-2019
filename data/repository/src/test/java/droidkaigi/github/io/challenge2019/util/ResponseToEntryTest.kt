@file:Suppress("NonAsciiCharacters")

package droidkaigi.github.io.challenge2019.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import droidkaigi.github.io.challenge2019.data.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.domain.hackernews.EntryType
import org.junit.Before
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals

class ResponseToEntryTest {

    lateinit var jsonAdapter: JsonAdapter<ItemResponse>

    @Before
    fun createJsonAdapter() {
        val builder = Moshi.Builder().build()
        jsonAdapter = builder.adapter(ItemResponse::class.java)
    }

    @Test
    fun `Story のエンコード`() {
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
        val entry = jsonAdapter.fromJson(json)!!.toEntry()
        assertEquals(entry.id.id, 8863)
        assertEquals(entry.author, "dhouston")
        assertEquals(entry.type, EntryType.STORY)
        assertEquals(entry.time.time, 1175714200 * 1000L)
        assertEquals(entry.htmlText, null)
        assertEquals(entry.parent, null)
        assertEquals(entry.poll, null)
        assertEquals(entry.url, URL("http://www.getdropbox.com/u/2/screencast.html"))
        assertEquals(entry.score, 111)
        assertEquals(entry.title, "My YC app: Dropbox - Throw away your USB drive")
        assertEquals(entry.descendants, 71)
        assertEquals(
            entry.kids.map { it.id }, listOf<Long>(
                8952,
                9224,
                8917,
                8884,
                8887,
                8943,
                8869,
                8958,
                9005,
                9671,
                8940,
                9067,
                8908,
                9055,
                8865,
                8881,
                8872,
                8873,
                8955,
                10403,
                8903,
                8928,
                9125,
                8998,
                8901,
                8902,
                8907,
                8894,
                8878,
                8870,
                8980,
                8934,
                8876
            )
        )
    }

    @Test
    fun `Ask のエンコード`() {
        val json = """
{
  "by" : "tel",
  "descendants" : 16,
  "id" : 121003,
  "kids" : [ 121016, 121109, 121168 ],
  "score" : 25,
  "text" : "<i>or</i> HN: the Next Iteration<p>I get the impression that with Arc being released a lot of people who never had time for HN before are suddenly dropping in more often. (PG: what are the numbers on this? I'm envisioning a spike.)<p>Not to say that isn't great, but I'm wary of Diggification. Between links comparing programming to sex and a flurry of gratuitous, ostentatious  adjectives in the headlines it's a bit concerning.<p>80% of the stuff that makes the front page is still pretty awesome, but what's in place to keep the signal/noise ratio high? Does the HN model still work as the community scales? What's in store for (++ HN)?",
  "time" : 1203647620,
  "title" : "Ask HN: The Arc Effect",
  "type" : "story",
  "url" : ""
}
        """.trimIndent()
        val entry = jsonAdapter.fromJson(json)!!.toEntry()
        assertEquals(entry.id.id, 121003)
        assertEquals(entry.descendants, 16)
        assertEquals(
            entry.kids.map { it.id }, listOf<Long>(
                121016, 121109, 121168
            )
        )
        assertEquals(entry.score, 25)
        assertEquals(
            entry.htmlText,
            "<i>or</i> HN: the Next Iteration<p>I get the impression that with Arc being released a lot of people who never had time for HN before are suddenly dropping in more often. (PG: what are the numbers on this? I'm envisioning a spike.)<p>Not to say that isn't great, but I'm wary of Diggification. Between links comparing programming to sex and a flurry of gratuitous, ostentatious  adjectives in the headlines it's a bit concerning.<p>80% of the stuff that makes the front page is still pretty awesome, but what's in place to keep the signal/noise ratio high? Does the HN model still work as the community scales? What's in store for (++ HN)?"
        )
        assertEquals(entry.time.time, 1203647620 * 1000L)
        assertEquals(entry.title, "Ask HN: The Arc Effect")
        assertEquals(entry.type, EntryType.STORY)
        assertEquals(entry.url, null)
        assertEquals(entry.author, "tel")
    }

    @Test
    fun `Job のエンコード`() {
        val json = """
{
  "by" : "justin",
  "id" : 192327,
  "score" : 6,
  "text" : "Justin.tv is the biggest live video site online. We serve hundreds of thousands of video streams a day, and have supported up to 50k live concurrent viewers. Our site is growing every week, and we just added a 10 gbps line to our colo. Our unique visitors are up 900% since January.<p>There are a lot of pieces that fit together to make Justin.tv work: our video cluster, IRC server, our web app, and our monitoring and search services, to name a few. A lot of our website is dependent on Flash, and we're looking for talented Flash Engineers who know AS2 and AS3 very well who want to be leaders in the development of our Flash.<p>Responsibilities<p><pre><code>    * Contribute to product design and implementation discussions\n    * Implement projects from the idea phase to production\n    * Test and iterate code before and after production release \n</code></pre>\nQualifications<p><pre><code>    * You should know AS2, AS3, and maybe a little be of Flex.\n    * Experience building web applications.\n    * A strong desire to work on website with passionate users and ideas for how to improve it.\n    * Experience hacking video streams, python, Twisted or rails all a plus.\n</code></pre>\nWhile we're growing rapidly, Justin.tv is still a small, technology focused company, built by hackers for hackers. Seven of our ten person team are engineers or designers. We believe in rapid development, and push out new code releases every week. We're based in a beautiful office in the SOMA district of SF, one block from the caltrain station. If you want a fun job hacking on code that will touch a lot of people, JTV is for you.<p>Note: You must be physically present in SF to work for JTV. Completing the technical problem at <a href=\"http://www.justin.tv/problems/bml\" rel=\"nofollow\">http://www.justin.tv/problems/bml</a> will go a long way with us. Cheers!",
  "time" : 1210981217,
  "title" : "Justin.tv is looking for a Lead Flash Engineer!",
  "type" : "job",
  "url" : ""
}
        """.trimIndent()
        val entry = jsonAdapter.fromJson(json)!!.toEntry()
        assertEquals(entry.author, "justin")
        assertEquals(entry.id.id, 192327)
        assertEquals(entry.score, 6)
        assertEquals(
            entry.htmlText,
            "Justin.tv is the biggest live video site online. We serve hundreds of thousands of video streams a day, and have supported up to 50k live concurrent viewers. Our site is growing every week, and we just added a 10 gbps line to our colo. Our unique visitors are up 900% since January.<p>There are a lot of pieces that fit together to make Justin.tv work: our video cluster, IRC server, our web app, and our monitoring and search services, to name a few. A lot of our website is dependent on Flash, and we're looking for talented Flash Engineers who know AS2 and AS3 very well who want to be leaders in the development of our Flash.<p>Responsibilities<p><pre><code>    * Contribute to product design and implementation discussions\n    * Implement projects from the idea phase to production\n    * Test and iterate code before and after production release \n</code></pre>\nQualifications<p><pre><code>    * You should know AS2, AS3, and maybe a little be of Flex.\n    * Experience building web applications.\n    * A strong desire to work on website with passionate users and ideas for how to improve it.\n    * Experience hacking video streams, python, Twisted or rails all a plus.\n</code></pre>\nWhile we're growing rapidly, Justin.tv is still a small, technology focused company, built by hackers for hackers. Seven of our ten person team are engineers or designers. We believe in rapid development, and push out new code releases every week. We're based in a beautiful office in the SOMA district of SF, one block from the caltrain station. If you want a fun job hacking on code that will touch a lot of people, JTV is for you.<p>Note: You must be physically present in SF to work for JTV. Completing the technical problem at <a href=\"http://www.justin.tv/problems/bml\" rel=\"nofollow\">http://www.justin.tv/problems/bml</a> will go a long way with us. Cheers!"
        )
        assertEquals(entry.time.time, 1210981217 * 1000L)
        assertEquals(entry.title, "Justin.tv is looking for a Lead Flash Engineer!")
        assertEquals(entry.type, EntryType.JOB)
        assertEquals(entry.url, null)
    }

    @Test
    fun `Poll のエンコード`() {
        val json = """
{
  "by" : "pg",
  "id" : 160705,
  "poll" : 160704,
  "score" : 335,
  "text" : "Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.",
  "time" : 1207886576,
  "type" : "pollopt"
}
        """.trimIndent()
        val entry = jsonAdapter.fromJson(json)!!.toEntry()
        assertEquals(entry.author, "pg")
        assertEquals(entry.id.id, 160705)
        assertEquals(entry.poll?.id, 160704)
        assertEquals(entry.score, 335)
        assertEquals(entry.htmlText, "Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.")
        assertEquals(entry.time.time, 1207886576 * 1000L)
        assertEquals(entry.type, EntryType.POLLOPT)
    }
}