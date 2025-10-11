package com.wealthmanager.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.BuildConfig
import com.wealthmanager.R
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import com.wealthmanager.ui.components.DialogCard
import com.wealthmanager.ui.components.PrimaryButton
import com.wealthmanager.ui.components.SecondaryButton
import com.wealthmanager.ui.components.SecondaryCard
import com.wealthmanager.ui.components.TextButton
import com.wealthmanager.utils.rememberUrlLauncher

/**
 * About dialog showcasing app features and information.
 */
@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    firstLaunchManager: FirstLaunchManager? = null,
) {
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val scrollState = rememberScrollState()
    val openUrl = rememberUrlLauncher()

    var canScrollDown by remember { mutableStateOf(false) }
    var canScrollUp by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        canScrollDown = scrollState.value < scrollState.maxValue - 10
        canScrollUp = scrollState.value > 10
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
    ) {
        DialogCard(
            modifier =
                Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.95f),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(scrollState),
                ) {
                    AppHeaderSection(onClose = onDismiss)

                    Spacer(modifier = Modifier.height(20.dp))

                    VersionInfoSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    KeyFeaturesSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    TechnologyStackSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    SecurityPrivacySection()

                    Spacer(modifier = Modifier.height(16.dp))

                    DevelopmentSection(
                        onOpenGitHub = { openUrl("https://github.com/kuoyaoming/Wealth-Manager") },
                        onOpenIssues = { openUrl("https://github.com/kuoyaoming/Wealth-Manager/issues") },
                        hapticManager = hapticManager,
                        view = view,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ActionButtonsSection(
                        onDismiss = onDismiss,
                        firstLaunchManager = firstLaunchManager,
                        hapticManager = hapticManager,
                        view = view,
                    )
                }

                if (canScrollUp) {
                    ScrollIndicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isTop = true,
                    )
                }

                if (canScrollDown) {
                    ScrollIndicator(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isTop = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppHeaderSection(onClose: () -> Unit) {
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
    SecondaryCard(
        modifier = Modifier.fillMaxWidth(),
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
                text =
                    stringResource(
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
    SecondaryCard(
        modifier = Modifier.fillMaxWidth(),
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

            val featureItems = getFeatureItems()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp),
            ) {
                items(featureItems) { feature ->
                    FeatureCard(feature = feature)
                }
            }
        }
    }
}

@Composable
private fun TechnologyStackSection() {
    SecondaryCard(
        modifier = Modifier.fillMaxWidth(),
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

            val techStackItems = getTechStackItems()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(techStackItems) { tech ->
                    TechBadge(tech = tech)
                }
            }
        }
    }
}

@Composable
private fun SecurityPrivacySection() {
    SecondaryCard(
        modifier = Modifier.fillMaxWidth(),
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
                description = stringResource(R.string.about_local_storage_desc),
            )

            SecurityHighlight(
                icon = Icons.Default.Fingerprint,
                title = stringResource(R.string.about_biometric_auth),
                description = stringResource(R.string.about_biometric_auth_desc),
            )

            SecurityHighlight(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.about_encryption),
                description = stringResource(R.string.about_encryption_desc),
            )
        }
    }
}

@Composable
private fun DevelopmentSection(
    onOpenGitHub: () -> Unit,
    onOpenIssues: () -> Unit,
    hapticManager: HapticFeedbackManager,
    view: android.view.View,
) {
    SecondaryCard(
        modifier = Modifier.fillMaxWidth(),
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
                SecondaryButton(onClick = {
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                    onOpenGitHub()
                }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.about_view_source))
                }

                Spacer(modifier = Modifier.width(8.dp))

                SecondaryButton(onClick = {
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                    onOpenIssues()
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
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
    view: android.view.View,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = {
                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                onDismiss()
            },
        ) {
            Text(stringResource(R.string.action_close))
        }

        Spacer(modifier = Modifier.width(8.dp))

        PrimaryButton(
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
    isTop: Boolean,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(if (isTop) 32.dp else 80.dp)
                .background(
                    brush =
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors =
                                if (isTop) {
                                    listOf(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                    )
                                } else {
                                    listOf(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    )
                                },
                        ),
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

data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

data class TechStackItem(
    val name: String,
    val description: String,
)

@Composable
private fun FeatureCard(feature: FeatureItem) {
    SecondaryCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun SecurityHighlight(
    icon: ImageVector,
    title: String,
    description: String,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
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

@Composable
private fun getFeatureItems(): List<FeatureItem> {
    return listOf(
        FeatureItem(
            icon = Icons.Default.AccountBalance,
            title = stringResource(R.string.feature_assets_tracking_title),
            description = stringResource(R.string.feature_assets_tracking_desc),
        ),
        FeatureItem(
            icon = Icons.Default.TrendingUp,
            title = stringResource(R.string.feature_insights_title),
            description = stringResource(R.string.feature_insights_desc),
        ),
        FeatureItem(
            icon = Icons.Default.Security,
            title = stringResource(R.string.feature_secure_backup_title),
            description = stringResource(R.string.feature_secure_backup_desc),
        ),
        FeatureItem(
            icon = Icons.Default.PhoneAndroid,
            title = stringResource(R.string.feature_wear_title),
            description = stringResource(R.string.feature_wear_desc),
        ),
    )
}

@Composable
private fun getTechStackItems(): List<TechStackItem> {
    return listOf(
        TechStackItem(stringResource(R.string.tech_kotlin), stringResource(R.string.about_tech_description)),
        TechStackItem(stringResource(R.string.tech_compose), stringResource(R.string.about_tech_description)),
        TechStackItem(stringResource(R.string.tech_material3), stringResource(R.string.about_tech_description)),
        TechStackItem(stringResource(R.string.tech_room), stringResource(R.string.about_tech_description)),
        TechStackItem(stringResource(R.string.tech_hilt), stringResource(R.string.about_tech_description)),
        TechStackItem(stringResource(R.string.tech_retrofit), stringResource(R.string.about_tech_description)),
    )
}
