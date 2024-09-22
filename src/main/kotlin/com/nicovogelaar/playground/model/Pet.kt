package com.nicovogelaar.playground.model

import java.time.LocalDateTime
import java.util.UUID

data class Pet(
    val id: UUID,
    val name: String,
    val category: String,
    val status: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
