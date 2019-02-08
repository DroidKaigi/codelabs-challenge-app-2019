package droidkaigi.github.io.challenge2019.ingest

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

class IngestManager @Inject constructor() {

    /**
     * 計測を行うメソッドです
     * 通信のステータスコードを返します
     */
    fun track(): Int {
        val latch = CountDownLatch(1)
        //なんかすごく重い通信処理
        Thread.sleep(10000)
        //statusCodeの代わりです
        val response = Random.nextInt(0, 10)
        return when (response) {
            1 -> {
                latch.countDown()
                latch.await()
                500
            }
            2 -> {
                latch.await(5, TimeUnit.SECONDS)
                503
            }
            else -> {
                latch.countDown()
                latch.await()
                200
            }
        }
    }
}