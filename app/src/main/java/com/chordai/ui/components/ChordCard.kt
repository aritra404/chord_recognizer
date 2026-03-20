package com.chordai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chordai.data.api.ChordSegment
import com.chordai.ui.theme.*
import com.chordai.ui.util.parseChord
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ChordCard(
    segment: ChordSegment,
    isActive: Boolean,
    isPast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(targetValue = if (isActive) 1.02f else 1f, label = "scale")
    val display = parseChord(segment.chord)
    val opacity = if (isPast) 0.4f else 1.0f

    Surface(
        modifier = modifier
            .width(85.dp) // Smaller width
            .height(100.dp) // Smaller height
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .then(
                if (isActive) Modifier.border(2.dp, PrimaryGradient, RoundedCornerShape(16.dp))
                else Modifier
            ),
        color = if (isActive) Color.White.copy(alpha = 0.08f) else CardBackground.copy(alpha = 0.4f),
        tonalElevation = if (isActive) 12.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize().graphicsLayer { this.alpha = opacity },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${display.root}${display.symbol}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp, // Reduced font size
                    fontWeight = FontWeight.Bold,
                    brush = if (isActive) PrimaryGradient else null
                ),
                color = if (!isActive) Color.White else Color.Unspecified
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = display.quality,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (isActive) VibePurple else TextSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = formatTime(segment.start),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary.copy(alpha = 0.4f)
            )
        }
    }
}

private fun formatTime(seconds: Float): String {
    val m = seconds.toInt() / 60
    val s = seconds.toInt() % 60
    return String.format("%d:%02d", m, s)
}
