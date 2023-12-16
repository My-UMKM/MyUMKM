package com.example.myumkm.data.remote.retrofit

import com.example.myumkm.data.remote.response.ChatResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("predict")
    suspend fun predict(
        @Field("message") message: String
    ): ChatResponse
}