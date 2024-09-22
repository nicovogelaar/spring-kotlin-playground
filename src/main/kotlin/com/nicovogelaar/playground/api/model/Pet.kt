package com.nicovogelaar.playground.api.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import java.util.UUID

@GraphQLDescription("Represents a pet in the system")
data class Pet(
    @GraphQLName("id")
    @GraphQLDescription("Unique identifier of the pet")
    val id: UUID,
    @GraphQLName("name")
    @GraphQLDescription("Name of the pet")
    val name: String,
    @GraphQLName("category")
    @GraphQLDescription("Category of the pet (e.g., Dog, Cat)")
    val category: String,
    @GraphQLName("status")
    @GraphQLDescription("Current status of the pet (e.g., Available, Adopted)")
    val status: String,
)
