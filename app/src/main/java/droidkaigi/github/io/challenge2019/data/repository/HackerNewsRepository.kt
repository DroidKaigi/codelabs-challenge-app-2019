package droidkaigi.github.io.challenge2019.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

// Singleton
object HackerNewsRepository {

    val topStories: LiveData<List<Story>> = MutableLiveData()
}