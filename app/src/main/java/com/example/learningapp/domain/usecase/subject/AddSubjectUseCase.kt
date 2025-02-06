package com.example.learningapp.domain.usecase.subject

import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class AddSubjectUseCase @Inject constructor(
    private val repository: QuestionRepository
) {

    suspend operator fun invoke(name: String) : Long{
        return repository.addSubject(name)
    }

}