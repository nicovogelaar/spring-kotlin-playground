package com.nicovogelaar.playground.graphql

import com.expediagroup.graphql.generator.extensions.toGraphQLContext
import com.expediagroup.graphql.server.spring.execution.DefaultSpringGraphQLContextFactory
import graphql.GraphQLContext
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.UUID
import kotlin.coroutines.CoroutineContext

@Component
class CustomContextFactory : DefaultSpringGraphQLContextFactory() {
    override suspend fun generateContext(request: ServerRequest): GraphQLContext {
        val correlationId = request.headers().firstHeader("X-Correlation-ID") ?: UUID.randomUUID().toString()

        val originalMdcContext = MDC.getCopyOfContextMap() ?: emptyMap()

        val newMdcContext =
            originalMdcContext.toMutableMap().apply {
                put("correlationId", correlationId)
            }

        MDC.setContextMap(newMdcContext)

        val context =
            mapOf(
                CoroutineContext::class to MDCContext(),
            ).toGraphQLContext()

        MDC.setContextMap(originalMdcContext)

        return context
    }
}
