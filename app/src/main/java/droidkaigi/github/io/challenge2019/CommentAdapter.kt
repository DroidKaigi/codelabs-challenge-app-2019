package droidkaigi.github.io.challenge2019

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.databinding.ItemCommentBinding
import droidkaigi.github.io.challenge2019.databinding.ItemFooterBinding


class CommentAdapter(
    var comments: List<Item?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)
    class FooterViewHolder(binding: ItemFooterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_NORMAL -> {
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
            ITEM_FOOTER -> {
                val binding = ItemFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FooterViewHolder(binding)
            }
            else -> throw IllegalArgumentException("this viewType is undefined: $viewType")
        }
    }

    override fun getItemCount(): Int = comments.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position < comments.size) {
            return ITEM_NORMAL
        }
        return ITEM_FOOTER
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_NORMAL -> {
                (holder as ViewHolder).binding.item = comments[position]
            }
        }
    }

    companion object {
        const val ITEM_NORMAL = 0
        const val ITEM_FOOTER = 1
    }
}