package com.nicovogelaar.playground.model

import java.util.UUID

data class Store(
    val id: UUID,
    val name: String,
    val location: String,
    val inventory: List<Pet>,
)
