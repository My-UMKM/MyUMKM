package com.example.myumkm.ui.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myumkm.R
import com.example.myumkm.databinding.ParentItemBinding

class ParentAdapter(private val tagList: List<ParentItem>) : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(binding: ParentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleTv = binding.parentTitleTv
        val childRecyclerView = binding.childRecyclerView
        val constraintLayout = binding.constraintLayout
        private val arrowTv = binding.arrow

        fun collapseExpandedView() {
            childRecyclerView.visibility = View.GONE
            arrowTv.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
        }

        fun expandView() {
            childRecyclerView.visibility = View.VISIBLE
            arrowTv.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val binding = ParentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = tagList[position]
        holder.titleTv.text = parentItem.tag
        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

        val adapter = ChildAdapter(parentItem.childList)
        holder.childRecyclerView.adapter = adapter

        val isExpandable: Boolean = parentItem.isExpandable
        holder.childRecyclerView.visibility = if (isExpandable) View.VISIBLE else View.GONE

        if (isExpandable) {
            holder.expandView()
        } else {
            holder.collapseExpandedView()
        }

        holder.constraintLayout.setOnClickListener {
            expandedItem(position)
            parentItem.isExpandable = !parentItem.isExpandable
            notifyItemChanged(position, Unit)
            if (parentItem.isExpandable) {
                holder.expandView()
            } else {
                holder.collapseExpandedView()
            }
        }
    }

    private fun expandedItem(position: Int) {
        val temp = tagList.indexOfFirst {
            it.isExpandable
        }
        if (temp >= 0 && temp != position) {
            tagList[temp].isExpandable = false
            notifyItemChanged(temp, 0)
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}