package com.nicovogelaar.playground.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

const val ONE_HOUR = 3600000L

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secretKey: String = "",
    var validityInMs: Long = ONE_HOUR,
)

private val logger = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
) {
    companion object {
        private const val AUTHORITIES_KEY = "roles"
    }

    private lateinit var secretKey: SecretKey

    @PostConstruct
    fun init() {
        val secret =
            Base64.getEncoder()
                .encodeToString(jwtProperties.secretKey.toByteArray())
        secretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }

    fun createToken(authentication: Authentication): String {
        val username = authentication.name
        val authorities = authentication.authorities

        val claims =
            Jwts.claims().setSubject(username).apply {
                if (authorities.isNotEmpty()) {
                    this[AUTHORITIES_KEY] = authorities.joinToString(",") { it.authority }
                }
            }

        val now = Date()
        val validity = Date(now.time + jwtProperties.validityInMs)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims =
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

        val authoritiesClaim = claims[AUTHORITIES_KEY]
        val authorities =
            if (authoritiesClaim == null) {
                AuthorityUtils.NO_AUTHORITIES
            } else {
                AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString())
            }

        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)

            logger.debug { "Expiration date: ${claims.body.expiration}" }
            true
        } catch (e: JwtException) {
            logger.debug { "Invalid JWT token: ${e.message}" }
            false
        } catch (e: IllegalArgumentException) {
            logger.debug { "Invalid JWT token: ${e.message}" }
            false
        }
    }
}
