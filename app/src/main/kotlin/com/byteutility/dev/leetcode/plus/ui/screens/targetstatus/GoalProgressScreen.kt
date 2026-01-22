package com.byteutility.dev.leetcode.plus.ui.screens.targetstatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.byteutility.dev.leetcode.plus.ui.dialogs.WeeklyGoalResetDialog
import com.byteutility.dev.leetcode.plus.ui.model.ProgressUiState

@Composable
fun GoalProgressScreen(
    onPopCurrent: () -> Unit,
    onNavigateToProblemDetails: (String) -> Unit,
) {
    val viewmodel: GoalProgressViewModel = hiltViewModel()
    viewmodel.init()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    var showGoalResetDialog by rememberSaveable { mutableStateOf(false) }
    ProgressScreenContent(
        uiState = uiState,
        onPopCurrent = onPopCurrent,
        onNavigateToProblemDetails = onNavigateToProblemDetails,
        onResetGoal = {
            if (uiState.problemsWithStatus.isNotEmpty()) {
                showGoalResetDialog = true
            }
        }
    )

    if (showGoalResetDialog) {
        WeeklyGoalResetDialog(
            onConfirm = {
                viewmodel.resetGoal()
                showGoalResetDialog = false
            },
            onDismiss = {
                showGoalResetDialog = false
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProgressScreenContent(
    uiState: ProgressUiState,
    onPopCurrent: () -> Unit,
    onResetGoal: () -> Unit,
    onNavigateToProblemDetails: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Progress", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    TextButton(
                        onClick = onResetGoal,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error // Visual cue for a destructive action
                        )
                    ) {
                        Text(
                            text = "Reset Goal",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onPopCurrent() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.problemsWithStatus.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            DateRow(
                                startDate = uiState.period.startDate,
                                endDate = uiState.period.endDate
                            )
                        }
                        items(uiState.problemsWithStatus) {
                            ProblemCard(it, onNavigateToProblemDetails)
                        }
                    }
                } else {
                    Text(
                        "Set a goal to see your progress",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                    )
                }
            }
            // AdBannerAdaptive(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun ProblemCard(
    problemStatus: ProblemStatus,
    onNavigateToProblemDetails: (String) -> Unit
) {
    // Define M3-compliant colors based on the theme palette
    val themeData = when (problemStatus.status) {
        "Completed" -> Triple(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primary,
            Icons.Rounded.CheckCircle
        )

        "In Progress" -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.tertiary,
            Icons.Rounded.History
        )

        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Rounded.PlayCircle
        )
    }

    // Using FilledCard or OutlinedCard is standard for lists
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onNavigateToProblemDetails(problemStatus.titleSlug) },
        colors = CardDefaults.cardColors(containerColor = themeData.first),
        shape = MaterialTheme.shapes.medium,
        // Lower elevation looks more modern and less "floating"
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        ListItem(
            modifier = Modifier.background(Color.Transparent),
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(
                    text = problemStatus.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            supportingContent = {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    // Status Badge
                    Text(
                        text = problemStatus.status,
                        style = MaterialTheme.typography.labelLarge,
                        color = themeData.second,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Attempts count with subtle text
                    Text(
                        text = "Attempts: ${problemStatus.attemptsCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            trailingContent = {
                // Large status icon for visual confirmation
                Icon(
                    imageVector = themeData.third,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = themeData.second
                )
            }
        )
    }
}

@Composable
fun DateRow(startDate: String, endDate: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Start Date
            DateItem(label = "Starts", date = startDate, alignment = Alignment.Start)

            // Timeline Connector
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.outlineVariant
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // End Date
            DateItem(label = "Ends", date = endDate, alignment = Alignment.End)
        }
    }
}

@Composable
private fun DateItem(
    label: String,
    date: String,
    alignment: Alignment.Horizontal
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DateItem(label: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
@Preview
fun LeetCodeProgressScreenPreview() {
    val problemStatuses = remember {
        listOf(
            ProblemStatus("Two Sum", "two-sum", "Not Started", "Easy", 0),
            ProblemStatus(
                "Longest Substring Without Repeating Characters",
                "two-sum",
                "In Progress",
                "Medium",
                1,
            ),
            ProblemStatus(
                "Median of Two Sorted Arrays",
                "two-sum",
                "Completed",
                "Hard",
                1,
            ),
            ProblemStatus("Add Two Numbers", "two-sum", "Not Started", "Medium", 0),
            ProblemStatus("Valid Parentheses", "two-sum", "In Progress", "Easy", 5),
            ProblemStatus("Merge Two Sorted Lists", "two-sum", "Not Started", "Easy", 0),
            ProblemStatus("Climbing Stairs", "two-sum", "Completed", "Easy", 2)
        )
    }
    ProgressScreenContent(
        ProgressUiState(
            problemStatuses,
            WeeklyGoalPeriod("14 June 2024", "21 June 2024")
        ),
        onPopCurrent = {},
        onNavigateToProblemDetails = {},
        onResetGoal = {}
    )
}
