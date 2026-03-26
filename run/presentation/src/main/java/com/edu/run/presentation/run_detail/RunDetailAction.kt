package com.edu.run.presentation.run_detail

sealed interface RunDetailAction {
    data object OnBackClick : RunDetailAction
    data object OnDeleteClick : RunDetailAction
}
