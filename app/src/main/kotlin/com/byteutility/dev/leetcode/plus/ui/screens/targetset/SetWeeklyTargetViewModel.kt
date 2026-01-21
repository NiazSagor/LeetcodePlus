package com.byteutility.dev.leetcode.plus.ui.screens.targetset

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.predefined.PredefinedProblemSetMetadataProvider
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.worker.ClearGoalWorker
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType
import com.byteutility.dev.leetcode.plus.domain.model.SetMetadata
import com.byteutility.dev.leetcode.plus.ui.common.ProblemFilterDelegate
import com.byteutility.dev.leetcode.plus.ui.common.ProblemFilterDelegateInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO Remove context injection from viewmodel, rather triggering all workers from a single class approach

private const val PROBLEMS_PER_PAGE = 20

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val problemsRepository: ProblemsRepository,
    private val weeklyGoalRepository: WeeklyGoalRepository,
    private val predefinedProblemSetMetadataProvider: PredefinedProblemSetMetadataProvider,
    private val filterDelegate: ProblemFilterDelegate,
) : ViewModel(), ProblemFilterDelegateInterface by filterDelegate {

    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    private val _popCurrentDestination = MutableSharedFlow<Unit>()
    val popCurrentDestination = _popCurrentDestination.asSharedFlow()

    private val _selectedProblems = MutableStateFlow<List<LeetCodeProblem>>(emptyList())
    val selectedProblems = _selectedProblems.asStateFlow()

    val predefinedProblemSets = predefinedProblemSetMetadataProvider.getAvailableStaticSets()

    private val _selectedStaticProblemSet = MutableStateFlow<SetMetadata?>(null)

    val selectedStaticProblemSet = _selectedStaticProblemSet.asStateFlow()

    private val _allProblemsList = _selectedStaticProblemSet
        .flatMapLatest { set ->
            val problemSet = set?.let { ProblemSetType.PredefinedProblemSet(metadata = it) }
            flowOf(problemsRepository.getProblems(problemSet))
        }

    private val sharedFilteredList = _allProblemsList
        .flatMapLatest { latestProblems ->
            filterDelegate.onProblemSetChanged(latestProblems)
            filterDelegate.filteredProblemsList
        }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1
        )

    val totalPages = sharedFilteredList.map { problems ->
        (problems.size + PROBLEMS_PER_PAGE - 1) / PROBLEMS_PER_PAGE
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val problemsList = sharedFilteredList
        .combine(_currentPage) { problems, currentPage ->
            val safePage = if (currentPage * PROBLEMS_PER_PAGE >= problems.size) 0 else currentPage
            problems.drop(safePage * PROBLEMS_PER_PAGE).take(PROBLEMS_PER_PAGE)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            totalPages.collect { maxPages ->
                if (_currentPage.value >= maxPages && maxPages > 0) {
                    _currentPage.value = 0
                }
            }
        }
    }

    fun onProblemSetSelected(setMetadata: SetMetadata) {
        if (_selectedStaticProblemSet.value == setMetadata) {
            _selectedStaticProblemSet.value = null
            return
        }
        _selectedStaticProblemSet.value = setMetadata
    }

    fun onProblemSelected(problem: LeetCodeProblem, selected: Boolean) {
        if (_selectedProblems.value.size < 7 || selectedProblems.value.contains(problem)) {
            _selectedProblems.value = if (selected) {
                _selectedProblems.value + problem
            } else {
                _selectedProblems.value - problem
            }
        }
    }

    fun handleWeeklyGoalSet(
        problems: List<LeetCodeProblem>,
        period: WeeklyGoalPeriod
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            weeklyGoalRepository.saveWeeklyGoal(problems, period)
            _popCurrentDestination.emit(Unit)

            // Clear job to work manager so that it clears storage after a week
            ClearGoalWorker.enqueueWork(context)
        }
    }

    fun changePage(page: Int) {
        _currentPage.value = page
    }
}
