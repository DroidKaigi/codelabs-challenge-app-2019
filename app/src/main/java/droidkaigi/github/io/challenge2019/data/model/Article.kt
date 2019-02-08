package droidkaigi.github.io.challenge2019.data.model

data class Article(
    val content: Item,
    val alreadyRead: Boolean
) {
    val id = content.id
}
