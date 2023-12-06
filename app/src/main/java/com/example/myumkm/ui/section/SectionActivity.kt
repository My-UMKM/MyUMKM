package com.example.myumkm.ui.section

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myumkm.data.entity.SectionEntity
import com.example.myumkm.databinding.ActivitySectionBinding
import com.example.myumkm.ui.chat.ChatActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySectionBinding
    private lateinit var adapter: SectionAdapter
    private lateinit var viewModel: SectionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SectionViewModelFactory.getInstance(this))[SectionViewModel::class.java]
        val options = FirestoreRecyclerOptions.Builder<SectionEntity>()
            .setQuery(viewModel.iSectionRepository.getSections(viewModel.user.id), SectionEntity::class.java)
            .build()
        adapter = SectionAdapter(options)
        binding.rvSection.adapter = adapter
        binding.rvSection.layoutManager = LinearLayoutManager(this)

        binding.itemSection.imgBtnSection.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
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

    override fun onBackPressed() {
        super.onBackPressed()
        NavUtils.navigateUpFromSameTask(this)
    }

    companion object {
        const val SECTION = "section"
    }
}