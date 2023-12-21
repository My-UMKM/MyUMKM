package com.example.myumkm.ui.chat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myumkm.data.entity.ChatEntity
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.databinding.ActivityChatBinding
import com.example.myumkm.ui.section.SectionActivity.Companion.SECTION
import com.example.myumkm.util.ResultState
import com.google.firebase.firestore.ListenerRegistration

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    private var listenerRegistration: ListenerRegistration? = null
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

        adapter = ChatAdapter(viewModel.user.name) {
            binding.rvChat.scrollToPosition(adapter.itemCount - 1)
        }
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(this)

        viewModel.sectionId.observe(this) { sectionId ->
            // Remove the old listener if one exists
            listenerRegistration?.remove()

            // Add a new listener for the new sectionId
            if (sectionId != null) {
                listenerRegistration = viewModel.iChatRepository.getChats(sectionId)
                    .addSnapshotListener { snapshots, error ->
                        if (error != null) {
                            // Handle the error
                            return@addSnapshotListener
                        }

                        // Update the adapter with the new data
                        val chats = snapshots?.toObjects(ChatEntity::class.java)
                        if (chats != null) {
                            adapter.updateChats(chats)
                        }
                    }
            }
        }

        binding.rvChat.layoutManager = LinearLayoutManager(this)

        binding.sendButton.setOnClickListener {
            val message = ChatEntity(
                chatContent = binding.messageEditText.text.toString(),
                chatTimestamp = System.currentTimeMillis(),
                chatResponse = null,
                chatbotTimestamp = null,
                isBotTyping = true,
                sectionId = viewModel.sectionId.value
            )
            adapter.setTypingState(true)
            sendState()
            viewModel.insertChat(message)
            adapter.setTypingState(false)
        }
        setupView()
    }

    private fun setupView() {
        viewModel.insertChat.observe(this) { state ->
            when(state) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Error -> {
                    showLoading(false)
                }
                is ResultState.Success -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarChat.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun sendState() {
        adapter.setTypingState(true)
        binding.messageEditText.text.clear()
    }
}