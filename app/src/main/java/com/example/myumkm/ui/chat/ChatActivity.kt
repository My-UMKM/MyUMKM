package com.example.myumkm.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.databinding.ActivityChatBinding
import com.example.myumkm.ui.section.SectionActivity.Companion.SECTION
import com.example.myumkm.util.toast
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val section = IntentCompat.getParcelableExtra(intent, SECTION, SectionEntity::class.java)

        val sectionId = section?.id
        viewModel = ViewModelProvider(this, ChatViewModelFactory.getInstance(this, sectionId))[ChatViewModel::class.java]
        if (section != null) {
            viewModel.sectionId.postValue(section.id)
        }

        val options = FirestoreRecyclerOptions.Builder<ChatEntity>()
            .setQuery(viewModel.iChatRepository.getChats(viewModel.sectionId.value), ChatEntity::class.java)
            .build()
        adapter = ChatAdapter(options, viewModel.user.name) {
            binding.rvChat.scrollToPosition(adapter.itemCount - 1)
        }
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

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

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}