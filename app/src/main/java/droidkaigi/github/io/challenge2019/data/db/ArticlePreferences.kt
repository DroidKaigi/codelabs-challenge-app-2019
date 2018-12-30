package droidkaigi.github.io.challenge2019.data.db

import android.content.Context
import android.preference.PreferenceManager

class ArticlePreferences {

    companion object {
        private const val ARTICLE_IDS_KEY = "article_ids_key"

        fun saveArticleIds(context: Context, articleId: String) {
            val p = PreferenceManager.getDefaultSharedPreferences(context)
            val data = p.getStringSet(ARTICLE_IDS_KEY, mutableSetOf()) ?: mutableSetOf()
            val tmps = mutableSetOf<String>()
            tmps.addAll(data)
            tmps.add(articleId)
            p.edit().putStringSet(ARTICLE_IDS_KEY, tmps).commit()
        }

        fun getArticleIds(context: Context): Set<String> {
            val p = PreferenceManager.getDefaultSharedPreferences(context)
            return p.getStringSet(ARTICLE_IDS_KEY, setOf()) ?: setOf()
        }
    }

}