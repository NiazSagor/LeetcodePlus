package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.predefined.PredefinedProblemSetMetadataProvider
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType
import com.byteutility.dev.leetcode.plus.domain.model.SetMetadata
import com.byteutility.dev.leetcode.plus.ui.common.ProblemFilterDelegate
import com.byteutility.dev.leetcode.plus.ui.common.ProblemFilterDelegateInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AllProblemsViewModel @Inject constructor(
    private val problemsRepository: ProblemsRepository,
    private val predefinedProblemSetMetadataProvider: PredefinedProblemSetMetadataProvider,
    private val filterDelegate: ProblemFilterDelegate,
) : ViewModel(), ProblemFilterDelegateInterface by filterDelegate {

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
        }

    val problemsList = _allProblemsList.flatMapLatest { latestProblems ->
        filterDelegate.onProblemSetChanged(latestProblems)
        filterDelegate.filteredProblemsList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onProblemSetSelected(setMetadata: SetMetadata) {
        if (_selectedStaticProblemSet.value == setMetadata) {
            _selectedStaticProblemSet.value = null
            return
        }
        _selectedStaticProblemSet.value = setMetadata
    }
}
