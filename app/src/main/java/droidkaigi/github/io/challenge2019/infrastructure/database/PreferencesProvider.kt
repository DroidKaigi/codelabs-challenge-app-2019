package droidkaigi.github.io.challenge2019.infrastructure.database

import android.content.Context
import android.preference.PreferenceManager
import javax.inject.Inject

// TODO: あとでRoomに変える。IFもちゃんと作る
class PreferencesProvider @Inject constructor(private val context: Context) {

    fun saveArticleIds(articleId: String) {
        val p = PreferenceManager.getDefaultSharedPreferences(context)
        val data = p.getStringSet(ARTICLE_IDS_KEY, mutableSetOf()) ?: mutableSetOf()
        val tmps = mutableSetOf<String>()
        tmps.addAll(data)
        tmps.add(articleId)
        p.edit().putStringSet(ARTICLE_IDS_KEY, tmps).apply()
    }

    fun getArticleIds(): Set<String> {
        val p = PreferenceManager.getDefaultSharedPreferences(context)
        return p.getStringSet(ARTICLE_IDS_KEY, setOf()) ?: setOf()
    }

    companion object {
        private const val ARTICLE_IDS_KEY = "article_ids_key"
    }
}
