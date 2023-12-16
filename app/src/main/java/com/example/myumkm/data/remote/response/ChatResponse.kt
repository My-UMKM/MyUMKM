package com.example.myumkm.data.remote.response

import com.google.gson.annotations.SerializedName

data class ChatResponse(

	@field:SerializedName("response")
	val response: ChatResponse,

	@field:SerializedName("response_text")
	val responseText: String,

	@field:SerializedName("section")
	val section: String
)
