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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO Remove context injection from viewmodel, rather triggering all workers from a single class approach

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val problemsRepository: ProblemsRepository,
    private val weeklyGoalRepository: WeeklyGoalRepository,
    private val predefinedProblemSetMetadataProvider: PredefinedProblemSetMetadataProvider,
    private val filterDelegate: ProblemFilterDelegate,
) : ViewModel() {

    private val _popCurrentDestination = MutableSharedFlow<Unit>()
    val popCurrentDestination = _popCurrentDestination.asSharedFlow()

    private val _selectedProblems = MutableStateFlow<List<LeetCodeProblem>>(emptyList())
    val selectedProblems = _selectedProblems.asStateFlow()

    val predefinedProblemSets = predefinedProblemSetMetadataProvider.getAvailableStaticSets()

    private val _selectedStaticProblemSet = MutableStateFlow<SetMetadata?>(null)

    val selectedStaticProblemSet = _selectedStaticProblemSet.asStateFlow()

    private val _allProblemsList = _selectedStaticProblemSet
        .flatMapLatest { set ->
            flow {
                var problemSet: ProblemSetType? = null
                if (set != null) {
                    problemSet = ProblemSetType.PredefinedProblemSet(metadata = set)
                }
                emit(problemsRepository.getProblems(problemSet))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedTags = filterDelegate.selectedTags.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedDifficulties = filterDelegate.selectedDifficulties.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tags = filterDelegate.tags.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val difficulties = filterDelegate.difficulties.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeFilterCount = filterDelegate.activeFilterCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )


    val problemsList = _allProblemsList.flatMapLatest { latestProblems ->
        filterDelegate.onProblemSetChanged(latestProblems)
        filterDelegate.filteredProblemsList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onTagSelected(tag: String) {
        filterDelegate.onTagSelected(tag)
    }

    fun onDifficultySelected(difficulty: String) {
        filterDelegate.onDifficultySelected(difficulty)
    }

    fun clearFilters() {
        filterDelegate.clearFilters()
    }

    fun onSearchQueryChanged(query: String) {
        filterDelegate.onSearchQueryChanged(query)
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
}
