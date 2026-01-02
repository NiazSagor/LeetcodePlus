package com.byteutility.dev.leetcode.plus.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.ui.theme.easyCategory
import com.byteutility.dev.leetcode.plus.ui.theme.hardCategory
import com.byteutility.dev.leetcode.plus.ui.theme.mediumCategory

@Composable
fun ProblemItem(
    problem: LeetCodeProblem,
    onNavigateToProblemDetails: (String) -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val difficultyColor = getDifficultyColor(problem.difficulty)

    OutlinedCard(
        onClick = { onNavigateToProblemDetails(problem.titleSlug) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 0.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(difficultyColor)
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title Section
                Text(
                    text = problem.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Metadata Row (Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Difficulty Indicator
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = problem.difficulty,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    // Tag Indicator
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = problem.tag,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // 2. The Dynamic Slot
            if (trailingContent != null) {
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    trailingContent()
                }
            }
        }
    }
}

@Composable
fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty.lowercase()) {
        "easy" -> MaterialTheme.colorScheme.easyCategory
        "medium" -> MaterialTheme.colorScheme.mediumCategory
        "hard" -> MaterialTheme.colorScheme.hardCategory
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
@Preview
fun PreviewProblemItem() {
    val problem = LeetCodeProblem(
        title = "Two Sum",
        difficulty = "Easy",
        tag = "Array"
    )

    ProblemItem(
        problem = problem,
        onNavigateToProblemDetails = {},
        trailingContent = {
            Checkbox(
                checked = false,
                onCheckedChange = { },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    )
}
