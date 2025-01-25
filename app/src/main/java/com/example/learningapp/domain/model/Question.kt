package com.example.learningapp.domain.model

data class Question (
    val id: Int,
    val title: String,
    val answer: String,
    val isLearned: Boolean
)