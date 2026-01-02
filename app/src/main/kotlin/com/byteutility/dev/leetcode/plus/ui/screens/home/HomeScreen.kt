package com.byteutility.dev.leetcode.plus.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.responseVo.Contest
import com.byteutility.dev.leetcode.plus.ui.common.ProgressIndicator
import com.byteutility.dev.leetcode.plus.ui.model.YouTubeVideo
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.UserDetailsUiState
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.VideosByPlayListState
import com.byteutility.dev.leetcode.plus.utils.formatContestDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import java.time.Duration
import java.time.OffsetDateTime
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.Duration.Companion.seconds

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onSetGoal: () -> Unit = {},
    onGoalStatus: () -> Unit = {},
    onTroubleShoot: () -> Unit = {},
    onNavigateToProblemDetails: (String) -> Unit = {},
    onNavigateToVideoSolutions: () -> Unit = {},
    onNavigateToAllProblems: () -> Unit = {},
    onNavigateToContestDetail: (Contest) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dailyProblem by viewModel.dailyProblem.collectAsStateWithLifecycle()
    val dailyProblemSolved by viewModel.dailyProblemSolved.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshUiState()
        onPauseOrDispose { }
    }
    HomeLayout(
        uiState = uiState,
        dailyProblem = dailyProblem,
        dailyProblemSolved = dailyProblemSolved,
        onSetGoal = onSetGoal,
        onGoalStatus = onGoalStatus,
        onTroubleShoot = onTroubleShoot,
        onNavigateToProblemDetails = onNavigateToProblemDetails,
        onLoadMoreSubmission = {
            viewModel.loadNextAcSubmissions()
        },
        onLoadMoreVideos = {
            viewModel.loadNextVideos()
        },
        onSearchClick = onNavigateToVideoSolutions,
        onLogout = {
            viewModel.logout()
            onLogout.invoke()
        },
        onSetInAppReminder = { contest ->
            viewModel.setInAppReminder(contest)
        },
        checkInAppContestReminderStatus = {
            viewModel.checkInAppContestReminderStatus(it)
        },
        onNavigateToAllProblems = onNavigateToAllProblems,
        onNavigateToContestDetail = onNavigateToContestDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLayout(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
    onSetGoal: () -> Unit,
    onGoalStatus: () -> Unit,
    onTroubleShoot: () -> Unit,
    onNavigateToProblemDetails: (String) -> Unit,
    onLoadMoreSubmission: () -> Unit,
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
    onSetInAppReminder: (Contest) -> Unit,
    checkInAppContestReminderStatus: suspend (Contest) -> Boolean,
    onNavigateToAllProblems: () -> Unit = {},
    onNavigateToContestDetail: (Contest) -> Unit = {},
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    val brandGreen = Color(0xFF498A5C)
    var showMenu by remember { mutableStateOf(false) }
    val tooltipState = rememberTooltipState()
    var clickCount by remember { mutableIntStateOf(0) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    /**
                     * 5 times click in a shorter period will open troubleshoot page
                     */
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime <= 1000) {
                                clickCount++
                                if (clickCount == 5) {
                                    onTroubleShoot.invoke()
                                    clickCount = 0
                                }
                            } else {
                                clickCount = 1
                            }
                            lastClickTime = currentTime
                            scope.launch {
                                delay(2000)
                                clickCount = 0
                            }
                        })
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip(
                                containerColor = brandGreen,
                                contentColor = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (uiState.isWeeklyGoalSet) "View Goal Status" else "Set Weekly Goal")
                            }
                        },
                        state = tooltipState
                    ) {
                        IconButton(
                            onClick = {
                                if (uiState.isWeeklyGoalSet) {
                                    onGoalStatus.invoke()
                                } else {
                                    onSetGoal.invoke()
                                }
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .background(
                                    color = brandGreen.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = if (uiState.isWeeklyGoalSet) Icons.Default.TrackChanges else Icons.Default.EmojiEvents,
                                contentDescription = "Weekly Goal",
                                tint = brandGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Box() {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showMenu = false
                                    showLogoutDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Logout,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                UserProfileContent(
                    uiState = uiState,
                    dailyProblem = dailyProblem,
                    dailyProblemSolved = dailyProblemSolved,
                    onNavigateToProblemDetails = onNavigateToProblemDetails,
                    onLoadMoreSubmission = onLoadMoreSubmission,
                    onLoadMoreVideos = onLoadMoreVideos,
                    onSearchClick = onSearchClick,
                    onSetInAppReminder = onSetInAppReminder,
                    checkInAppContestReminderStatus = checkInAppContestReminderStatus,
                    onNavigateToContestDetail = onNavigateToContestDetail
                )
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    model: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 16.dp, start = 8.dp)
            .size(32.dp) // Standard M3 small avatar size
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = model,
            placeholder = painterResource(R.drawable.profile_placeholder),
            contentDescription = "Profile",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserProfileContent(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
    onNavigateToProblemDetails: (String) -> Unit,
    onLoadMoreSubmission: () -> Unit,
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit,
    onSetInAppReminder: (Contest) -> Unit,
    checkInAppContestReminderStatus: suspend (Contest) -> Boolean,
    onNavigateToContestDetail: (Contest) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UserProfileCard(uiState.userBasicInfo)

                // Data sync info message
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Data updated every ${uiState.syncInterval} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                DailyProblemCard(
                    title = dailyProblem.title,
                    verdict = if (dailyProblemSolved) "Completed" else "Pending",
                    titleSlug = dailyProblem.titleSlug,
                    difficulty = dailyProblem.difficulty,
                    onNavigateToProblemDetails = onNavigateToProblemDetails
                )
                UserStatisticsCard(uiState.userContestInfo)
                YouTubeVideoRowContent(
                    uiState.videosByPlayListState,
                    onLoadMoreVideos,
                    onSearchClick
                )
                Text(
                    text = "Upcoming contests",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp)
                )
                AutoScrollingContestList(
                    contests = uiState.leetcodeUpcomingContestsState.contests,
                    onSetInAppReminder = onSetInAppReminder,
                    checkInAppContestReminderStatus = checkInAppContestReminderStatus,
                    onNavigateToContestDetail = onNavigateToContestDetail
                )
                UserProblemCategoryStats(userProblemSolvedInfo = uiState.userProblemSolvedInfo)

                if (uiState.userSubmissionState.submissions.isEmpty()) {
                    Text(
                        text = "You have no recent submissions",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        "Recent AC",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(uiState.userSubmissionState.submissions.size) { index ->
            val item = uiState.userSubmissionState.submissions[index]
            if (index >= uiState.userSubmissionState.submissions.size - 1 && !uiState.userSubmissionState.endReached && !uiState.userSubmissionState.isLoading) {
                onLoadMoreSubmission()
            }
            SubmissionItem(
                submission = item,
                onClick = { onNavigateToProblemDetails(item.titleSlug) }
            )
        }

        item {
            if (uiState.userSubmissionState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun UserPieChart(userProblemSolvedInfo: UserProblemSolvedInfo) {
    PieChart(
        pieChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice(userProblemSolvedInfo.getEasyPercentage(), Color(0xFFE0F7FA)),
                PieChartData.Slice(userProblemSolvedInfo.getMediumPercentage(), Color(0xFFFFF9C4)),
                PieChartData.Slice(userProblemSolvedInfo.getHardPercentage(), Color(0xFFFFCDD2))
            )
        ),
        modifier = Modifier.size(200.dp),
        animation = simpleChartAnimation(),
        sliceDrawer = SimpleSliceDrawer(50F),
    )
}

@Composable
fun UserProblemCategoryStats(
    modifier: Modifier = Modifier,
    userProblemSolvedInfo: UserProblemSolvedInfo,
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                UserPieChart(userProblemSolvedInfo)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProblemCategoriesSolved(userProblemSolvedInfo)
                }
            }
        }
    }
}

@Composable
fun UserProfileCard(user: UserBasicInfo) {
    val brandGreen = Color(0xFF498A5C)
    val lightGreenBackground = brandGreen.copy(alpha = 0.05f)

    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .background(color = lightGreenBackground, shape = RoundedCornerShape(16.dp))
            .border(1.dp, brandGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Elevated Avatar with Brand Ring
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    color = brandGreen.copy(alpha = 0.1f),
                    modifier = Modifier.size(72.dp)
                ) {}
                AsyncImage(
                    model = user.avatar,
                    placeholder = painterResource(R.drawable.profile_placeholder),
                    contentDescription = "User avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, brandGreen, CircleShape)
                )
            }

            // 2. Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Metadata Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileBadge(
                        icon = Icons.Default.Public,
                        text = user.country,
                        brandGreen = brandGreen
                    )
                    ProfileBadge(
                        icon = Icons.Default.Star,
                        text = "#${user.ranking}",
                        brandGreen = brandGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileBadge(
    icon: ImageVector,
    text: String,
    brandGreen: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = brandGreen.copy(alpha = 0.1f),
        contentColor = brandGreen
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UserStatisticsCard(user: UserContestInfo) {
    // We derive a soft green from your brand color for the card background
    val brandGreen = Color(0xFF498A5C)
    val lightGreenBackground = brandGreen.copy(alpha = 0.05f)

    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .background(color = lightGreenBackground, shape = RoundedCornerShape(16.dp))
            .border(1.dp, brandGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Green accent
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .clip(CircleShape)
                        .background(brandGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Contest Statistics",
                    style = MaterialTheme.typography.titleSmall,
                    color = brandGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            // Statistics Rows
            StatRow(
                icon = Icons.Default.Star,
                label = "Contest Rating",
                value = String.format("%.1f", user.rating),
                accentColor = brandGreen
            )
            StatRow(
                icon = Icons.Default.Leaderboard,
                label = "Global Ranking",
                value = "#${user.globalRanking}",
                accentColor = brandGreen
            )
            StatRow(
                icon = Icons.Default.CalendarToday,
                label = "Attendance",
                value = "${user.attend} days",
                accentColor = brandGreen
            )
        }
    }
}

@Composable
private fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon Container with the Green Touch
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = accentColor.copy(alpha = 0.15f), // Soft green pill
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor, // Solid brand green icon
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ProblemCategoriesSolved(
    userProblemSolvedInfo: UserProblemSolvedInfo,
) {
    ProblemCategoryBox(
        category = "Easy",
        solved = userProblemSolvedInfo.easy,
        total = 830,
        backgroundColor = Color(0xFFE0F7FA)
    )

    ProblemCategoryBox(
        category = "Medium",
        solved = userProblemSolvedInfo.medium,
        total = 1742,
        backgroundColor = Color(0xFFFFF9C4)
    )

    ProblemCategoryBox(
        category = "Hard",
        solved = userProblemSolvedInfo.hard,
        total = 756,
        backgroundColor = Color(0xFFFFCDD2)
    )
}

@Composable
fun ProblemCategoryBox(category: String, solved: Int, total: Int, backgroundColor: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
        modifier = Modifier
            .size(width = 100.dp, height = 70.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = category)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$solved/$total")
        }
    }
}

@Composable
fun SubmissionItem(
    submission: UserSubmission,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Title: ${submission.title}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF323232)
            )
            Text(
                text = "Date: ${submission.timestamp}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Language: ${submission.lang}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF323232)
                )
            }
        }
    }
}

