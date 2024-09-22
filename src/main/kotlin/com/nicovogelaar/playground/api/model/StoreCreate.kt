package com.nicovogelaar.playground.api.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Input model for creating a store")
data class StoreCreate(
    @GraphQLDescription("Name of the store")
    val name: String,
    @GraphQLDescription("Location of the store")
    val location: String,
)
