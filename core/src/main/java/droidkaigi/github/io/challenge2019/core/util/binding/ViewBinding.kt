package droidkaigi.github.io.challenge2019.core.util.binding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visible")
fun View.visible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}