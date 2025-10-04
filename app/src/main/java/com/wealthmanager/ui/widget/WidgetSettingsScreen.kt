package com.wealthmanager.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R
import com.wealthmanager.widget.WidgetManager
import com.wealthmanager.widget.WidgetStatusMonitor

/**
 * Settings screen for widget configuration and privacy management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: WidgetStatusMonitor = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Widget Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Widget Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (uiState.displayState) {
                    WidgetErrorHandler.WidgetDisplayState.NORMAL -> MaterialTheme.colorScheme.primaryContainer
                    WidgetErrorHandler.WidgetDisplayState.ERROR -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (uiState.displayState) {
                            WidgetErrorHandler.WidgetDisplayState.NORMAL -> Icons.Default.CheckCircle
                            WidgetErrorHandler.WidgetDisplayState.ERROR -> Icons.Default.Error
                            else -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when (uiState.displayState) {
                            WidgetErrorHandler.WidgetDisplayState.NORMAL -> MaterialTheme.colorScheme.primary
                            WidgetErrorHandler.WidgetDisplayState.ERROR -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Widget Status: ${uiState.displayState.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Installed Widgets: ${uiState.installedWidgetCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (uiState.statusMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy Settings Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Privacy Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Show Asset Amount Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Show Asset Amount",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Display total asset value on widgets",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.hasAssets && !uiState.displayState.name.contains("PRIVACY"),
                        onCheckedChange = { checked ->
                            viewModel.updatePrivacySettings(checked, false)
                        },
                        enabled = uiState.hasAssets
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Privacy Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Privacy Mode",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Hide all sensitive information",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.displayState.name.contains("PRIVACY"),
                        onCheckedChange = { checked ->
                            viewModel.updatePrivacySettings(true, checked)
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Recommendations Card
        if (uiState.recommendations.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    uiState.recommendations.forEach { recommendation ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = recommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Actions Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Force Update Button
                Button(
                    onClick = { viewModel.forceUpdateWidgets() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Force Update Widgets")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Installation Instructions Button
                OutlinedButton(
                    onClick = { /* Show installation dialog */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Help, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Installation Instructions")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error Display
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}