package com.wealthmanager.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.BuildConfig
import com.wealthmanager.R
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import com.wealthmanager.ui.settings.ApiKeyGuideDialog
import com.wealthmanager.utils.rememberUrlLauncher

/**
 * About Dialog Component - Completely Redesigned
 * Modern about page showcasing app features, technology stack, and user benefits
 */
@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    firstLaunchManager: FirstLaunchManager? = null,
) {
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val scrollState = rememberScrollState()
    val openUrl = rememberUrlLauncher()

    // Scroll state detection
    var canScrollDown by remember { mutableStateOf(false) }
    var canScrollUp by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        canScrollDown = scrollState.value < scrollState.maxValue - 10
        canScrollUp = scrollState.value > 10
    }

    var showApiGuideDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(scrollState),
                ) {
                    // Header with app branding
                    AppHeaderSection(onClose = onDismiss)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Version and build info
                    VersionInfoSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Key features showcase
                    KeyFeaturesSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Technology stack
                    TechnologyStackSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Security and privacy highlights
                    SecurityPrivacySection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // API configuration guide
                    ApiConfigurationSection(
                        onShowGuide = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                            showApiGuideDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Development and contribution info
                    DevelopmentSection(
                        onOpenGitHub = { openUrl("https://github.com/kuoyaoming/Wealth-Manager") },
                        onOpenIssues = { openUrl("https://github.com/kuoyaoming/Wealth-Manager/issues") },
                        hapticManager = hapticManager,
                        view = view
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    ActionButtonsSection(
                        onDismiss = onDismiss,
                        firstLaunchManager = firstLaunchManager,
                        hapticManager = hapticManager,
                        view = view
                    )
                }

                // Scroll indicators
                if (canScrollUp) {
                    ScrollIndicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isTop = true
                    )
                }

                if (canScrollDown) {
                    ScrollIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isTop = false
                    )
                }
            }
        }
    }

    // API Key guide dialog
    if (showApiGuideDialog) {
        ApiKeyGuideDialog(onDismiss = { showApiGuideDialog = false })
    }
}

@Composable
private fun AppHeaderSection(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.about_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun VersionInfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_version_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(
                    R.string.about_version_format,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = stringResource(R.string.about_build_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun KeyFeaturesSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_key_features),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Feature grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(getFeatureItems()) { feature ->
                    FeatureCard(feature = feature)
                }
            }
        }
    }
}

@Composable
private fun TechnologyStackSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_technology_stack),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(R.string.about_tech_description),
                style = MaterialTheme.typography.bodyMedium,
            )

            // Technology badges
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(getTechStackItems()) { tech ->
                    TechBadge(tech = tech)
                }
            }
        }
    }
}

@Composable
private fun SecurityPrivacySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_security_privacy),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            SecurityHighlight(
                icon = Icons.Default.Storage,
                title = stringResource(R.string.about_local_storage),
                description = stringResource(R.string.about_local_storage_desc)
            )

            SecurityHighlight(
                icon = Icons.Default.Fingerprint,
                title = stringResource(R.string.about_biometric_auth),
                description = stringResource(R.string.about_biometric_auth_desc)
            )

            SecurityHighlight(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.about_encryption),
                description = stringResource(R.string.about_encryption_desc)
            )
        }
    }
}

@Composable
private fun ApiConfigurationSection(onShowGuide: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_api_configuration),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(R.string.about_api_description),
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    onClick = onShowGuide,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.about_configure_apis))
                }
            }
        }
    }
}

@Composable
private fun DevelopmentSection(
    onOpenGitHub: () -> Unit,
    onOpenIssues: () -> Unit,
    hapticManager: HapticFeedbackManager,
    view: android.view.View
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.about_development),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(R.string.about_development_description),
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(
                    onClick = { 
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        onOpenGitHub()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.about_view_source))
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                OutlinedButton(
                    onClick = { 
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        onOpenIssues()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.about_report_issue))
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onDismiss: () -> Unit,
    firstLaunchManager: FirstLaunchManager?,
    hapticManager: HapticFeedbackManager,
    view: android.view.View
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = {
                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                onDismiss()
            }
        ) {
            Text(stringResource(R.string.action_close))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                firstLaunchManager?.markAboutDialogShown()
                onDismiss()
            },
        ) {
            Text(stringResource(R.string.action_got_it))
        }
    }
}

@Composable
private fun ScrollIndicator(
    modifier: Modifier = Modifier,
    isTop: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTop) 32.dp else 80.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = if (isTop) {
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        )
                    }
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (!isTop) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.small,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.cd_scroll_down_more),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.scroll_down_more),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

// Data classes and helper functions
data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val description: String
)

data class TechStackItem(
    val name: String,
    val description: String
)

@Composable
private fun FeatureCard(feature: FeatureItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = feature.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TechBadge(tech: TechStackItem) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = tech.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SecurityHighlight(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// Helper functions for data
private fun getFeatureItems(): List<FeatureItem> {
    return listOf(
        FeatureItem(
            icon = Icons.Default.AccountBalance,
            title = "Portfolio Management",
            description = "Track cash and stocks"
        ),
        FeatureItem(
            icon = Icons.Default.TrendingUp,
            title = "Real-time Data",
            description = "Live market prices"
        ),
        FeatureItem(
            icon = Icons.Default.Security,
            title = "Biometric Security",
            description = "Fingerprint protection"
        ),
        FeatureItem(
            icon = Icons.Default.PhoneAndroid,
            title = "Wear OS Support",
            description = "Smartwatch integration"
        )
    )
}

private fun getTechStackItems(): List<TechStackItem> {
    return listOf(
        TechStackItem("Kotlin", "Language"),
        TechStackItem("Compose", "UI Framework"),
        TechStackItem("Material 3", "Design System"),
        TechStackItem("Room", "Database"),
        TechStackItem("Hilt", "DI Framework"),
        TechStackItem("Retrofit", "Networking")
    )
}
