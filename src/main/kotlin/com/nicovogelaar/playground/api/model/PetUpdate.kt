package com.nicovogelaar.playground.api.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Input model for updating a pet")
data class PetUpdate(
    @GraphQLDescription("Name of the pet")
    val name: String,
    @GraphQLDescription("Category of the pet")
    val category: String,
    @GraphQLDescription("Status of the pet")
    val status: String,
)
