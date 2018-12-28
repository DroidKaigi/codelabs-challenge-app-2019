package droidkaigi.github.io.challenge2019.ingest

import java.util.concurrent.CountDownLatch
import kotlin.random.Random

class IngestManager {
    fun track() {
        val latch = CountDownLatch(1)
        //なんかすごく重い通信処理
        Thread.sleep(10000)
        //statusCodeの代わりです
        val response = Random.nextInt(0,10)
        if (response > 5) {
            //success
            latch.countDown()
        } else {
            //error
            //なんとcountDownしません
        }
        latch.await()
    }
}