package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.R
import com.wealthmanager.accessibility.rememberAccessibilityState
import com.wealthmanager.ui.responsive.rememberResponsiveLayout

/**
 * Responsive API key application guide dialog.
 */
@Composable
fun ApiKeyGuideDialogResponsive(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val accessibilityState = rememberAccessibilityState()
    val responsiveLayout = rememberResponsiveLayout()
    val scrollState = rememberScrollState()
    
    var canScrollDown by remember { mutableStateOf(false) }
    var canScrollUp by remember { mutableStateOf(false) }
    
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        canScrollDown = scrollState.value < scrollState.maxValue - 10
        canScrollUp = scrollState.value > 10
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxSize(0.8f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.api_guide_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                if (canScrollDown || canScrollUp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (canScrollUp) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = stringResource(R.string.api_guide_scroll_up),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (canScrollDown) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = stringResource(R.string.api_guide_scroll_down),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.api_guide_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    ApiKeyCard(
                        title = stringResource(R.string.api_guide_finnhub_global_title),
                        description = stringResource(R.string.api_guide_finnhub_global_desc),
                        steps = listOf(
                            stringResource(R.string.api_guide_steps).split("\n")
                        ).flatten(),
                        websiteUrl = "https://finnhub.io",
                        context = context
                    )
                    
                    ApiKeyCard(
                        title = stringResource(R.string.api_guide_exchange_rate_title),
                        description = stringResource(R.string.api_guide_exchange_rate_desc),
                        steps = listOf(
                            stringResource(R.string.api_guide_steps).split("\n")
                        ).flatten(),
                        websiteUrl = "https://exchangerate-api.com",
                        context = context
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.api_guide_important_reminder),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = stringResource(R.string.api_guide_reminder_points),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.api_guide_complete))
                    }
                }
            }
        }
    }
}

@Composable
private fun ApiKeyCard(
    title: String,
    description: String,
    steps: List<String>,
    websiteUrl: String,
    context: android.content.Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                steps.forEach { step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(websiteUrl)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = stringResource(R.string.api_guide_open_in_new_window),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.api_guide_visit_website))
            }
        }
    }
}