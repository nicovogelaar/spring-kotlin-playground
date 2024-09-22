package com.nicovogelaar.playground.api

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class PlaygroundQuery : Query {
    @GraphQLDescription("Hello")
    fun hello(): String {
        return "Hello, GraphQL!"
    }
}
