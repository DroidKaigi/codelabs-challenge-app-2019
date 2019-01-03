package droidkaigi.github.io.challenge2019.data.repository

import android.arch.lifecycle.LiveData
import droidkaigi.github.io.challenge2019.data.repository.entity.Comment
import droidkaigi.github.io.challenge2019.data.repository.entity.Story

// Singleton
object HackerNewsRepository {

    // TODO: 3.Implement HackerNewsRepository

    fun getTopStories(): LiveData<Resource<List<Story?>>> {
        TODO("Implement this method")
    }

    fun getStory(id: Long): LiveData<Resource<Story>> {
        TODO("Implement this method")
    }

    fun getComments(story: Story): LiveData<Resource<List<Comment?>>> {
        TODO("Implement this method")
    }
}