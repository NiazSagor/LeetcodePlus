package com.byteutility.dev.leetcode.plus.ui.common

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ProblemFilterDelegateInterface {
    val selectedTags: StateFlow<List<String>>
    val selectedDifficulties: StateFlow<List<String>>
    val tags: Flow<List<String>>
    val difficulties: Flow<List<String>>
    val activeFilterCount: Flow<Int>
    val filteredProblemsList: Flow<List<LeetCodeProblem>>
    fun onProblemSetChanged(newList: List<LeetCodeProblem>)
    fun onTagSelected(tag: String)
    fun onDifficultySelected(difficulty: String)
    fun clearFilters()
    fun onSearchQueryChanged(query: String)
}
