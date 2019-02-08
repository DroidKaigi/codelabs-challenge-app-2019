package droidkaigi.github.io.challenge2019

import android.view.LayoutInflater
import android.view.ViewGroup
import droidkaigi.github.io.challenge2019.core.data.api.response.Item
import droidkaigi.github.io.challenge2019.databinding.ItemCommentBinding


class CommentAdapter(
    var comments: List<Item?>
) : androidx.recyclerview.widget.RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCommentBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = comments[position]
    }
}