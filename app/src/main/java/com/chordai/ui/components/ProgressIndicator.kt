package com.chordai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chordai.ui.theme.*

@Composable
fun CircularAnalysisProgress(
    progress: Float,
    label: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        // Outer Glowing Circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .shadow(40.dp, CircleShape, ambientColor = VibePurple, spotColor = VibePurple)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(VibeGlow, Color.Transparent)))
        )

        // Inner Solid Circle
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(PrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Sparkle Icon (Simulated with text for now as per image)
                Text("✨", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Analyzing",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                Text(
                    "Audio...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
