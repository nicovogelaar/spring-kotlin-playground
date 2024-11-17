package com.nicovogelaar.playground.api

import com.nicovogelaar.playground.security.JwtTokenProvider
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AuthenticationRequest(
    val username: String,
    val password: String,
)

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val tokenProvider: JwtTokenProvider,
    private val authenticationManager: ReactiveAuthenticationManager,
) {
    @PostMapping("/login")
    suspend fun login(
        @RequestBody authRequest: AuthenticationRequest,
    ): ResponseEntity<Map<String, String>> {
        val authenticationToken = UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)

        val authentication = authenticationManager.authenticate(authenticationToken).awaitFirstOrNull()

        return if (authentication != null) {
            val jwt = tokenProvider.createToken(authentication)
            val httpHeaders =
                HttpHeaders().apply {
                    add(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
                }
            val tokenBody = mapOf("access_token" to jwt)
            ResponseEntity(tokenBody, httpHeaders, HttpStatus.OK)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
