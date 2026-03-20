package com.chordai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chordai.data.api.ChordSegment

@Composable
fun ChordTimeline(
    chords: List<ChordSegment>,
    activeIndex: Int,
    onChordClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Ensure the active chord is always scrolled to the start (left side)
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0) {
            listState.animateScrollToItem(activeIndex)
        }
    }

    LazyRow(
        state = listState,
        // Small start padding to prevent sticking too hard to the edge, 
        // but keeping it minimal to satisfy "left side" requirement
        contentPadding = PaddingValues(start = 24.dp, end = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp), // Tighter spacing for smaller cards
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(chords) { index, chord ->
            ChordCard(
                segment = chord,
                isActive = index == activeIndex,
                isPast = index < activeIndex,
                onClick = { onChordClick(index) }
            )
        }
    }
}
