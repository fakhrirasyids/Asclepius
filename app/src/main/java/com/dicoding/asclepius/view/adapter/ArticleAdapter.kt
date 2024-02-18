package com.dicoding.asclepius.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.models.ArticlesItem
import com.dicoding.asclepius.databinding.ItemArticlesRowBinding

class ArticleAdapter :
    ListAdapter<ArticlesItem, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemArticlesRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class MyViewHolder(private val binding: ItemArticlesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(articlesItem: ArticlesItem) {
            binding.apply {
                Glide.with(root)
                    .load(articlesItem.urlToImage)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(ivArticle)

                tvArticleTitle.text = articlesItem.title
                tvArticleDesc.text = articlesItem.description

                layoutItem.setOnClickListener {
                    onItemClick?.invoke(articlesItem.url.toString())
                }

                layoutContent.setOnClickListener {
                    onItemClick?.invoke(articlesItem.url.toString())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}