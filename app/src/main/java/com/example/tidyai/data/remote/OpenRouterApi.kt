package com.example.tidyai.data.remote

import com.example.tidyai.data.remote.model.OpenRouterRequest
import com.example.tidyai.data.remote.model.OpenRouterResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun analyzeImage(
        @Header("Authorization") authHeader: String,
        @Header("HTTP-Referer") referer: String = "https://tidyai.example.com",
        @Header("X-Title") title: String = "TidyAI",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse
}
