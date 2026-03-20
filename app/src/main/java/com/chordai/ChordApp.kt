package com.chordai

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChordApp : Application() {

    companion object {
        var isYtDlpReady = false
            private set
        var isYtDlpUpdated = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        initYoutubeDL()
    }

    private fun initYoutubeDL() {
        try {
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
            isYtDlpReady = true
            Log.d("ChordApp", "YoutubeDL initialized successfully")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("ChordApp", "Updating yt-dlp...")
                    val status = YoutubeDL.getInstance().updateYoutubeDL(
                        this@ChordApp,
                        YoutubeDL.UpdateChannel.STABLE
                    )
                    isYtDlpUpdated = true
                    Log.d("ChordApp", "yt-dlp update status: $status")
                } catch (e: Exception) {
                    isYtDlpUpdated = true // Allow attempts even if update fails
                    Log.e("ChordApp", "yt-dlp update failed: ${e.message}")
                }
            }

        } catch (e: Exception) {
            isYtDlpReady = false
            Log.e("ChordApp", "YoutubeDL init FAILED: ${e.message}", e)
        }
    }
}