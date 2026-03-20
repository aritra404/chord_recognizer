package com.chordai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chordai.data.api.ChordAnalysisResponse
import com.chordai.ui.components.CircularAnalysisProgress
import com.chordai.ui.theme.*
import com.chordai.ui.util.UiState
import com.chordai.viewmodel.AnalysisViewModel

@Composable
fun AnalysisScreen(
    videoId: String,
    videoTitle: String,
    videoUrl: String,
    thumbnailUrl: String,
    onAnalysisComplete: (ChordAnalysisResponse) -> Unit,
    onCancel: () -> Unit,
    viewModel: AnalysisViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.simulatedProgress.collectAsState()
    val currentStageIndex by viewModel.currentStageIndex.collectAsState()
    val currentStage = viewModel.stages[currentStageIndex]

    LaunchedEffect(Unit) {
        viewModel.startAnalysis(videoUrl)
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onAnalysisComplete((uiState as UiState.Success).data)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(videoTitle, style = MaterialTheme.typography.titleLarge, color = Color.White, maxLines = 1)
                    Text("PREPARING AI ANALYSIS", style = MaterialTheme.typography.labelSmall, color = VibePurple)
                }
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            CircularAnalysisProgress(
                progress = progress,
                label = currentStage.label
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Current Task Display
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Current Task", style = MaterialTheme.typography.labelSmall, color = VibePurple)
                        Text(currentStage.label, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    }
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleLarge.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(VibePurple)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tasks Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    viewModel.stages.forEachIndexed { index, stage ->
                        TaskItem(
                            label = stage.label,
                            status = when {
                                index < currentStageIndex -> TaskStatus.Done
                                index == currentStageIndex -> TaskStatus.Processing
                                else -> TaskStatus.Pending
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Analysis", color = Color.White)
                }
            }

            Text(
                "POWERED BY SONICAI ENGINE V2.4",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
    }
}

enum class TaskStatus { Done, Processing, Pending }

@Composable
fun TaskItem(label: String, status: TaskStatus) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (status == TaskStatus.Done) StatusDone.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                // Check is CORE safe
                TaskStatus.Done -> Icon(Icons.Default.Check, contentDescription = null, tint = StatusDone, modifier = Modifier.size(16.dp))
                TaskStatus.Processing -> CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = VibePurple)
                TaskStatus.Pending -> Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusPending))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = if (status == TaskStatus.Pending) TextSecondary else Color.White)
            if (status == TaskStatus.Done) {
                Text("Completed", style = MaterialTheme.typography.labelSmall, color = StatusDone)
            } else if (status == TaskStatus.Processing) {
                Text("Processing...", style = MaterialTheme.typography.labelSmall, color = VibePurple)
            }
        }
    }
}
