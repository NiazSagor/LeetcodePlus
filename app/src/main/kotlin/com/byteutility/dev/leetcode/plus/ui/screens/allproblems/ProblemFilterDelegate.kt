package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.isMatchingQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class ProblemFilterDelegate {

    private val _searchQuery = MutableStateFlow("")

    private val allProblemsList = MutableStateFlow<List<LeetCodeProblem>>(emptyList())
    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    private val _selectedDifficulties = MutableStateFlow<List<String>>(emptyList())

    val selectedTags = _selectedTags
    val selectedDifficulties = _selectedDifficulties

    val searchQuery = _searchQuery

    val tags = allProblemsList.map { list ->
        list.map { it.tag }.distinct()
    }

    val difficulties = allProblemsList.map { list ->
        list.map { it.difficulty }.distinct()
    }

    val activeFilterCount = combine(
        _selectedTags,
        _selectedDifficulties,
    ) { selectedTags, selectedDifficulties ->
        selectedTags.size + selectedDifficulties.size
    }

    val filteredProblemsList = combine(
        allProblemsList,
        _searchQuery,
        _selectedTags,
        _selectedDifficulties,
    ) { problems, searchQuery, selectedTags, selectedDifficulties ->
        problems.filter { problem ->
            val matchesTag = selectedTags.isEmpty() || selectedTags.contains(problem.tag)
            val matchesDifficulty =
                selectedDifficulties.isEmpty() || selectedDifficulties.contains(problem.difficulty)
            val matchesQuery = searchQuery.isEmpty() || problem.isMatchingQuery(searchQuery)
            matchesTag && matchesDifficulty && matchesQuery
        }
    }

    fun onProblemSetChanged(newList: List<LeetCodeProblem>) {
        allProblemsList.value = newList
    }

    fun onTagSelected(tag: String) {
        if (_selectedTags.value.contains(tag)) {
            _selectedTags.value = _selectedTags.value.filter { it != tag }
        } else {
            _selectedTags.value += tag
        }
    }

    fun onDifficultySelected(difficulty: String) {
        if (_selectedDifficulties.value.contains(difficulty)) {
            _selectedDifficulties.value = _selectedDifficulties.value.filter { it != difficulty }
        } else {
            _selectedDifficulties.value += difficulty
        }
    }

    fun clearFilters() {
        _selectedTags.value = emptyList()
        _selectedDifficulties.value = emptyList()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