@Composable
fun DailyProblemCard(
    title: String,
    verdict: String,
    titleSlug: String,
    difficulty: String,
    onNavigateToProblemDetails: (String) -> Unit
) {
    var animatedContent by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (isActive) {
            animatedContent = true
            delay(1.seconds.inWholeMilliseconds)
            animatedContent = false
            delay(15.seconds.inWholeMilliseconds)
        }
    }

    Crossfade(
        targetState = animatedContent,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) { state ->
        var remainingTime by remember { mutableStateOf(calculateRemainingTime()) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1000L)
                remainingTime = calculateRemainingTime()
            }
        }
        when (state) {
            true -> ProblemTextPlaceholder(
                remainingTime = remainingTime
            )

            false -> ProblemDetailsCard(
                title = title,
                titleSlug = titleSlug,
                remainingTime = remainingTime,
                difficulty = difficulty,
                verdict = verdict,
            ) {
                onNavigateToProblemDetails(it)
            }
        }
    }
}

@Composable
fun ProblemTextPlaceholder(remainingTime: String) {

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
    )

    {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "LeetCode Daily Problem",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Solve today for a streak!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = remainingTime,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ProblemDetailsCard(
    title: String,
    titleSlug: String,
    verdict: String,
    difficulty: String,
    remainingTime: String,
    onNavigateToProblemDetails: (String) -> Unit
) {
    val backgroundColor = when (verdict) {
        "Completed" -> MaterialTheme.colorScheme.secondaryContainer
        "Pending" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (verdict) {
        "Completed" -> MaterialTheme.colorScheme.onSecondaryContainer
        "Pending" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .clickable {
                onNavigateToProblemDetails.invoke(titleSlug)
            }
    )

    {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = verdict,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(0.25f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = difficulty,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = when (difficulty) {
                        "Easy" -> Color(0xFF4CAF50)
                        "Medium" -> Color(0xFFFFC107)

                        else -> Color(0xFFF44336)
                    }
                )
                Text(
                    text = remainingTime,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun YouTubeVideoRowContent(
    state: VideosByPlayListState,
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit
) {
    val videos = state.videos.map {
        YouTubeVideo(
            videoId = it.id,
            thumbnailUrl = it.snippet.thumbnails.high.url,
            title = it.snippet.title
        )
    }
    YouTubeVideoRow(state, videos, onLoadMoreVideos, onSearchClick)
}

@Composable
fun YouTubeVideoRow(
    state: VideosByPlayListState,
    videos: List<YouTubeVideo> = mutableListOf(),
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SearchVideosButton(onClick = onSearchClick)
        }
        itemsIndexed(videos) { index, video ->
            if (index >= state.videos.size - 1 && !state.endReached && !state.isLoading) {
                onLoadMoreVideos()
            }
            Box(
                modifier = Modifier
                    .size(135.dp, 100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=${video.videoId}")
                        )
                        context.startActivity(intent)
                    }
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = "YouTube Thumbnail",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SearchVideosButton(onClick: () -> Unit) {
    val brandGreen = Color(0xFF498A5C)
    // A slightly stronger tint than the cards to show it is a "clickable action"
    val actionSurface = brandGreen.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .size(135.dp, 100.dp)
            .background(color = actionSurface, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(1.dp, brandGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon in a small container to make it pop
            Surface(
                shape = CircleShape,
                color = brandGreen.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = brandGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = "Search Videos",
                color = brandGreen,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoScrollingContestList(
    contests: List<Contest>,
    modifier: Modifier = Modifier,
    scrollIntervalMillis: Long = 3000L,
    onSetInAppReminder: (Contest) -> Unit,
    checkInAppContestReminderStatus: suspend (Contest) -> Boolean,
    onNavigateToContestDetail: (Contest) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedContest by remember { mutableStateOf<Contest?>(null) }
    var isInAppContestReminderSet by remember { mutableStateOf(false) }

    LaunchedEffect(contests) {
        if (contests.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(scrollIntervalMillis)
            if (contests.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % contests.size
                scope.launch {
                    listState.animateScrollToItem(currentIndex)
                }
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(contests) { contest ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .clickable { onNavigateToContestDetail(contest) }
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = contest.event,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatContestDate(contest.start),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Link to contest",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contest.href))
                                    context.startActivity(intent)
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Add a reminder",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    selectedContest = contest
                                    scope.launch {
                                        isInAppContestReminderSet =
                                            checkInAppContestReminderStatus(contest)
                                        showBottomSheet = true
                                    }
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        selectedContest?.let { contest ->
            AddReminderBottomSheet(
                onDismiss = { showBottomSheet = false },
                onAddToCalendar = {
                    val beginTime =
                        OffsetDateTime.parse(contest.start + "Z").toInstant()
                            .toEpochMilli()
                    val endTime =
                        beginTime + Duration.ofSeconds(contest.duration.toLong())
                            .toMillis()

                    val intent = Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(
                            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            beginTime
                        )
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                        .putExtra(CalendarContract.Events.TITLE, contest.event)
                        .putExtra(
                            CalendarContract.Events.DESCRIPTION,
                            "LeetCode Contest: ${contest.event}"
                        )
                        .putExtra(
                            CalendarContract.Events.EVENT_LOCATION,
                            contest.href
                        )
                        .putExtra(
                            CalendarContract.Events.AVAILABILITY,
                            CalendarContract.Events.AVAILABILITY_BUSY
                        )

                    context.startActivity(intent)
                    showBottomSheet = false
                },
                onSetInAppReminder = {
                    onSetInAppReminder(contest)
                    showBottomSheet = false
                },
                isInAppContestReminderSet = isInAppContestReminderSet
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderBottomSheet(
    onDismiss: () -> Unit,
    onAddToCalendar: () -> Unit,
    onSetInAppReminder: () -> Unit,
    isInAppContestReminderSet: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add a reminder for the contest", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onAddToCalendar) {
                Text("Add to Google Calendar")
            }
            Button(onClick = onSetInAppReminder, enabled = !isInAppContestReminderSet) {
                Text(if (isInAppContestReminderSet) "In-App reminder already set" else "Set in-app reminder")
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Logout") },
        text = { Text(text = "Are you sure you want to logout?") },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun calculateRemainingTime(): String {
    val now = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val midnight = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
    }

    val diffMillis = midnight.timeInMillis - now.timeInMillis
    val hours = (diffMillis / (1000 * 60 * 60)) % 24
    val minutes = (diffMillis / (1000 * 60)) % 60
    val seconds = (diffMillis / 1000) % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetails() {
    val submissions = listOf(
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
    )
    HomeLayout(
        uiState = UserDetailsUiState(
            userBasicInfo = UserBasicInfo(
                name = "Mindy Shannon",
                userName = "Annette Jones",
                avatar = "venenatis",
                ranking = 8869,
                country = "Gambia, The"
            ),
            userContestInfo = UserContestInfo(
                rating = 14.15,
                globalRanking = 3679,
                attend = 7232
            ),
            userProblemSolvedInfo = UserProblemSolvedInfo(
                easy = 4592,
                medium = 5761,
                hard = 6990
            ),
        ),
        LeetCodeProblem("Two Sum", "", ""),
        false,
        onSetGoal = {},
        onGoalStatus = {},
        onTroubleShoot = {},
        onNavigateToProblemDetails = {},
        onLoadMoreSubmission = {},
        onLoadMoreVideos = {},
        onSearchClick = {},
        onLogout = {},
        onSetInAppReminder = {},
        checkInAppContestReminderStatus = { false },
        onNavigateToContestDetail = {}
    )
}

@Preview
@Composable
fun PreviewProblemDetailsCard() {
    ProblemDetailsCard(
        title = "Two Sum of Integers of all the time very long",
        titleSlug = "",
        verdict = "Pending",
        difficulty = "Easy",
        "05:19:09"
    ) { }
}

@Preview
@Composable
fun PreviewContestScreen() {
    val sampleContests = listOf(
        Contest(
            duration = 5400,
            end = "2025-09-27T16:00:00",
            event = "Biweekly Contest 166",
            host = "leetcode.com",
            href = "https://leetcode.com/contest/biweekly-contest-166",
            id = 61794058,
            nProblems = null,
            nStatistics = null,
            parsedAt = null,
            problems = null,
            resource = "leetcode.com",
            resourceId = 102,
            start = "2025-09-27T14:30:00"
        ),
        Contest(
            duration = 5400,
            end = "2025-09-21T04:00:00",
            event = "Weekly Contest 468",
            host = "leetcode.com",
            href = "https://leetcode.com/contest/weekly-contest-468",
            id = 61794059,
            nProblems = null,
            nStatistics = null,
            parsedAt = null,
            problems = null,
            resource = "leetcode.com",
            resourceId = 102,
            start = "2025-09-21T02:30:00"
        )
    )

    AutoScrollingContestList(
        contests = sampleContests,
        onSetInAppReminder = {},
        checkInAppContestReminderStatus = { false },
        onNavigateToContestDetail = {}
    )
}
