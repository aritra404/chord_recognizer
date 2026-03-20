package com.chordai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chordai.ui.theme.VibePurple

@Composable
fun WaveformAnimation(
    modifier: Modifier = Modifier,
    barCount: Int = 32,
    isProcessing: Boolean = true,
    color: Color = VibePurple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val heightScale by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = if (isProcessing) index * 30 else 0,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(if (isProcessing) heightScale else 0.1f)
                    .background(color.copy(alpha = if (isProcessing) 1f else 0.3f), RoundedCornerShape(2.dp))
            )
        }
    }
}
