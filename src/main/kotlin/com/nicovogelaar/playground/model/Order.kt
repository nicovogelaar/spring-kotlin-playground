package com.nicovogelaar.playground.model

import java.util.UUID

data class Order(
    val id: UUID,
    val store: Store,
    val pet: Pet,
)

data class DraftOrder(
    val store: Store,
    val pet: Pet,
)
