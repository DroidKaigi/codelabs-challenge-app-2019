package droidkaigi.github.io.challenge2019.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import droidkaigi.github.io.challenge2019.R
import droidkaigi.github.io.challenge2019.data.model.Article
import droidkaigi.github.io.challenge2019.databinding.ItemFooterBinding
import droidkaigi.github.io.challenge2019.databinding.ItemStoryBinding


class StoryAdapter(
    var stories: MutableList<Article?>,
    private val onClickItem: ((Article) -> Unit)? = null,
    private val onClickMenuItem: ((Article, Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType(val id: Int) {
        ItemStory(0),
        Footer(1)
    }

    class ItemStoryViewHolder(val binding: ItemStoryBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    class FooterViewHolder(binding: ItemFooterBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return if (viewType == ViewType.ItemStory.id) {
            val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemStoryViewHolder(binding)
        } else {
            val binding = ItemFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = if (stories.isEmpty()) 0 else stories.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == stories.size) {
            return ViewType.Footer.id
        }
        return ViewType.ItemStory.id
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemStoryViewHolder -> {
                val item = stories[position]
                if (item != null) {
                    holder.binding.alreadyRead = item.alreadyRead
                    holder.binding.item = item.content
                    holder.binding.root.setOnClickListener {
                        onClickItem?.invoke(item)
                    }
                    holder.binding.menuButton.setOnClickListener {
                        val popupMenu = PopupMenu(holder.binding.menuButton.context, holder.binding.menuButton)
                        popupMenu.inflate(R.menu.story_menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            val menuItemId = menuItem.itemId
                            when (menuItemId) {
                                R.id.copy_url,
                                R.id.refresh -> {
                                    onClickMenuItem?.invoke(item, menuItemId)
                                    true
                                }
                                else -> false
                            }
                        }
                        popupMenu.show()
                    }
                } else {
                    holder.binding.item = null
                    holder.binding.root.setOnClickListener(null)
                    holder.binding.menuButton.setOnClickListener(null)
                }
            }
        }
    }
}