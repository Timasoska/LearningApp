package com.example.learningapp.data.local.mappers

import com.example.learningapp.data.local.entities.SubjectEntity
import com.example.learningapp.domain.model.Subject

fun SubjectEntity.toDomain() = Subject (
        id = id,
        name = name
    )

fun Subject.toEntity() = SubjectEntity(
    id = id,
    name = name
)