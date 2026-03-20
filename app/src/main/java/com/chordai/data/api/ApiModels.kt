package com.chordai.data.api

import com.google.gson.annotations.SerializedName

data class YouTubeSearchResponse(
    val query: String,
    val results: List<YouTubeResult>
)

data class YouTubeResult(
    val id: String,
    val title: String,
    val url: String,
    val duration: Int,
    val thumbnail: String,
    val channel: String
)

data class ChordAnalysisRequest(
    val url: String,
    @SerializedName("chord_dict") val chordDict: String = "submission"
)

data class ChordAnalysisResponse(
    @SerializedName("duration_seconds") val durationSeconds: Float,
    @SerializedName("chord_count") val chordCount: Int,
    val chords: List<ChordSegment>,
    val filename: String? = null,
    @SerializedName("chord_dict") val chordDict: String? = null,
    var audioUrl: String? = null
)

data class ChordSegment(
    val start: Float,
    val end: Float,
    val chord: String
)

data class HealthResponse(
    val status: String,
    val message: String
)
