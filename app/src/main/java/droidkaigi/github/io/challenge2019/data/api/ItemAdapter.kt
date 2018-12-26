package droidkaigi.github.io.challenge2019.data.api

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import droidkaigi.github.io.challenge2019.data.api.response.Item
import droidkaigi.github.io.challenge2019.databinding.ItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ItemAdapter(
    var itemIds: List<Long>,
    private val hackerNewsApi: HackerNewsApi
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())

    class ViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = itemIds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // FIXME: ignore response when unbind viewHolder
        hackerNewsApi.getItem(itemIds[position]).enqueue(object : Callback<Item> {

            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (!response.isSuccessful) return

                response.body()?.let { item ->
                    handler.post {
                        holder.binding.apply {
                            title.text = item.title
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {

            }
        })
    }
}