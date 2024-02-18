package com.dicoding.asclepius.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.models.History
import com.dicoding.asclepius.databinding.ItemHistoryRowBinding

class HistoryAdapter : PagingDataAdapter<History, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    var onHistoryClick: ((History, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHistoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { storyList ->
            storyList?.let { item ->
                holder.bind(item)
            }
        }
    }

    inner class ViewHolder(private var binding: ItemHistoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var getHistory: History

        fun bind(item: History) {
            getHistory = item

            binding.apply {
                root.setOnClickListener {
                    onHistoryClick?.invoke(item, ivHistory)
                }

                ivHistory.setImageURI(Uri.parse(item.imageUri))
                tvInferenceTime.text = StringBuilder("${item.inferenceTime} ms")
                tvResults.text = item.results
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(
                oldHistory: History,
                newHistory: History
            ): Boolean {
                return oldHistory == newHistory
            }

            override fun areContentsTheSame(
                oldHistory: History,
                newHistory: History
            ): Boolean {
                return oldHistory.id == newHistory.id
            }
        }
    }
}