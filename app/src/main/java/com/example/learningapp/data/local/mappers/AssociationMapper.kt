package com.example.learningapp.data.local.mappers

import com.example.learningapp.data.local.entities.AssociationEntity
import com.example.learningapp.domain.model.Association

fun AssociationEntity.toDomain() = Association(
    id = id,
    questionId = id,
    association = association
)

fun Association.toEntity() = AssociationEntity(
    id = id,
    questionId = questionId,
    association = association
)