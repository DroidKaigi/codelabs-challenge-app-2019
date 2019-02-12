package droidkaigi.github.io.challenge2019.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

class DataBoundViewHolder<out T : ViewDataBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root)