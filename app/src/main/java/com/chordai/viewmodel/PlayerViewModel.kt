package com.chordai.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.chordai.data.api.ChordAnalysisResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val player = ExoPlayer.Builder(application).build()

    private val _analysisData = MutableStateFlow<ChordAnalysisResponse?>(null)
    val analysisData: StateFlow<ChordAnalysisResponse?> = _analysisData

    private val _currentTime = MutableStateFlow(0f)
    val currentTime: StateFlow<Float> = _currentTime

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _activeChordIndex = MutableStateFlow(-1)
    val activeChordIndex: StateFlow<Int> = _activeChordIndex

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })

        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    val time = player.currentPosition / 1000f
                    _currentTime.value = time
                    updateActiveChord(time)
                }
                delay(100)
            }
        }
    }

    fun setAnalysisData(data: ChordAnalysisResponse) {
        _analysisData.value = data
        
        val mediaUrl = data.audioUrl ?: data.filename?.let { filename ->
            if (filename.startsWith("http")) filename
            else "https://aritra321-chord-recog.hf.space/download/$filename"
        }

        if (mediaUrl != null) {
            val mediaItem = MediaItem.fromUri(mediaUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun seekTo(seconds: Float) {
        player.seekTo((seconds * 1000).toLong())
        _currentTime.value = seconds
        updateActiveChord(seconds)
    }

    private fun updateActiveChord(time: Float) {
        val chords = _analysisData.value?.chords ?: return
        val index = chords.indexOfFirst { time >= it.start && time < it.end }
        if (index != _activeChordIndex.value) {
            _activeChordIndex.value = index
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
