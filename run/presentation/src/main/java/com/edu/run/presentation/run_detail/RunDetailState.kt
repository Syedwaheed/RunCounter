package com.edu.run.presentation.run_detail

import com.edu.run.presentation.run_overview.model.RunUI

data class RunDetailState(
    val run: RunUI? = null,
    val isLoading: Boolean = true
)
