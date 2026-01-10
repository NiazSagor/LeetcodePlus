package com.byteutility.dev.leetcode.plus.ui.di

import com.byteutility.dev.leetcode.plus.ui.common.ProblemFilterDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ProblemModule {

    @Provides
    fun provideProblemFilterDelegate(): ProblemFilterDelegate {
        return ProblemFilterDelegate()
    }
}
