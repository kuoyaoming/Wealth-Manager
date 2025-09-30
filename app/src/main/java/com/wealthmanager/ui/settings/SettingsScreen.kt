package com.wealthmanager.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import com.wealthmanager.ui.about.AboutDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    val uiState by viewModel.uiState.collectAsState()
    var hapticEnabled by remember { mutableStateOf(hapticManager.getSettings().hapticEnabled) }
    var soundEnabled by remember { mutableStateOf(hapticManager.getSettings().soundEnabled) }
    var hapticIntensity by remember { mutableStateOf(hapticManager.getSettings().intensity) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showBackupWarningDialog by remember { mutableStateOf(false) }
    var pendingFinancialBackupToggle by remember { mutableStateOf(false) }

    LaunchedEffect(hapticEnabled, soundEnabled, hapticIntensity) {
        hapticManager.updateSettings(
            HapticFeedbackManager.HapticSettings(
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                intensity = hapticIntensity
            )
        )
    }

    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(uiState.currentLanguageCode) }
    LaunchedEffect(uiState.currentLanguageCode) {
        if (uiState.currentLanguageCode.isNotEmpty() && uiState.currentLanguageCode != currentLanguage) {
            // Update the current language first
            currentLanguage = uiState.currentLanguageCode
            
            // Directly recreate the activity to apply language changes
            (context as? Activity)?.recreate()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_to_dashboard)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LanguageSettingsCard(
                currentLanguageCode = uiState.currentLanguageCode,
                languageOptions = uiState.availableLanguages,
                onLanguageSelected = { languageCode ->
                    if (languageCode != uiState.currentLanguageCode) {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        viewModel.setLanguage(languageCode)
                    }
                }
            )

            HapticFeedbackSettingsCard(
                hapticEnabled = hapticEnabled,
                onHapticEnabledChange = { hapticEnabled = it },
                soundEnabled = soundEnabled,
                onSoundEnabledChange = { soundEnabled = it },
                hapticIntensity = hapticIntensity,
                onHapticIntensityChange = { hapticIntensity = it },
                hapticManager = hapticManager,
                view = view
            )

            TestHapticFeedbackCard(
                hapticManager = hapticManager,
                view = view
            )

            BiometricSettingsCard(
                enabled = uiState.biometricEnabled,
                onToggle = { enabled ->
                    if (enabled != uiState.biometricEnabled) {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        viewModel.setBiometricEnabled(enabled)
                    }
                }
            )

            BackupSettingsCard(
                enabled = uiState.financialBackupEnabled,
                onToggle = { enabled ->
                    if (enabled && !uiState.financialBackupEnabled) {
                        pendingFinancialBackupToggle = true
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                        showBackupWarningDialog = true
                    } else if (!enabled && uiState.financialBackupEnabled) {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        viewModel.setFinancialBackupEnabled(false)
                    }
                }
            )

            AboutSettingsCard(
                onShowAbout = {
                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                    showAboutDialog = true
                }
            )
        }
    }

    if (showBackupWarningDialog) {
        AlertDialog(
            onDismissRequest = {
                showBackupWarningDialog = false
                pendingFinancialBackupToggle = false
            },
            title = { Text(stringResource(R.string.backup_warning_title)) },
            text = {
                Text(stringResource(R.string.backup_warning_message))
            },
            confirmButton = {
                TextButton(onClick = {
                    showBackupWarningDialog = false
                    if (pendingFinancialBackupToggle) {
                        viewModel.setFinancialBackupEnabled(true)
                        pendingFinancialBackupToggle = false
                    }
                }) {
                    Text(stringResource(R.string.enable_backup_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBackupWarningDialog = false
                    pendingFinancialBackupToggle = false
                    viewModel.setFinancialBackupEnabled(false)
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false },
            firstLaunchManager = viewModel.firstLaunchManager
        )
    }
}

@Composable
private fun LanguageSettingsCard(
    currentLanguageCode: String,
    languageOptions: List<LanguageOption>,
    onLanguageSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.settings_language_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = stringResource(R.string.settings_language_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            languageOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = option.languageCode.equals(currentLanguageCode, ignoreCase = true),
                        onClick = { onLanguageSelected(option.languageCode) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(option.displayNameRes),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (languageOptions.any { it.languageCode.equals(currentLanguageCode, ignoreCase = true) }) {
                Text(
                    text = stringResource(R.string.language_change_requires_restart),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HapticFeedbackSettingsCard(
    hapticEnabled: Boolean,
    onHapticEnabledChange: (Boolean) -> Unit,
    soundEnabled: Boolean,
    onSoundEnabledChange: (Boolean) -> Unit,
    hapticIntensity: HapticFeedbackManager.HapticIntensity,
    onHapticIntensityChange: (HapticFeedbackManager.HapticIntensity) -> Unit,
    hapticManager: HapticFeedbackManager,
    view: android.view.View
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.settings_haptic_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Enable haptic feedback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.settings_haptic_enable),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.settings_haptic_enable_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = hapticEnabled,
                    onCheckedChange = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onHapticEnabledChange(it)
                    }
                )
            }

            // Enable sound feedback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "啟用音效回饋",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "為操作提供音效回饋",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onSoundEnabledChange(it)
                    }
                )
            }

            // Haptic feedback intensity
            if (hapticEnabled) {
                Column {
                    Text(
                        text = stringResource(R.string.settings_haptic_intensity),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val intensityOptions = listOf(
                        HapticFeedbackManager.HapticIntensity.LIGHT to stringResource(R.string.settings_haptic_intensity_light),
                        HapticFeedbackManager.HapticIntensity.MEDIUM to stringResource(R.string.settings_haptic_intensity_medium),
                        HapticFeedbackManager.HapticIntensity.STRONG to stringResource(R.string.settings_haptic_intensity_strong),
                        HapticFeedbackManager.HapticIntensity.CONFIRM to stringResource(R.string.settings_haptic_intensity_confirm)
                    )

                    intensityOptions.forEach { (intensity, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = hapticIntensity == intensity,
                                onClick = {
                                    hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                                    onHapticIntensityChange(intensity)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestHapticFeedbackCard(
    hapticManager: HapticFeedbackManager,
    view: android.view.View
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.settings_haptic_test_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = stringResource(R.string.settings_haptic_test_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.settings_haptic_intensity_light))
                }

                Button(
                    onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.MEDIUM)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.settings_haptic_intensity_medium))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.STRONG)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.settings_haptic_intensity_strong))
                }

                Button(
                    onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.settings_haptic_intensity_confirm))
                }
            }
        }
    }
}

@Composable
private fun BackupSettingsCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.settings_financial_backup_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.settings_financial_backup_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(if (enabled) R.string.settings_status_enabled else R.string.settings_status_disabled),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}

@Composable
private fun BiometricSettingsCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.settings_biometric_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.settings_biometric_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(if (enabled) R.string.settings_status_enabled else R.string.settings_status_disabled),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}

@Composable
private fun AboutSettingsCard(
    onShowAbout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.settings_about_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.settings_about_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onShowAbout,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.settings_view))
            }
        }
    }
}
