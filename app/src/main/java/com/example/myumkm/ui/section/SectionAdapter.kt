package com.example.myumkm.ui.section

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myumkm.R
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.databinding.ItemSectionBinding
import com.example.myumkm.ui.chat.ChatActivity
import com.example.myumkm.ui.section.SectionActivity.Companion.SECTION
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SectionAdapter(
    options: FirestoreRecyclerOptions<SectionEntity>,
    ): FirestoreRecyclerAdapter<SectionEntity, SectionAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_section, parent, false)
        val binding = ItemSectionBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: SectionEntity) {
        val section = getItem(position)
        holder.bind(section)
    }

    inner class ViewHolder(private val binding: ItemSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: SectionEntity) {
            binding.tvSection.text = section.sectionName
            binding.imgBtnSection.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra(SECTION, section)
                itemView.context.startActivity(intent)
            }
        }
    }
}