package com.example.myumkm.data.remote.retrofit

import com.example.myumkm.data.remote.request.PredictRequest
import com.example.myumkm.data.remote.response.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("predict")
    suspend fun predict(
        @Body request: PredictRequest
    ): ChatResponse
}