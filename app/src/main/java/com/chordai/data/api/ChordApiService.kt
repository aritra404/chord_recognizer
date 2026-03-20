package com.chordai.data.api

import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import okhttp3.MultipartBody

interface ChordApiService {
    @GET("search")
    suspend fun searchYouTube(
        @Query("q") query: String,
        @Query("max_results") maxResults: Int = 10
    ): YouTubeSearchResponse

    @Multipart
    @POST("analyze")
    suspend fun analyzeAudio(
        @Part file: MultipartBody.Part
    ): ChordAnalysisResponse

    @GET("health")
    suspend fun checkHealth(): HealthResponse
}
