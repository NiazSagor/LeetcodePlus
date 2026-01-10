package com.byteutility.dev.leetcode.plus.data.model

data class LeetCodeProblem(
    val title: String,
    val difficulty: String,
    val tag: String,
    val titleSlug: String = "",
)

fun LeetCodeProblem.isMatchingQuery(
    query: String,
) = this.title.contains(query, true) ||
        this.difficulty.contains(query, true) ||
        this.tag.contains(query, true)
