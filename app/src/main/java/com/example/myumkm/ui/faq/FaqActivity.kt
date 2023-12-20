package com.example.myumkm.ui.faq

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myumkm.R
import com.example.myumkm.databinding.ActivityFaqBinding

class FaqActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqBinding
    private val faqList = ArrayList<ParentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tagRecyclerView.setHasFixedSize(true)
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)

        faqDataList()
        val adapter = ParentAdapter(faqList)
        binding.tagRecyclerView.adapter = adapter
    }

    @SuppressLint("DiscouragedApi")
    private fun faqDataList() {
        val tagArray = resources.getStringArray(R.array.data_tag)

        for (tag in tagArray) {
            val questionArrayResName = "question_tag_$tag"
            val answerArrayResName = "answer_tag_$tag"

            val questionArray = resources.getStringArray(resources.getIdentifier(questionArrayResName, "array", packageName))
            val answerArray = resources.getStringArray(resources.getIdentifier(answerArrayResName, "array", packageName))

            val childItems = ArrayList<ChildItem>()
            for (i in questionArray.indices) {
                childItems.add(ChildItem(questionArray[i], answerArray[i]))
            }

            faqList.add(ParentItem(tag, childItems))
        }
    }
}