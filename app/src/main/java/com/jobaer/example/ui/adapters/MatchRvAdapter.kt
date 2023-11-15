package com.jobaer.example.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jobaer.example.R
import com.jobaer.example.databinding.ItemMatchBinding
import com.jobaer.example.models.Match


class MatchRvAdapter(private val handler: MatchClickHandler) :
    ListAdapter<Match, MatchRvAdapter.ViewHolder>(MatchDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemMatchBinding? = DataBindingUtil.bind(itemView)
    }

    private class MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_match, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = getItem(position)
        holder.binding?.position = position
        holder.binding?.match = match
        holder.binding?.handler = handler
        holder.binding?.executePendingBindings()
    }
}

interface MatchClickHandler {
    fun onWatchHighlightView(match: Match)
    fun onNotification(position: Int, match: Match)
}