package com.example.learningapp.domain.usecase.subject

import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class GetSubjectByIdUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
   suspend operator fun invoke(id: Int): Subject{
       return repository.getSubjectById(id)
   }
}