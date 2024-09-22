package com.nicovogelaar.playground.api.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import java.util.UUID

@GraphQLDescription("Represents an order placed in the system")
data class Order(
    @GraphQLName("id")
    @GraphQLDescription("Unique identifier of the order")
    val id: UUID,
    @GraphQLName("store")
    @GraphQLDescription("Store where the order was placed")
    val store: Store,
    @GraphQLName("pet")
    @GraphQLDescription("Pet being ordered")
    val pet: Pet,
)
