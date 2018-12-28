package droidkaigi.github.io.challenge2019

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.squareup.moshi.Moshi

abstract class BaseActivity : AppCompatActivity() {

    internal val moshi = Moshi.Builder().build()

    abstract fun getContentView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
    }

    fun showError(throwable: Throwable) {
        Log.v("error", throwable.message)
        Toast.makeText(baseContext, throwable.message, Toast.LENGTH_SHORT).show()
    }
}