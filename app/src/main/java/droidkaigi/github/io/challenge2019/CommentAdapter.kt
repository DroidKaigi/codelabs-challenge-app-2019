package droidkaigi.github.io.challenge2019

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import droidkaigi.github.io.challenge2019.data.api.response.ItemResponse
import droidkaigi.github.io.challenge2019.databinding.ItemCommentBinding


class CommentAdapter(
    var comments: List<ItemResponse?>
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = comments[position]
    }
}