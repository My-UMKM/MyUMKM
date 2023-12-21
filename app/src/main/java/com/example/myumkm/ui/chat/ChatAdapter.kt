package com.example.myumkm.ui.chat

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myumkm.R
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.databinding.ItemMessageBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter(
    private val currentUserName: String?,
    private val onItemInserted: () -> Unit,
): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var isLastItemTyping = false

    private var chatList: List<ChatEntity> = listOf()

    fun updateChats(newChats: List<ChatEntity>) {
        chatList = newChats
        notifyDataSetChanged()
        onItemInserted()
    }

    override fun getItemCount() = chatList.size

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                onItemInserted()
            }
        })
    }

    fun setTypingState(isTyping: Boolean) {
        isLastItemTyping = isTyping
        notifyItemChanged(itemCount - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatEntity = chatList[position]
        holder.bind(chatEntity, position == itemCount - 1 && isLastItemTyping, currentUserName)
    }

    class ViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        private val handler = Handler(Looper.getMainLooper())
        private val typingRunnable = object : Runnable {
            private var dotCount = 0

            override fun run() {
                val dots = ".".repeat(dotCount)
                binding.tvBotMessage.text = "typing$dots"

                dotCount = (dotCount % 3) + 1
                handler.postDelayed(this, 200)
            }
        }
        fun bind(chat: ChatEntity, isTyping: Boolean, currentUserName: String?) {
            binding.tvUser.text = currentUserName
            binding.tvMessage.text = chat.chatContent
            binding.tvTimestamp.text = chat.chatTimestamp.toString()
            if (chat.chatbotTimestamp != null && chat.chatResponse != null) {
                binding.tvBot.text = "Bot"
                binding.tvBotMessage.text = chat.chatResponse
                binding.tvBotTimestamp.text = chat.chatbotTimestamp.toString()
                binding.tvBot.visibility = View.VISIBLE
                binding.tvBotMessage.visibility = View.VISIBLE
                binding.tvBotTimestamp.visibility = View.VISIBLE
            } else if (isTyping) {
                handler.post(typingRunnable)
                binding.tvBot.text = "Bot"
                binding.tvBotTimestamp.text = System.currentTimeMillis().toString()
                binding.tvBot.visibility = View.VISIBLE
                binding.tvBotMessage.visibility = View.VISIBLE
                binding.tvBotTimestamp.visibility = View.VISIBLE
            } else {
                handler.removeCallbacks(typingRunnable)
                binding.tvBot.visibility = View.GONE
                binding.tvBotMessage.visibility = View.GONE
                binding.tvBotTimestamp.visibility = View.GONE
            }
        }
    }
}