package com.example.myumkm.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myumkm.R
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.databinding.ActivityChatBinding
import com.example.myumkm.ui.section.SectionActivity.Companion.SECTION
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val section = IntentCompat.getParcelableExtra(intent, SECTION, SectionEntity::class.java)
        val sectionId = section?.sectionId
        viewModel = ViewModelProvider(this, ChatViewModelFactory.getInstance(this, sectionId))[ChatViewModel::class.java]
        if (section != null) {
            viewModel.sectionId.postValue(section.sectionId)
        }
        adapter = ChatAdapter(viewModel.chatsOptions, viewModel.userId) {
            binding.rvChat.scrollToPosition(adapter.itemCount - 1)
        }
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

//        lifecycleScope.launch {
//            viewModel.chats.collectLatest { pagingData ->
//                adapter.submitData(pagingData)
//                binding.rvChat.scrollToPosition(adapter.itemCount - 1)
//            }
//        }

        binding.sendButton.setOnClickListener {
            val message = ChatEntity(
                chatContent = binding.messageEditText.text.toString(),
                chatTimestamp = System.currentTimeMillis(),
                chatResponse = null,
                chatbotTimestamp = null,
                isBotTyping = true,
                sectionId = viewModel.sectionId.value
            )
            viewModel.insertChat(message)
//            adapter.setTypingState(true)
//            binding.messageEditText.text.clear()
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                if (viewModel.sectionId.value == "") {
//                    val section = SectionEntity(
//                        sectionName = "Greeting",
//                        userId = viewModel.userId
//                    )
//                    viewModel.sectionId.postValue(viewModel.insertSection(section))
//                    supportActionBar?.title = section.sectionName
//                }
//
//                val updatedMessage = message.copy(
//                    chatId = chatId,
//                    chatResponse = "Halo boi",
//                    chatbotTimestamp = System.currentTimeMillis(),
//                    isBotTyping = false,
//                    sectionId = viewModel.sectionId.value
//                )
//                viewModel.updateChat(updatedMessage)
//                adapter.setTypingState(false)
//
//            }, 2000)
        }
    }
}