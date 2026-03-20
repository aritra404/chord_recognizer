package com.chordai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chordai.data.api.ChordAnalysisResponse
import com.chordai.ui.screens.AnalysisScreen
import com.chordai.ui.screens.PlayerScreen
import com.chordai.ui.screens.SearchScreen
import com.chordai.ui.screens.SettingsScreen
import com.chordai.ui.theme.*
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentAnalysisResult = remember { mutableStateOf<ChordAnalysisResponse?>(null) }

    Scaffold(
        bottomBar = {
            if (currentRoute == "search" || currentRoute == "history" || currentRoute == "settings") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(36.dp)),
                        color = CardBackground.copy(alpha = 0.9f),
                        tonalElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NavItem(
                                selected = currentRoute == "search",
                                icon = Icons.Default.Home,
                                label = "Home",
                                onClick = { navController.navigate("search") }
                            )
                            NavItem(
                                selected = currentRoute == "history",
                                icon = Icons.Default.List,
                                label = "History",
                                onClick = { navController.navigate("history") }
                            )
                            NavItem(
                                selected = currentRoute == "settings",
                                icon = Icons.Default.Settings,
                                label = "Settings",
                                onClick = { navController.navigate("settings") }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "search",
            modifier = Modifier.padding(bottom = if (currentRoute == "search" || currentRoute == "history" || currentRoute == "settings") 0.dp else 0.dp)
        ) {
            composable("search") {
                SearchScreen(
                    onNavigateToAnalysis = { result ->
                        val encodedTitle = URLEncoder.encode(result.title, "UTF-8")
                        val encodedUrl = URLEncoder.encode(result.url, "UTF-8")
                        val encodedThumb = URLEncoder.encode(result.thumbnail, "UTF-8")
                        navController.navigate("analysis/${result.id}/$encodedTitle/$encodedUrl/$encodedThumb")
                    }
                )
            }
            
            composable(
                route = "analysis/{videoId}/{videoTitle}/{videoUrl}/{thumbnailUrl}",
                arguments = listOf(
                    navArgument("videoId") { type = NavType.StringType },
                    navArgument("videoTitle") { type = NavType.StringType },
                    navArgument("videoUrl") { type = NavType.StringType },
                    navArgument("thumbnailUrl") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                val title = URLDecoder.decode(backStackEntry.arguments?.getString("videoTitle") ?: "", "UTF-8")
                val url = URLDecoder.decode(backStackEntry.arguments?.getString("videoUrl") ?: "", "UTF-8")
                val thumb = URLDecoder.decode(backStackEntry.arguments?.getString("thumbnailUrl") ?: "", "UTF-8")

                AnalysisScreen(
                    videoId = videoId,
                    videoTitle = title,
                    videoUrl = url,
                    thumbnailUrl = thumb,
                    onAnalysisComplete = { result ->
                        currentAnalysisResult.value = result
                        val encodedTitle = URLEncoder.encode(title, "UTF-8")
                        val encodedThumb = URLEncoder.encode(thumb, "UTF-8")
                        navController.navigate("player/$videoId/$encodedTitle/$encodedThumb") {
                            popUpTo("search")
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "player/{videoId}/{videoTitle}/{thumbnailUrl}",
                 arguments = listOf(
                    navArgument("videoId") { type = NavType.StringType },
                    navArgument("videoTitle") { type = NavType.StringType },
                    navArgument("thumbnailUrl") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                val title = URLDecoder.decode(backStackEntry.arguments?.getString("videoTitle") ?: "", "UTF-8")
                val thumb = URLDecoder.decode(backStackEntry.arguments?.getString("thumbnailUrl") ?: "", "UTF-8")
                
                currentAnalysisResult.value?.let { data ->
                    PlayerScreen(
                        videoId = videoId,
                        analysisData = data,
                        videoTitle = title,
                        thumbnailUrl = thumb,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            
            composable("history") { HistoryScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}

@Composable
fun NavItem(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) Color.White else TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            if (selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(VibePurple)
                )
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🕐", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No history yet", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.5f))
        }
    }
}
