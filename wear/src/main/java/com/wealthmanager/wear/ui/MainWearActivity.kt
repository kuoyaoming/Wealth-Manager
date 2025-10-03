package com.wealthmanager.wear.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.wealthmanager.wear.R
import com.wealthmanager.wear.tiles.state.TileStateRepository
import com.wealthmanager.wear.ui.theme.WealthManagerWearTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size

class MainWearActivity : ComponentActivity() {

    private val tileStateRepository by lazy { TileStateRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp(
                loadTileAdded = { tileStateRepository.loadState().tileAdded },
                onManualSync = {
                    lifecycleScope.launch {
                        runManualSync()
                    }
                }
            )
        }
    }

    private suspend fun runManualSync() {
        try {
            tileStateRepository.requestSync(manual = true)
            Toast.makeText(this, R.string.wear_home_sync_success, Toast.LENGTH_SHORT).show()
        } catch (exception: Exception) {
            Toast.makeText(this, R.string.wear_home_sync_failure, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun WearApp(
    loadTileAdded: () -> Boolean,
    onManualSync: suspend () -> Unit
) {
    WealthManagerWearTheme {
        var tileAdded by remember { mutableStateOf(false) }
        var isSyncing by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            tileAdded = loadTileAdded()
        }

        LaunchedEffect(isSyncing) {
            if (isSyncing) {
                onManualSync()
                tileAdded = loadTileAdded()
                isSyncing = false
            }
        }

        val listState = rememberScalingLazyListState()
        val textColor = MaterialTheme.colors.onBackground

        ScalingLazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState
        ) {
            item {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
            item {
                Text(
                    text = stringResource(R.string.wear_home_title),
                    style = MaterialTheme.typography.title3,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
            if (tileAdded) {
                item {
                    Text(
                        text = stringResource(R.string.wear_home_tile_added),
                        style = MaterialTheme.typography.body2,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.wear_home_tile_instructions),
                        style = MaterialTheme.typography.body2,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.wear_home_tile_hint),
                        style = MaterialTheme.typography.caption1,
                        color = textColor.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            item {
                Button(
                    onClick = { if (!isSyncing) isSyncing = true },
                    enabled = !isSyncing,
                    modifier = Modifier.size(width = 120.dp, height = 40.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp), 
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colors.onPrimary
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.wear_home_sync_button),
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(
        loadTileAdded = { false },
        onManualSync = {}
    )
}

