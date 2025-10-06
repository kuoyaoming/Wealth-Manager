package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.focus.onFocusChanged
import com.wealthmanager.R

/**
 * API Key input field with Credentials/Autofill support
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApiKeyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = stringResource(R.string.settings_api_input_placeholder),
    isPassword: Boolean = true,
    showAutofillStatus: Boolean = true,
    autofillHint: String? = null,
    onImeAction: (() -> Unit)? = null,
    textFieldModifier: Modifier = Modifier.fillMaxWidth(),
    focusRequester: FocusRequester? = null,
    enableSystemAutofill: Boolean = false,
    onAutofillClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val autofill = LocalAutofill.current
    val autofillTree = LocalAutofillTree.current
    val autofillNode = remember {
        AutofillNode(
            onFill = { onValueChange(it) },
            autofillTypes = listOf(AutofillType.Password),
        )
    }
    DisposableEffect(Unit) {
        autofillTree += autofillNode
        onDispose { /* no-op */ }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )

        if (showAutofillStatus && value.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.api_key_autofill_status, if (autofillHint != null) "($autofillHint)" else ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        var fieldModifier = textFieldModifier
        if (focusRequester != null) fieldModifier = fieldModifier.focusRequester(focusRequester)
        fieldModifier = fieldModifier
            .onGloballyPositioned { layoutCoordinates ->
                val rect = layoutCoordinates.boundsInWindow()
                autofillNode.boundingBox = Rect(rect.left, rect.top, rect.right, rect.bottom)
            }
            .onFocusChanged { focusState ->
                if (!enableSystemAutofill) return@onFocusChanged
                if (focusState.isFocused) {
                    autofill?.requestAutofillForNode(autofillNode)
                } else {
                    autofill?.cancelAutofillForNode(autofillNode)
                }
            }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = fieldModifier,
            placeholder = { Text(placeholder) },
            visualTransformation =
                if (isPassword && !isPasswordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = { onImeAction?.invoke() },
                ),
            trailingIcon = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (onAutofillClick != null) {
                        IconButton(onClick = onAutofillClick) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = stringResource(R.string.api_key_use_saved_key),
                            )
                        }
                    }
                    if (isPassword) {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) stringResource(R.string.api_key_hide_password) else stringResource(R.string.api_key_show_password),
                            )
                        }
                    }
                }
            },
        )

        if (value.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.api_key_autofill_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
