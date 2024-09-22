package com.nicovogelaar.playground.api.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import java.util.UUID

@GraphQLDescription("Represents a store in the system")
data class Store(
    @GraphQLName("id")
    @GraphQLDescription("Unique identifier of the store")
    val id: UUID,
    @GraphQLName("name")
    @GraphQLDescription("Name of the store")
    val name: String,
    @GraphQLName("location")
    @GraphQLDescription("Location of the store (e.g., address or region)")
    val location: String,
    @GraphQLName("inventory")
    @GraphQLDescription("List of pets available in the store's inventory")
    val inventory: List<Pet>,
)
