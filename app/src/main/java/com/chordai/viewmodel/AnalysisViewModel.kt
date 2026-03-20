package com.chordai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chordai.data.api.ChordAnalysisResponse
import com.chordai.data.repository.ChordRepository
import com.chordai.ui.util.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AnalysisStage(
    val label: String,
    val emoji: String,
    val estimatedProgressStart: Float,
    val estimatedProgressEnd: Float,
    val estimatedDurationMs: Long
)

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChordRepository()

    private val _uiState = MutableStateFlow<UiState<ChordAnalysisResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<ChordAnalysisResponse>> = _uiState

    private val _currentStageIndex = MutableStateFlow(0)
    val currentStageIndex: StateFlow<Int> = _currentStageIndex

    private val _simulatedProgress = MutableStateFlow(0f)
    val simulatedProgress: StateFlow<Float> = _simulatedProgress

    val stages = listOf(
        AnalysisStage("Updating yt-dlp", "🔄", 0f, 0.10f, 30_000),
        AnalysisStage("Downloading audio", "⬇️", 0.10f, 0.35f, 25_000),
        AnalysisStage("Uploading file", "📤", 0.35f, 0.45f, 10_000),
        AnalysisStage("Separating instruments", "🎸", 0.45f, 0.65f, 60_000),
        AnalysisStage("Extracting harmonics", "🎵", 0.65f, 0.80f, 30_000),
        AnalysisStage("Running AI model", "🤖", 0.80f, 0.95f, 40_000),
        AnalysisStage("Finalizing", "📊", 0.95f, 0.99f, 10_000),
    )

    fun startAnalysis(url: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val simulationJob = launch {
                simulateProgress()
            }

            try {
                // 1. Download audio via yt-dlp directly (handles auth/cookies internally)
                val cacheDir = getApplication<Application>().cacheDir
                val audioFile = repository.downloadAudioFromYouTube(url, cacheDir)

                // 2. Upload and analyze
                val result = repository.analyze(audioFile)

                // 3. Cleanup
                audioFile.delete()

                simulationJob.cancel()
                _simulatedProgress.value = 1f
                delay(500)
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                simulationJob.cancel()
                _uiState.value = UiState.Error("Analysis failed: ${e.message}")
            }
        }
    }

    private suspend fun simulateProgress() {
        for (i in stages.indices) {
            _currentStageIndex.value = i
            val stage = stages[i]
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < stage.estimatedDurationMs) {
                val elapsed = System.currentTimeMillis() - startTime
                val stageProgress = elapsed.toFloat() / stage.estimatedDurationMs
                val totalProgress = stage.estimatedProgressStart +
                        (stage.estimatedProgressEnd - stage.estimatedProgressStart) * stageProgress

                _simulatedProgress.value = totalProgress
                delay(100)
            }
        }
    }
}