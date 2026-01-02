package com.byteutility.dev.leetcode.plus.ui.screens.targetset

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.ui.common.LeetCodeSearchBar
import com.byteutility.dev.leetcode.plus.ui.common.ProblemItem
import com.byteutility.dev.leetcode.plus.ui.dialogs.WeeklyGoalSetDialog
import com.byteutility.dev.leetcode.plus.ui.screens.allproblems.FilterBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetWeeklyTargetScreen(
    onPopCurrent: () -> Unit = {},
    onNavigateToProblemDetails: (String) -> Unit = {}
) {
    val viewModel: SetWeeklyTargetViewModel = hiltViewModel()
    val problems by viewModel.problemsList.collectAsStateWithLifecycle()
    val selectedProblems by viewModel.selectedProblems.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val difficulties by viewModel.difficulties.collectAsStateWithLifecycle()
    val selectedDifficulties by viewModel.selectedDifficulties.collectAsStateWithLifecycle()
    val selectedTags by viewModel.selectedTags.collectAsStateWithLifecycle()
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
    val selectedStaticProblemSet by viewModel.selectedStaticProblemSet.collectAsStateWithLifecycle()
    val predefinedProblemSet = viewModel.predefinedProblemSets

    LaunchedEffect(Unit) {
        viewModel.popCurrentDestination.collect {
            onPopCurrent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Set Weekly Goals",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onPopCurrent() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        showFilterBottomSheet = true
                    }) {
                        BadgedBox(
                            badge = {
                                if (activeFilterCount > 0) {
                                    Badge {
                                        Text(activeFilterCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Problems"
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        var needToShowConfirmDialog by rememberSaveable { mutableStateOf(false) }

        if (showFilterBottomSheet) {
            FilterBottomSheet(
                problemSets = predefinedProblemSet,
                selectedStaticProblemSet = selectedStaticProblemSet,
                selectedTags = selectedTags,
                selectedDifficulties = selectedDifficulties,
                tags = tags,
                difficulties = difficulties,
                onTagSelected = { viewModel.onTagSelected(it) },
                onDifficultySelected = { viewModel.onDifficultySelected(it) },
                onApply = { showFilterBottomSheet = false },
                onClear = {
                    viewModel.clearFilters()
                    showFilterBottomSheet = false
                },
                onDismiss = { showFilterBottomSheet = false },
                onProblemSetSelected = { viewModel.onProblemSetSelected(it) }
            )
        }

        ProblemSelection(
            selectedProblems = selectedProblems,
            modifier = Modifier.padding(innerPadding),
            problems = problems, {
                Log.i("SetWeeklyTargetScreen", "Problems selected for week")
                needToShowConfirmDialog = true
            },
            onNavigateToProblemDetails = onNavigateToProblemDetails,
            onProblemSelected = { problem, selected ->
                viewModel.onProblemSelected(problem, selected)
            },
            onSearchQueryChange = { viewModel.onSearchQueryChanged(it) }
        )
        if (needToShowConfirmDialog) {
            WeeklyGoalSetDialog { period ->
                viewModel.handleWeeklyGoalSet(selectedProblems, period)
            }
        }
    }
}

@Composable
fun ProblemSelection(
    selectedProblems: List<LeetCodeProblem>,
    modifier: Modifier = Modifier,
    problems: List<LeetCodeProblem>,
    onConfirm: (List<LeetCodeProblem>) -> Unit,
    onNavigateToProblemDetails: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onProblemSelected: (LeetCodeProblem, Boolean) -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    val itemsPerPage = 20
    val totalPages = (problems.size + itemsPerPage - 1) / itemsPerPage
    val displayedItems = problems.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .then(modifier)
    ) {
        LeetCodeSearchBar(
            query = searchText,
            onQueryChange = {
                onSearchQueryChange(it)
                searchText = it
            },
            placeholder = "Search Problems...",
        )

        LazyColumn(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(1.0f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(displayedItems) { problem ->
                ProblemItem(
                    problem = problem,
                    onNavigateToProblemDetails = onNavigateToProblemDetails,
                    trailingContent = {
                        Checkbox(
                            checked = selectedProblems.contains(problem),
                            onCheckedChange = { selected ->
                                onProblemSelected.invoke(problem, selected)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentPage > 0) currentPage-- },
                enabled = currentPage > 0
            ) {
                Text("Previous")
            }

            Text("Page ${currentPage + 1} of $totalPages")

            Button(
                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                enabled = currentPage < totalPages - 1
            ) {
                Text("Next")
            }
        }

        Button(
            onClick = { onConfirm(selectedProblems) },
            enabled = selectedProblems.size == 7,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Confirm")
        }
    }
}

@Composable
fun ProblemItem(
    problem: LeetCodeProblem,
    isSelected: Boolean,
    onProblemSelected: (Boolean) -> Unit,
    onNavigateToProblemDetails: (String) -> Unit = {}
) {
    val backgroundColor: Color = when (problem.difficulty) {
        "Easy" -> Color(0xFFE0F7FA)
        "Medium" -> Color(0xFFFFF9C4)
        "Hard" -> Color(0xFFFFCDD2)
        else -> Color(0xFFE0F7FA)
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    onNavigateToProblemDetails.invoke(problem.titleSlug)
                }
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Text(text = problem.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Tag: ${problem.tag}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Difficulty: ${problem.difficulty}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onProblemSelected(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ProblemSelectionPreview() {
    val problems = remember { getDummyProblems() }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Set Weekly Goals") })
        },
    ) { innerPadding ->
        ProblemSelection(
            problems,
            Modifier.padding(innerPadding),
            problems = problems,
            {},
            onNavigateToProblemDetails = {},
            onProblemSelected = { _, _ -> },
            onSearchQueryChange = {}
        )
    }
}

fun getDummyProblems(): List<LeetCodeProblem> {
    return listOf(
        LeetCodeProblem("Two Sum", "Easy", "Array"),
        LeetCodeProblem("Binary Tree Level Order Traversal", "Medium", "Tree"),
        LeetCodeProblem("Longest Substring Without Repeating Characters", "Medium", "String"),
        LeetCodeProblem("Median of Two Sorted Arrays", "Hard", "Array"),
        LeetCodeProblem("Search in Rotated Sorted Array", "Medium", "Binary Search"),
        LeetCodeProblem("Longest Palindromic Substring", "Medium", "String"),
        LeetCodeProblem("Valid Parentheses", "Easy", "Stack"),
        LeetCodeProblem("Merge Intervals", "Medium", "Sorting"),
        LeetCodeProblem("Word Ladder", "Hard", "Graph")
    )
}
