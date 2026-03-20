package com.chordai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chordai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* Back */ }, modifier = Modifier.background(Surface, RoundedCornerShape(12.dp)).size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Settings",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // API Configuration
        SettingSection(
            title = "API Config",
            icon = Icons.Default.Settings // Safe Standard Icon
        ) {
            Text("Backend Base URL", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = "https://aritra321-chord-recog.hf.space",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chord Dictionary
        SettingSection(
            title = "Chord Dictionary",
            icon = Icons.Default.Info // Safe Standard Icon
        ) {
            Text("Learning Path", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { /* Select */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Guitar Beginner")
                    // ArrowDropDown is CORE safe
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Default Key", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { /* Select */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Major")
                    // ArrowDropDown is CORE safe
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Preferences
        SettingSection(
            title = "App Preferences",
            icon = Icons.Default.Build // Safe Standard Icon
        ) {
            PreferenceRow("Auto Scroll", true)
            PreferenceRow("High Quality Audio", true)
            PreferenceRow("Haptic Feedback", false)
        }
    }
}

@Composable
fun SettingSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = VibePurple, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun PreferenceRow(label: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VibePurple,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color.White.copy(alpha = 0.05f)
            )
        )
    }
}
