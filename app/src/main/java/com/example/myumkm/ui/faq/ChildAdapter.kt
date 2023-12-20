package com.example.myumkm.ui.faq

import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myumkm.databinding.ChildItemBinding


class ChildAdapter(private val childList: List<ChildItem>) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    class ChildViewHolder(binding: ChildItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val question = binding.childQuestionTv
        val answer = binding.childAnswerTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = ChildItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val childItem = childList[position]
        holder.question.text = childItem.question
        val answerText = childItem.answer

        val startLink = answerText.indexOf("https://")
        val endLink = answerText.length

        if (startLink != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val uri = Uri.parse(answerText.substring(startLink, endLink))
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    widget.context.startActivity(intent)
                }
            }

            val clickableText = "Klik disini"
            val clickableTextLink = answerText.indexOf("https://")

            if (clickableTextLink != -1) {
                val spannableString = SpannableString(answerText.replaceRange(clickableTextLink, endLink, clickableText))
                spannableString.setSpan(
                    clickableSpan,
                    clickableTextLink,
                    clickableTextLink + clickableText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                holder.answer.text = spannableString
                holder.answer.movementMethod = LinkMovementMethod.getInstance()
            } else {
                holder.answer.text = answerText
            }
        }
    }

    override fun getItemCount(): Int {
        return childList.size
    }
}