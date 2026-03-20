package com.chordai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.chordai.data.api.ChordAnalysisResponse
import com.chordai.ui.components.ChordTimeline
import com.chordai.ui.theme.*
import com.chordai.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    videoId: String,
    analysisData: ChordAnalysisResponse,
    videoTitle: String,
    thumbnailUrl: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = viewModel()
) {
    val currentTime by viewModel.currentTime.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val activeIndex by viewModel.activeChordIndex.collectAsState()

    LaunchedEffect(analysisData) {
        viewModel.setAnalysisData(analysisData)
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(Surface, CircleShape).size(40.dp)) {
                    // ArrowBack is CORE
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(videoTitle, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), color = Color.White, maxLines = 1)
                    Text("AI PRODUCER V2", style = MaterialTheme.typography.labelSmall, color = VibePurple)
                }
                IconButton(onClick = { /* More */ }, modifier = Modifier.background(Surface, CircleShape).size(40.dp)) {
                    // MoreVert is CORE
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(40.dp, RoundedCornerShape(24.dp), ambientColor = VibePurple, spotColor = VibePurple)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface)
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Chord Timeline Label
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("AI CHORD ANALYSIS", style = MaterialTheme.typography.labelSmall, color = VibePurple)
                Text("AUTO-SCROLL ON", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timeline
            ChordTimeline(
                chords = analysisData.chords,
                activeIndex = activeIndex,
                onChordClick = { index -> viewModel.seekTo(analysisData.chords[index].start) },
                modifier = Modifier.height(110.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Slider
            Slider(
                value = currentTime,
                onValueChange = { viewModel.seekTo(it) },
                valueRange = 0f..analysisData.durationSeconds,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = VibePurple,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(currentTime), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(formatTime(analysisData.durationSeconds), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls - Using strictly standard CORE icons only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Refresh is CORE
                IconButton(onClick = {}) { Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary) }
                
                // ArrowBack is CORE
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp)) }
                
                // Play/Pause
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient)
                        .clickable { viewModel.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        // Settings is a safe CORE icon to represent 'active/busy' or we can use two narrow Boxes
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    } else {
                        // PlayArrow is CORE
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    }
                }

                // ArrowForward is CORE
                IconButton(onClick = {}) { Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp)) }
                
                // Favorite is CORE
                IconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = null, tint = TextSecondary) }
            }
        }
    }
}

private fun formatTime(seconds: Float): String {
    val m = seconds.toInt() / 60
    val s = seconds.toInt() % 60
    return String.format("%d:%02d", m, s)
}
