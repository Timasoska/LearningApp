package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class GetQuestionsBySubjectUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(subjectId: Int): List<Question> {
        return repository.getQuestionsBySubject(subjectId)
    }
}
