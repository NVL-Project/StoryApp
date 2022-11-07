package com.nvl.storyapp.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.core.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nvl.storyapp.data.server.response.StoryItem
import com.nvl.storyapp.R
import com.nvl.storyapp.databinding.ItemStoryBinding
import com.nvl.storyapp.view.detail.DetailActivity
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter :
    PagingDataAdapter<StoryItem, MainAdapter.StoriesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val itemStoryBinding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(itemStoryBinding)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class StoriesViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SimpleDateFormat")
        fun bind(storyItem: StoryItem){
            val tmZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            tmZ.timeZone = TimeZone.getTimeZone("ICT")
            val time = tmZ.parse(storyItem.createdAt)?.time
            val prettyTime = PrettyTime(Locale.getDefault())
            val ago = prettyTime.format(time?.let { Date(it) })
            with(binding){
                tvUsername.text = storyItem.name
                tvDescription.text = ago
                Glide.with(itemView.context)
                    .load(storyItem.photoUrl)
                    .centerCrop()
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error))
                    .into(ivUser)
                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivUser, "photo"),
                            Pair(tvUsername, "name"),
                            Pair(tvDescription, "create"),
                        )
                    val i = Intent(itemView.context, DetailActivity::class.java)
                    i.putExtra(DetailActivity.EXTRA_PERSON, storyItem)
                    itemView.context.startActivity(i, optionsCompat.toBundle())
                }
            }
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}