package com.example.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ItemListStoryBinding
import com.example.storyapp.utils.withDateFormat
import com.example.storyapp.view.detail.DetailActivity

class StoryAdapter(
    private val getCommentCount: (storyId: String) -> LiveData<Int>,
    private val lifecycleOwner: LifecycleOwner,
) : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    class StoryViewHolder(private val binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            story: ListStoryItem,
            getCommentCount: (storyId: String) -> LiveData<Int>,
            lifecycleOwner: LifecycleOwner
        ) {
            binding.apply {
                tvUserName.text = story.name
                tvDescriptionStory.text = story.description
                tvTimestamp.text = story.createdAt?.withDateFormat()
                Glide.with(root.context)
                    .load(story.photoUrl)
                    .into(imgItemStory)
                val liveData = getCommentCount(story.id)
                liveData.observe(lifecycleOwner) { count ->
                    tvCommentCount.text = "$count"
                }
                root.setOnClickListener {
                    val intent = Intent(root.context, DetailActivity::class.java)
                    intent.putExtra(TAG, story.id)
                    root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it, getCommentCount, lifecycleOwner) }
    }

    companion object {
        private const val TAG = "story_id"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}