package com.chordai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chordai.data.api.YouTubeResult
import com.chordai.ui.components.SearchBar
import com.chordai.ui.components.VideoCard
import com.chordai.ui.theme.*
import com.chordai.ui.util.UiState
import com.chordai.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onNavigateToAnalysis: (YouTubeResult) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo & Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(VibePurple), contentAlignment = Alignment.Center) {
                        Text("V", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIBE.AI",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Surface), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onQueryChanged(it) },
                onSearch = { viewModel.performSearch() },
                isSearching = uiState is UiState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item { CategoryChip("ALL RESULTS", isSelected = true) }
                item { CategoryChip("AI Remixes", isSelected = false) }
                item { CategoryChip("Artists", isSelected = false) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "TOP MATCHES",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is UiState.Idle -> {
                    EmptySearchState()
                }
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VibePurple)
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        itemsIndexed(state.data) { index, result ->
                            VideoCard(
                                result = result,
                                onClick = { onNavigateToAnalysis(result) }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorState(message = state.message, onRetry = { viewModel.performSearch() })
                }
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean) {
    Surface(
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) VibePurple else Surface,
        contentColor = if (isSelected) Color.White else TextSecondary
    ) {
        Box(modifier = Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎵", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your AI Studio", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Text("Search for a track to begin", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("❌", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = VibePurple)) {
            Text("Try Again")
        }
    }
}
