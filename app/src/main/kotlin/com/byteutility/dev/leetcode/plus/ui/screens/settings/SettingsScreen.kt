package com.byteutility.dev.leetcode.plus.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.BuildConfig
import com.byteutility.dev.leetcode.plus.core.settings.config.IntervalConfigurations
import com.byteutility.dev.leetcode.plus.core.settings.model.IntervalOption
import com.byteutility.dev.leetcode.plus.ui.screens.settings.composables.DailyProblemWidgetPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userInfo by viewModel.userBasicInfo.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()
    val syncInterval by viewModel.syncInterval.collectAsStateWithLifecycle()
    val notificationInterval by viewModel.notificationInterval.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSyncIntervalDialog by remember { mutableStateOf(false) }
    var showNotificationIntervalDialog by remember { mutableStateOf(false) }
    var isWidgetPinned by remember { mutableStateOf(viewModel.isDailyProblemWidgetPinned()) }

    LifecycleResumeEffect(Unit) {
        isWidgetPinned = viewModel.isDailyProblemWidgetPinned()
        onPauseOrDispose {}
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showSyncIntervalDialog) {
        IntervalSelectionDialog(
            intervalOptions = IntervalConfigurations.syncIntervalOptions,
            currentInterval = syncInterval,
            onConfirm = { newInterval ->
                showSyncIntervalDialog = false
                viewModel.updateSyncInterval(newInterval)
            },
            onDismiss = { showSyncIntervalDialog = false },
            dialogTitle = "Set Sync Interval",
            dialogSubtitle = "Choose how often to sync data:"
        )
    }

    if (showNotificationIntervalDialog) {
        IntervalSelectionDialog(
            intervalOptions = IntervalConfigurations.notificationIntervalOptions,
            currentInterval = notificationInterval,
            onConfirm = { newInterval ->
                showNotificationIntervalDialog = false
                viewModel.updateNotificationInterval(newInterval)
            },
            onDismiss = { showNotificationIntervalDialog = false },
            dialogTitle = "Set Notification Interval",
            dialogSubtitle = "Choose how often to receive push notifications:"
        )
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            SettingsSection(title = "Account") {
                if (userInfo.userName.isNotEmpty()) {
                    SettingsInfoRow(label = "Username", value = userInfo.userName)

                    // M3 Dividers are very subtle (Outline Variant)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    SettingsInfoRow(label = "Name", value = userInfo.name)

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    SettingsInfoRow(label = "Ranking", value = "#${userInfo.ranking}")

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Modern Action Row
                    ListItem(
                        headlineContent = {
                            Text("Logout", color = MaterialTheme.colorScheme.error)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable { showLogoutDialog = true },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }

            SettingsSection(title = "Data Sync") {
                // 1. Actionable Row (Clickable)
                SettingsActionRow(
                    label = "Sync Interval",
                    value = "$syncInterval minutes",
                    onClick = { showSyncIntervalDialog = true }
                )

                // Subtle M3 Divider
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // 2. Static Info Row
                SettingsInfoRow(
                    label = "Last Synced",
                    value = lastSyncTime
                )
            }

            SettingsSection(title = "Notifications") {
                // 1. Actionable Row (Clickable)
                SettingsActionRow(
                    label = "Notifications Interval",
                    value = "$notificationInterval minutes",
                    onClick = { showNotificationIntervalDialog = true }
                )
            }

            SettingsSection(title = "Daily Problem Widget") {
                // 1. The Switch Control
                SettingsSwitchRow(
                    label = if (isWidgetPinned) "Widget Pinned" else "Pin Widget",
                    subLabel = if (!isWidgetPinned) "Add daily problem widget to your home screen" else null,
                    checked = isWidgetPinned,
                    enabled = !isWidgetPinned,
                    onCheckedChange = { checked ->
                        if (checked) {
                            viewModel.requestPinWidget()
                        }
                        isWidgetPinned = checked
                    }
                )

                // 2. The Conditional Preview
                if (!isWidgetPinned) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DailyProblemWidgetPlaceholder()
                    }
                }
            }
            SettingsSection(title = "App Information") {
                SettingsInfoRow(
                    label = "Version",
                    value = BuildConfig.VERSION_NAME
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                SettingsInfoRow(
                    label = "Version Code",
                    value = BuildConfig.VERSION_CODE.toString()
                )
            }
            SettingsSection(title = "About") {
                Column(modifier = Modifier.padding(16.dp)) {
                    // App Description
                    Text(
                        text = "LeetCode Plus is your companion app for tracking progress, managing goals, and staying updated with contests.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Features Header
                    Text(
                        text = "Key Features",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF498A5C), // Emerald Green
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Feature List
                    val features = listOf(
                        "Track statistics and progress",
                        "Set and monitor weekly goals",
                        "View daily challenges",
                        "Browse all problems",
                        "Get contest reminders",
                        "Access video solutions"
                    )

                    features.forEach { feature ->
                        FeatureRow(text = feature)
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(100.dp)
            )
        }
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF498A5C), CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingsSwitchRow(
    label: String,
    subLabel: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        },
        supportingContent = subLabel?.let {
            {
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        trailingContent = {
            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF498A5C),
                    checkedThumbColor = Color.White
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsActionRow(
    label: String,
    value: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF498A5C), // Emerald Green
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsInfoRow(
    label: String,
    value: String
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Section Header
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF498A5C),
            modifier = Modifier.padding(8.dp) // Emerald Green accent
        )

        // Grouped Items Surface
        Surface(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun IntervalSelectionDialog(
    intervalOptions: List<IntervalOption>,
    currentInterval: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogSubtitle: String
) {
    var selectedInterval by remember { mutableLongStateOf(currentInterval) }
    val emeraldGreen = Color(0xFF498A5C)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Text(
                        text = dialogSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                }
                items(intervalOptions) { interval ->
                    val isSelected = selectedInterval == interval.time

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedInterval = interval.time },
                        headlineContent = {
                            Text(
                                text = interval.displayLabel,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        supportingContent = if (interval.isDefault) {
                            { Text("Recommended Default") }
                        } else {
                            null
                        },
                        trailingContent = {
                            RadioButton(
                                selected = isSelected,
                                onClick = null, // Handled by ListItem clickable
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = emeraldGreen
                                )
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = if (isSelected) {
                                emeraldGreen.copy(alpha = 0.08f)
                            } else {
                                Color.Transparent
                            }
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedInterval) },
                colors = ButtonDefaults.textButtonColors(contentColor = emeraldGreen)
            ) {
                Text("Apply", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    //windowInsets = BottomSheetDefaults.windowInsets
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon & Title
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Log out of LeetCode Plus?",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "You'll lose access to your synced progress until you sign back in.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Destructive Action
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Log out", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel Action
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keep me signed in", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
