package com.chordai.ui.util

data class ChordDisplay(
    val root: String,
    val quality: String,
    val symbol: String
)

fun parseChord(harte: String): ChordDisplay {
    if (harte == "N") return ChordDisplay("—", "No Chord", "N")
    
    val parts = harte.split(":")
    val root = parts[0]
    val suffix = if (parts.size > 1) parts[1] else "maj"

    return when {
        suffix.startsWith("maj") -> ChordDisplay(root, "Major", "△")
        suffix.startsWith("min") -> ChordDisplay(root, "Minor", "m")
        suffix.contains("7") && suffix.contains("min") -> ChordDisplay(root, "Minor 7", "m7")
        suffix.contains("7") -> ChordDisplay(root, "Major 7", "7")
        suffix.contains("hdim7") -> ChordDisplay(root, "Half Dim", "ø7")
        suffix.contains("dim") -> ChordDisplay(root, "Diminished", "°")
        suffix.contains("sus4") -> ChordDisplay(root, "Sus 4", "sus4")
        else -> ChordDisplay(root, "Major", "")
    }
}
