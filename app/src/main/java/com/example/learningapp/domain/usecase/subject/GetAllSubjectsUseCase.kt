package com.example.learningapp.domain.usecase.subject

import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSubjectsUseCase @Inject constructor(
    private val repository: QuestionRepository
){

    private suspend operator fun invoke() : Flow<List<Subject>> {
        return repository.getAllSubjects()
    }

}