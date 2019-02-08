package droidkaigi.github.io.challenge2019.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

fun AppCompatActivity.showError(e: Throwable) {
    Timber.e(e)
    Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.primaryClip = ClipData.newPlainText(label, text)
}