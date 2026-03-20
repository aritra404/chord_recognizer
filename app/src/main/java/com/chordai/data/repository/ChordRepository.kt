package com.chordai.data.repository

import android.util.Log
import com.chordai.ChordApp
import com.chordai.data.api.ChordApiService
import com.chordai.data.api.YouTubeResult
import com.chordai.data.api.ChordAnalysisResponse
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChordRepository(private val baseUrl: String = "https://aritra321-chord-recog.hf.space/") {

    private val api: ChordApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("ngrok-skip-browser-warning", "1")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ChordApiService::class.java)
    }

    suspend fun search(query: String): List<YouTubeResult> {
        return try {
            val response = api.searchYouTube(query)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun extractAudioUrl(videoUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = YoutubeDLRequest(videoUrl)
            // Use 'bestaudio' to get the highest quality audio stream URL
            request.addOption("-f", "bestaudio")
            val videoInfo = YoutubeDL.getInstance().getInfo(request)
            videoInfo.url
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadAudioFromYouTube(videoUrl: String, outputDir: File): File = withContext(Dispatchers.IO) {
        if (!ChordApp.isYtDlpReady) throw IllegalStateException("YoutubeDL not initialized")

        // Wait for yt-dlp update to finish (max 60 seconds)
        val waitStart = System.currentTimeMillis()
        while (!ChordApp.isYtDlpUpdated) {
            if (System.currentTimeMillis() - waitStart > 60_000) {
                throw IOException("Timed out waiting for yt-dlp update. Please try again.")
            }
            Log.d("ChordRepository", "Waiting for yt-dlp update...")
            kotlinx.coroutines.delay(1_000)
        }

        outputDir.mkdirs()

        outputDir.listFiles()
            ?.filter { it.extension == "mp3" || it.extension == "webm" || it.extension == "m4a" }
            ?.forEach { it.delete() }

        val request = YoutubeDLRequest(videoUrl)
        request.addOption("-f", "bestaudio/best")  // Fallback to best if bestaudio unavailable
        request.addOption("--no-playlist")
        request.addOption("-x")
        request.addOption("--audio-format", "mp3")
        request.addOption("--audio-quality", "0")
        request.addOption("--no-update")
        request.addOption("-o", "${outputDir.absolutePath}/%(id)s.%(ext)s")

        YoutubeDL.getInstance().execute(request)

        outputDir.listFiles()
            ?.filter { it.extension == "mp3" || it.extension == "webm" || it.extension == "m4a" }
            ?.maxByOrNull { it.lastModified() }
            ?: throw IOException("Downloaded file not found in ${outputDir.absolutePath}")
    }

    suspend fun analyze(file: File): ChordAnalysisResponse {
        val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return api.analyzeAudio(body)
    }
}
