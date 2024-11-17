package com.nicovogelaar.playground.security

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.User
import reactor.core.publisher.Mono

suspend fun Mono<SecurityContext>.getUserRoles(): List<String> {
    return this
        .map { securityContext ->
            val user = securityContext.authentication.principal as? User
            user?.authorities?.map { it.authority } ?: emptyList()
        }
        .awaitSingle()
}
