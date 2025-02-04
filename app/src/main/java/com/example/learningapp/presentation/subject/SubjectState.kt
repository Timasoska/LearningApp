package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SubjectState (
    val error: String? = null,
    val isLoading: Boolean = false,
    val subjects: Flow<List<Subject>> = emptyFlow()
)
