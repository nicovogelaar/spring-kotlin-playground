package com.nicovogelaar.playground.api.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Model for creating a new pet")
data class PetCreate(
    @GraphQLDescription("Name of the pet")
    val name: String,
    @GraphQLDescription("Category of the pet")
    val category: String,
    @GraphQLDescription("Status of the pet")
    val status: String,
)
