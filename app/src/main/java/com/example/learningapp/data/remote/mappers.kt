package com.example.learningapp.data.remote
import com.example.learningapp.domain.model.Question // Доменная модель клиента
import com.example.learningapp.domain.model.Subject   // Доменная модель клиента

// --- Subject Mappers ---

// Из DTO ответа сервера в доменную модель клиента
fun SubjectResponseDto.toDomainModel(): Subject {
    return Subject(
        id = this.id,
        name = this.name
        // userId из DTO здесь не используется в доменной модели Subject клиента, что нормально
    )
}

// Из доменной модели клиента в DTO запроса на сервер
fun Subject.toRequestDto(): SubjectRequestDto {
    return SubjectRequestDto(
        name = this.name
    )
}

// --- Question Mappers ---

// Из DTO ответа сервера в доменную модель клиента
fun QuestionResponseDto.toDomainModel(): Question {
    return Question(
        id = this.id,
        title = this.title,
        answer = this.answer,
        isLearned = this.isLearned,
        subjectId = this.subjectId
    )
}

// Из доменной модели клиента в DTO запроса на сервер
fun Question.toRequestDto(): QuestionRequestDto {
    return QuestionRequestDto(
        title = this.title,
        answer = this.answer,
        isLearned = this.isLearned // Передаем текущий статус
    )
}