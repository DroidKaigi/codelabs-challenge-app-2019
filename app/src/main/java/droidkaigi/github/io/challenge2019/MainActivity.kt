package droidkaigi.github.io.challenge2019

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import droidkaigi.github.io.challenge2019.presenter.StoriesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val fragment = StoriesFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        }
    }

}