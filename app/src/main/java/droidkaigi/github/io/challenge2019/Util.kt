package droidkaigi.github.io.challenge2019

import android.view.View

class Util {
    companion object {
        fun setVisibility(isVisible: Boolean): Int {
            return if (isVisible) View.VISIBLE else View.GONE
        }
    }
}